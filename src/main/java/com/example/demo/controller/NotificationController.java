package com.example.demo.controller;

import com.example.demo.model.Notification;
import com.example.demo.model.User;
import com.example.demo.repository.NotificationsRepository;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationsRepository notificationsRepository;

    @GetMapping
    public String showNotifications(HttpSession session, Model model) {

        // セッションからログインユーザーを取得
		User currentUser = (User) session.getAttribute("loginUser");
		if (currentUser == null) {
			return "redirect:/login";
		}

        // 1. 自分（Receiver）宛の通知をIDの降順（新しい順）で全件取得
        List<Notification> notifications = notificationsRepository.findByReceiverOrderByIdDesc(currentUser);

        // 2. 取得した通知の中に「未読（isReadがfalse）」の物があれば、すべて既読（true）にする
        for (Notification n : notifications) {
            if (!n.isRead()) { // もし未読なら
                n.setRead(true); // 既読状態にセット
            }
        }

        // 3. 状態を変更した通知たちをデータベースに一括で上書き保存する
        notificationsRepository.saveAll(notifications);
        
        model.addAttribute("notifications", notifications);
        return "notification_list";
    }
}
