CREATE TABLE voters (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    email VARCHAR(100) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    course VARCHAR(100) NOT NULL,
    section VARCHAR(20) NOT NULL,
    hasVoted BOOLEAN DEFAULT FALSE,
    registered_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    last_login DATETIME
);

CREATE TABLE candidates (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    symbol VARCHAR(100) NOT NULL,
    photo VARCHAR(255)
);

CREATE TABLE votes (
    id INT AUTO_INCREMENT PRIMARY KEY,
    voter_id INT NOT NULL,
    candidate_id INT NOT NULL,
    voted_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (voter_id) REFERENCES voters(id),
    FOREIGN KEY (candidate_id) REFERENCES candidates(id)
);

CREATE TABLE admin (
    id INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(100) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    last_login DATETIME
);

CREATE TABLE election (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    post VARCHAR(100) NOT NULL,
    description TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Ensure you are using the correct database
USE VoteWise;

-- Insert an initial admin user for the Admin Portal
INSERT INTO admin (username, password) 
VALUES ('admin', 'admin123');

-- Insert initial election
INSERT INTO election (name, post, description) 
VALUES ('LPU Student Council Elections 2026', 'General Secretary', 'Vote for General Secretary for LPU Student Council');

-- Insert initial candidates for the election (symbol-based, no party)
INSERT INTO candidates (name, symbol) VALUES ('Alice Smith', 'Book');
INSERT INTO candidates (name, symbol) VALUES ('Bob Jones', 'Pen');
INSERT INTO candidates (name, symbol) VALUES ('Charlie Brown', 'Lamp');

-- Verify the data was inserted correctly
SELECT * FROM admin;
SELECT * FROM candidates;

--ctrl+Shift+B for compile and run or run java -cp "out;lib\mysql-connector-j-9.6.0.jar" gui.LoginForm
