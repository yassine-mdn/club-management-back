package ma.ac.uir.projets8.service;

import jakarta.mail.MessagingException;
import lombok.extern.log4j.Log4j2;
import ma.ac.uir.projets8.model.Mail;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class EmailServiceTest {

    @Autowired
    EmailService emailService;

    @Test
    void htmlEmailGetsSent() throws IOException, MessagingException {

        Map<String, Object> properties = new HashMap<String, Object>();
        properties.put("name", "John Michel!");
        properties.put("location", "Sri Lanka");
        properties.put("sign", "Java Developer");

        Mail mail = Mail.builder()
                .to("notsharingmyemail@gmail.com")
                .htmlTemplate(new Mail.HtmlTemplate("example-email", properties))
                .subject("This is sample email with spring boot and thymeleaf")
                .build();

        emailService.sendHtmlEmail(mail);
    }
}