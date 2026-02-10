# ClassLoop

A school assignment aggregation system that collects class schedules, assignments, quizzes, and exams into a single timeline view for students.

## Features

- **User Management**: Student, Teacher, and Admin roles
- **JWT Authentication**: Secure token-based authentication
- **Class Management**: Teachers can create and manage classes
- **Assignment Tracking**: Create assignments with due dates and workload estimates
- **Timeline View**: Visual timeline with conflict detection
  - LOW conflict: < 60 minutes of work
  - MEDIUM conflict: 60-120 minutes of work  
  - HIGH conflict: > 120 minutes of work
- **Admin Panel**: Complete user and enrollment management
- **Responsive UI**: Built with Next.js, TailwindCSS, and Framer Motion

## Technology Stack

### Backend
- Java 17+
- Spring Boot 3.3.0
- Spring Security (JWT-based authentication)
- Spring Data JPA with Hibernate
- H2 Database (in-memory for development)

### Frontend
- Next.js 15.2.9
- React 19
- TypeScript
- TailwindCSS
- Framer Motion (animations)

### Build Tools
- Gradle 8.10.2 with Kotlin DSL
- Node.js (frontend build)

## Quick Start

### Prerequisites
- Java 17 or higher
- Node.js 18+ (for development)

### Building the Application

Build everything (frontend + backend) into a single executable JAR:

```bash
./gradlew buildJar
```

This will:
1. Install frontend dependencies
2. Build the frontend as a static export
3. Copy frontend files to Spring Boot resources
4. Build the complete JAR with embedded frontend

### Running the Application

```bash
java -jar build/libs/classloop-1.0.0.jar
```

The application will start on `http://localhost:8080`

### Development Mode

#### Backend Only
```bash
./gradlew bootRun
```

#### Frontend Only
```bash
cd frontend
npm install
npm run dev
```

Frontend will run on `http://localhost:3000` and proxy API requests to the backend.

## API Endpoints

### Authentication
- `POST /api/auth/register` - Register a new user
- `POST /api/auth/login` - Login and get JWT token

### Classes
- `GET /api/classes` - Get all classes for authenticated user
- `POST /api/classes` - Create a new class (Teacher only)

### Assignments
- `GET /api/assignments` - Get assignments for enrolled classes
- `POST /api/assignments` - Create assignment (Teacher only)

### Timeline
- `GET /api/timeline` - Get timeline with conflict analysis

### Admin (Admin role only)
- `GET /api/admin/users` - List all users
- `POST /api/admin/users` - Create user
- `PUT /api/admin/users/{id}` - Update user
- `DELETE /api/admin/users/{id}` - Delete user
- `GET /api/admin/enrollments` - List all enrollments
- `POST /api/admin/enrollments` - Create enrollment
- `DELETE /api/admin/enrollments/{id}` - Delete enrollment

## Project Structure

```
.
├── src/main/java/com/classloop/    # Backend Java source
│   ├── ClassLoopApplication.java
│   ├── config/                     # Security & Web config
│   ├── auth/                       # JWT authentication
│   ├── user/                       # User management
│   ├── classroom/                  # Class management
│   ├── assignment/                 # Assignment management
│   ├── enrollment/                 # Student-class enrollment
│   ├── timeline/                   # Timeline with conflict detection
│   └── util/                       # Utility classes
├── src/main/resources/
│   ├── application.properties      # Spring Boot configuration
│   └── static/                     # Frontend build (generated)
├── frontend/                       # Next.js frontend
│   ├── app/                        # Next.js app router pages
│   ├── components/                 # React components
│   ├── lib/                        # API client
│   └── package.json
├── build.gradle.kts                # Gradle build configuration
└── settings.gradle.kts
```

## Default Configuration

- **Port**: 8080
- **Database**: H2 in-memory (data persists only during runtime)
- **H2 Console**: http://localhost:8080/h2-console
  - JDBC URL: `jdbc:h2:mem:classloop`
  - Username: `sa`
  - Password: (empty)

## Security

- JWT tokens expire after 24 hours
- Passwords are hashed with BCrypt
- Role-based access control (STUDENT, TEACHER, ADMIN)
- CSRF protection disabled for stateless JWT authentication

## License

This project was implemented based on the AGENT.md specification.