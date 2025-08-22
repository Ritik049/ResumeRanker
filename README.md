# Resume Ranker / Smart Resume Analyzer

## Project Overview

**Resume Ranker** is a **Java Spring Boot application** that analyzes and ranks resumes against job descriptions.  
It offers both **Normal Resume Analysis** and **Smart AI-Powered Resume Analysis**.  

Key features:  
- Match score computation using **cosine similarity**  
- Keyword extraction from resumes and job descriptions  
- Missing skill identification  
- AI-powered feedback and strengths summary  
- JWT-based authentication with **Redis token revocation**  
- CI/CD integration with **Jenkins** and Docker  

---

## Table of Contents

1. [Technologies Used](#technologies-used)  
2. [Project Structure](#project-structure)  
3. [Architecture](#architecture)  
4. [Getting Started](#getting-started)  
5. [Important APIs](#important-apis)  
6. [Postman Links](#postman-links)  
7. [License](#license)  

---

## Technologies Used

| Layer | Technology / Library | Purpose |
|-------|-------------------|---------|
| Backend | Spring Boot | REST API framework |
| Security | Spring Security, JWT | Authentication & Authorization |
| Caching | Redis | JWT token revocation and session management |
| NLP | Stanford CoreNLP, Apache Tika | Resume text extraction, tokenization |
| Similarity | Cosine Similarity | Resume vs Job scoring |
| AI / ML | Custom AI prompts / Smart Analysis | Resume evaluation and feedback generation |
| Build & CI/CD | Maven, Jenkins, Docker | Build automation, containerization, CI/CD |
| Database | H2 | User & resume data storage |
| Frontend | React | Resume upload interface and results |

---

## Project Structure
!(ResumeArchitecture.png)
