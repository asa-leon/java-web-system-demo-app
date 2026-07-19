package com.example.demo.controller.async;

import com.example.demo.model.Bill;
import com.example.demo.model.User;
import com.example.demo.model.Vote;
import com.example.demo.model.BillNotification;
import com.example.demo.repository.BillRepository;
import com.example.demo.repository.VoteRepository;
import com.example.demo.repository.NotificationsRepository;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
@RequestMapping("/api/bills/{billId}/vote")
@RequiredArgsConstructor
public class VoteApiController {
    
    private final BillRepository billRepository;
    private final VoteRepository voteRepository;
    private final NotificationsRepository notificationsRepository;

    @PostMapping
    public ResponseEntity<?> toggleVote(
        @PathVariable("billId") Long billId,
        HttpSession session) {
        
        // 0. セッションからログインユーザーを取得
        User currentUser = (User) session.getAttribute("loginUser");
        if (currentUser == null) {

            // APIでデータを返す処理の場合は、セッション切れの際は401エラーを返す
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("ログインが必要です");
        }
        // 1. 対象の投稿を探す
        Bill bill = billRepository.findById(billId)
            .orElseThrow(() -> new IllegalArgumentException("Invalid bill ID"));

        // 2-2. html側で自分自身への投票禁止を回避された場合の処理
        if (bill.getUser().getId().equals(currentUser.getId())) {
            // 処理を進行させずにここで静かに弾く
            return ResponseEntity.badRequest().body("自分の投稿にはアクションできません。");
        }

        // 3. すでにVoteしているかチェック
        var existingVote = voteRepository.findByUserAndBill(currentUser, bill);

        boolean voted;
        if (existingVote.isPresent()) {
            // すでにVoteがあれば「いいね解除」
            voteRepository.delete(existingVote.get());
            voted = false;
        } else {
            // なければいいね登録
            Vote vote = new Vote();
            vote.setUser(currentUser);
            vote.setBill(bill);
            voteRepository.save(vote);
            voted = true;

            // 通知機能： 投票（Vote）通知を裏で作成して保存する
            if (!bill.getUser().getId().equals(currentUser.getId())) {
                BillNotification notification = new BillNotification();
                notification.setType(BillNotification.BillNotificationType.VOTE); // タイプ：VOTE
                notification.setSender(currentUser);
                notification.setReceiver(bill.getUser());
                notification.setBill(bill);

                notificationsRepository.save(notification);
            }
        }

        // 最新の総Vote数を数えなおす
        long newCount = voteRepository.countByBill(bill);

        // フロントエンドに結果を返す（JSON）
        return ResponseEntity.ok(Map.of(
            "voted", voted,
            "voteCount", newCount
        ));
    }
    
}
