package logic;

// This file should now be located in the 'logic' folder

import db.VoterDAO;

import java.security.SecureRandom;

public class VoterService {
    private VoterDAO voterDAO = new VoterDAO();
    private OtpEmailService otpEmailService = new OtpEmailService();
    private String pendingOtpEmail;
    private String pendingOtpCode;
    private long pendingOtpExpiryMillis;
    private final SecureRandom random = new SecureRandom();
    private String otpDeliveryError = "";

    private static final long OTP_VALIDITY_MILLIS = 5 * 60 * 1000;

    private boolean isValidEmail(String email) {
        return email != null && email.trim().toLowerCase().matches("^[a-z0-9._%+-]+@lpu\\.in$");
    }

    public boolean isValidLpuEmail(String email) {
        return isValidEmail(email);
    }

    public boolean sendRegistrationOtp(String email) {
        if (!isValidEmail(email)) {
            otpDeliveryError = "Invalid LPU email format.";
            return false;
        }

        int otp = 100000 + random.nextInt(900000);
        String generatedOtp = String.valueOf(otp);
        String normalizedEmail = email.trim().toLowerCase();

        if (!otpEmailService.sendOtpEmail(normalizedEmail, generatedOtp)) {
            otpDeliveryError = otpEmailService.getLastError();
            return false;
        }

        pendingOtpCode = generatedOtp;
        pendingOtpEmail = normalizedEmail;
        pendingOtpExpiryMillis = System.currentTimeMillis() + OTP_VALIDITY_MILLIS;
        otpDeliveryError = "";
        return true;
    }

    public boolean verifyRegistrationOtp(String email, String enteredOtp) {
        if (email == null || enteredOtp == null) {
            return false;
        }
        if (pendingOtpCode == null || pendingOtpEmail == null) {
            return false;
        }
        if (System.currentTimeMillis() > pendingOtpExpiryMillis) {
            clearPendingOtp();
            return false;
        }

        boolean ok = pendingOtpEmail.equals(email.trim().toLowerCase())
                && pendingOtpCode.equals(enteredOtp.trim());
        if (ok) {
            clearPendingOtp();
        }
        return ok;
    }

    public String getOtpDeliveryError() {
        return otpDeliveryError;
    }

    private void clearPendingOtp() {
        pendingOtpCode = null;
        pendingOtpEmail = null;
        pendingOtpExpiryMillis = 0;
    }

    public int authenticate(String email, String password) {
        if (!isValidEmail(email) || password == null || password.isEmpty()) {
            return -1;
        }

        VoterDAO.VoterAuthData authData = voterDAO.getVoterAuthData(email.trim().toLowerCase());
        if (authData == null || authData.hasVoted) {
            return -1;
        }

        if (!SecurityUtil.verifyPassword(password, authData.storedPassword)) {
            return -1;
        }

        // Migrate old plain-text passwords to hash on successful login.
        if (!SecurityUtil.isPasswordHashed(authData.storedPassword)) {
            String newHash = SecurityUtil.hashPassword(password);
            voterDAO.updateVoterPasswordHash(authData.id, newHash);
        }

        // Update last login timestamp
        voterDAO.updateVoterLastLogin(authData.id);

        return authData.id;
    }

    public boolean register(String name, String email, String password, String course, String section) {
        if (name == null || name.trim().isEmpty() ||
                !isValidEmail(email) ||
                password == null || password.length() < 6 ||
                course == null || course.trim().isEmpty() ||
                section == null || section.trim().isEmpty()) {
            // Enforce a minimum password length of 6 for basic security
            return false;
        }
        String passwordHash = SecurityUtil.hashPassword(password);
        return voterDAO.registerVoter(name.trim(), email.trim().toLowerCase(), passwordHash, course.trim(), section.trim().toUpperCase());
    }
}
