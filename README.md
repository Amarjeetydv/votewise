# VoteWise - Online Voting System

A Java-based desktop application for conducting secure online elections with an admin portal and voter interface.

## Project Overview

VoteWise implements a three-layer architecture:
- **GUI Layer**: Swing-based user interface with LoginForm, AdminDashboard, and VotingForm
- **Database Layer**: DAO (Data Access Object) pattern for database operations
- **Logic Layer**: Service classes and model objects for business logic

## Features

- **Admin Portal**: Add/delete candidates, view real-time voting results and registered voters
- **Voter Portal**: Cast votes with candidate + election symbol selection (no party concept)
- **LPU-only Registration**: Accepts only `@lpu.in` email IDs
- **OTP Verification**: Registration requires OTP verification by real email delivery over SMTP
- **Extended Profile**: Voter registration captures name, email, course, and section
- **Security**: Transaction-based voting to prevent duplicate votes and ensure data integrity
- **Password Security**: PBKDF2 hashed password storage for voters and admins (with automatic migration from old plain-text records at successful login)
- **Database**: MySQL backend with proper schema and constraints

## Architecture

```
GUI Layer (Swing)
    ↓
Service Layer (AdminService, VoterService)
    ↓
DAO Layer (CandidateDAO, VoteDAO, VoterDAO, AdminDAO)
    ↓
Database (MySQL - VoteWise)
```

## Prerequisites

- Java 8 or higher
- MySQL 5.7 or higher
- MySQL Connector/J 9.6.0 (included in `lib/`)

## Setup Instructions

### 1. Database Setup
```sql
-- Create database and tables
mysql -u root -p < tables.sql

-- Verify installation
USE VoteWise;
SELECT * FROM admin;
SELECT * FROM candidates;
```

### 2. Configure Database Connection
Edit `db/DBConnection.java`:
```java
private static final String URL = "jdbc:mysql://localhost:3306/VoteWise";
private static final String USER = "root";
private static final String PASSWORD = "your_password";
```

### 3. Configure SMTP for OTP Email
Set environment variables before running the app:

```powershell
$env:SMTP_HOST = "smtp.gmail.com"
$env:SMTP_PORT = "587"
$env:SMTP_USER = "your_email@gmail.com"
$env:SMTP_PASS = "your_app_password"
$env:SMTP_FROM = "your_email@gmail.com"
```

Notes:
- For Gmail, use an App Password (not your normal account password).
- OTP emails are sent only to valid `@lpu.in` addresses.

Alternative (recommended):
- Login as admin and open `SMTP Settings` from the dashboard.
- Use `Test SMTP` to verify credentials by sending a test mail before saving.
- Save host/port/user/password/from once; app stores it in `config/smtp.properties`.
- After this, OTP mail works without setting env variables each run.

### 4. Compile Project
```powershell
javac -d out -cp "lib/*" db/*.java gui/*.java logic/*.java
```

### 5. Run Application
```powershell
java -cp "out;lib/*" gui.LoginForm
```

## Default Credentials

**Admin Login:**
- Username: `admin`
- Password: `admin123`

**Voter Registration:**
- Open RegisterForm and create a new voter account with LPU email, course, and section
- Verify registration through OTP (shown in app dialog in demo mode)

## File Structure

```
VoteWise/
├── db/              # Database access layer
│   ├── DBConnection.java
│   ├── AdminDAO.java
│   ├── CandidateDAO.java
│   ├── VoteDAO.java
│   └── VoterDAO.java
├── gui/             # User interface layer
│   ├── LoginForm.java
│   ├── AdminDashboard.java
│   ├── VotingForm.java
│   └── RegisterForm.java
├── logic/           # Business logic layer
│   ├── Candidate.java
│   ├── AdminService.java
│   └── VoterService.java
├── out/             # Compiled classes
├── lib/             # External libraries (MySQL connector)
├── tables.sql       # Database schema
└── README.md        # This file
```

## Key Features Explained

### Transaction Safety (VoteDAO)
- Uses database transactions to ensure vote and voter status updates are atomic
- Prevents duplicate voting by updating `hasVoted` flag in same transaction
- Rolls back on failure

### DAO Pattern
- Separates database logic from business logic
- Makes code maintainable and testable
- PreparedStatement prevents SQL injection

### Multi-Tab Admin Dashboard
- View candidates and voting results
- View registered voters
- Add new candidates
- Delete candidates
- Real-time winner detection

## Testing

Run the application and follow this test scenario:
1. **Admin Login**: Log in with admin/admin123
2. **Add Candidate**: Add a new candidate via dialog
3. **Logout**: Return to login screen
4. **Voter Registration**: Register a new voter account
5. **Vote**: Log in as voter and cast a vote
6. **Check Results**: Return to admin dashboard and refresh to see updated vote count

## Technologies Used

- **Language**: Java 8
- **UI Framework**: Swing
- **Database**: MySQL
- **Pattern**: MVC, DAO Pattern
- **Build Tool**: Java Compiler (javac)

## Author

Amarjeet Yadav (MCA Student) - Java Practical Assignment

## License

Educational Project
