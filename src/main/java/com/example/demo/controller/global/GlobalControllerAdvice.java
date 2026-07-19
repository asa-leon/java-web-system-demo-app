package com.example.demo.controller.global;

import com.example.demo.model.User;
import com.example.demo.repository.NotificationsRepository;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.ui.Model;

@ControllerAdvice // 全てのコントローラーに裏で割り込むアノテーション
@RequiredArgsConstructor
public class GlobalControllerAdvice {
    
    private final NotificationsRepository notificationsRepository;

    @ModelAttribute // 全ての画面のModelに自動的に値を注入する
    public void getUnreadNotificationCount(HttpSession session, Model model) {
        
        // セッションから現在のログインユーザーのオブジェクトを取得する
        User currentUser = (User) session.getAttribute("loginUser");

        if (currentUser != null) {

            // 分離した未読数をそれぞれ取得して格納
            long unreadMessageCount = notificationsRepository.countUnreadMessageNotifications(currentUser);
            long unreadBillCount = notificationsRepository.countUnreadBillNotifications(currentUser);

            model.addAttribute("unreadMessageCount", unreadMessageCount);
            model.addAttribute("unreadBillCount", unreadBillCount);

            // 既存のHTMLが崩れない様、全体の未読数（合算）も残しておく
            model.addAttribute("unreadCount", unreadMessageCount + unreadBillCount);
        } else {
            model.addAttribute("unreadMessageCount", 0L);
            model.addAttribute("unreadBillCount", 0L);
            model.addAttribute("unreadCount", 0L);
        }
    }
}
