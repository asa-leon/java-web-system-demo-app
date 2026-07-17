package com.example.demo.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "likes") // データベース上に新しく作られる中間テーブル
@Getter
@Setter
public class Like {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	// いいねしたユーザー
	@ManyToOne
	@JoinColumn(name = "user_id", nullable = false, 
        foreignKey = @ForeignKey(foreignKeyDefinition = "FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE"))
    private User user;

	// いいねされた投稿
	@ManyToOne
	@JoinColumn(name = "bill_id", nullable = false,
		foreignKey = @ForeignKey(foreignKeyDefinition = "FOREIGN KEY (bill_id) REFERENCES bills(id) ON DELETE CASCADE"))
	private Bill bill;
}
