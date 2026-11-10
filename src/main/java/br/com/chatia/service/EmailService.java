package br.com.chatia.service;

import br.com.chatia.config.AppConfig;
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
 * Envio de e-mails via SMTP.
 * Configuração externa via AppConfig (variável de ambiente ou config.properties):
 *   SMTP_HOST      ex.: smtp.gmail.com
 *   SMTP_PORT      ex.: 587 (opcional, padrão 587)
 *   SMTP_USER      ex.: seuemail@gmail.com
 *   SMTP_PASSWORD  senha de app do Gmail (não a senha normal)
 */
public class EmailService {

    public boolean isConfigured() {
        return notBlank(AppConfig.get("SMTP_HOST"))
                && notBlank(AppConfig.get("SMTP_USER"))
                && notBlank(AppConfig.get("SMTP_PASSWORD"));
    }

    public void send(String to, String subject, String body) throws MessagingException {
        if (!isConfigured()) {
            throw new IllegalStateException(
                    "O envio de e-mail não está configurado (SMTP_HOST, SMTP_USER, SMTP_PASSWORD)."
            );
        }

        String host = AppConfig.get("SMTP_HOST");
        String port = AppConfig.get("SMTP_PORT", "587");
        String user = AppConfig.get("SMTP_USER");
        String password = AppConfig.get("SMTP_PASSWORD");

        Properties props = new Properties();
        props.put("mail.smtp.host", host);
        props.put("mail.smtp.port", port);
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.starttls.required", "true");
        props.put("mail.smtp.connectiontimeout", "10000");
        props.put("mail.smtp.timeout", "10000");

        Session session = Session.getInstance(props, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(user, password);
            }
        });

        MimeMessage message = new MimeMessage(session);
        message.setFrom(new InternetAddress(user));
        message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(to));
        message.setSubject(subject, "UTF-8");
        message.setText(body, "UTF-8");

        Transport.send(message);
    }

    private boolean notBlank(String value) {
        return value != null && !value.isBlank();
    }
}
