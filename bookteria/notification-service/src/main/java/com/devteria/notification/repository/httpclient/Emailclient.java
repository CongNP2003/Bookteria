package com.devteria.notification.repository.httpclient;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

import com.devteria.notification.dto.Response.EmailResponse;
import com.devteria.notification.dto.request.EmailRequest;

@FeignClient(name = "email-client", url = "https://api.brevo.com")
public interface Emailclient {
    @PostMapping(value = "/v3/smtp/email", produces = MediaType.APPLICATION_JSON_VALUE)
    EmailResponse sendCEmail(@RequestHeader("api-Key") String apikey, @RequestBody EmailRequest body);
}
