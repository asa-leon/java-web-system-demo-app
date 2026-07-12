package com.example.demo.controller;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.demo.model.Message;
import com.example.demo.model.MessageNotification;
import com.example.demo.model.User;
import com.example.demo.repository.MessageRepository;
import com.example.demo.repository.NotificationsRepository;
import com.example.demo.repository.UserRepository;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class MessageController {

	private final MessageRepository messageRepository;
	private final UserRepository userRepository;
	private final NotificationsRepository notificationsRepository;
	private final HttpSession session;

	// 特定の相手とのチャット画面を表示する
	@GetMapping("/messages/chat/{talkToUserId}")
	public String chatWithUser(@PathVariable("talkToUserId") Long talkToUserId, Model model) {

		// 1. セッションからログインユーザーを取得
		User loginUser = (User) session.getAttribute("loginUser");

		// ログインしていなければ強制的にログイン画面へ戻す
		if (loginUser == null) {
			return "redirect:/login";
		}

		// 2. チャット相手のユーザー情報を取得
		User talkToUser = userRepository.findById(talkToUserId)
				.orElseThrow(() -> new IllegalArgumentException("無効なユーザーIDです:" + talkToUserId));

		// 3. テストで実証済みの「findChatHistory」を使って、二人の間のメッセージ履歴を取得
		List<Message> chatHistory = messageRepository.findChatHistory(loginUser.getId(), talkToUser.getId());

		// 4. 画面（HTML）にデータを渡す
		model.addAttribute("loginUser", loginUser);
		model.addAttribute("talkToUser", talkToUser);
		model.addAttribute("chatHistory", chatHistory);

		return "chat";
	}

	// メッセージを送信する（保存処理）
	@PostMapping("/messages/send")
	public String sendMessage(
			@RequestParam("recipientId") Long recipientId,
			@RequestParam("content") String content) {

		User loginUser = (User) session.getAttribute("loginUser");
		
		if (loginUser == null) {
			return "redirect:/login";
		}

		User recipient = userRepository.findById(recipientId)
				.orElseThrow(() -> new IllegalArgumentException("無効な受信者IDです:" + recipientId));

		// 1. メッセージオブジェクトを作成して保存
		Message message = new Message();
		message.setSender(loginUser);
		message.setRecipient(recipient);
		message.setContent(content);

		messageRepository.save(message);

		// 2. 通知機能：メッセージ受信通知を裏で作成して保存する
		MessageNotification notification = new MessageNotification();
		notification.setSender(loginUser);
		notification.setReceiver(recipient);
		notification.setRead(false);

		notificationsRepository.save(notification);

		// 送信後は、元のチャット画面にリダイレクト（再読み込み）する
		return "redirect:/messages/chat/" + recipientId;
	}
}
