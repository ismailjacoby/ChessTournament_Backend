package be.technobel.chesstournament.bll.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import java.util.Properties;

/**
 * Configuration class for setting up the JavaMailSender used for sending emails.
 */
@Configuration
public class MailConfig {

    // Injected values from the application.yml

    @Value("${spring.mail.host}")
    private String host;

    @Value("${spring.mail.port}")
    private int port;

    @Value("${spring.mail.username}")
    private String username;

    @Value("${spring.mail.password}")
    private String password;

    /**
     * Configures and provides an instance of JavaMailSender for sending emails.
     * The JavaMailSender is configured with the properties specified in the application configuration.
     *
     * @return An instance of JavaMailSender configured for sending emails.
     */
    @Bean
    public JavaMailSender javaMailSender() {
        // Create an instance of JavaMailSenderImpl
        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();

        // Set the host, port, username, and password for the mail sender
        mailSender.setHost(host);
        mailSender.setPort(port);
        mailSender.setUsername(username);
        mailSender.setPassword(password);

        // Set the host, port, username, and password for the mail sender
        Properties props = mailSender.getJavaMailProperties();

        // Set the mail transport protocol to SMTP
        props.put("mail.transport.protocol", "smtp");

        // Enable SMTP authentication
        props.put("mail.smtp.auth", "true");

        // Enable STARTTLS for secure communication
        props.put("mail.smtp.starttls.enable", "true");

        // Return the configured JavaMailSender instance
        return mailSender;
    }
}
