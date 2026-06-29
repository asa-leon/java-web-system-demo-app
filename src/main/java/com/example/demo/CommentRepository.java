package com.example.demo;

import com.example.demo.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    
    // 特定の投稿ID（post.id）に紐づくコメントを、作成日時の古い順（昇順）で取得する
    List<Comment> findByPostIdOrderByCreatedAtAsc(Long postId);
}
