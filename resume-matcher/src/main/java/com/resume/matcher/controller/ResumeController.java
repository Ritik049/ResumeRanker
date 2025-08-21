package com.resume.matcher.controller;

import com.resume.matcher.dto.ResumeJobRequest;
import com.resume.matcher.dto.ResumeJobResponse;
import com.resume.matcher.service.ResumeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/resumes")
@CrossOrigin(origins = "http://localhost:3000")
public class ResumeController {

    @Autowired
    private ResumeService resumeService;




    @PostMapping("/upload")
    public ResponseEntity<String> uploadResume(@RequestParam("file") MultipartFile file) {
        try {
            String text = resumeService.extractText(file);
            System.out.println("TEXT "+text);
            return ResponseEntity.ok(text);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to process file");
        }
    }

    @PostMapping("/tokenise")
    public ResponseEntity<String> getTokens(@RequestBody  String text)
    {
        try
        {
            String tokens = resumeService.extractTokens(text);
            System.out.println("TOKENS "+tokens);
            return ResponseEntity.ok(tokens);
        }
        catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to process file");
        }

    }

    @PostMapping("/analyze")
    public ResponseEntity<ResumeJobResponse> getAnalyse(@RequestBody ResumeJobRequest resumeJobRequest)
    {
        try
        {
            ResumeJobResponse response = resumeService.getAnalyse(resumeJobRequest);

            return ResponseEntity.ok(response);
        }
        catch(Exception e)
        {
            throw new RuntimeException(e);
        }
    }



}

