package com.resume.matcher.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data

@Getter
@Setter
public class ResumeJobRequest {

    String resume;
    String jobDescription;

    public String getResume() {
        return resume;
    }

    public void setResume(String resume) {
        this.resume = resume;
    }

    public String getJobDescription() {
        return jobDescription;
    }

    public void setJobDescription(String jobDescription) {
        this.jobDescription = jobDescription;
    }

    public ResumeJobRequest(String resume, String jobDescription) {
        this.resume = resume;
        this.jobDescription = jobDescription;
    }
}
