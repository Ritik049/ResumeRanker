package com.resume.matcher.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.resume.matcher.dto.ResumeJobRequest;
import com.resume.matcher.util.PdfGenerator;
import org.apache.tika.Tika;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.netty.http.client.HttpClient;

import java.time.Duration;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class SmartResumeService {

    private final Tika tika = new Tika();
    private final WebClient webClient;

    public SmartResumeService(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder
                .clientConnector(new ReactorClientHttpConnector(
                        HttpClient.create().responseTimeout(Duration.ofSeconds(30))))
                .build();
    }

    @Value("${gemini.api.url}")
    private String geminiApiUrl;

    @Value("${gemini.api.key}")
    private String geminiApiKey;

    // Extract text from file efficiently
    public String extractText(MultipartFile file) {
        try {
            System.out.println("Parsing file: " + file.getOriginalFilename());
            return tika.parseToString(file.getInputStream());
        } catch (Exception e) {
            throw new RuntimeException("Error parsing file", e);
        }
    }

    // Analyze resume asynchronously
    public Mono<String> getAnalyseAsync(ResumeJobRequest resumeJobRequest) {
        String prompt = buildPrompt(resumeJobRequest);
        Map<String, Object> requestBody = Map.of(
                "contents", new Object[]{
                        Map.of("parts", new Object[]{
                                Map.of("text", prompt)
                        })
                }
        );

        // Fix 1: Correctly append API key as a query parameter
        return webClient.post()
                .uri(geminiApiUrl + "?key=" + geminiApiKey)
                .header("Content-Type", "application/json")
                .bodyValue(requestBody)
                .retrieve()
                .bodyToMono(String.class)
                .map(this::extractResponseContent)
                // Fix 2: Chain the getFormattedResponse call reactively
                .flatMap(this::getFormattedResponse) // Use flatMap for Mono<Mono<String>>
                .map(this::extractResponseContent) // If getFormattedResponse returns a JSON string that needs further extraction
                .doOnNext(finalFormattedResponse -> PdfGenerator.generatePDF(finalFormattedResponse, "ResumeAnalysis.pdf"));
    }

    // Build the prompt
    private String buildPrompt(ResumeJobRequest resumeJobRequest) {
        return String.format("""
            You are an intelligent resume analysis assistant.
            Compare the candidate's resume with the job description and provide the following:
            1. Match score (0-100) using TF-IDF & cosine similarity.
            2. Extract resume & job keywords.
            3. Identify missing/weakly represented skills.
            4. Personalized feedback to improve the resume.
            5. Summary of strengths based on resume.
            6. Format as a structured JSON suitable for a PDF report.

            Resume Text: %s
            Job Description Text: %s

            Return results in JSON with fields: match_score, resume_keywords, job_keywords, missing_skills, feedback, strengths_summary.
        """, resumeJobRequest.getResume(), resumeJobRequest.getJobDescription());
    }

    // Extract response content
//    private String extractResponseContent(String response) {
//        try {
//            ObjectMapper mapper = new ObjectMapper();
//            JsonNode rootNode = mapper.readTree(response);
//            // Defensive coding: Check if nodes exist before accessing
//            if (rootNode.path("candidates").isArray() && rootNode.path("candidates").get(0) != null &&
//                    rootNode.path("candidates").get(0).path("content").path("parts").isArray() &&
//                    rootNode.path("candidates").get(0).path("content").path("parts").get(0) != null) {
//                return rootNode.path("candidates").get(0).path("content").path("parts").get(0).path("text").asText();
//            } else {
//                return "Error: Unexpected response structure from Gemini API. Raw response: " + response;
//            }
//        } catch (Exception e) {
//            return "Error processing request: " + e.getMessage() + ". Raw response: " + response;
//        }
//    }


    private String extractResponseContent(String response) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            JsonNode rootNode = mapper.readTree(response);

            // Use a defensive check to navigate the JSON structure
            if (rootNode.hasNonNull("candidates") && rootNode.get("candidates").isArray() &&
                    rootNode.get("candidates").get(0).hasNonNull("content") &&
                    rootNode.get("candidates").get(0).get("content").hasNonNull("parts") &&
                    rootNode.get("candidates").get(0).get("content").get("parts").isArray() &&
                    rootNode.get("candidates").get(0).get("content").get("parts").get(0).hasNonNull("text")) {

                String fullText = rootNode.get("candidates").get(0)
                        .get("content").get("parts").get(0)
                        .get("text").asText();

                // Use regex to find and extract the JSON from the Markdown block
                Pattern pattern = Pattern.compile("```json\\n([\\s\\S]*?)\\n```");
                Matcher matcher = pattern.matcher(fullText);

                if (matcher.find()) {
                    String jsonString = matcher.group(1);
                    // Log the extracted JSON for debugging
                    System.out.println("Extracted JSON: " + jsonString);
                    return jsonString;
                } else {
                    // If no code block is found, return the text as is (may not be valid JSON)
                    System.err.println("Warning: No JSON code block found. Returning raw text.");
                    return fullText;
                }
            } else {
                System.err.println("Error: Unexpected response structure from Gemini API. Raw response: " + response);
                return "Error: Unexpected response structure from Gemini API.";
            }
        } catch (Exception e) {
            System.err.println("Error processing Gemini API response: " + e.getMessage() + ". Raw response: " + response);
            return "Error processing request: " + e.getMessage();
        }
    }

    // Format the response asynchronously - now returns Mono<String>
    private Mono<String> getFormattedResponse(String rawJsonResponse) {
        String prompt = "Format this JSON into a professional Resume Analysis Report:\n\n" + rawJsonResponse;

        Map<String, Object> requestBody = Map.of(
                "contents", new Object[]{
                        Map.of("parts", new Object[]{
                                Map.of("text", prompt)
                        })
                }
        );

        // Fix 3: Correctly append API key as a query parameter
        return webClient.post()
                .uri(geminiApiUrl + "?key=" + geminiApiKey)
                .header("Content-Type", "application/json")
                .bodyValue(requestBody)
                .retrieve()
                .bodyToMono(String.class);
    }
}
////////
////////package com.resume.matcher.service;
////////
////////import com.fasterxml.jackson.databind.JsonNode;
////////import com.fasterxml.jackson.databind.ObjectMapper;
////////import com.resume.matcher.dto.ResumeJobRequest;
////////import com.resume.matcher.util.PdfGenerator;
////////import org.apache.tika.Tika;
////////import org.springframework.beans.factory.annotation.Value;
////////import org.springframework.http.client.reactive.ReactorClientHttpConnector;
////////import org.springframework.stereotype.Service;
////////import org.springframework.web.multipart.MultipartFile;
////////import org.springframework.web.reactive.function.client.WebClient;
////////import reactor.core.publisher.Mono;
////////import reactor.netty.http.client.HttpClient;
////////
////////import java.time.Duration;
////////import java.util.List;
////////import java.util.Map;
////////
////////@Service
////////public class SmartResumeService {
////////
////////    private final Tika tika = new Tika();
////////    private final WebClient webClient;
////////
////////    public SmartResumeService(WebClient.Builder webClientBuilder) {
////////        this.webClient = webClientBuilder
////////                .clientConnector(new ReactorClientHttpConnector(
////////                        HttpClient.create().responseTimeout(Duration.ofSeconds(30))))
////////                .build();
////////    }
////////
////////    @Value("${gemini.api.url}")
////////    private String geminiApiUrl;
////////
////////    @Value("${gemini.api.key}")
////////    private String geminiApiKey;
////////
////////    // Extract text from file efficiently
////////    public String extractText(MultipartFile file) {
////////        try {
////////            System.out.println("Parsing file: " + file.getOriginalFilename());
////////            return tika.parseToString(file.getInputStream());
////////        } catch (Exception e) {
////////            throw new RuntimeException("Error parsing file", e);
////////        }
////////    }
////////
////////    // Analyze resume asynchronously
////////    public Mono<String> getAnalyseAsync(ResumeJobRequest resumeJobRequest) {
////////        String prompt = buildPrompt(resumeJobRequest);
////////
////////        // ✅ FIXED: Use List.of instead of Object[]
////////        Map<String, Object> requestBody = Map.of(
////////                "contents", List.of(
////////                        Map.of("parts", List.of(
////////                                Map.of("text", prompt)
////////                        ))
////////                )
////////        );
////////
////////        return webClient.post()
////////                .uri(geminiApiUrl + "?key=" + geminiApiKey)
////////                .header("Content-Type", "application/json")
////////                .bodyValue(requestBody)
////////                .retrieve()
////////                .bodyToMono(String.class)
////////                .map(this::extractResponseContent)
////////                .flatMap(this::getFormattedResponse) // Format nicely
////////                .map(this::extractResponseContent)
////////                .doOnNext(finalFormattedResponse ->
////////                        PdfGenerator.generatePDF(finalFormattedResponse, "ResumeAnalysis.pdf"));
////////    }
////////
////////    // Build the prompt
////////    private String buildPrompt(ResumeJobRequest resumeJobRequest) {
////////        return String.format("""
////////            You are an intelligent resume analysis assistant.
////////            Compare the candidate's resume with the job description and provide the following:
////////            1. Match score (0-100) using TF-IDF & cosine similarity.
////////            2. Extract resume & job keywords.
////////            3. Identify missing/weakly represented skills.
////////            4. Personalized feedback to improve the resume.
////////            5. Summary of strengths based on resume.
////////            6. Format as a structured JSON suitable for a PDF report.
////////
////////            Resume Text: %s
////////            Job Description Text: %s
////////
////////            Return results in JSON with fields: match_score, resume_keywords, job_keywords, missing_skills, feedback, strengths_summary.
////////        """, resumeJobRequest.getResume(), resumeJobRequest.getJobDescription());
////////    }
////////
////////    // Extract response content safely
////////    private String extractResponseContent(String response) {
////////        try {
////////            ObjectMapper mapper = new ObjectMapper();
////////            JsonNode rootNode = mapper.readTree(response);
////////
////////            if (rootNode.path("candidates").isArray()
////////                    && rootNode.path("candidates").get(0) != null
////////                    && rootNode.path("candidates").get(0).path("content").path("parts").isArray()
////////                    && rootNode.path("candidates").get(0).path("content").path("parts").get(0) != null) {
////////                return rootNode.path("candidates").get(0)
////////                        .path("content").path("parts").get(0)
////////                        .path("text").asText();
////////            } else {
////////                return "Error: Unexpected response structure from Gemini API. Raw response: " + response;
////////            }
////////        } catch (Exception e) {
////////            return "Error processing request: " + e.getMessage() + ". Raw response: " + response;
////////        }
////////    }
////////
////////    // Format the response asynchronously
////////    private Mono<String> getFormattedResponse(String rawJsonResponse) {
////////        String prompt = "Format this JSON into a professional Resume Analysis Report:\n\n" + rawJsonResponse;
////////
////////        // ✅ FIXED: Use List.of instead of Object[]
////////        Map<String, Object> requestBody = Map.of(
////////                "contents", List.of(
////////                        Map.of("parts", List.of(
////////                                Map.of("text", prompt)
////////                        ))
////////                )
////////        );
////////
////////        return webClient.post()
////////                .uri(geminiApiUrl + "?key=" + geminiApiKey)
////////                .header("Content-Type", "application/json")
////////                .bodyValue(requestBody)
////////                .retrieve()
////////                .bodyToMono(String.class);
////////    }
////////}
//////package com.resume.matcher.service;
//////
//////import com.fasterxml.jackson.databind.JsonNode;
//////import com.fasterxml.jackson.databind.ObjectMapper;
//////import com.resume.matcher.dto.ResumeJobRequest;
//////import com.resume.matcher.util.PdfGenerator;
//////import org.apache.tika.Tika;
//////import org.springframework.beans.factory.annotation.Value;
//////import org.springframework.http.client.reactive.ReactorClientHttpConnector;
//////import org.springframework.stereotype.Service;
//////import org.springframework.web.multipart.MultipartFile;
//////import org.springframework.web.reactive.function.client.WebClient;
//////import reactor.core.publisher.Mono;
//////import reactor.netty.http.client.HttpClient;
//////
//////import java.time.Duration;
//////import java.util.List;
//////import java.util.Map;
//////
//////@Service
//////public class SmartResumeService {
//////
//////    private final Tika tika = new Tika();
//////    private final WebClient webClient;
//////
//////    public SmartResumeService(WebClient.Builder webClientBuilder) {
//////        this.webClient = webClientBuilder
//////                .clientConnector(new ReactorClientHttpConnector(
//////                        HttpClient.create().responseTimeout(Duration.ofSeconds(30))))
//////                .build();
//////    }
//////
//////    @Value("${gemini.api.url}")
//////    private String geminiApiUrl;
//////
//////    @Value("${gemini.api.key}")
//////    private String geminiApiKey;
//////
//////    /**
//////     * Extracts text from a given file efficiently.
//////     *
//////     * @param file The multipart file to parse.
//////     * @return The extracted text as a String.
//////     */
//////    public String extractText(MultipartFile file) {
//////        try {
//////            System.out.println("Parsing file: " + file.getOriginalFilename());
//////            return tika.parseToString(file.getInputStream());
//////        } catch (Exception e) {
//////            throw new RuntimeException("Error parsing file", e);
//////        }
//////    }
//////
//////    /**
//////     * Analyzes resume asynchronously by making a call to the Gemini API.
//////     *
//////     * @param resumeJobRequest The request containing the resume and job description.
//////     * @return A Mono emitting the final formatted analysis report as a String.
//////     */
//////    public Mono<String> getAnalyseAsync(ResumeJobRequest resumeJobRequest) {
//////        String prompt = buildPrompt(resumeJobRequest);
//////
//////        // Define the request body using a more robust structure
//////        Map<String, Object> requestBody = Map.of(
//////                "contents", List.of(
//////                        Map.of("parts", List.of(
//////                                Map.of("text", prompt)
//////                        ))
//////                )
//////        );
//////
//////        return webClient.post()
//////                .uri(uriBuilder -> uriBuilder.path(geminiApiUrl).queryParam("key", geminiApiKey).build())
//////                .header("Content-Type", "application/json")
//////                .bodyValue(requestBody)
//////                .retrieve()
//////                .bodyToMono(String.class)
//////                .map(this::extractResponseContent)
//////                .flatMap(this::getFormattedResponse)
//////                .map(this::extractResponseContent)
//////                .doOnNext(finalFormattedResponse ->
//////                        PdfGenerator.generatePDF(finalFormattedResponse, "ResumeAnalysis.pdf"));
//////    }
//////
//////    /**
//////     * Builds the prompt string to be sent to the Gemini API.
//////     *
//////     * @param resumeJobRequest The request object with resume and job description.
//////     * @return A formatted prompt string.
//////     */
//////    private String buildPrompt(ResumeJobRequest resumeJobRequest) {
//////        return String.format("""
//////            You are an intelligent resume analysis assistant.
//////            Compare the candidate's resume with the job description and provide the following:
//////            1. Match score (0-100) using TF-IDF & cosine similarity.
//////            2. Extract resume & job keywords.
//////            3. Identify missing/weakly represented skills.
//////            4. Personalized feedback to improve the resume.
//////            5. Summary of strengths based on resume.
//////            6. Format as a structured JSON suitable for a PDF report.
//////
//////            Resume Text: %s
//////            Job Description Text: %s
//////
//////            Return results in JSON with fields: match_score, resume_keywords, job_keywords, missing_skills, feedback, strengths_summary.
//////        """, resumeJobRequest.getResume(), resumeJobRequest.getJobDescription());
//////    }
//////
//////    /**
//////     * Extracts the text content from the Gemini API JSON response.
//////     *
//////     * @param response The raw JSON response from the API.
//////     * @return The extracted text or an error message.
//////     */
//////    private String extractResponseContent(String response) {
//////        try {
//////            ObjectMapper mapper = new ObjectMapper();
//////            JsonNode rootNode = mapper.readTree(response);
//////
//////            if (rootNode.path("candidates").isArray()
//////                    && rootNode.path("candidates").get(0) != null
//////                    && rootNode.path("candidates").get(0).path("content").path("parts").isArray()
//////                    && rootNode.path("candidates").get(0).path("content").path("parts").get(0) != null) {
//////                return rootNode.path("candidates").get(0)
//////                        .path("content").path("parts").get(0)
//////                        .path("text").asText();
//////            } else {
//////                return "Error: Unexpected response structure from Gemini API. Raw response: " + response;
//////            }
//////        } catch (Exception e) {
//////            return "Error processing request: " + e.getMessage() + ". Raw response: " + response;
//////        }
//////    }
//////
//////    /**
//////     * Formats the raw JSON response into a professional report using the Gemini API.
//////     *
//////     * @param rawJsonResponse The JSON string to be formatted.
//////     * @return A Mono emitting the formatted report.
//////     */
//////    private Mono<String> getFormattedResponse(String rawJsonResponse) {
//////        String prompt = "Format this JSON into a professional Resume Analysis Report:\n\n" + rawJsonResponse;
//////
//////        Map<String, Object> requestBody = Map.of(
//////                "contents", List.of(
//////                        Map.of("parts", List.of(
//////                                Map.of("text", prompt)
//////                        ))
//////                )
//////        );
//////
//////        return webClient.post()
//////                .uri(uriBuilder -> uriBuilder.path(geminiApiUrl).queryParam("key", geminiApiKey).build())
//////                .header("Content-Type", "application/json")
//////                .bodyValue(requestBody)
//////                .retrieve()
//////                .bodyToMono(String.class);
//////    }
//////}
////
////package com.resume.matcher.service;
////
////import com.fasterxml.jackson.databind.JsonNode;
////import com.fasterxml.jackson.databind.ObjectMapper;
////import com.resume.matcher.dto.ResumeJobRequest;
////import com.resume.matcher.util.PdfGenerator;
////import org.apache.tika.Tika;
////import org.springframework.beans.factory.annotation.Value;
////import org.springframework.http.client.reactive.ReactorClientHttpConnector;
////import org.springframework.stereotype.Service;
////import org.springframework.web.multipart.MultipartFile;
////import org.springframework.web.reactive.function.client.WebClient;
////import reactor.core.publisher.Mono;
////import reactor.netty.http.client.HttpClient;
////
////import java.time.Duration;
////import java.util.List;
////import java.util.Map;
////
////@Service
////public class SmartResumeService {
////
////    private final Tika tika = new Tika();
////    private final WebClient webClient;
////
////    public SmartResumeService(WebClient.Builder webClientBuilder) {
////        this.webClient = webClientBuilder
////                .clientConnector(new ReactorClientHttpConnector(
////                        HttpClient.create().responseTimeout(Duration.ofSeconds(30))))
////                .build();
////    }
////
////    @Value("${gemini.api.url}")
////    private String geminiApiUrl;
////
////    @Value("${gemini.api.key}")
////    private String geminiApiKey;
////
////    /**
////     * Extracts text from a given file efficiently.
////     *
////     * @param file The multipart file to parse.
////     * @return The extracted text as a String.
////     */
////    public String extractText(MultipartFile file) {
////        try {
////            System.out.println("Parsing file: " + file.getOriginalFilename());
////            return tika.parseToString(file.getInputStream());
////        } catch (Exception e) {
////            throw new RuntimeException("Error parsing file", e);
////        }
////    }
////
////    /**
////     * Analyzes resume asynchronously by making a call to the Gemini API.
////     *
////     * @param resumeJobRequest The request containing the resume and job description.
////     * @return A Mono emitting the final formatted analysis report as a String.
////     */
////    public Mono<String> getAnalyseAsync(ResumeJobRequest resumeJobRequest) {
////        String prompt = buildPrompt(resumeJobRequest);
////
////        Map<String, Object> requestBody = Map.of(
////                "contents", List.of(
////                        Map.of("parts", List.of(
////                                Map.of("text", prompt)
////                        ))
////                )
////        );
////
////        // ✅ FIXED: Use a complete, absolute URI. The uriBuilder.path() method is not suitable here.
////        String fullUri = geminiApiUrl + "?key=" + geminiApiKey;
////
////        return webClient.post()
////                .uri(fullUri)
////                .header("Content-Type", "application/json")
////                .bodyValue(requestBody)
////                .retrieve()
////                .bodyToMono(String.class)
////                .map(this::extractResponseContent)
////                .flatMap(this::getFormattedResponse)
////                .map(this::extractResponseContent)
////                .doOnNext(finalFormattedResponse ->
////                        PdfGenerator.generatePDF(finalFormattedResponse, "ResumeAnalysis.pdf"));
////    }
////
////    /**
////     * Builds the prompt string to be sent to the Gemini API.
////     *
////     * @param resumeJobRequest The request object with resume and job description.
////     * @return A formatted prompt string.
////     */
////    private String buildPrompt(ResumeJobRequest resumeJobRequest) {
////        return String.format("""
////            You are an intelligent resume analysis assistant.
////            Compare the candidate's resume with the job description and provide the following:
////            1. Match score (0-100) using TF-IDF & cosine similarity.
////            2. Extract resume & job keywords.
////            3. Identify missing/weakly represented skills.
////            4. Personalized feedback to improve the resume.
////            5. Summary of strengths based on resume.
////            6. Format as a structured JSON suitable for a PDF report.
////
////            Resume Text: %s
////            Job Description Text: %s
////
////            Return results in JSON with fields: match_score, resume_keywords, job_keywords, missing_skills, feedback, strengths_summary.
////        """, resumeJobRequest.getResume(), resumeJobRequest.getJobDescription());
////    }
////
////    /**
////     * Extracts the text content from the Gemini API JSON response.
////     *
////     * @param response The raw JSON response from the API.
////     * @return The extracted text or an error message.
////     */
////    private String extractResponseContent(String response) {
////        try {
////            ObjectMapper mapper = new ObjectMapper();
////            JsonNode rootNode = mapper.readTree(response);
////
////            if (rootNode.path("candidates").isArray()
////                    && rootNode.path("candidates").get(0) != null
////                    && rootNode.path("candidates").get(0).path("content").path("parts").isArray()
////                    && rootNode.path("candidates").get(0).path("content").path("parts").get(0) != null) {
////                return rootNode.path("candidates").get(0)
////                        .path("content").path("parts").get(0)
////                        .path("text").asText();
////            } else {
////                return "Error: Unexpected response structure from Gemini API. Raw response: " + response;
////            }
////        } catch (Exception e) {
////            return "Error processing request: " + e.getMessage() + ". Raw response: " + response;
////        }
////    }
////
////    /**
////     * Formats the raw JSON response into a professional report using the Gemini API.
////     *
////     * @param rawJsonResponse The JSON string to be formatted.
////     * @return A Mono emitting the formatted report.
////     */
////    private Mono<String> getFormattedResponse(String rawJsonResponse) {
////        String prompt = "Format this JSON into a professional Resume Analysis Report:\n\n" + rawJsonResponse;
////
////        Map<String, Object> requestBody = Map.of(
////                "contents", List.of(
////                        Map.of("parts", List.of(
////                                Map.of("text", prompt)
////                        ))
////                )
////        );
////a
////        // ✅ FIXED: Use a complete, absolute URI.
////        String fullUri = geminiApiUrl + "?key=" + geminiApiKey;
////
////        return webClient.post()
////                .uri(fullUri)
////                .header("Content-Type", "application/json")
////                .bodyValue(requestBody)
////                .retrieve()
////                .bodyToMono(String.class);
////    }
////}
//
//
//package com.resume.matcher.service;
//
//import com.fasterxml.jackson.databind.JsonNode;
//import com.fasterxml.jackson.databind.ObjectMapper;
//import com.resume.matcher.dto.ResumeJobRequest;
//import com.resume.matcher.util.PdfGenerator;
//import org.apache.tika.Tika;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.http.client.reactive.ReactorClientHttpConnector;
//import org.springframework.stereotype.Service;
//import org.springframework.web.multipart.MultipartFile;
//import org.springframework.web.reactive.function.client.WebClient;
//import reactor.core.publisher.Mono;
//import reactor.netty.http.client.HttpClient;
//
//import java.time.Duration;
//import java.util.List;
//import java.util.Map;
//
//@Service
//public class SmartResumeService {
//
//    private final Tika tika = new Tika();
//    private final WebClient webClient;
//
//    public SmartResumeService(WebClient.Builder webClientBuilder) {
//        this.webClient = webClientBuilder
//                .clientConnector(new ReactorClientHttpConnector(
//                        HttpClient.create().responseTimeout(Duration.ofSeconds(30))))
//                .build();
//    }
//
//    @Value("${gemini.api.url}")
//    private String geminiApiUrl;
//
//    @Value("${gemini.api.key}")
//    private String geminiApiKey;
//
//    /**
//     * Extracts text from a given file efficiently.
//     *
//     * @param file The multipart file to parse.
//     * @return The extracted text as a String.
//     */
//    public String extractText(MultipartFile file) {
//        try {
//            System.out.println("Parsing file: " + file.getOriginalFilename());
//            return tika.parseToString(file.getInputStream());
//        } catch (Exception e) {
//            throw new RuntimeException("Error parsing file", e);
//        }
//    }
//
//    /**
//     * Analyzes resume asynchronously by making a call to the Gemini API.
//     *
//     * @param resumeJobRequest The request containing the resume and job description.
//     * @return A Mono emitting the final formatted analysis report as a String.
//     */
//    public Mono<String> getAnalyseAsync(ResumeJobRequest resumeJobRequest) {
//        String prompt = buildPrompt(resumeJobRequest);
//
//        Map<String, Object> requestBody = Map.of(
//                "contents", List.of(
//                        Map.of("parts", List.of(
//                                Map.of("text", prompt)
//                        ))
//                )
//        );
//
//        // ✅ FIXED: Use a complete, absolute URI. The uriBuilder.path() method is not suitable here.
//        String fullUri = geminiApiUrl + "?key=" + geminiApiKey;
//
//        return webClient.post()
//                .uri(fullUri)
//                .header("Content-Type", "application/json")
//                .bodyValue(requestBody)
//                .retrieve()
//                .bodyToMono(String.class)
//                .map(this::extractResponseContent)
//                .flatMap(this::getFormattedResponse)
//                .map(this::extractResponseContent)
//                .doOnNext(finalFormattedResponse ->
//                        PdfGenerator.generatePDF(finalFormattedResponse, "ResumeAnalysis.pdf"));
//    }
//
//    /**
//     * Builds the prompt string to be sent to the Gemini API.
//     *
//     * @param resumeJobRequest The request object with resume and job description.
//     * @return A formatted prompt string.
//     */
//    private String buildPrompt(ResumeJobRequest resumeJobRequest) {
//        return String.format("""
//            You are an intelligent resume analysis assistant.
//            Compare the candidate's resume with the job description and provide the following:
//            1. Match score (0-100) using TF-IDF & cosine similarity.
//            2. Extract resume & job keywords.
//            3. Identify missing/weakly represented skills.
//            4. Personalized feedback to improve the resume.
//            5. Summary of strengths based on resume.
//            6. Format as a structured JSON suitable for a PDF report.
//
//            Resume Text: %s
//            Job Description Text: %s
//
//            Return results in JSON with fields: match_score, resume_keywords, job_keywords, missing_skills, feedback, strengths_summary.
//        """, resumeJobRequest.getResume(), resumeJobRequest.getJobDescription());
//    }
//
//    /**
//     * Extracts the text content from the Gemini API JSON response.
//     *
//     * @param response The raw JSON response from the API.
//     * @return The extracted text or an error message.
//     */
//    private String extractResponseContent(String response) {
//        try {
//            ObjectMapper mapper = new ObjectMapper();
//            JsonNode rootNode = mapper.readTree(response);
//
//            if (rootNode.path("candidates").isArray()
//                    && rootNode.path("candidates").get(0) != null
//                    && rootNode.path("candidates").get(0).path("content").path("parts").isArray()
//                    && rootNode.path("candidates").get(0).path("content").path("parts").get(0) != null) {
//                return rootNode.path("candidates").get(0)
//                        .path("content").path("parts").get(0)
//                        .path("text").asText();
//            } else {
//                return "Error: Unexpected response structure from Gemini API. Raw response: " + response;
//            }
//        } catch (Exception e) {
//            return "Error processing request: " + e.getMessage() + ". Raw response: " + response;
//        }
//    }
//
//    /**
//     * Formats the raw JSON response into a professional report using the Gemini API.
//     *
//     * @param rawJsonResponse The JSON string to be formatted.
//     * @return A Mono emitting the formatted report.
//     */
//    private Mono<String> getFormattedResponse(String rawJsonResponse) {
//        String prompt = "Format this JSON into a professional Resume Analysis Report:\n\n" + rawJsonResponse;
//
//        Map<String, Object> requestBody = Map.of(
//                "contents", List.of(
//                        Map.of("parts", List.of(
//                                Map.of("text", prompt)
//                        ))
//                )
//        );
//
//        // ✅ FIXED: Use a complete, absolute URI.
//        String fullUri = geminiApiUrl + "?key=" + geminiApiKey;
//
//        return webClient.post()
//                .uri(fullUri)
//                .header("Content-Type", "application/json")
//                .bodyValue(requestBody)
//                .retrieve()
//                .bodyToMono(String.class);
//    }
//}


