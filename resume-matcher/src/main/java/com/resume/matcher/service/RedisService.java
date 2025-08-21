package com.resume.matcher.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.List;

@Service
public class RedisService {

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    public void storeToken(String jti,String username) {
        redisTemplate.opsForValue().set("jwt:" + jti, "valid", Duration.ofHours(1));
        redisTemplate.opsForList().rightPush("user:" + username + ":jtis", jti);


}

    public boolean isTokenValid(String jti) {
        String status = redisTemplate.opsForValue().get("jwt:" + jti);
        return "valid".equals(status);
    }

    public void revokeToken(String jti, String username) {
        redisTemplate.delete("jwt:" + jti);
        redisTemplate.opsForList().remove("user:" + username + ":jtis", 1, jti);
    }

    public void revokeAllTokensForUser(String username) {
        List<String> jtIs = redisTemplate.opsForList().range("user:" + username + ":jtis", 0, -1);
        if (jtIs != null) {
            jtIs.forEach(jti -> redisTemplate.delete("jwt:" + jti));
            redisTemplate.delete("user:" + username + ":jtis");
        }
    }

}
