package com.linkedin.posts_service.service.impl;

import com.linkedin.posts_service.auth.UserContextHolder;
import com.linkedin.posts_service.clients.ConnectionClient;
import com.linkedin.posts_service.dto.PersonDto;
import com.linkedin.posts_service.dto.PostsCreateRequestDto;
import com.linkedin.posts_service.dto.PostsDto;
import com.linkedin.posts_service.entity.Post;
import com.linkedin.posts_service.event.PostCreatedEvent;
import com.linkedin.posts_service.exception.ResourceNotFoundException;
import com.linkedin.posts_service.repository.PostsRepository;
import com.linkedin.posts_service.service.PostsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class PostsServiceImpl implements PostsService {

    private final PostsRepository postsRepository;
    private final ModelMapper modelMapper;
    private final ConnectionClient connectionClient;
    private final KafkaTemplate<Long, PostCreatedEvent> kafkaTemplate;

    @Override
    public PostsDto createPost(PostsCreateRequestDto postsCreateRequestDto) {
        Long userId = UserContextHolder.getCurrentUserId();
        Post post = modelMapper.map(postsCreateRequestDto,Post.class);
        post.setUserId(userId);

        Post savedPost = postsRepository.save(post);

        PostCreatedEvent postCreatedEvent = PostCreatedEvent.builder()
                .postId(savedPost.getId())
                .creatorId(userId)
                .content(savedPost.getContent())
                .build();

        kafkaTemplate.send("post-created-topic",postCreatedEvent);
        return modelMapper.map(savedPost, PostsDto.class);
    }

    @Override
    public PostsDto getPostById(Long postId) {
        Long userId = UserContextHolder.getCurrentUserId();

        List<PersonDto> firstConnection = connectionClient.getFirstConnections();
        Post post = postsRepository.findById(postId)
                 .orElseThrow(() -> new ResourceNotFoundException("Post not found with id: "+postId));

        return modelMapper.map(post,PostsDto.class);
    }

    @Override
    public List<PostsDto> getAllPostsByUser(Long userId) {
        List<Post> posts = postsRepository.findByUserId(userId);
        return posts
                .stream()
                .map((element)->modelMapper.map(element,PostsDto.class))
                .collect(Collectors.toList());
    }
}
