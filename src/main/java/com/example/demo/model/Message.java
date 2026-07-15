package com.example.demo.model;

import java.time.LocalDateTime;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;


@Entity
@Table(name = "messages")
@Data   
public class Message {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	// 送信者
	@ManyToOne
	@JoinColumn(name = "sender_id", nullable = false)
	private User sender;

	// 受信者
	@ManyToOne
	@JoinColumn(name = "recipient_id", nullable = false)
	private User recipient;

	// メッセージ本文
	@Column(nullable = false, length = 1000)
	private String content;

	// 送信日時（自動で現在時刻が入る様に設定
	@Column(name = "created_at", nullable = false, updatable = false)
	private LocalDateTime createdAt = LocalDateTime.now();
}
