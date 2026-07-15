package com.example.demo.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name="comments")
@Getter
@Setter
public class Comment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    // 誰がコメントしたか（Userとの多対一リレーション）
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    // どの当行に対するコメントか（Postとの多対一リレーション）
    @ManyToOne
    @JoinColumn(name = "post_id", nullable = false)
    private Post post;

    // データベース保存時に自動で日時を設定する
    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }
}