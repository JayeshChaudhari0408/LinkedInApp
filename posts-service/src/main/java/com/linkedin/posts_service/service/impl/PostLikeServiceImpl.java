package com.linkedin.posts_service.service.impl;

import com.linkedin.posts_service.entity.PostsLike;
import com.linkedin.posts_service.exception.BadRequestException;
import com.linkedin.posts_service.exception.ResourceNotFoundException;
import com.linkedin.posts_service.repository.PostLikesRepository;
import com.linkedin.posts_service.repository.PostsRepository;
import com.linkedin.posts_service.service.PostLikeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class PostLikeServiceImpl implements PostLikeService {

    private final PostsRepository postsRepository;
    private final PostLikesRepository postLikesRepository;

    public void likePost(Long postId,Long userId) {
        boolean exists = postsRepository.existsById(postId);
        if(!exists) throw new ResourceNotFoundException("Post not found with id: "+postId);

        boolean alreadyLiked = postLikesRepository.existsByUserIdAndPostId(userId,postId);
        if(alreadyLiked) throw new BadRequestException("Cannot like the same post again");

        PostsLike postsLike = new PostsLike();
        postsLike.setPostId(postId);
        postsLike.setUserId(userId);
        postLikesRepository.save(postsLike);

    }

    @Override
    public void unlikePost(Long userId, Long postId) {
        boolean exists = postsRepository.existsById(postId);
        if(!exists) throw new ResourceNotFoundException("Post not found with id: "+postId);

        boolean alreadyLiked = postLikesRepository.existsByUserIdAndPostId(userId,postId);
        if(!alreadyLiked) throw new BadRequestException("Cannot dislike the post as it is not liked");

        postLikesRepository.deleteByUserIdAndPostId(userId,postId);
    }
}
