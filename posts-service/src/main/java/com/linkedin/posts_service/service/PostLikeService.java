package com.linkedin.posts_service.service;

public interface PostLikeService {

    void likePost(Long userId, Long postId);

    void unlikePost(Long userId, Long postId);
}
