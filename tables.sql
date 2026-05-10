CREATE TABLE voters (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    email VARCHAR(100) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    hasVoted BOOLEAN DEFAULT FALSE
);

CREATE TABLE candidates (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    party VARCHAR(100) NOT NULL,
    photo VARCHAR(255)
);

CREATE TABLE votes (
    id INT AUTO_INCREMENT PRIMARY KEY,
    voter_id INT NOT NULL,
    candidate_id INT NOT NULL,
    timestamp DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (voter_id) REFERENCES voters(id),
    FOREIGN KEY (candidate_id) REFERENCES candidates(id)
);

CREATE TABLE admin (
    id INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(100) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL
);

-- Ensure you are using the correct database
USE VoteWise;

-- Insert an initial admin user for the Admin Portal
INSERT INTO admin (username, password) 
VALUES ('admin', 'admin123');

-- Insert initial candidates for the election
INSERT INTO candidates (name, party) VALUES ('Alice Smith', 'Alpha Party');
INSERT INTO candidates (name, party) VALUES ('Bob Jones', 'Beta Party');
INSERT INTO candidates (name, party) VALUES ('Charlie Brown', 'Gamma Party');

-- Verify the data was inserted correctly
SELECT * FROM admin;
SELECT * FROM candidates;

--ctrl+Shift+B for compile and run or run java -cp "out;lib\mysql-connector-j-9.6.0.jar" gui.LoginForm
