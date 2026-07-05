package com.example.demo.repository;

import com.example.demo.model.Like;
import com.example.demo.model.Post;
import com.example.demo.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface LikeRepository extends JpaRepository<Like, Long> {
    
    // 特定のユーザーが、特定の投稿にいいねしているかを探す
    Optional<Like> findByUserAndPost(User user, Post post);

    // 該当する投稿の総いいね数を数える
    long countByPost(Post post);

    // 特定のユーザーがすでにVoteしているかどうかの判定用
    boolean existsByUserAndPost(User user, Post post);
}
