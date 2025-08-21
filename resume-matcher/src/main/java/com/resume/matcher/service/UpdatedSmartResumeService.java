package com.resume.matcher.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.resume.matcher.dto.ResumeJobRequest;
import com.resume.matcher.util.PdfGenerator;
import org.apache.tika.Tika;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Map;

@Service
public class UpdatedSmartResumeService {

    private final Tika tika = new Tika();

    private WebClient webClient;

    public UpdatedSmartResumeService(WebClient.Builder webClientBuilder)
    {
        this.webClient = webClientBuilder.build();
    }

    @Value("${gemini.api.url}")
    private String geminiApiUrl;

    @Value("${gemini.api.key}")
    private String geminiApiKey;

    //Extracting Text from File
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

    //Generate Response
    public String getAnalyse(ResumeJobRequest resumeJobRequest)
    {
        String prompt = buildPrompt(resumeJobRequest);
        // String prompt = "SAY HELLO";

        //Craft a request
        Map<String,Object> requestBody = Map.of(
                "contents",new Object[]{
                        Map.of("parts",new Object[]
                                {
                                        Map.of("text",prompt)
                                })
                }
        );

        //Do request and get response
        String response = webClient.post().uri(geminiApiUrl+geminiApiKey).header("Content-Type","application/json").bodyValue(requestBody).retrieve().bodyToMono(String.class).block();

        String finalResponse =  extractResponseContent(response);

        String formattedResponse = getFormattedResponse(finalResponse);
        String finalFormattedResponse = extractResponseContent(formattedResponse);
//        PdfGenerator.generatePDF(finalFormattedResponse, "C:/Users/lenovo/Downloads/resume-matcher (2)/resume-matcher/resume-matcher/src/main/java/com/resume/matcher/Analysis/ResumeAnalysis.pdf");
        System.out.println("FINAL RESPONSE "+finalResponse);
        System.out.println("FINAL Formatted RESPONSE "+finalFormattedResponse);
        return finalResponse;
    }

    //build prompt
    private String buildPrompt(ResumeJobRequest resumeJobRequest) {
        StringBuilder prompt = new StringBuilder();

        prompt.append("You are an intelligent resume analysis assistant.\n");
        prompt.append("Your task is to compare a candidate's resume with a job description and provide the following:\n");
        prompt.append("1. A match score (0-100) based on relevance using TF-IDF and cosine similarity.\n");
        prompt.append("2. A list of keywords extracted from both the resume and the job description.\n");
        prompt.append("3. A list of missing or weakly represented skills in the resume that are important in the job description.\n");
        prompt.append("4. Personalized feedback on how the resume can be improved to better match the job.\n");
        prompt.append("5. A short summary of the candidate's strengths based on the resume.\n");
        prompt.append("6. Format the output in a structured way suitable for generating a PDF report.\n\n");

        prompt.append("Resume Text:\n");
        prompt.append(resumeJobRequest.getResume()).append("\n\n");

        prompt.append("Job Description Text:\n");
        prompt.append(resumeJobRequest.getJobDescription()).append("\n\n");

        prompt.append("Now perform the analysis and return the results in a structured JSON format with fields: match_score, resume_keywords, job_keywords, missing_skills, feedback, strengths_summary.");

        return prompt.toString();
    }

    //Extracting response
    public String extractResponseContent(String response)
    {
        try
        {
            ObjectMapper mapper = new ObjectMapper();
            JsonNode rootNode = mapper.readTree(response);
            return rootNode.path("candidates").get(0).path("content").path("parts").get(0).path("text").asText();


        }catch(Exception e)
        {
            return "Error processing request: "+e.getMessage();
        }
    }

    public String getFormattedResponse(String rawJsonResponse) {
        String prompt = "Format this JSON into a professional,and With heading Resume Analysis Report and not put date and remember  not use ** for making words bold and only in 1 page and in human-readable resume analysis report:\n\n" + rawJsonResponse;

        Map<String, Object> requestBody = Map.of(
                "contents", new Object[] {
                        Map.of("parts", new Object[] {
                                Map.of("text", prompt)
                        })
                }
        );

        return webClient.post()
                .uri(geminiApiUrl + geminiApiKey)
                .header("Content-Type", "application/json")
                .bodyValue(requestBody)
                .retrieve()
                .bodyToMono(String.class)
                .block();
    }






}


