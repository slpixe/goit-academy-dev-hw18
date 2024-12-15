# Note Manager

A Java Spring MVC application designed to help with **notes management**. This project provides a secure and user-friendly functionalities for creating, reading, updating, and deleting notes.
The purpose of this application is to provide a backend service for managing notes, which handles user authentication, session management, account lockout mechanisms, and input validation.
This project is an enhanced version of the [Note Manager](https://github.com/ruslanaprus/goit-academy-dev-hw16), and it has an improved architecture for better maintainability and scalability.

---

## Technologies Used

- **Java 21**: Core programming language for the application.
- **Spring Boot 3.4.0**: Simplifies application development with embedded server and configuration support.
- **Spring Data JPA**: Provides powerful instruments for database access.
- **Spring Security**: For user authentication and authorization.
- **PostgreSQL**: Database for storing note data.
- **Flyway**: Manages database schema migrations and populates initial data.
- **Jakarta Bean Validation**: Ensures data integrity with annotations `@NotNull` and `@NotEmpty`.
- **Lombok**: Reduces boilerplate code with annotations like `@Builder`, `@Getter`, and `@RequiredArgsConstructor`.
- **JUnit 5 & Mockito**: Provides a robust framework for writing unit and integration tests.
- **Thymeleaf**: Template engine for rendering dynamic HTML pages with embedded backend data.

---

## Main Features

### 1. Database Configuration
**Database-Backed Note Management**: Notes are stored in a database managed by Flyway migrations.

### 2. Security
**User Authentication and Authorization**:
- Login and logout functionalities.
- **Separation of Notes Between Users**: Each user's notes are isolated and private.
- **User Registration**: New users can sign up via a registration page.
- **Session Management**:
  - Users can log out, and their session will be invalidated.
  - Only one active session per user is allowed.
- **Account Lockout Mechanism**:
  - Tracks failed login attempts and locks account after 3 unsuccessful login attempts.
  - Unlocks accounts automatically after the lockout duration expires.

### 3. Business Logic
- **CRUD Operations**:
  - **List All Notes**: Retrieve all existing notes with pagination support.
  - **Get Note by ID**: Fetch a specific note using its unique ID.
  - **Create Note**: Add a new note with an auto-generated ID.
  - **Update Note**: Modify an existing note.
  - **Delete Note**: Remove a note by ID.
- **Search Notes**: Allows users to search for notes containing a specific keyword. This feature scans note titles and content for matches.
- **User Input Validation**: Validates note data such as title and content using Jakarta Bean Validation.

### 4. Error Handling
- **Global Exception Handling**: Provides error messages via a global exception handler.

### 5. Web Layer
- **Endpoints**: 
  - Manages HTTP requests for the note management functionality.
  - Supports endpoints for listing, creating, updating, and deleting notes.
- **Pagination**: Allows efficient display of notes, in case of large datasets.

### 6. View Layer
- **Thymeleaf integration**:
  - **Sign Up Page**: Allows new users to register.
  - **Login Page**: Provides user authentication.
  - **List All Notes**: Displays all notes.
  - **Create a Note**: Form to create a new note.
  - **Edit Note**: Form to update a note.
  - **Error page**: Displays user-friendly error messages for invalid inputs or missing resources.
  - **Dynamic and Reusable Code with Thymeleaf**: Thymeleaf fragments are used for repeatable elements like headers and navigation menus.
  - **User's Name Display on Navbar**: The logged-in user's name is displayed on the navigation bar.

---

## Getting Started

### Prerequisites

- **Java 21**: Ensure Java 21 is installed on your system.
- **Gradle**: This project uses Gradle for dependency management and build tasks.

### Installation

1. Clone the repository:
```shell
git clone git@github.com:ruslanaprus/goit-academy-dev-hw18.git
cd goit-academy-dev-hw18
```
2. Database Configuration: Copy the `.env.example` file into `.env`, and populate it with your DB details (keys: [GOIT_DB2_URL, GOIT_DB_USER, GOIT_DB_PASS]). This file will be used to set DB properties for Flyway plugin in `build.gradle` and for your application.


3. Run Flyway Migration: To apply database migrations, run:
```shell
gradle flywayMigrate
```
4. Build the project:
```shell
./gradlew clean build
```
5. Run the application:
```shell
./gradlew bootRun
```
6. Visit the website at http://localhost:8080/signup or http://localhost:8080/login

---

## Project Structure

- **`com.example.notemanager`**: Main entry point for the application.
- **`model`**: Defines the `Note` and `User` classes, the core entities of the application.
- **`repository`**: Defines repository operations, allows easy integration with various data sources.
- **`service`**: Contains `NoteService` for managing CRUD operations and `UserService` for managing account.
- **`controller`**: Handles web requests for note operations.
- **`config`**: Configures access control, implements session management, and customises login and logout behaviour.
- **`exception`**: Includes custom exceptions, the `ExceptionMessages` enum, and a global exception handler.

---

## Future Enhancements

- **REST API**: Add controllers to expose note management functionalities via HTTP. Expand endpoints to support pagination, search, and filtering.
- **Scalability Improvements**: Optimise the application for large-scale deployment.
- **Implement Sharing Notes with other users**