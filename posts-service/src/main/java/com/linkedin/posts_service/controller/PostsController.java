package com.linkedin.posts_service.controller;

import com.linkedin.posts_service.dto.PostsCreateRequestDto;
import com.linkedin.posts_service.dto.PostsDto;
import com.linkedin.posts_service.service.PostsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/core")
@RequiredArgsConstructor
public class PostsController {

    private final PostsService postsService;

    @PostMapping
    public ResponseEntity<PostsDto> createPost(@RequestBody PostsCreateRequestDto postsCreateRequestDto) {
        PostsDto createdPost = postsService.createPost(postsCreateRequestDto);
        return new ResponseEntity<>(createdPost, HttpStatus.CREATED);
    }

    @GetMapping("/{postId}")
    public ResponseEntity<PostsDto> getPost(@PathVariable Long postId) {
        PostsDto post=postsService.getPostById(postId);
        return ResponseEntity.ok(post);
    }

    @GetMapping("/users/{userId}/allPosts")
    public  ResponseEntity<List<PostsDto>> getAllPostsOfUser(@PathVariable Long userId) {
        List<PostsDto> postsDtos = postsService.getAllPostsByUser(userId);
        return ResponseEntity.ok(postsDtos);
    }
}
