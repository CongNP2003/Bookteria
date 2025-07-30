package com.devteria.notification.dto.request;

import jakarta.validation.constraints.Email;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Recepient {
    String name;
    @Email
    String email;
}
