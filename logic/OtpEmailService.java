package logic;

import jakarta.mail.Authenticator;
import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import jakarta.mail.PasswordAuthentication;
import jakarta.mail.Session;
import jakarta.mail.Transport;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;

import java.util.Properties;

/**
 * SMTP-based OTP mail sender.
 * Reads SMTP configuration from settings file first and then from environment variables.
 */
public class OtpEmailService {
    private String lastError = "";
    private SmtpConfigStore smtpConfigStore = new SmtpConfigStore();

    public boolean sendOtpEmail(String recipientEmail, String otpCode) {
        lastError = "";

        Properties config = smtpConfigStore.load();

        String host = firstNonEmpty(config.getProperty("host"), getenvOrDefault("SMTP_HOST", "smtp.gmail.com"));
        String port = firstNonEmpty(config.getProperty("port"), getenvOrDefault("SMTP_PORT", "587"));
        String user = firstNonEmpty(config.getProperty("user"), System.getenv("SMTP_USER"));
        String pass = firstNonEmpty(config.getProperty("pass"), System.getenv("SMTP_PASS"));
        String from = firstNonEmpty(config.getProperty("from"), getenvOrDefault("SMTP_FROM", user));

        if (isBlank(host) || isBlank(port) || isBlank(user) || isBlank(pass)) {
            lastError = "SMTP_USER/SMTP_PASS is not configured.";
            return false;
        }

        String subject = "VoteWise OTP Verification";
        String body = "Your VoteWise OTP is: " + otpCode + "\n\n" +
                "This OTP is valid for 5 minutes.\n" +
                "If you did not request this, please ignore this email.";

        return sendEmail(host, port, user, pass, from, recipientEmail, subject, body);
    }

    public boolean sendTestEmailWithConfig(String host, String port, String user, String pass, String from, String recipientEmail) {
        lastError = "";

        if (isBlank(host) || isBlank(port) || isBlank(user) || isBlank(pass) || isBlank(recipientEmail)) {
            lastError = "Host, port, user, password, and recipient email are required.";
            return false;
        }

        String effectiveFrom = isBlank(from) ? user.trim() : from.trim();
        String subject = "VoteWise SMTP Test";
        String body = "This is a test email from VoteWise SMTP settings.\n" +
                "If you received this, SMTP configuration is working.";

        return sendEmail(host.trim(), port.trim(), user.trim(), pass, effectiveFrom, recipientEmail.trim(), subject, body);
    }

    private boolean sendEmail(String host, String port, String user, String pass,
                              String from, String recipientEmail, String subject, String body) {
        if (isBlank(from)) {
            from = user;
        }

        Properties props = new Properties();
        props.put("mail.smtp.host", host);
        props.put("mail.smtp.port", port);
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");

        Session session = Session.getInstance(props, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(user, pass);
            }
        });

        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(from));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(recipientEmail));
            message.setSubject(subject);
            message.setText(body);

            Transport.send(message);
            return true;
        } catch (MessagingException e) {
            lastError = e.getMessage();
            return false;
        }
    }

    public String getLastError() {
        return lastError;
    }

    private String getenvOrDefault(String key, String fallback) {
        String value = System.getenv(key);
        return (value == null || value.trim().isEmpty()) ? fallback : value.trim();
    }

    private String firstNonEmpty(String first, String second) {
        if (first != null && !first.trim().isEmpty()) {
            return first.trim();
        }
        return second == null ? null : second.trim();
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }
}
