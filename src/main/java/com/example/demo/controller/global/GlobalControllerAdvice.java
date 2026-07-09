package com.example.demo.controller.global;

import com.example.demo.model.User;
import com.example.demo.repository.NotificationsRepository;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

@ControllerAdvice // 全てのコントローラーに裏で割り込むアノテーション
@RequiredArgsConstructor
public class GlobalControllerAdvice {
    
    private final NotificationsRepository notificationsRepository;

    @ModelAttribute("unreadCount") // 全ての画面（Model）に自動的に "unreadCount" という変数を注入する
    public long getUnreadNotificationCount(HttpSession session) {
        
        // セッションから現在のログインユーザーのオブジェクトを取得する
        User currentUser = (User) session.getAttribute("loginUser");

        if (currentUser != null) {
            // 未読数をカウントして返す
            return notificationsRepository.countByReceiverAndIsReadFalse(currentUser);
        }

        return 0;
    }
}
