package com.devteria.chat.service;

import com.corundumstudio.socketio.SocketIOServer;
import com.devteria.chat.dto.request.ChatMessageRequest;
import com.devteria.chat.dto.response.ChatMessageResponse;
import com.devteria.chat.entity.ChatMessage;
import com.devteria.chat.entity.ParticipantInfo;
import com.devteria.chat.exception.AppException;
import com.devteria.chat.exception.ErrorCode;
import com.devteria.chat.mapper.ChatMessageMapper;
import com.devteria.chat.repository.ChatMessageRepository;
import com.devteria.chat.repository.ConversationRepository;
import com.devteria.chat.repository.httpclient.ProfileClient;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ChatMessageService {

    SocketIOServer socketIOServer;

    private final ConversationRepository conversationRepository;
    ChatMessageRepository chatMessageRepository;
    ProfileClient profileClient;

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

    public ChatMessageResponse create(ChatMessageRequest request) {
        String  userId = SecurityContextHolder.getContext().getAuthentication().getName();
        // validate conversation
        conversationRepository
                .findById(request.getConversationId())
                .orElseThrow(() -> new AppException(ErrorCode.CONVERSATION_NOT_FOUND))
                .getParticipants().stream()
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
        String message = chatMessage.getMessage();
        // publish socket event to client
        socketIOServer.getAllClients().forEach(socketIOClient -> {
            socketIOClient.sendEvent("message", message);
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
