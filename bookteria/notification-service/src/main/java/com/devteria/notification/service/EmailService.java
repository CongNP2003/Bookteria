package com.devteria.notification.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.devteria.notification.dto.Response.EmailResponse;
import com.devteria.notification.dto.request.EmailRequest;
import com.devteria.notification.dto.request.SendEmailRequest;
import com.devteria.notification.dto.request.Sender;
import com.devteria.notification.exception.AppException;
import com.devteria.notification.exception.ErrorCode;
import com.devteria.notification.repository.httpclient.Emailclient;

import feign.FeignException;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@Setter
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class EmailService {
    Emailclient emailclient;

    String apiKey = "Nhập token bervo api vào đây";

    public EmailResponse senEmail(SendEmailRequest request) {
        EmailRequest emailRequest = EmailRequest.builder()
                .sender(Sender.builder()
                        .name("Phuc Cong")
                        .email("linhanhanam0906@gmail.com")
                        .build())
                .to(List.of(request.getTo()))
                .subject(request.getSubject())
                .htmlContent(request.getHtmlContent())
                .build();

        try {
            return emailclient.sendCEmail(apiKey, emailRequest);
        } catch (FeignException e) {
            throw new AppException(ErrorCode.CANNOT_SEND_EMAIL);
        }
    }
}
