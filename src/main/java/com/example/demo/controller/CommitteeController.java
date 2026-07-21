package com.example.demo.controller;

import com.example.demo.model.Committee;
import com.example.demo.model.Bill;
import com.example.demo.repository.CommitteeRepository;
import com.example.demo.repository.BillRepository;
import com.example.demo.repository.TagRepository;

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
	public String show(@PathVariable("id") Long id, Model model) {
		Committee committee = committeeRepository.findById(id)
			.orElseThrow(() -> new IllegalArgumentException("指定された委員会が見つかりません: " + id));
		
		// その委員会に提出された法案一覧を取得
		List<Bill> bills = billRepository.findByCommitteeIdOrderByCreatedAtDesc(id);

		model.addAttribute("committee", committee);
		model.addAttribute("bills", bills);
		model.addAttribute("trends", tagRepository.findTop5Trends());

		return "committees/show";
	}
}
