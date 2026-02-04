package com.example.backend.business;

import com.example.backend.exception.BaseException;
import com.example.backend.exception.EmailException;
import com.example.backend.service.EmailService;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

@Service
public class EmailBusiness {

    private final EmailService emailService;

    public EmailBusiness(EmailService emailService) {
        this.emailService = emailService;
    }

    public void sendActivateUserEmail(String email, String name, String token) throws BaseException {
        // HTML
        String html;
        try {
            html = readEmailTemplate();
        } catch (IOException e) {
            throw EmailException.templateNotFound();
        }

        String finalLink = "http://localhost:4200/activate" + token;
        html = html.replace("${P_NAME}", name);
        html = html.replace("${LINK}", finalLink);

        // subject
        String subject = "Please activate your account";

        emailService.send(email, subject, html);

    }

    private String readEmailTemplate() throws IOException {
        ClassPathResource resource =
                new ClassPathResource("templates/" + "email-activate-user.html");

        try (InputStream in = resource.getInputStream()) {
            return new String(in.readAllBytes(), StandardCharsets.UTF_8);
        }
    }
}
