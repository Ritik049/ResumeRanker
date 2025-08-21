package com.resume.matcher.jwt;


import com.resume.matcher.security.CustomUserDetailsService;
import com.resume.matcher.service.RedisService;
import jakarta.servlet.*;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;


import java.io.IOException;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Autowired
    private JwtAuthenticationHelper jwtHelper;

    @Autowired
    private CustomUserDetailsService userDetailsService;

    @Autowired
    private RedisService redisService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String requestHeader = request.getHeader("Authorization");


        if (requestHeader != null && requestHeader.startsWith("Bearer ")) {
            String token = requestHeader.substring(7);
            String username = jwtHelper.getUsernameFromToken(token);
            String jti = jwtHelper.getJtiFromToken(token);




            if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                boolean status = redisService.isTokenValid(jti);
                if (!status) {
                    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                    response.getWriter().write("Token is revoked or invalid");
                    return;
                }


                UserDetails userDetails = userDetailsService.loadUserByUsername(username);

                if (!jwtHelper.isTokenExpired(token)) { // ✅ Ensure validation instead of just expiration check
                    UsernamePasswordAuthenticationToken authenticationToken =
                            new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                    authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authenticationToken);
                }
            }
        }
        else {
            if (request.getCookies() != null) {
                String token  = "";
                for (Cookie cookie : request.getCookies()) {
                    if ("jwt".equals(cookie.getName())) {
                        token = cookie.getValue();
                        break;
                    }
                }

                String username = jwtHelper.getUsernameFromToken(token);
                String jti = jwtHelper.getJtiFromToken(token);




                if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                    boolean status = redisService.isTokenValid(jti);
                    if (!status) {
                        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                        response.getWriter().write("Token is revoked or invalid");
                        return;
                    }


                    UserDetails userDetails = userDetailsService.loadUserByUsername(username);

                    if (!jwtHelper.isTokenExpired(token)) { // ✅ Ensure validation instead of just expiration check
                        UsernamePasswordAuthenticationToken authenticationToken =
                                new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                        authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                        SecurityContextHolder.getContext().setAuthentication(authenticationToken);
                    }
                }
            }

        }
        filterChain.doFilter(request, response);
    }

    private String extractJwtFromCookie(HttpServletRequest request) {
        if (request.getCookies() != null) {
            for (Cookie cookie : request.getCookies()) {
                if ("jwt".equals(cookie.getName())) {
                    return cookie.getValue();
                }
            }
        }
        return null;
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getRequestURI();
        System.out.println("Skipping JWT filter for: " + path);
        return path.startsWith("/auth/login") || path.startsWith("/auth/logout");
    }

}
