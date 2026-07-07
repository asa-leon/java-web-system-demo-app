package com.example.demo.controller.global;

import com.example.demo.model.User;
import com.example.demo.repository.NotificationsRepository;
import com.example.demo.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

@ControllerAdvice // 全てのコントローラーに裏で割り込むアノテーション
@RequiredArgsConstructor
public class GlobalControllerAdvice {
    
    private final NotificationsRepository notificationsRepository;
    private final UserRepository userRepository;

    @ModelAttribute("unreadCount") // 全ての画面（Model）に自動的に "unreadCount" という変数を注入する
    public long getUnreadNotificationCount() {
        
        // 現在のログインユーザー（仮でID:2のHigako）
        User currentUser = userRepository.findById(2L).orElse(null);

        if (currentUser != null) {
            // 未読数をカウントして返す
            return notificationsRepository.countByReceiverAndIsReadFalse(currentUser);
        }

        return 0;
    }

}
