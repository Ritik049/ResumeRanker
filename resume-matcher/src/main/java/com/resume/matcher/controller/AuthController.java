package com.resume.matcher.controller;


import com.resume.matcher.dto.JwtRequest;
import com.resume.matcher.dto.JwtResponse;
import com.resume.matcher.service.AuthService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@CrossOrigin(origins = "http://localhost:3000")
public class AuthController {

    @Autowired
    AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody JwtRequest jwtRequest, HttpServletResponse response)
    {
       // return new ResponseEntity<>(authService.login(jwtRequest), HttpStatus.OK);
        return authService.login(jwtRequest,response);
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(@CookieValue("jwt") String token, HttpServletResponse response) {
        System.out.println(token);
        return authService.logout(token,response);

    }
}