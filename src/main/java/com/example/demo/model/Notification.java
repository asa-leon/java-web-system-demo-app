package com.example.demo.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;

@Entity
@Table(name = "notifications")
@Getter
@Setter
public class Notification {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 通知の種類
    // 1. Enumで定義
    public enum NotificationType {
        LIKE, VOTE, COMMENT
    }
    // 2. @Enumerated以下でテーブルに追加する状態を作る
    @Enumerated(EnumType.STRING)
    @Column(name = "notification_type", nullable = false)
    private NotificationType type;

    // 通知を受ける人（投稿の作者など）
    @ManyToOne
    @JoinColumn(name = "receiver_id", nullable = false,
        foreignKey = @ForeignKey(foreignKeyDefinition = "FOREIGN KEY (receiver_id) REFERENCES users(id) ON DELETE CASCADE")
    )
    private User receiver;

    // アクションを起こした人（ボタンを押した人など）
    @ManyToOne
    @JoinColumn(name = "sender_id", nullable = false,
        foreignKey = @ForeignKey(foreignKeyDefinition = "FOREIGN KEY (sender_id) REFERENCES users(id) ON DELETE CASCADE")
    )
    private User sender;

    // どの投稿に対するアクションか
    @ManyToOne
    @JoinColumn(name = "post_id", nullable = false,
        foreignKey = @ForeignKey(foreignKeyDefinition = "FOREIGN KEY (post_id) REFERENCES posts(id) ON DELETE CASCADE")
    )
    private Post post;

    // 既読フラグ（未読：false, 既読：true）
    @Column(name = "is_read", nullable = false)
    private boolean isRead = false;

    // 通知発生日時
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }
}
