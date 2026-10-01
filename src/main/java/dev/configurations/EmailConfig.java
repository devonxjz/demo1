package dev.configurations;

import lombok.Getter;

import java.io.InputStream;
import java.util.Properties;

@Getter
public class EmailConfig {

    private static final EmailConfig INSTANCE = new EmailConfig();

    private final String host;
    private final int port;
    private final boolean auth;
    private final boolean starttls;
    private final String username;
    private final String password;
    private final String from;
    private final boolean enabled;

    public static EmailConfig getInstance() {
        return INSTANCE;
    }

    public EmailConfig() {
        this(loadProperties());
    }

    public EmailConfig(Properties props) {
        this.host = get(props, "MAIL_SMTP_HOST", "mail.smtp.host", "smtp.gmail.com");
        this.port = getInt(props, "MAIL_SMTP_PORT", "mail.smtp.port", 587);
        this.auth = Boolean.parseBoolean(get(props, "MAIL_SMTP_AUTH", "mail.smtp.auth", "true"));
        this.starttls = Boolean.parseBoolean(get(props, "MAIL_SMTP_STARTTLS", "mail.smtp.starttls.enable", "true"));
        this.username = get(props, "MAIL_SMTP_USERNAME", "mail.smtp.username", "");
        this.password = get(props, "MAIL_SMTP_PASSWORD", "mail.smtp.password", "");
        this.from = get(props, "MAIL_FROM", "mail.from", "noreply@demo1.dev");
        this.enabled = Boolean.parseBoolean(get(props, "MAIL_ENABLED", "mail.enabled", "false"));
    }

    public String getPassword() {
        return password != null ? password.replace(" ", "") : "";
    }

    public boolean isReadyToSend() {
        return enabled && host != null && !host.isBlank()
                && username != null && !username.isBlank()
                && !getPassword().isBlank();
    }

    public Properties toSessionProperties() {
        Properties p = new Properties();
        p.put("mail.smtp.host", host);
        p.put("mail.smtp.port", String.valueOf(port));
        p.put("mail.smtp.auth", String.valueOf(auth));
        p.put("mail.smtp.starttls.enable", String.valueOf(starttls));
        p.put("mail.smtp.ssl.protocols", "TLSv1.2 TLSv1.3");
        return p;
    }

    private static String get(Properties props, String envKey, String propKey, String def) {
        String env = System.getenv(envKey);
        return (env != null && !env.isBlank()) ? env : (props != null ? props.getProperty(propKey, def) : def);
    }

    private static int getInt(Properties props, String envKey, String propKey, int def) {
        try {
            return Integer.parseInt(get(props, envKey, propKey, String.valueOf(def)).trim());
        } catch (Exception ignored) {
            return def;
        }
    }

    private static Properties loadProperties() {
        Properties props = new Properties();
        try (InputStream in = EmailConfig.class.getClassLoader().getResourceAsStream("application.properties")) {
            if (in != null) {
                props.load(in);
            }
        } catch (Exception ignored) {
        }
        return props;
    }
}
