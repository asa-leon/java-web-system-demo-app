package com.example.demo;

import com.example.demo.model.Bill;
import com.example.demo.model.Comment;
import com.example.demo.model.Committee;
import com.example.demo.model.User;
import com.example.demo.repository.CommentRepository;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
// デフォルトのH2自動置き換えを無効化し、本物のMySQLに接続させる
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
// src/test/resources/application-test.propertiesを読み込ませる
@ActiveProfiles("test")
public class CommentRepositoryTest {
	
	@Autowired
	private CommentRepository commentRepository;

	@Autowired
	private TestEntityManager entityManager;

	@Test
	@DisplayName("質疑通告が正常に保存され、答弁状態の更新と未答弁カウントが正しく機能すること")
	void testQuestionAndAnsweredFlow() {
		// --- Given（準備） ---
		User author = new User();
		author.setUserId("author_user");
		author.setName("提案議員");
		author.setEmail("author@example.com");
		author.setPassword("password");
		entityManager.persist(author);

		User questioner = new User();
		questioner.setUserId("questioner_user");
		questioner.setName("質問議員");
		questioner.setEmail("questioner@example.com");
		questioner.setPassword("password");
		entityManager.persist(questioner);

		// 1. 委員会（Committee）を生成して永続化
		Committee committee = new Committee();
		committee.setName("内閣委員会");
		entityManager.persist(committee);

		Bill bill = new Bill();
		bill.setTitle("AI倫理法案");
		bill.setDescription("AI活用のガイドラインを定める");
		bill.setUser(author);
		bill.setCommittee(committee);
		entityManager.persist(bill);

		// 1. 質疑通告の投稿
		Comment question = new Comment();
		question.setContent("第3条の解釈について質疑します。");
		question.setBill(bill);
		question.setUser(questioner);
		question.setQuestion(true);
		question.setAnswered(false);
		entityManager.persist(question);

		entityManager.flush();
		
		// --- When & Then 1: 未答弁件数の検証 ---
		long unreadCount = commentRepository.countByBillIdAndIsQuestionTrueAndIsAnsweredFalse(bill.getId());
		assertThat(unreadCount).isEqualTo(1);

		// --- Given 2: 答弁の投稿
		Comment answer = new Comment();
		answer.setContent("第3条は以下の様に解釈されます。");
		answer.setBill(bill);
		answer.setUser(author);
		answer.setParent(question);

		// 親コメント（質疑）を答弁済みに変更
		question.setAnswered(true);
		entityManager.persist(question);
		entityManager.persist(answer);

		entityManager.flush();

		// --- When & Then 2: 答弁後の未答弁件数の検証
		long updatedCount = commentRepository.countByBillIdAndIsQuestionTrueAndIsAnsweredFalse(bill.getId());
		assertThat(updatedCount).isEqualTo(0);
		assertThat(question.isAnswered()).isTrue();
	}
}
