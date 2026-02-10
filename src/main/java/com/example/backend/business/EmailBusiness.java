package com.example.backend.business;

import com.example.backend.exception.BaseException;
import com.example.backend.exception.EmailException;
import com.example.common.EmailRequest;
import lombok.extern.log4j.Log4j2;
import org.springframework.core.io.ClassPathResource;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.CompletableFuture;

@Service
@Log4j2
public class EmailBusiness {

    private final KafkaTemplate<String, EmailRequest> kafkaEmailTemplate;

    public EmailBusiness(KafkaTemplate<String, EmailRequest> kafkaTemplate) {
        this.kafkaEmailTemplate = kafkaTemplate;
    }

    public void sendActivateUserEmail(String email, String name, String token) throws BaseException {
        // HTML
        String html;
        try {
            html = readEmailTemplate();
        } catch (IOException e) {
            throw EmailException.templateNotFound();
        }

        log.info("Token = " + token);

        String finalLink = "http://localhost:4200/activate/" + token;
        html = html.replace("${P_NAME}", name);
        html = html.replace("${P_LINK}", finalLink);


        EmailRequest emailRequest = new EmailRequest();
        emailRequest.setTo(email); //ส่งถึง
        emailRequest.setSubject("Please activate your account");
        emailRequest.setContent(html);

        // ส่ง message เข้า Kafka topic ที่ชื่อ activation-email
        CompletableFuture<SendResult<String, EmailRequest>> future = kafkaEmailTemplate.send("activation-email", emailRequest);

        future.whenComplete((result, ex) -> {

            if (ex != null) {
                log.error("Kafka send fail", ex);
            } else { //ถ้าส่งได้
                log.info("Kafka send success {}", result);
            }

        });
    }

    private String readEmailTemplate() throws IOException {
        ClassPathResource resource =
                new ClassPathResource("templates/" + "email-activate-user.html");

        try (InputStream in = resource.getInputStream()) {
            return new String(in.readAllBytes(), StandardCharsets.UTF_8);
        }
    }
}
