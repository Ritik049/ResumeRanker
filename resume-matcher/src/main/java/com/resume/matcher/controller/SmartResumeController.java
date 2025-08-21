//package com.resume.matcher.controller;
//
//import com.resume.matcher.dto.ResumeJobRequest;
//import com.resume.matcher.dto.ResumeJobResponse;
//import com.resume.matcher.service.SmartResumeService;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.*;
//import org.springframework.web.multipart.MultipartFile;
//
//@RestController
//@RequestMapping("/smart/api/resumes")
//@CrossOrigin(origins = "*")
//public class SmartResumeController {
//
//    @Autowired
//    SmartResumeService smartResumeService;
//
//    @PostMapping("/upload")
//    public ResponseEntity<String> uploadResume(@RequestParam("file") MultipartFile file) {
//        try {
//            String text = smartResumeService.extractText(file);
//            System.out.println("TEXT "+text);
//            return ResponseEntity.ok(text);
//        } catch (Exception e) {
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to process file");
//        }
//    }
//
//    @PostMapping("/analyze")
//    public ResponseEntity<String> getAnalyse(@RequestBody ResumeJobRequest resumeJobRequest)
//    {
//        try
//        {
////            ResumeJobResponse response = smartResumeService.getAnalyse(resumeJobRequest);
//             String response = smartResumeService.getAnalyseAsync(resumeJobRequest);
//            return ResponseEntity.ok(response);
//        }
//        catch(Exception e)
//        {
//            throw new RuntimeException(e);
//        }
//    }
//
//
//
//}

package com.resume.matcher.controller;

import com.resume.matcher.dto.ResumeJobRequest;
import com.resume.matcher.service.SmartResumeService;
import com.resume.matcher.service.UpdatedSmartResumeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/smart/api/resumes")
@CrossOrigin(origins = "*")
public class SmartResumeController {

    @Autowired
    private UpdatedSmartResumeService smartResumeService;
//    private SmartResumeService smartResumeService;

    @PostMapping("/upload")
    public ResponseEntity<String> uploadResume(@RequestParam("file") MultipartFile file) {
        try {
            return ResponseEntity.ok(smartResumeService.extractText(file));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Failed to process file");
        }
    }

//    @PostMapping("/analyze")
//    public Mono<ResponseEntity<String>> analyzeResume(@RequestBody ResumeJobRequest resumeJobRequest) {
//        return smartResumeService.getAnalyseAsync(resumeJobRequest)
//                .map(ResponseEntity::ok);
//    }

    @PostMapping("/analyze")
    public String analyzeResume(@RequestBody ResumeJobRequest resumeJobRequest) {
        return smartResumeService.getAnalyse(resumeJobRequest);

    }
}

