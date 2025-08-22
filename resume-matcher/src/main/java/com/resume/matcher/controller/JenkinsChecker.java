package com.resume.matcher.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/jenkins-check")
public class JenkinsChecker {

    @GetMapping
    public String checkingJenkins()
    {
        return "Jenkins pushed this fine and CI/CD worked";
    }
}
