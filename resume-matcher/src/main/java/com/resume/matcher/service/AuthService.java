package com.resume.matcher.service;



import com.resume.matcher.dto.JwtRequest;
import com.resume.matcher.dto.JwtResponse;
import com.resume.matcher.jwt.JwtAuthenticationHelper;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.graphql.GraphQlProperties;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    @Autowired
    AuthenticationManager manager;

    @Autowired
    JwtAuthenticationHelper jwtHelper;

    @Autowired
    UserDetailsService userDetailsService;

    @Autowired
    RedisService redisService;

    public ResponseEntity<?> login(JwtRequest jwtRequest, HttpServletResponse servletResponse) {

        //authenticate with Authentication manager
        this.doAuthenticate(jwtRequest.getUsername(),jwtRequest.getPassword());


        UserDetails userDetails = userDetailsService.loadUserByUsername(jwtRequest.getUsername());
        // Revoke previous tokens
        redisService.revokeAllTokensForUser(userDetails.getUsername());


        String token = jwtHelper.generateToken(userDetails);
        String jti = jwtHelper.getJtiFromToken(token);

        // Store new token
        redisService.storeToken(jti, userDetails.getUsername());


        ResponseCookie cookie = ResponseCookie.from("jwt", token)
                .httpOnly(true)
                .secure(false)
                .path("/")
                .maxAge(3600)
                .build();

        servletResponse.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());



        JwtResponse response = JwtResponse.builder().jwtToken(token).build();

        return ResponseEntity.ok("Login Successful");
    }

    private void doAuthenticate(String username, String password) {

        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(username, password);
        try {
            manager.authenticate(authenticationToken);

        }catch (BadCredentialsException e) {
            throw new BadCredentialsException("Invalid Username or Password");
        }
    }

    public ResponseEntity<?>logout(String token, HttpServletResponse response)
    {
        try {
            String jti = jwtHelper.getJtiFromToken(token);
            String username = jwtHelper.getUsernameFromToken(token);

            // Revoke token in Redis
            redisService.revokeToken(jti, username);

            // Expire cookie
            ResponseCookie expiredCookie = ResponseCookie.from("jwt", "")
                    .httpOnly(true)
                    .secure(true)
                    .path("/")
                    .maxAge(0)
                    .sameSite("None")
                    .build();

            response.addHeader(HttpHeaders.SET_COOKIE, expiredCookie.toString());

            return ResponseEntity.ok("Logged out successfully");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid or missing token");
        }
    }

}
