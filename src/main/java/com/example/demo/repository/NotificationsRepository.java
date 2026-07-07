package com.example.demo.repository;

import com.example.demo.model.Notification;
import com.example.demo.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface NotificationsRepository extends JpaRepository<Notification, Long> {
    
    // 特定のユーザー（仮に自分）宛の通知を、最新日時順（降順）で全件取得する
    List<Notification> findByReceiverOrderByCreatedAtDesc(User receriver);

    // 特定のユーザーの「未読（isRead = false」の通知数をカウントする（ヘッダーバッヂ用）
    long countByReceiverAndIsReadFalse(User receiver);
}
