package com.example.demo.repository;

import com.example.demo.model.Vote;
import com.example.demo.model.Bill;
import com.example.demo.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface VoteRepository extends JpaRepository<Vote, Long> {
    
    // 特定のユーザーが、特定の提案にすでにVoteしているかを探す
    Optional<Vote> findByUserAndBill(User user, Bill bill);

    // 提案毎に、いくつのVote（投票）がついているかを集計する
    long countByBill(Bill bill);

    // 特定のユーザーがすでにVoteしているかどうかの判定用
    boolean existsByUserAndBill(User user, Bill bill);
}
