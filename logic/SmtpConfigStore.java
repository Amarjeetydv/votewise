package logic;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;

/**
 * Persists SMTP settings in a local properties file.
 */
public class SmtpConfigStore {
    private static final Path CONFIG_PATH = Paths.get("config", "smtp.properties");

    public Properties load() {
        Properties properties = new Properties();
        if (!Files.exists(CONFIG_PATH)) {
            return properties;
        }

        try (InputStream in = Files.newInputStream(CONFIG_PATH)) {
            properties.load(in);
        } catch (IOException e) {
            // Return empty properties on read failure; caller can show validation message.
            return new Properties();
        }
        return properties;
    }

    public boolean save(String host, String port, String user, String pass, String from) {
        Properties properties = new Properties();
        properties.setProperty("host", safe(host));
        properties.setProperty("port", safe(port));
        properties.setProperty("user", safe(user));
        properties.setProperty("pass", safe(pass));
        properties.setProperty("from", safe(from));

        try {
            Files.createDirectories(CONFIG_PATH.getParent());
            try (OutputStream out = Files.newOutputStream(CONFIG_PATH)) {
                properties.store(out, "VoteWise SMTP Settings");
                return true;
            }
        } catch (IOException e) {
            return false;
        }
    }

    private String safe(String value) {
        return value == null ? "" : value.trim();
    }
}
