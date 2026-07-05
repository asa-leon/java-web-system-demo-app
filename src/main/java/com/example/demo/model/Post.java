package com.example.demo.model;

import java.util.List;
import java.util.ArrayList;

import jakarta.persistence.Entity;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OrderBy;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "posts")
@Getter
@Setter
public class Post {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "投稿内容は必須です")
    @Size(max = 140, message = "投稿は140文字以内で入力してください")
    private String content;

    // 「多（Post）対 1（User）」のリレーションを設定
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false, foreignKey = @ForeignKey(foreignKeyDefinition = "FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE")) // データベース側で user_id という外部キー（FK）を作って紐づける
    private User user;

    // 投稿日時
    @jakarta.persistence.Column(name = "created_at", updatable = false)
    private java.time.LocalDateTime createdAt;
    // 保存される直前に、自動で現在日時をセットするアノテーション
    @jakarta.persistence.PrePersist
    protected void onCreate() {
        this.createdAt = java.time.LocalDateTime.now();
    }

    // データベースのカラムではなく、画面表示時に対象ユーザーに応じて動的に詰めるフィールドを利用
    @Transient
    private long likeCount; // 総いいね数

    @Transient
    private boolean likedByMe; // 自分がいいねしているか

    // 1つの投稿に対する複数のコメント（一対多）
    // cascade = CascadeType.ALL にすることで、投稿が削除されたらそのコメントも自動で全削除される
    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("createdAt ASC") // 画面に出すときに自動で古い順に並び替える
    private List<Comment> comments;

    // 投稿に紐づく複数のハッシュタグ（多対多）
    @ManyToMany
    @JoinTable(
        name = "post_tags", // これが自動生成される「中間テーブル」の名前になる
        joinColumns = @JoinColumn(name = "post_id"), // 自分（Post）側のID列
        inverseJoinColumns = @JoinColumn(name = "tag_id") // 相手（Tag）側のID列
    )
    private List<Tag> tags = new ArrayList<>();

    // Vote（いいね）を一時的に取り扱う為の記述
    @Transient // DBのカラムには作らない、画面表示用の一時的なフィールド
    private long voteCount;

    @Transient // 現在ログインしているユーザーが、この投稿を「いいね」しているかどうかのフラグ
    private boolean votedByMe;
}
