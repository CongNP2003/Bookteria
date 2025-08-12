package com.devteria.notification.controller;

import com.devteria.event.dto.NotificationEvent;
import com.devteria.notification.dto.request.Recepient;
import com.devteria.notification.dto.request.SendEmailRequest;
import com.devteria.notification.service.EmailService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Component
public class NotificationController {

    EmailService emailService;
    @KafkaListener(topics = "notification-delivery")
    public void listenerNotification(NotificationEvent message){
        log.info("Message received {} :"+ message);
        emailService.senEmail(SendEmailRequest.builder()
                        .to(Recepient.builder()
                                .email(message.getRecepient())
                                .build())
                        .subject(message.getSubject())
                        .htmlContent(message.getBody())
                .build());
    }
}
