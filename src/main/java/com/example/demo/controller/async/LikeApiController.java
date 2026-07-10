package com.example.demo.controller.async;

import com.example.demo.model.Post;
import com.example.demo.model.User;
import com.example.demo.model.Like;
import com.example.demo.model.Notification;
import com.example.demo.repository.LikeRepository;
import com.example.demo.repository.PostRepository;
import com.example.demo.repository.UserRepository;
import com.example.demo.repository.NotificationsRepository;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/posts/{id}/like") // 分かりやすく /api/を頭につけたURLにする
@RequiredArgsConstructor
public class LikeApiController {
    
	private final PostRepository postRepository;
	private final LikeRepository likeRepository;
	private final NotificationsRepository notificationRepository;

	@PostMapping
	public ResponseEntity<?> toggleLikeAsync(
		@PathVariable("id") Long id,
		HttpSession session) {

		// セッションからログインユーザーを取得
		User currentUser = (User) session.getAttribute("loginUser");
		if (currentUser == null) {
			// セッション切れの場合は401エラーを返す
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("ログインが必要です");
		}
		
		// 1. 対象の投稿を探す
		Post post = postRepository.findById(id)
			.orElseThrow(() -> new IllegalArgumentException("Invalid post ID: " + id));

		// 3. 既に自分がこの投稿にいいねしているかチェック
		Optional<Like> existingLike = likeRepository.findByUserAndPost(currentUser, post);

		boolean liked;
		if (existingLike.isPresent()) {
			// 3-1. 既にあれば「いいね解除」：レコードを削除
			likeRepository.delete(existingLike.get());
			liked = false;
		} else {
			// 3-2. なければ「いいね登録」：レコードを新規保存
			Like newLike = new Like();
			newLike.setUser(currentUser);
			newLike.setPost(post);
			likeRepository.save(newLike);
			liked = true;

			// 通知機能： いいね通知を裏で作成して保存する
			// 自作自演（自分の投稿に自分でいいね）でなければ通知を送る
			if (!post.getUser().getId().equals(currentUser.getId())) {
				Notification notification = new Notification();
				notification.setType(Notification.NotificationType.LIKE); // タイプ：LIKE
				notification.setSender(currentUser); // アクションを起こした人（自分）
				notification.setReceiver(post.getUser()); // 通知を受ける人（投稿の作者）
				notification.setPost(post); // 対象の投稿

				notificationRepository.save(notification);
			}
		}

		// 4. 最新の総いいね数をカウントし直す
		long currentLikeCount = likeRepository.countByPost(post);

		// 5. フロントに「最新の数」と「自分がいいね状態か」をデータで返す
		return ResponseEntity.ok(Map.of(
			"likes", currentLikeCount,
			"liked", liked
		));
	}
}
