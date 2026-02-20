# 📝 Quiz App

A team-based backend project built with **Java & Spring Boot**.

Quiz App is a secure and scalable examination management system that supports multiple roles (Admin, Teacher, Student) with JWT-based authentication and automated grading powered by AI.

---

## 👥 Team Project

This project was developed as a collaborative team effort, focusing on clean architecture, role-based access control, and scalable backend design.

---

## 🔐 Authentication & Authorization

- User Registration & Login
- JWT (JSON Web Token) Authentication
- Role-Based Access Control
- Stateless Security Architecture

### 👤 Supported Roles
- **Admin**
- **Teacher**
- **Student**

Each role has different permissions and system access levels.

---

## 👑 Admin Features

The Admin has full system control through a dedicated dashboard:

- Manage all users (Teachers & Students)
- Assign students to teachers
- Monitor system activities
- Control and manage overall platform workflow

---

## 👨‍🏫 Teacher Features

Teachers can:

- Create quizzes
- Add questions (MCQ & Written)
- Publish quizzes
- Set quiz start time

### ⏳ Timed Quiz Access
- Quizzes are only accessible at the scheduled start time
- Students cannot access quizzes before publishing
- Controlled availability ensures exam integrity

---

## 👨‍🎓 Student Features

Students can:

- Take quizzes assigned to them
- Submit answers
- View results instantly after submission

---

## 🤖 AI-Powered Automatic Grading

One of the key features of this system is:

- Automatic grading for:
  - Multiple Choice Questions (MCQ)
  - Written Questions

For written answers, grading is handled using **Ollama AI**, allowing intelligent evaluation instead of simple keyword matching.

This provides:
- Fair grading
- Smart answer analysis
- Reduced manual correction effort

---

## ⚡ Instant Results

- Grades are calculated automatically
- Students can see their results immediately after submission
- Real-time performance evaluation

---

## 🏗️ System Architecture

- Layered Architecture (Controller - Service - Repository)
- RESTful APIs
- JWT Security Filter
- Role-Based Authorization
- AI Integration (Ollama) for written answer correction

---

## 🛠️ Tech Stack

- Java
- Spring Boot
- Spring Security
- JWT
- JPA / Hibernate
- REST APIs
- Ollama AI (for written answer grading)

---

## 🎯 Project Goals

This project demonstrates:

- Secure authentication with JWT
- Role-based system design
- AI-powered grading system
- Timed quiz publishing
- Scalable backend architecture
- Real-time automatic result calculation

---

⭐ Designed to simulate a real-world online examination platform with intelligent grading capabilities.
