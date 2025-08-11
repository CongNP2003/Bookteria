package com.devteria.notification.controller;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.devteria.notification.dto.ApiResponse;
import com.devteria.notification.dto.Response.EmailResponse;
import com.devteria.notification.dto.request.SendEmailRequest;
import com.devteria.notification.service.EmailService;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class EmailController {

    EmailService emailService;

    @PostMapping("email/send")
    ApiResponse<EmailResponse> sendEmail(@RequestBody SendEmailRequest request) {
        return ApiResponse.<EmailResponse>builder()
                .result(emailService.senEmail(request))
                .build();
    }

    @KafkaListener(topics = "Onboard-successful")
    public void listener(String message){
        log.info("Message received {} :"+ message);
    }
}
