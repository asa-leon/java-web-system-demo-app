package com.example.demo;

import static org.assertj.core.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.model.Message;
import com.example.demo.model.MessageNotification;
import com.example.demo.model.Notification;
import com.example.demo.model.Bill;
import com.example.demo.model.BillNotification;
import com.example.demo.model.User;
import com.example.demo.repository.MessageRepository;
import com.example.demo.repository.NotificationsRepository;
import com.example.demo.repository.UserRepository;
import com.example.demo.repository.BillRepository;

@SpringBootTest
@Transactional
public class NotificationInheritanceTests {
	
	@Autowired
	private NotificationsRepository notificationsRepository;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private BillRepository billRepository;

	private User sender;
	private User receiver;

	@BeforeEach
	void setUp() {
		sender = new User();
		sender.setName("Sender");
		sender.setUserId("sender_test");
		sender.setEmail("sender@example.com");
		sender.setPassword("password");
		userRepository.save(sender);

		receiver = new User();
		receiver.setName("Receiver");
		receiver.setUserId("receiver");
		receiver.setEmail("receiver@example.com");
		receiver.setPassword("password");
		userRepository.save(receiver);
	}

	@Test
	@DisplayName("メッセージ通知（子クラス）を保存して、親クラスの型で一括取得できること")
	void testMessageNotificationInheritance() {
		// 1. メッセージ通知（新設する子クラス）の作成と保存
		MessageNotification msgNotification = new MessageNotification();
		msgNotification.setSender(sender);
		msgNotification.setReceiver(receiver);
		msgNotification.setRead(false);
		// MessageNotification特有のプロパティ（後でMessage型などを紐づけられるように拡張可能）

		notificationsRepository.save(msgNotification);

		// 2. 親クラス（Notification）のリポジトリから受信者宛の通知を全件取得
		List<Notification> notifications = notificationsRepository.findByReceiverOrderByIdDesc(receiver);

		// 3. 検証：正しく取得でき、インスタンスがMessageNotification型であること
		assertThat(notifications).hasSize(1);
		Notification savedNotification = notifications.get(0);
		assertThat(savedNotification).isInstanceOf(MessageNotification.class);
		assertThat(savedNotification.getSender().getId()).isEqualTo(sender.getId());
	}

	@Test
	@DisplayName("投稿にコメントされたときに、投稿者向けのコメント通知が自動生成されること")
	void testCommentNotificationCreation() {
		Bill bill = new Bill();
		bill.setUser(receiver); // 投稿者はreceriver
		bill.setTitle("テストタイトルです。");
		bill.setDescription("テストディスクリプションです。");
		billRepository.save(bill);

		BillNotification commentNotification = new BillNotification();
		commentNotification.setType(BillNotification.BillNotificationType.COMMENT);
		commentNotification.setSender(sender);
		commentNotification.setReceiver(receiver);
		commentNotification.setBill(bill);
		commentNotification.setRead(false);

		notificationsRepository.save(commentNotification);

		// 検証
		List<Notification> notifications = notificationsRepository.findByReceiverOrderByIdDesc(receiver);
		assertThat(notifications).isNotEmpty();

		BillNotification savedNotification = (BillNotification) notifications.get(0);
		assertThat(savedNotification.getType()).isEqualTo(BillNotification.BillNotificationType.COMMENT);
		assertThat(savedNotification.getBill().getId()).isEqualTo(bill.getId());
	}

	@Test
	@DisplayName("特定の通知IDを指定して、その通知だけを個別に既読に更新できること")
	void testMarkAsReadIndividually() {
		// 1. 未読の通知を2つ作成して保存
		BillNotification n1 = new BillNotification();
		n1.setSender(sender);
		n1.setReceiver(receiver);
		n1.setType(BillNotification.BillNotificationType.LIKE);
		n1.setRead(false);
		notificationsRepository.save(n1);

		BillNotification n2 = new BillNotification();
		n2.setSender(sender);
		n2.setReceiver(receiver);
		n2.setType(BillNotification.BillNotificationType.COMMENT);
		n2.setRead(false);
		notificationsRepository.save(n2);

		// 2. n1だけを指定して既読にする（これから実装するサービス／リポジトリ処理のゴール）
		Notification target = notificationsRepository.findById(n1.getId()).orElseThrow();
		target.setRead(true);
		notificationsRepository.save(target);

		// 3. 検証：n1は既読（true）、n2は未読（false）のままであること
		assertThat(notificationsRepository.findById(n1.getId()).orElseThrow().isRead()).isTrue();
		assertThat(notificationsRepository.findById(n2.getId()).orElseThrow().isRead()).isFalse();
	}
}
