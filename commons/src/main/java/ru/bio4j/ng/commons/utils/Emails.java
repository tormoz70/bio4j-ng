package ru.bio4j.ng.commons.utils;

import java.util.*;
import javax.mail.*;
import javax.mail.internet.*;

public class Emails {

//    private static final String SMTP_HOST_NAME = "87.229.232.206";
    private static final String SMTP_HOST_NAME = "mail.fond-kino.ru";
    private static final String SMTP_AUTH_USER = "eais_support";
    private static final String SMTP_AUTH_PWD  = "76sVuJWQ";
    private static final String SMTP_SENDER = "eais_support@fond-kino.ru";
    private static final String SMTP_SENDER_NAME = "Электронный кинобилет";
    private static final String SMTP_ENCODING = "UTF-8";
    private static final String SMTP_CONTENT_PLAIN = "text/plain";

    private static void send0(String to, String subject, String content, String contentType, String encoding) throws MessagingException {
        Properties props = new Properties();
        props.put("mail.transport.protocol", "smtp");
        props.put("mail.smtp.host", SMTP_HOST_NAME);
        props.put("mail.smtp.port", "25");
        props.put("mail.smtp.auth", "true");

        Authenticator auth = new Authenticator (){
            @Override
            public PasswordAuthentication getPasswordAuthentication() {
                String username = SMTP_AUTH_USER;
                String password = SMTP_AUTH_PWD;
                return new PasswordAuthentication(username, password);
            }
        };
        Session mailSession = Session.getDefaultInstance(props, auth);
        Transport transport = mailSession.getTransport();
        MimeMessage message = new MimeMessage(mailSession);
        message.setSubject(subject, encoding);
        message.setContent(content, String.format("%s; charset=%s", contentType, encoding));
        message.setFrom(new InternetAddress(SMTP_SENDER));
        //message.setDescription(SMTP_SENDER_NAME, encoding);
        message.addRecipient(Message.RecipientType.TO, new InternetAddress(to));

        transport.connect();
        transport.sendMessage(message, message.getRecipients(Message.RecipientType.TO));
        transport.close();
    }

    private static void send(String to, String subject, String content, String contentType, String encoding) throws MessagingException {
        String[] toList = Strings.split(to, ",", ";", " ");
        for (String a : toList) {
            String toAddr = a.toLowerCase().trim();
            if(!Strings.isNullOrEmpty(toAddr))
                send0(toAddr, subject, content, contentType, encoding);
        }
    }


    public static void sendPlain(String to, String subject, String content) throws MessagingException {
        send(to, subject, content, SMTP_CONTENT_PLAIN, SMTP_ENCODING);
    }


}
