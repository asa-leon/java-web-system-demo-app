package com.example.demo.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
// ⭕ データベース上でこのクラスを表す識別値を指定
@DiscriminatorValue("POST")
@Getter
@Setter
public class PostNotification extends Notification {
	
	// 投稿への通知特有のEnum
	public enum PostNotificationType {
		LIKE, VOTE, COMMENT
	}

	@Enumerated(EnumType.STRING)
	@Column(name = "notification_type")
	private PostNotificationType type;

	// 投稿への通知にのみ必須となるpostカラム
	@ManyToOne
	@JoinColumn(name = "post_id",
		foreignKey = @ForeignKey(foreignKeyDefinition = "FOREIGN KEY (post_id) REFERENCES posts(id) ON DELETE CASCADE")
	)
	private Post post;
}
