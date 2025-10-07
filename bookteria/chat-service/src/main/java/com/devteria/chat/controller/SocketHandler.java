package com.devteria.chat.controller;

import com.corundumstudio.socketio.SocketIOClient;
import com.corundumstudio.socketio.SocketIOServer;
import com.corundumstudio.socketio.annotation.OnConnect;
import com.corundumstudio.socketio.annotation.OnDisconnect;
import com.corundumstudio.socketio.annotation.OnEvent;
import com.devteria.chat.dto.request.IntrospectRequest;
import com.devteria.chat.entity.WebSocketSession;
import com.devteria.chat.repository.WebSocketSessionRepository;
import com.devteria.chat.service.IdentityService;
import com.devteria.chat.service.WebSocketSessionService;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.Instant;

@Slf4j
@Component
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class SocketHandler {
    private final WebSocketSessionRepository webSocketSessionRepository;
    SocketIOServer server;
    IdentityService identityService;
    WebSocketSessionService webSocketSessionService;

    @OnConnect
    public void clientConnected(SocketIOClient client) {
        // get token reques param
        String token = client.getHandshakeData().getSingleUrlParam("token");

        //veryfile token
        var introspectRespones = identityService.introspect(IntrospectRequest.builder()
                        .token(token)
                .build());
        if (introspectRespones.isValid()) {
            log.info("Client connected: {}", client.getSessionId());
            // Persist websocketSession ( lưu trữ thông tin session )
            WebSocketSession webSocketSession = WebSocketSession.builder()
                    .socketSessionId(client.getSessionId().toString())
                    .userId(introspectRespones.getUserId())
                    .creatrdAt(Instant.now())
                    .build();

            webSocketSession = webSocketSessionService.create(webSocketSession);
            log.info("WebSocKerSession created with id : {}", webSocketSession.getId());

        } else {
            log.info("Authentication faile: {}", client.getSessionId());
            client.disconnect();
        }
    }

    @OnDisconnect
    public void clientDisconnected(SocketIOClient client) {
        log.info("Client disConnected: {}", client.getSessionId());
        webSocketSessionRepository.deleteBySocketSessionId(client.getSessionId().toString());
    }

    @PostConstruct
    public void startServer() {
        server.start();
        server.addListeners(this);
        log.info("Socket server started");
    }

    @PreDestroy
    public void stopServer() {
        server.stop();
        log.info("Socket server stoped");
    }
}
