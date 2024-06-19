package com.example.baby_cry_identfication;

import android.content.Context;

import com.google.api.client.auth.oauth2.Credential;
import com.sun.mail.smtp.SMTPTransport;

import javax.mail.Message;
import javax.mail.Session;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Properties;

public class JavaMailAPI {

    private final String toEmail;  // Recipient's email
    private final String subject;  // Email subject
    private final String message;  // Email message

    public JavaMailAPI(String toEmail, String subject, String message) {
        this.toEmail = toEmail;
        this.subject = subject;
        this.message = message;
    }

    public void sendEmail(Context context) {
        try {
            Credential credential = OAuth2Util.getCredentials(context);

            Properties props = new Properties();
            props.put("mail.smtp.auth", "true");
            props.put("mail.smtp.starttls.enable", "true");
            props.put("mail.smtp.host", "smtp.gmail.com");
            props.put("mail.smtp.port", "587");

            Session session = Session.getInstance(props);

            MimeMessage email = createEmail(toEmail, "me", subject, message);

            SMTPTransport transport = (SMTPTransport) session.getTransport("smtp");
            transport.connect("smtp.gmail.com", "user", credential.getAccessToken());
            transport.sendMessage(email, email.getAllRecipients());
            transport.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private MimeMessage createEmail(String to, String from, String subject, String bodyText) throws Exception {
        Session session = Session.getDefaultInstance(System.getProperties(), null);
        MimeMessage email = new MimeMessage(session);

        email.setFrom(new InternetAddress(from));
        email.addRecipient(Message.RecipientType.TO, new InternetAddress(to));
        email.setSubject(subject);
        email.setText(bodyText);
        return email;
    }
}
