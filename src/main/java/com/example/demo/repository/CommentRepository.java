package com.example.demo.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.model.Comment;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    
    // 特定の投稿ID（bill.id）に紐づくコメントを、作成日時の古い順（昇順）で取得する
    List<Comment> findByBillIdOrderByCreatedAtAsc(Long billId);
}
