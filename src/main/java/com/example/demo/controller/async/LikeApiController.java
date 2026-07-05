package com.example.demo.controller.async;

import com.example.demo.model.Post;
import com.example.demo.model.User;
import com.example.demo.model.Like;
import com.example.demo.repository.LikeRepository;
import com.example.demo.repository.PostRepository;
import com.example.demo.repository.UserRepository;

import lombok.RequiredArgsConstructor;
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
	private final UserRepository userRepository;
	private final LikeRepository likeRepository;

	@PostMapping
	public ResponseEntity<?> toggleLikeAsync(@PathVariable("id") Long id) {
		
		// 1. 対象の投稿を探す
		Post post = postRepository.findById(id)
			.orElseThrow(() -> new IllegalArgumentException("Invalid post ID: " + id));
		
		// 2. 現在のログインユーザー（仮でID:2のユーザー）を取得
		User me = userRepository.findById(2L) 
			.orElseThrow(() -> new IllegalStateException("User not found"));

		// 3. 既に自分がこの投稿にいいねしているかチェック
		Optional<Like> existingLike = likeRepository.findByUserAndPost(me, post);

		boolean liked;
		if (existingLike.isPresent()) {
			// 3-1. 既にあれば「いいね解除」：レコードを削除
			likeRepository.delete(existingLike.get());
			liked = false;
		} else {
			// 3-2. なければ「いいね登録」：レコードを新規保存
			Like newLike = new Like();
			newLike.setUser(me);
			newLike.setPost(post);
			likeRepository.save(newLike);
			liked = true;
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
