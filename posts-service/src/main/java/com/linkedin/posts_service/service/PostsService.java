package com.linkedin.posts_service.service;

import com.linkedin.posts_service.dto.PostsCreateRequestDto;
import com.linkedin.posts_service.dto.PostsDto;

import java.util.List;


public interface PostsService {

    PostsDto createPost(PostsCreateRequestDto postsCreateRequestDto, Long userId);

    PostsDto getPostById(Long postId);

    List<PostsDto> getAllPostsByUser(Long userId);
}
