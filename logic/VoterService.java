package logic;

// This file should now be located in the 'logic' folder

import db.VoterDAO;

public class VoterService {
    private VoterDAO voterDAO = new VoterDAO();

    private boolean isValidEmail(String email) {
        return email != null && email.matches("^[A-Za-z0-9+_.-]+@(.+)$");
    }

    public int authenticate(String email, String password) {
        if (!isValidEmail(email) || password == null || password.isEmpty()) {
            return -1;
        }
        return voterDAO.validateVoter(email.trim(), password);
    }

    public boolean register(String name, String email, String password) {
        if (name == null || name.trim().isEmpty() || !isValidEmail(email) || password == null || password.length() < 6) {
            // Enforce a minimum password length of 6 for basic security
            return false;
        }
        return voterDAO.registerVoter(name.trim(), email.trim(), password);
    }
}
