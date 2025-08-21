package com.resume.matcher.service;


import com.resume.matcher.dto.ResumeJobRequest;
import com.resume.matcher.dto.ResumeJobResponse;
import com.resume.matcher.util.KeyWordExtractor;
import com.resume.matcher.util.SimilarityUtil;
import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.util.CoreMap;
import org.apache.tika.Tika;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.nio.charset.StandardCharsets;


import java.io.IOException;
import java.util.*;

import static edu.stanford.nlp.util.Sets.assertEquals;

@Service
public class ResumeService {

    private final Tika tika = new Tika();


    public String extractText(MultipartFile file) {
       try {

           System.out.println("Parsing file: " + file.getOriginalFilename());
//           String content = new String(file.getBytes(), StandardCharsets.UTF_8); //not needed
           String text = tika.parseToString(file.getInputStream());
           System.out.println("EXTRACTED TEXT"+text);

           return text;

       }
       catch (Exception e) {
           e.printStackTrace();
              throw new RuntimeException("Error occurred while parsing file", e);
       }
    }

    public String extractTokens(String text)
    {
        Properties props = new Properties();
        props.setProperty("annotators", "tokenize");
        StanfordCoreNLP pipeline = new StanfordCoreNLP(props);

//        String text = "The german shepard display an act of kindness";
        Annotation document = new Annotation(text);
        pipeline.annotate(document);

        List<CoreMap> sentences = document.get(CoreAnnotations.SentencesAnnotation.class);

        StringBuilder tokens = new StringBuilder();

        for (CoreMap sentence : sentences) {
            System.out.println("SENTENCES "+sentence);
            for (CoreLabel token : sentence.get(CoreAnnotations.TokensAnnotation.class)) {
                String word = token.get(CoreAnnotations.TextAnnotation.class);
                tokens.append(word).append(" ");
            }
        }


        String resume = "Java Spring Boot REST API developer with experience in microservices and Docker.";
        String job = "Looking for a backend developer skilled in Java, Spring Boot, and containerization.";
        float score = getMatchScore(resume, job);
        System.out.println("Match Score: " + score);

        //Extracting keywords
        List<String>resumeKeyWords = getResumeKeywords(resume);
        for(String s : resumeKeyWords)
        {
            System.out.println("REsume Keys: "+s);
        }

        List<String>missing = getMissingKeywords(resume,job);
        for(String s : missing)
        {
            System.out.println("missing Keys: "+s);
        }

        // assertEquals("The german shepard display an act of kindness", tokens.toString().trim());
      return tokens.toString();
    }

    public float getMatchScore(String resumeText, String jobDescription) {
        try {
            return SimilarityUtil.computeSimilarity(resumeText, jobDescription);
        } catch (IOException e) {
            throw new RuntimeException("Error computing similarity", e);
        }
    }

    public List<String> getResumeKeywords(String resumeText) {
        return KeyWordExtractor.extractKeywords(resumeText, 15); // top 15 keywords
    }

    public List<String> getMissingKeywords(String resumeText, String jobText) {
        List<String> resumeKeywords = KeyWordExtractor.extractKeywords(resumeText, 20);
        List<String> jobKeywords = KeyWordExtractor.extractKeywords(jobText, 20);

        Set<String> missing = new HashSet<>(jobKeywords);
        missing.removeAll(resumeKeywords);

        return new ArrayList<>(missing);
    }

    public ResumeJobResponse getAnalyse(ResumeJobRequest resumeJobRequest)
    {
         float matchScore = getMatchScore(resumeJobRequest.getResume(),resumeJobRequest.getJobDescription());
         List<String>resumeKeywords = getResumeKeywords(resumeJobRequest.getResume());
         List<String>jobKeywords = getResumeKeywords(resumeJobRequest.getJobDescription());
         List<String>missingKeywords = getMissingKeywords(resumeJobRequest.getResume(), resumeJobRequest.getJobDescription());

         ResumeJobResponse resumeJobResponse = new ResumeJobResponse();
         resumeJobResponse.setMatchScore(matchScore);
         resumeJobResponse.setResumeKeywords(resumeKeywords);
         resumeJobResponse.setJobKeywords(jobKeywords);
         resumeJobResponse.setMissingKeywords(missingKeywords);

         return resumeJobResponse;
    }



}