//package com.resume.matcher.service;
//
//
//import com.fasterxml.jackson.databind.JsonNode;
//import com.fasterxml.jackson.databind.ObjectMapper;
//import com.resume.matcher.dto.ResumeJobRequest;
//import com.resume.matcher.dto.SmartResumeJobRequest;
//import com.resume.matcher.util.PdfGenerator;
//import org.apache.tika.Tika;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.stereotype.Service;
//import org.springframework.web.multipart.MultipartFile;
//import org.springframework.web.reactive.function.client.WebClient;
//
//import java.util.Map;
//
//@Service
//public class SmartResumeService {
//
//    private final Tika tika = new Tika();
//
//    private WebClient webClient;
//
//    public SmartResumeService(WebClient.Builder webClientBuilder)
//    {
//        this.webClient = webClientBuilder.build();
//    }
//
//    @Value("${gemini.api.url}")
//    private String geminiApiUrl;
//
//    @Value("${gemini.api.key}")
//    private String geminiApiKey;
//
//    //Extracting Text from File
//    public String extractText(MultipartFile file) {
//        try {
//
//            System.out.println("Parsing file: " + file.getOriginalFilename());
////           String content = new String(file.getBytes(), StandardCharsets.UTF_8); //not needed
//            String text = tika.parseToString(file.getInputStream());
//            System.out.println("EXTRACTED TEXT"+text);
//
//            return text;
//
//        }
//        catch (Exception e) {
//            e.printStackTrace();
//            throw new RuntimeException("Error occurred while parsing file", e);
//        }
//    }
//
//    //Generate Response
//    public String getAnalyse(ResumeJobRequest resumeJobRequest)
//    {
//        String prompt = buildPrompt(resumeJobRequest);
//        // String prompt = "SAY HELLO";
//
//        //Craft a request
//        Map<String,Object> requestBody = Map.of(
//                "contents",new Object[]{
//                        Map.of("parts",new Object[]
//                                {
//                                        Map.of("text",prompt)
//                                })
//                }
//        );
//
//        //Do request and get response
//        String response = webClient.post().uri(geminiApiUrl+geminiApiKey).header("Content-Type","application/json").bodyValue(requestBody).retrieve().bodyToMono(String.class).block();
//
//        String finalResponse =  extractResponseContent(response);
//
//        String formattedResponse = getFormattedResponse(finalResponse);
//        String finalFormattedResponse = extractResponseContent(formattedResponse);
//        PdfGenerator.generatePDF(finalFormattedResponse, "C:/Users/lenovo/Downloads/resume-matcher (2)/resume-matcher/resume-matcher/src/main/java/com/resume/matcher/Analysis/ResumeAnalysis.pdf");
//        System.out.println("FINAL RESPONSE "+finalResponse);
//        System.out.println("FINAL Formatted RESPONSE "+finalFormattedResponse);
//        return finalResponse;
//    }
//
//    //build prompt
//    private String buildPrompt(ResumeJobRequest resumeJobRequest) {
//        StringBuilder prompt = new StringBuilder();
//
//        prompt.append("You are an intelligent resume analysis assistant.\n");
//        prompt.append("Your task is to compare a candidate's resume with a job description and provide the following:\n");
//        prompt.append("1. A match score (0-100) based on relevance using TF-IDF and cosine similarity.\n");
//        prompt.append("2. A list of keywords extracted from both the resume and the job description.\n");
//        prompt.append("3. A list of missing or weakly represented skills in the resume that are important in the job description.\n");
//        prompt.append("4. Personalized feedback on how the resume can be improved to better match the job.\n");
//        prompt.append("5. A short summary of the candidate's strengths based on the resume.\n");
//        prompt.append("6. Format the output in a structured way suitable for generating a PDF report.\n\n");
//
//        prompt.append("Resume Text:\n");
//        prompt.append(resumeJobRequest.getResume()).append("\n\n");
//
//        prompt.append("Job Description Text:\n");
//        prompt.append(resumeJobRequest.getJobDescription()).append("\n\n");
//
//        prompt.append("Now perform the analysis and return the results in a structured JSON format with fields: match_score, resume_keywords, job_keywords, missing_skills, feedback, strengths_summary.");
//
//        return prompt.toString();
//    }
//
//    //Extracting response
//    public String extractResponseContent(String response)
//    {
//        try
//        {
//            ObjectMapper mapper = new ObjectMapper();
//            JsonNode rootNode = mapper.readTree(response);
//            return rootNode.path("candidates").get(0).path("content").path("parts").get(0).path("text").asText();
//
//
//        }catch(Exception e)
//        {
//            return "Error processing request: "+e.getMessage();
//        }
//    }
//
//    public String getFormattedResponse(String rawJsonResponse) {
//        String prompt = "Format this JSON into a professional,and With heading Resume Analysis Report and not put date and remember  not use ** for making words bold and only in 1 page and in human-readable resume analysis report:\n\n" + rawJsonResponse;
//
//        Map<String, Object> requestBody = Map.of(
//                "contents", new Object[] {
//                        Map.of("parts", new Object[] {
//                                Map.of("text", prompt)
//                        })
//                }
//        );
//
//        return webClient.post()
//                .uri(geminiApiUrl + geminiApiKey)
//                .header("Content-Type", "application/json")
//                .bodyValue(requestBody)
//                .retrieve()
//                .bodyToMono(String.class)
//                .block();
//    }
//
//
//
//
//
//
//}
