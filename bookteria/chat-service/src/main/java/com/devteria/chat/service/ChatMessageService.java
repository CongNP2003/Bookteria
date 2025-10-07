package com.devteria.chat.service;

import com.corundumstudio.socketio.SocketIOServer;
import com.devteria.chat.dto.request.ChatMessageRequest;
import com.devteria.chat.dto.response.ChatMessageResponse;
import com.devteria.chat.entity.ChatMessage;
import com.devteria.chat.entity.ParticipantInfo;
import com.devteria.chat.entity.WebSocketSession;
import com.devteria.chat.exception.AppException;
import com.devteria.chat.exception.ErrorCode;
import com.devteria.chat.mapper.ChatMessageMapper;
import com.devteria.chat.repository.ChatMessageRepository;
import com.devteria.chat.repository.ConversationRepository;
import com.devteria.chat.repository.WebSocketSessionRepository;
import com.devteria.chat.repository.httpclient.ProfileClient;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ChatMessageService {
    private final WebSocketSessionRepository webSocketSessionRepository;

    SocketIOServer socketIOServer;

    private final ConversationRepository conversationRepository;
    ChatMessageRepository chatMessageRepository;
    ProfileClient profileClient;
    ObjectMapper objectMapper;

    ChatMessageMapper chatMessageMapper;

    public List<ChatMessageResponse> getMessages(String conversationId) {
        String  userId = SecurityContextHolder.getContext().getAuthentication().getName();
        // validate conversation
        conversationRepository
                .findById(conversationId)
                .orElseThrow(() -> new AppException(ErrorCode.CONVERSATION_NOT_FOUND))
                .getParticipants().stream()
                .filter(participantInfo -> userId.equals(participantInfo.getUserId()))
                .findAny().orElseThrow(() -> new AppException(ErrorCode.CONVERSATION_NOT_FOUND));
        // get message
        var chatMessage = chatMessageRepository.findAllByConversationIdOrderByCreatedDateDesc(conversationId);

        return chatMessage.stream().map(this::tochatMessageResponse).toList();
    }

    public ChatMessageResponse create(ChatMessageRequest request) throws JsonProcessingException {
        String userId = SecurityContextHolder.getContext().getAuthentication().getName();
        // validate conversation
        var conversation = conversationRepository
                .findById(request.getConversationId())
                .orElseThrow(() -> new AppException(ErrorCode.CONVERSATION_NOT_FOUND));

        conversation.getParticipants().stream()
                .filter(participantInfo -> userId.equals(participantInfo.getUserId()))
                .findAny().orElseThrow(() -> new AppException(ErrorCode.CONVERSATION_NOT_FOUND));
        // get userInfo from profileService
        var userResponse = profileClient.getProfile(userId);
        if (Objects.isNull(userResponse)){
            throw new AppException(ErrorCode.UNCATEGORIZED_EXCEPTION);
        }

        // build chat message info
        var userInfo = userResponse.getResult();
        ChatMessage chatMessage = chatMessageMapper.toChatMessage(request);
        chatMessage.setSender(ParticipantInfo.builder()
                        .userId(userInfo.getUserId())
                        .username(userInfo.getUsername())
                        .lastName(userInfo.getLastName())
                        .firstName(userInfo.getFirstName())
                        .avatar(userInfo.getAvatar())
                .build());
        chatMessage.setCreatedDate(Instant.now());

        // create chat message
        chatMessage = chatMessageRepository.save(chatMessage);
        chatMessage.setId(chatMessage.getId());
        // publish socket event to client is conversation ( tức là nó chỉ gửi cho những người liên quan )

        // get participants userIds
        List<String> userIds = conversation.getParticipants().stream().map(ParticipantInfo::getUserId).toList();

        // lấy ra tất các WebSocketSession của userId thuộc về
        Map<String, WebSocketSession> webSocketSessions = webSocketSessionRepository.findAllByUserIdIn(userIds)
                .stream()
                .collect(Collectors.toMap(WebSocketSession ::getSocketSessionId, Function.identity()));

        ChatMessageResponse chatMessageResponse = chatMessageMapper.toChatMessageResponse(chatMessage);
        socketIOServer.getAllClients().forEach(socketIOClient -> {
            var webSocketSession = webSocketSessions.get(socketIOClient.getSessionId().toString());
            if (Objects.nonNull(webSocketSession)) {
                chatMessageResponse.setMe(webSocketSession.getUserId().equals(userId));
                String message = null;
                try {
                    message = objectMapper.writeValueAsString(chatMessageResponse);
                    socketIOClient.sendEvent("message", message);

                } catch (JsonProcessingException e) {
                    throw new RuntimeException(e);
                }
            }
        });

        return tochatMessageResponse(chatMessage);
    }

    private ChatMessageResponse tochatMessageResponse (ChatMessage chatMessage) {
        String userId = SecurityContextHolder.getContext().getAuthentication().getName();

        var chatMessageResponse = chatMessageMapper.toChatMessageResponse(chatMessage);

        // đoạn này kiểm tra xem request có phải là ngư gửi ko
        chatMessageResponse.setMe(userId.equals(chatMessage.getSender().getUserId()));
        return chatMessageResponse;
    }
}
