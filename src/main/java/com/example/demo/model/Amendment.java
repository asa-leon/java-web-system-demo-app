package com.example.demo.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;

@Entity
@Table(name = "amendments")
@Getter
@Setter
public class Amendment {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	// 対象の元法案（親）
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "bill_id", nullable = false)
	private Bill bill;

	// 修正案の提出者
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id", nullable = false)
	private User user;

	// 修正案のタイトル
	@Column(nullable = false)
	private String title;

	// 修正の要旨・理由
	@Column(columnDefinition = "TEXT", nullable = false)
	private String reason;

	// 修正後の本文・条文案
	@Column(columnDefinition = "TEXT", nullable = false)
	private String content;

	// 提出日時
	@Column(name = "created_at", nullable = false, updatable = false)
	private LocalDateTime createdAt = LocalDateTime.now();
}
