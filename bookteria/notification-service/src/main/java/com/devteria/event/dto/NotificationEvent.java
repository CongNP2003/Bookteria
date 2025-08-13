package com.devteria.event.dto;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class NotificationEvent {
    String Channel; // email,sms.....
    String recepient;
    String templateCode;
    Map<String, Object> param;
    String subject;
    String body;

}
