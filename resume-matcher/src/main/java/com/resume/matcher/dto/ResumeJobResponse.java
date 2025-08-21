package com.resume.matcher.dto;

import lombok.*;

import java.util.List;

@Data

@Getter
@Setter
public class ResumeJobResponse {
    float matchScore;
    List<String> resumeKeywords;
    List<String>jobKeywords;
    List<String>missingKeywords;

    public float getMatchScore() {
        return matchScore;
    }

    public void setMatchScore(float matchScore) {
        this.matchScore = matchScore;
    }

    public List<String> getResumeKeywords() {
        return resumeKeywords;
    }

    public void setResumeKeywords(List<String> resumeKeywords) {
        this.resumeKeywords = resumeKeywords;
    }

    public List<String> getJobKeywords() {
        return jobKeywords;
    }

    public void setJobKeywords(List<String> jobKeywords) {
        this.jobKeywords = jobKeywords;
    }

    public List<String> getMissingKeywords() {
        return missingKeywords;
    }

    public void setMissingKeywords(List<String> missingKeywords) {
        this.missingKeywords = missingKeywords;
    }

    public ResumeJobResponse(float matchScore, List<String> resumeKeywords, List<String> jobKeywords, List<String> missingKeywords) {
        this.matchScore = matchScore;
        this.resumeKeywords = resumeKeywords;
        this.jobKeywords = jobKeywords;
        this.missingKeywords = missingKeywords;
    }

    public ResumeJobResponse()
    {

    }
}

