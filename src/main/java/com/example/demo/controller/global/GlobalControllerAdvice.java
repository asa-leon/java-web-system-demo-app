package com.example.demo.controller.global;

import java.util.List;
import com.example.demo.model.User;
import com.example.demo.model.Notification;
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

            // リポジトリの基本メソッドで全件取得し、Java側で安全にカウントする
            List<Notification> allNotifications = notificationsRepository.findByReceiverOrderByCreatedAtDesc(currentUser);

            // 分離した未読数をそれぞれ取得して格納
            long unreadMessageCount = allNotifications.stream()
                .filter(n -> !n.isRead() && n.isMessageNotification())
                .count();

            long unreadBillCount = allNotifications.stream()
                .filter(n -> !n.isRead() && n.isBillNotification())
                .count();

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
