package com.example.demo.repository;

import com.example.demo.model.Notification;
import com.example.demo.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface NotificationsRepository extends JpaRepository<Notification, Long> {
    
    // 特定のユーザー（仮に自分）宛の通知を、最新日時順（降順）で全件取得する
    List<Notification> findByReceiverOrderByCreatedAtDesc(User receriver);

    // 特定のユーザーの「未読（isRead = false」の通知数をカウントする（ヘッダーバッヂ用）
    long countByReceiverAndIsReadFalse(User receiver);

    // 自分（Receiver）宛の通知をIDの降順（新しい順）で全件取得する
    List<Notification> findByReceiverOrderByIdDesc(User receiver);

    // メッセージ通知（MessageNotification）の未読数をカウント
    @Query("SELECT COUNT(n) FROM Notification n WHERE n.receiver = :receiver AND n.isRead = false AND TYPE(n) = MessageNotification")
    long countUnreadMessageNotifications(@Param("receiver") User receiver);

    // ポスト関連通知（PostNotification）の未読数をカウント
    @Query("SELECT COUNT(n) FROM Notification n WHERE n.receiver = :receiver AND n.isRead = false AND TYPE(n) = PostNotification")
    long countUnreadPostNotifications(@Param("receiver") User receiver);
}
