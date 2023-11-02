package ma.ac.uir.projets8.repository;

import jakarta.mail.MessagingException;
import ma.ac.uir.projets8.model.Mail;

import java.io.IOException;

public interface EmailRepository {

    void sendSimpleEmail(String to, String subject, String content);

    void sendHtmlEmail(Mail mail) throws MessagingException, IOException;
}
