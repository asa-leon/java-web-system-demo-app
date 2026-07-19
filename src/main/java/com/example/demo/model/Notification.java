package com.example.demo.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;

@Entity
@Table(name = "notifications")
// クラスの継承戦略を「単一テーブル（SINGLE_TABLE）」に設定
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
// どの子クラスかを識別するためのカラム（デフォルト名：dtype）を定義
@DiscriminatorColumn(name = "dtype", discriminatorType = DiscriminatorType.STRING)
@Getter
@Setter
public class Notification {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // ⭕ NotificationType から MESSAGE を削除し、ここでは既存の LIKE, VOTE, COMMENT のみを残すか、
    // あるいは今後の拡張のために Enum 自体を各子クラスの役割に応じて整理する。
    // 今回は親クラスから type カラムごと排除し、クラスの型そのもので識別する形にスマート化する。

    // 通知を受ける人
    @ManyToOne
    @JoinColumn(name = "receiver_id", nullable = false,
        foreignKey = @ForeignKey(foreignKeyDefinition = "FOREIGN KEY (receiver_id) REFERENCES users(id) ON DELETE CASCADE")
    )
    private User receiver;

    // アクションを起こした人
    @ManyToOne
    @JoinColumn(name = "sender_id", nullable = false,
        foreignKey = @ForeignKey(foreignKeyDefinition = "FOREIGN KEY (sender_id) REFERENCES users(id) ON DELETE CASCADE")
    )
    private User sender;

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

    // 画面の出し分け用メソッド
    public boolean isBillNotification() {
        return this instanceof BillNotification;
    }

    public boolean isMessageNotification() {
        return this instanceof MessageNotification;
    }
}
