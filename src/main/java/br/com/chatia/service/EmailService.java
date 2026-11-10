package br.com.chatia.service;

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
 * Configuração por variáveis de ambiente (nunca no código):
 *   SMTP_HOST      ex.: smtp.gmail.com
 *   SMTP_PORT      ex.: 587 (opcional, padrão 587)
 *   SMTP_USER      ex.: seuemail@gmail.com
 *   SMTP_PASSWORD  senha de app do Gmail (não a senha normal)
 */
public class EmailService {

    public boolean isConfigured() {
        return notBlank(System.getenv("SMTP_HOST"))
                && notBlank(System.getenv("SMTP_USER"))
                && notBlank(System.getenv("SMTP_PASSWORD"));
    }

    public void send(String to, String subject, String body) throws MessagingException {
        if (!isConfigured()) {
            throw new IllegalStateException(
                    "O envio de e-mail não está configurado (SMTP_HOST, SMTP_USER, SMTP_PASSWORD)."
            );
        }

        String host = System.getenv("SMTP_HOST");
        String port = notBlank(System.getenv("SMTP_PORT")) ? System.getenv("SMTP_PORT") : "587";
        String user = System.getenv("SMTP_USER");
        String password = System.getenv("SMTP_PASSWORD");

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
