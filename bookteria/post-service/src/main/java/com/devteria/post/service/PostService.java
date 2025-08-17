package com.devteria.post.service;

import com.devteria.post.dto.PageResponse;
import com.devteria.post.dto.request.PostRequest;
import com.devteria.post.dto.response.PostResponse;
import com.devteria.post.entity.Post;
import com.devteria.post.mapper.PostMapper;
import com.devteria.post.repository.PostRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class PostService {
    PostMapper postMapper;
    DateTimeFormatter dateTimeFormatter;
    PostRepository postRepository;

    public PostResponse createPost(PostRequest request)  {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        Post post = Post.builder()
                .userId(authentication.getName())
                .content(request.getContent())
                .createdDate(Instant.now())
                .modifiedDate(Instant.now())
                .build();

        Post result = postRepository.save(post);
        System.out.println(result);
        return postMapper.toPostResponse(result);
    }

    public PageResponse<PostResponse> getMyPost(int page, int size) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        Sort sort = Sort.by("createdDate").descending();

        Pageable pageable = PageRequest.of(page - 1, size, sort);
        String userId = authentication.getName();

        var pageData = postRepository.findAllByuserId(userId, pageable);

        var postList = pageData.getContent().stream().map(post -> {
            var postResponse = postMapper.toPostResponse(post);
            postResponse.setCreated(dateTimeFormatter.format(post.getCreatedDate()));
            return postResponse;
        }).toList();
//        return postRepository.findAllByuserId(userId)
//                .stream()
//                .map(postMapper::toPostResponse)
//                .toList();

        return PageResponse.<PostResponse>builder()
                .currenPage(page)
                .totalPages(pageData.getTotalPages())
                .totalElements(pageData.getTotalElements())
                .pageSize(pageData.getSize())
                .data(postList)
//                .data(pageData.getContent().stream().map(postMapper::toPostResponse).toList())
                .build();
    }
}
