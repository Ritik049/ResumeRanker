package com.resume.matcher.controller;

import com.resume.matcher.jwt.JwtAuthenticationHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/auth")
public class RedisController {

    @Autowired
    JwtAuthenticationHelper jwtHelper;

    @Autowired
    RedisTemplate redisTemplate;



    @PostMapping("/admin/revoke-token")
    public ResponseEntity<?> revokeToken(@RequestBody String token) {
        String jti = jwtHelper.getJtiFromToken(token);
        redisTemplate.delete("jwt:" + jti); // ⛔ Immediate revocation
        return ResponseEntity.ok("Token revoked");
    }

    @GetMapping("/admin/active-jtis")
    public ResponseEntity<List<String>> getActiveJtis() {
        Set<String> keys = redisTemplate.keys("jwt:*");
        List<String> jtis = keys.stream()
                .map(key -> key.replace("jwt:", ""))
                .collect(Collectors.toList());

        return ResponseEntity.ok(jtis);
    }
}
