package com.linkedin.posts_service.repository;

import com.linkedin.posts_service.entity.PostsLike;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

public interface PostLikesRepository extends JpaRepository<PostsLike,Long> {

    boolean existsByUserIdAndPostId(Long userId, Long postId);

    @Transactional
    void deleteByUserIdAndPostId(Long userId, Long postId);
}
