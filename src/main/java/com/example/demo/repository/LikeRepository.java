package com.example.demo.repository;

import com.example.demo.model.Like;
import com.example.demo.model.Bill;
import com.example.demo.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface LikeRepository extends JpaRepository<Like, Long> {
    
    // 特定のユーザーが、特定のて案にいいねしているかを探す
    Optional<Like> findByUserAndBill(User user, Bill bill);

    // 該当する投稿の総いいね数を数える
    long countByBill(Bill bill);

    // 特定のユーザーがすでにVoteしているかどうかの判定用
    boolean existsByUserAndBill(User user, Bill bill);
}
