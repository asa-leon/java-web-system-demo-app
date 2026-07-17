package com.example.demo.repository;

import com.example.demo.model.Vote;
import com.example.demo.model.Bill;
import com.example.demo.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface VoteRepository extends JpaRepository<Vote, Long> {
    
    // 特定のユーザーが、特定の投稿にすでにVoteしているかを探す
    Optional<Vote> findByUserAndPost(User user, Bill post);

    // 投稿ごとに、いくつのVote（いいね）がついているかを集計する
    long countByPost(Bill post);

    // 特定のユーザーがすでにVoteしているかどうかの判定用
    boolean existsByUserAndPost(User user, Bill post);
}
