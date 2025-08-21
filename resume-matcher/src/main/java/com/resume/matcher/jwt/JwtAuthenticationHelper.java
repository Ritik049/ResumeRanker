package com.resume.matcher.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.spec.SecretKeySpec;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Component
public class JwtAuthenticationHelper {


    @Value("${jwt.secret}")
    private String secret;

    private static final long JWT_TOKEN_VALIDITY = 60*60;

    public String getUsernameFromToken(String token)
    {
        Claims claims =  getClaimsFromToken(token);
        return claims.getSubject();
    }

    public String getJtiFromToken(String token)
    {
        Claims claims = getClaimsFromToken(token);
        return claims.getId();
    }

    public Claims getClaimsFromToken(String token)
    {
        return Jwts.parserBuilder().setSigningKey(secret.getBytes())
                .build().parseClaimsJws(token).getBody();
    }

    public Boolean isTokenExpired(String token)
    {
        Claims claims =  getClaimsFromToken(token);
        Date expDate = claims.getExpiration();
        return expDate.before(new Date());
    }

    public String generateToken(UserDetails userDetails) {

        Map<String,Object> claims = new HashMap<>();
        String jti = UUID.randomUUID().toString();

        return Jwts.builder().setClaims(claims).setSubject(userDetails.getUsername())
                .setId(jti)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis()+JWT_TOKEN_VALIDITY*1000))
                .signWith(new SecretKeySpec(secret.getBytes(),SignatureAlgorithm.HS512.getJcaName()),SignatureAlgorithm.HS512)
                .compact();
    }

}