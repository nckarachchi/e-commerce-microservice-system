package com.ecommerce.notificationservice.service;

import com.ecommerce.amqp.dto.EmailRequest;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
@Slf4j
public class EmailConsumer {
    private final EmailService emailService;

    @RabbitListener(queues = "${rabbitmq.queues.send-email}")
    public void sendEmailConsumer(EmailRequest emailRequest) {
        log.info("Consumed {} from send-email queue", emailRequest);
        emailService.sendEmail(emailRequest);
    }

}