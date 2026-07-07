package com.example.demo.controller.async;

import com.example.demo.model.Post;
import com.example.demo.model.User;
import com.example.demo.model.Vote;
import com.example.demo.model.Notification;
import com.example.demo.repository.PostRepository;
import com.example.demo.repository.UserRepository;
import com.example.demo.repository.VoteRepository;
import com.example.demo.repository.NotificationsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Map;


@RestController
@RequestMapping("/api/posts/{postId}/vote")
@RequiredArgsConstructor
public class VoteApiController {
    
    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final VoteRepository voteRepository;
    private final NotificationsRepository notificationsRepository;

    @PostMapping
    public ResponseEntity<?> toggleVote(@PathVariable("postId") Long postId) {
        
        // 1. 対象の投稿を探す
        Post post = postRepository.findById(postId)
            .orElseThrow(() -> new IllegalArgumentException("Invalid post ID"));

        // 2-1. 現在のログインユーザー（仮にID 2：自分を設定）を取得
		User currentUser = userRepository.findById(2L) 
			.orElseThrow(() -> new IllegalStateException("User not found"));

        // 2-2. html側で自分自身への投票禁止を回避された場合の処理
        if (post.getUser().getId().equals(currentUser.getId())) {
            // 処理を進行させずにここで静かに弾く
            return ResponseEntity.badRequest().body("自分の投稿にはアクションできません。");
        }

        // 3. すでにVoteしているかチェック
        var existingVote = voteRepository.findByUserAndPost(currentUser, post);

        boolean voted;
        if (existingVote.isPresent()) {
            // すでにVoteがあれば「いいね解除」
            voteRepository.delete(existingVote.get());
            voted = false;
        } else {
            // なければいいね登録
            Vote vote = new Vote();
            vote.setUser(currentUser);
            vote.setPost(post);
            voteRepository.save(vote);
            voted = true;

            // 通知機能： 投票（Vote）通知を裏で作成して保存する
            if (!post.getUser().getId().equals(currentUser.getId())) {
                Notification notification = new Notification();
                notification.setType(Notification.NotificationType.VOTE); // タイプ：VOTE
                notification.setSender(currentUser);
                notification.setReceiver(post.getUser());
                notification.setPost(post);

                notificationsRepository.save(notification);
            }
        }

        // 最新の総Vote数を数えなおす
        long newCount = voteRepository.countByPost(post);

        // フロントエンドに結果を返す（JSON）
        return ResponseEntity.ok(Map.of(
            "voted", voted,
            "voteCount", newCount
        ));
    }
    
}
