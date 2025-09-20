package com.devteria.chat.service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.StringJoiner;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.devteria.chat.dto.request.ConversationRequest;
import com.devteria.chat.dto.response.ConversationResponse;
import com.devteria.chat.entity.Conversation;
import com.devteria.chat.entity.ParticipantInfo;
import com.devteria.chat.exception.AppException;
import com.devteria.chat.exception.ErrorCode;
import com.devteria.chat.mapper.ConversationMapper;
import com.devteria.chat.repository.ConversationRepository;
import com.devteria.chat.repository.httpclient.ProfileClient;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ConversationService {
    ConversationRepository conversationRepository;
    ProfileClient profileClient;

    ConversationMapper conversationMapper;

    public List<ConversationResponse> myConversations() {
        String userId = SecurityContextHolder.getContext().getAuthentication().getName();
        List<Conversation> conversations = conversationRepository.findAllByParticipantIdsContains(userId);
        return conversations.stream().map(this::toConversationResponse).toList();
    }

    public ConversationResponse create(ConversationRequest request) {

        // fetch user info ( tức là lấy thông tin từ đâu đó, có thể service khác )
        String userId = SecurityContextHolder.getContext().getAuthentication().getName();
        var useInfoRespones = profileClient.getProfile(userId);
        // Trường hợp này là đang có user nói chuyện trực tiếp với nhau thôi
        var participantInfoRespones =
                profileClient.getProfile(request.getParticipantIds().getFirst());
        if (Objects.isNull(useInfoRespones) || Objects.isNull(participantInfoRespones)) {
            throw new AppException(ErrorCode.UNCATEGORIZED_EXCEPTION);
        }

        var userInfo = useInfoRespones.getResult();
        var participantInfo = participantInfoRespones.getResult();
        List<String> userIds = new ArrayList<>();
        userIds.add(userId);
        userIds.add(participantInfo.getUserId());

        var sortedIds = userIds.stream().sorted().toList();
        var userIdHash = generateParticipantHash(sortedIds);

        // kiểm tra xem ParticipantHash đã tồn tại hay chưa, nếu chưa thì tạo mới
        var conversation = conversationRepository.findByParticipantsHash(userIdHash).orElseGet(() ->{
            List<ParticipantInfo> participantInfos = List.of(
                    ParticipantInfo.builder()
                            .userId(userInfo.getUserId())
                            .username(userInfo.getUsername())
                            .lastName(userInfo.getLastName())
                            .firstName(userInfo.getFirstName())
                            .avatar(userInfo.getAvatar())
                            .build(),
                    ParticipantInfo.builder()
                            .userId(participantInfo.getUserId())
                            .username(participantInfo.getUsername())
                            .lastName(participantInfo.getLastName())
                            .firstName(participantInfo.getFirstName())
                            .avatar(participantInfo.getAvatar())
                            .build());

            // Build Conversation info

            Conversation newConversation = Conversation.builder()
                    .type(request.getType())
                    .participantsHash(userIdHash)
                    .createdDate(Instant.now())
                    .modifiedDate(Instant.now())
                    .participants(participantInfos)
                    .build();

            return conversationRepository.save(newConversation);
        });

        return toConversationResponse(conversation);
    }

    private String generateParticipantHash(List<String> ids) {
        StringJoiner stringJoiner = new StringJoiner("_");
        ids.forEach(stringJoiner::add);
        return stringJoiner.toString();
    }

    private ConversationResponse toConversationResponse(Conversation conversation) {
        String currentUserId =
                SecurityContextHolder.getContext().getAuthentication().getName();

        ConversationResponse conversationResponse = conversationMapper.toConversationResponse(conversation);

        conversation.getParticipants().stream()
                .filter(participantInfo -> !participantInfo.getUserId().equals(currentUserId))
                .findFirst()
                .ifPresent(participantInfo -> {
                    conversationResponse.setConversationName(participantInfo.getUsername());
                    conversationResponse.setConversationAvatar(participantInfo.getAvatar());
                });

        return conversationResponse;
    }
}
