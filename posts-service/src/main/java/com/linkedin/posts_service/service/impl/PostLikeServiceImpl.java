package com.linkedin.posts_service.service.impl;

import com.linkedin.posts_service.auth.UserContextHolder;
import com.linkedin.posts_service.entity.Post;
import com.linkedin.posts_service.entity.PostsLike;
import com.linkedin.posts_service.event.PostLikeEvent;
import com.linkedin.posts_service.exception.BadRequestException;
import com.linkedin.posts_service.exception.ResourceNotFoundException;
import com.linkedin.posts_service.repository.PostLikesRepository;
import com.linkedin.posts_service.repository.PostsRepository;
import com.linkedin.posts_service.service.PostLikeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class PostLikeServiceImpl implements PostLikeService {

    private final PostsRepository postsRepository;
    private final PostLikesRepository postLikesRepository;
    private final KafkaTemplate<Long, PostLikeEvent> kafkaTemplate;

    public void likePost(Long postId) {
        Long userId = UserContextHolder.getCurrentUserId();
        Post post= postsRepository.findById(postId)
                .orElseThrow(() -> new ResourceNotFoundException("Post not found with id: "+postId));
        boolean alreadyLiked = postLikesRepository.existsByUserIdAndPostId(userId,postId);
        if(alreadyLiked) throw new BadRequestException("Cannot like the same post again");

        PostsLike postsLike = new PostsLike();
        postsLike.setPostId(postId);
        postsLike.setUserId(userId);
        postLikesRepository.save(postsLike);

        PostLikeEvent postLikeEvent = PostLikeEvent.builder()
                .postId(postId)
                .creatorId(post.getUserId())
                .likedByUserId(userId)
                .build();

        kafkaTemplate.send("post-liked-topic", postId, postLikeEvent);
    }

    @Override
    public void unlikePost(Long postId) {
        Long userId = UserContextHolder.getCurrentUserId();
        boolean exists = postsRepository.existsById(postId);
        if(!exists) throw new ResourceNotFoundException("Post not found with id: "+postId);

        boolean alreadyLiked = postLikesRepository.existsByUserIdAndPostId(userId,postId);
        if(!alreadyLiked) throw new BadRequestException("Cannot dislike the post as it is not liked");

        postLikesRepository.deleteByUserIdAndPostId(userId,postId);
    }
}
