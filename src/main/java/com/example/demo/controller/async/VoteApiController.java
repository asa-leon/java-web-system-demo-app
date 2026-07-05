package com.example.demo.controller.async;

import com.example.demo.model.Post;
import com.example.demo.model.User;
import com.example.demo.model.Vote;
import com.example.demo.repository.PostRepository;
import com.example.demo.repository.VoteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Map;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


@RestController
@RequestMapping("/api/posts/{postId}/vote")
@RequiredArgsConstructor
public class VoteApiController {
    
    private final PostRepository postRepository;
    private final VoteRepository voteRepository;
    // private final UserRepository userRepository; // ユーザー取得用（現在、未使用）

    @PostMapping
    public ResponseEntity<?> toggleVote(@PathVariable("postId") Long postId) {
        
        // 1. 対象の投稿を探す
        Post post = postRepository.findById(postId)
            .orElseThrow(() -> new IllegalArgumentException("Invalid post ID"));

        // 2. 本来はログインユーザーを取得（仮にID 1のユーザーを設定）
        User currentUser = new User(); currentUser.setId(1L);

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
