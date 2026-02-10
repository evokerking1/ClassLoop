# ClassLoop – Full Implementation Specification

## Purpose of This Document

This document is written for **AI agents and human developers** to fully implement ClassLoop end-to-end without ambiguity. It defines architecture, responsibilities, data models, APIs, build steps, and deployment details.

ClassLoop is a **school assignment aggregation system** that collects class schedules, assignments, quizzes, and exams into a single timeline view for students.

---

## High-Level Architecture

```
Browser (Student / Teacher)
        ↓
Next.js Frontend (Static Export)
        ↓ (embedded)
Spring Boot Backend (Java)
        ↓
Relational Database (PostgreSQL / H2 for dev)
```

The **Next.js frontend is statically built and bundled into the Spring Boot JAR** and served via Spring's static resource handling.

---

## Technology Stack

### Backend

* Java 17+
* Spring Boot 3.x
* Spring Web
* Spring Security (JWT-based auth)
* Spring Data JPA
* Hibernate
* PostgreSQL (prod) / H2 (dev)

### Frontend

* Next.js 14+
* React 18
* TypeScript
* TailwindCSS
* Static export (`next build && next export`)

### Build Tools

* Gradle (preferred) or Maven
* Node.js (frontend build only)

---

## Core Domain Model

### User

Represents a student or teacher.

Fields:

* id (UUID)
* email
* passwordHash
* role (STUDENT | TEACHER | ADMIN)
* createdAt

---

### Class

Represents a school class.

Fields:

* id (UUID)
* name
* subject
* teacherId
* period

---

### Enrollment

Maps students to classes.

Fields:

* id (UUID)
* userId
* classId

---

### Assignment

Represents homework, quizzes, tests, or projects.

Fields:

* id (UUID)
* classId
* title
* description
* dueDate (ZonedDateTime)
* type (HOMEWORK | QUIZ | TEST | PROJECT)
* estimatedWorkloadMinutes

---

## Backend Implementation (Spring Boot)

### Package Structure

```
com.classloop
├── ClassLoopApplication.java
├── config
│   ├── SecurityConfig.java
│   └── WebConfig.java
├── auth
│   ├── AuthController.java
│   ├── JwtService.java
│   └── AuthFilter.java
├── user
├── classroom
├── assignment
├── enrollment
├── timeline
└── util
```

---

## REST API Specification

### Authentication

#### POST /api/auth/register

Registers a new user.

Request:

```json
{
  "email": "student@school.edu",
  "password": "password"
}
```

#### POST /api/auth/login

Returns JWT token.

---

### Classes

#### GET /api/classes

Returns all classes for the authenticated user.

#### POST /api/classes (TEACHER only)

Creates a class.

---

### Assignments

#### GET /api/assignments

Returns assignments for all enrolled classes.

#### POST /api/assignments

Creates assignment.

---

### Timeline (Core Feature)

#### GET /api/timeline

Returns assignments sorted by due date with overlap metadata.

Response:

```json
[
  {
    "assignmentId": "uuid",
    "className": "Math",
    "title": "Chapter 5 Homework",
    "dueDate": "2026-03-01T23:59",
    "workload": 45,
    "conflictLevel": "HIGH"
  }
]
```

ConflictLevel is computed based on multiple assignments due within a configurable window.

---

## Timeline Conflict Algorithm

1. Fetch all assignments for user
2. Group by due date (±24 hours)
3. Sum estimatedWorkloadMinutes
4. Assign conflict level:

   * LOW < 60 min
   * MEDIUM 60–120 min
   * HIGH > 120 min

---

## Frontend Implementation (Next.js)

### Directory Structure

```
frontend/
├── app/
│   ├── page.tsx
│   ├── login/
│   ├── dashboard/
│   └── timeline/
├── components/
├── lib/api.ts
├── styles/
└── tailwind.config.ts
```

---

## Frontend Pages

### Login Page

* Email/password form
* Stores JWT in HttpOnly cookie or memory

### Dashboard

* Summary cards
* Upcoming deadlines

### Timeline View

* Vertical or calendar-style layout
* Color-coded conflict levels
* Filters by class or assignment type

---

## Frontend → Backend Communication

* All API calls via `/api/**`
* JWT attached via Authorization header
* Centralized API wrapper (`lib/api.ts`)

---

## Static Export & JAR Embedding

### Next.js Build

```bash
npm run build
npm run export
```

Output directory:

```
frontend/out/
```

---

### Spring Boot Static Serving

Copy `frontend/out` to:

```
src/main/resources/static/
```

Spring will automatically serve:

* `/` → index.html
* `/timeline` → Next.js route

---

## WebConfig

Ensure React routing works:

* Forward all non-API routes to `index.html`

---

## Security Model

* JWT-based stateless auth
* Role-based access control
* Teachers can only manage their classes
* Students can only view enrolled data

---

## Database Schema

* users
* classes
* enrollments
* assignments

Foreign keys enforced.

---

## Dev vs Production

### Dev

* H2 database
* Local Node.js build

### Production

* PostgreSQL
* Frontend pre-built
* Single executable JAR

---

## Testing Strategy

* Backend: JUnit + MockMvc
* Frontend: Playwright or Cypress
* API contract tests

---

## Future Extensions

* Teacher analytics dashboard
* Calendar sync (ICS)
* Push notifications
* Mobile wrapper (PWA)

---

## End of Specification

This document is authoritative. Any AI agent should be able to implement ClassLoop using this specification without further clarification.
