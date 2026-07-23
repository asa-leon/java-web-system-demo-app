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

    // どの当行に対するコメントか（Billとの多対一リレーション）
    @ManyToOne
    @JoinColumn(name = "bill_id", nullable = false)
    private Bill bill;

    // 質疑通告フラグ（true: 質疑通告 / false: 通常コメント
    @Column(name = "is_question", nullable = false)
    private boolean isQuestion = false;

    // 答弁済みフラグ（true: 答弁完了 / false: 答弁待ち）
    @Column(name = "is_answered", nullable = false)
    private boolean isAnswered = false;

    // どの質疑（親コメント）に対する答弁か
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    private Comment parent;

    // データベース保存時に自動で日時を設定する
    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }
}