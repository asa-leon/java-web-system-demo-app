package com.example.demo.controller;

import com.example.demo.model.Committee;
import com.example.demo.model.Bill;
import com.example.demo.model.User;
import com.example.demo.repository.CommitteeRepository;
import com.example.demo.repository.BillRepository;
import com.example.demo.repository.TagRepository;
import com.example.demo.repository.UserRepository;
import com.example.demo.repository.LikeRepository;
import com.example.demo.repository.VoteRepository;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/committees")
@RequiredArgsConstructor
public class CommitteeController {
	
	private final CommitteeRepository committeeRepository;
	private final BillRepository billRepository;
	private final TagRepository tagRepository;
	private final UserRepository userRepository;
	private final LikeRepository likeRepository;
	private final VoteRepository voteRepository;

	// 委員会一覧（案内画面）を表示
	@GetMapping
	public String index(Model model) {
		List<Committee> committees = committeeRepository.findAll();
		model.addAttribute("committees",committees);

		// サイドバー用のトレンド等
		model.addAttribute("trends", tagRepository.findTop5Trends());

		return "committees/index";
	}

	// 特定の委員会に所属する法案一覧を表示
	@GetMapping("/{id}")
	public String show(@PathVariable("id") Long id, HttpSession session, Model model) {

		// 1. 対象の委員会を取得
		Committee committee = committeeRepository.findById(id)
			.orElseThrow(() -> new IllegalArgumentException("指定された委員会が見つかりません: " + id));
		
		// 2. 委員会に属する法案一覧を取得
		List<Bill> bills = billRepository.findByCommitteeIdOrderByCreatedAtDesc(id);

		// 3. セッションユーザーの取得と各Billへの状態付与を行う
		User sessionUser = (User) session.getAttribute("loginUser");

		if (sessionUser != null) {
			userRepository.findById(sessionUser.getId()).ifPresent(currentUser -> {
				model.addAttribute("loginUser", currentUser);

				for (Bill bill : bills) {
					bill.setLikeCount(likeRepository.countByBill(bill));
					bill.setLikedByMe(likeRepository.existsByUserAndBill(currentUser, bill));
					bill.setVoteCount(voteRepository.countByBill(bill));
					bill.setVotedByMe(voteRepository.existsByUserAndBill(currentUser, bill));
				}
			});
		} else {
			model.addAttribute("loginUser", null);
			for (Bill bill : bills) {
				bill.setLikeCount(likeRepository.countByBill(bill));
				bill.setLikedByMe(false);
				bill.setVoteCount(voteRepository.countByBill(bill));
				bill.setVotedByMe(false);
			}
		}

		// 4. モデルへのデータ格納
		model.addAttribute("committee", committee);
		model.addAttribute("bills", bills);
		model.addAttribute("trends", tagRepository.findTop5Trends());

		return "committees/show";
	}
}
