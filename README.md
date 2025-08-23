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
3. [Getting Started](#getting-started)  
4. [Important APIs](#important-apis)  
5. [Postman Links](#postman-links)  
6. [License](#license)  

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
Below is the project structure and flow of **ResumeRanker**:
![ResumeRanker Architecture](ResumeRankerArchitecture.png)

---

## Getting Started

Follow these steps to run the project locally.

### Prerequisites

- **Java 17+**
- **Maven**
- **Docker**
- **Redis**
- **Postman** (optional)

### Clone the Repository

git clone https://github.com/Ritik049/ResumeRanker.git
cd ResumeRanker




### Build the project 
cd resume-matcher
./mvnw clean package -DskipTests

### Run With Docker
docker build -t resume-ranker:latest ./resume-matcher \n
docker run -d --name resume-redis -p 6379:6379 redis  \n
docker run -d --name resume-ranker -p 8080:8080 resume-ranker:latest \n

### Run locally without  docker
cd resume-matcher
./mvnw spring-boot:run


---



## Important APIs

| Endpoint                     | Method | Description                       | Request Body / Headers                                               | Response                                            |
| ---------------------------- | ------ | --------------------------------- | -------------------------------------------------------------------- | --------------------------------------------------- |
| `/auth/register`             | POST   | Register a new user               | JSON `{ "username": "user", "password": "pass" }`                    | 200 OK, User registered                             |
| `/auth/login`                | POST   | User login, returns JWT token     | JSON `{ "username": "user", "password": "pass" }`                    | 200 OK, JWT token                                   |
| `/auth/logout`               | POST   | Logout user, invalidate JWT token | Bearer JWT in `Authorization` header                                 | 200 OK, Logged out                                  |
| `/auth/role/create`          | POST   | Create a new role (Admin only)    | JSON `{ "roleName": "ROLE_USER" }` + Bearer JWT                      | 200 OK, Role created                                |
| `/auth/admin/revoke-token`   | POST   | Revoke JWT token using Redis      | JSON `{ "token": "<jwt>" }` + Admin Bearer JWT                       | 200 OK, Token revoked                               |
| `/user/create`               | POST   | Create a new user (Admin only)    | JSON `{ "username": "user", "password": "pass" }` + Admin Bearer JWT | 200 OK, User created                                |
| `/api/resumes/analyze`       | POST   | Normal resume analysis            | JSON `{ "resume": "...", "jobDescription": "..." }` + Bearer JWT     | 200 OK, Match score, keywords, missing skills       |
| `/smart/api/resumes/analyze` | POST   | Smart AI-powered resume analysis  | JSON `{ "resume": "...", "jobDescription": "..." }` + Bearer JWT     | 200 OK, Match score, AI feedback, strengths summary |
| `/jenkins/check`             | GET    | CI/CD test endpoint               | Bearer JWT                                                           | 200 OK, "Jenkins pushed this fine and CI/CD worked" |

## Postman Links 

* Analyse APIs (Resume Analysis): [Analyse Collection](https://documenter.getpostman.com/view/36779828/2sB2x3nYvK#dcd935c6-d168-47e3-806d-62d125c528b2)

* Auth & User Management APIs: [Auth collection](https://documenter.getpostman.com/view/36779828/2sB3BLjSvq)

---

## 🎥 Project Demo Videos

### 🔹 ResumeRanker Demo
[![ResumeRanker Demo](https://img.youtube.com/vi/w0EpHqR-_9w/0.jpg)](https://youtu.be/w0EpHqR-_9w?si=p2AdxpOC46HMZkOc)

### 🔹 Jenkins CI/CD Integration Demo
[![Jenkins CI/CD Demo](https://img.youtube.com/vi/6EG2ySgDl2k/0.jpg)](https://youtu.be/6EG2ySgDl2k)

---

## License 
This project is licensed under the MIT License.
