# 🏋️‍♂️ QuickFitAI - AI Fitness Tracker

QuickFitAI is an **AI-powered fitness tracker application** built with **Java Spring Boot (Microservices)**, **React**, **MongoDB**, **PostgreSQL**, and **Keycloak Authentication**.  
It leverages **RabbitMQ** for asynchronous communication and integrates with **Gemini LLM** to generate personalized fitness activity recommendations.

---
## 🏗️ System Architecture

Below is the architecture diagram of QuickFitAI:  

![Architecture]![https://github.com/YashRaj0307](<Screenshot 2026-03-25 213236.png>)

---

## 🚀 Features

- 🔑 **Secure Authentication**
  - Keycloak with **PKCE flow** for OAuth 2.0
  - Integrated **Google OAuth2 login**

- ⚡ **Microservice Architecture**
  - User Services (**PostgreSQL** backend)
  - Activity Services (**MongoDB** backend)
  - AI Services (**MongoDB** + Gemini LLM integration)

- 📡 **Asynchronous Processing**
  - **RabbitMQ** for handling async requests to the LLM model

- 🤖 **AI-Powered Recommendations**
  - Uses **Gemini LLM** to provide personalized fitness activity suggestions

- 🐳 **Containerized Services**
  - **Dockerized Keycloak** for authentication
  - **Dockerized RabbitMQ** for messaging

---


## 🛠️ Tech Stack

- **Frontend**: React.js  
- **Backend**: Spring Boot (Microservices)  
- **Databases**: MongoDB, PostgreSQL  
- **Authentication**: Keycloak (PKCE + Google OAuth2)  
- **Message Queue**: RabbitMQ  
- **AI Integration**: Gemini LLM  
- **Service Discovery & Config**: Eureka, Config Server  
- **Containerization**: Docker  

---

## ⚙️ Setup & Installation

### 1. Clone the Repository
```bash
 git clone https://github.com/YashRaj0307
cd QuickFitAI
