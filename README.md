# ResumeRanker

**AI-powered Resume Ranking System**  
Spring Boot + Docker + Jenkins CI/CD + JWT secured APIs.

---

## Table of Contents
- [Features](#features)
- [Architecture](#architecture)
- [Technology Stack](#technology-stack)
- [Prerequisites](#prerequisites)
- [Installation & Running](#installation--running)
- [Endpoints](#endpoints)
- [CI/CD Pipeline](#cicd-pipeline)
- [Testing](#testing)
- [Notes / Tips](#notes--tips)

---

## Features
- AI-powered resume ranking using Gemini API
- JWT authentication for secure endpoints
- Dockerized for local & production deployment
- CI/CD via Jenkins for automated builds & deployments

---

## Architecture
- **Backend:** Spring Boot 3, layered architecture  
  - `controller` → REST endpoints  
  - `service` → business logic  
  - `repository` → DB/Redis interaction  
  - `security` → JWT, filters, authentication  
- **Database / Cache:** Redis for session & temporary data  
- **CI/CD:** Jenkins pipeline builds, pushes, deploys Docker images  
- **Containerization:** Docker, docker-compose for orchestration  
- **External API:** Gemini API for AI content ranking  

**Architecture Diagram:**  
![architecture](docs/sequence-diagrams/architecture.png)  *(add image if you have)*

---

## Technology Stack
- Java 17+, Spring Boot 3
- Maven build
- Redis
- JWT Authentication
- Docker & Docker Compose
- Jenkins for CI/CD
- Gemini API (Google Generative Language)

---

## Prerequisites
- Docker & Docker Compose
- Java 17+
- Maven / mvnw
- Jenkins (optional for CI/CD)
- Git

---

## Installation & Running

1. **Clone Repo**  
```bash
git clone https://github.com/YourUsername/ResumeRanker.git
cd ResumeRanker/resume-matcher
