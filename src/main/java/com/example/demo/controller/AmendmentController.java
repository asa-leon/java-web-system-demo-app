package com.example.demo.controller;

import com.example.demo.form.AmendmentForm;
import com.example.demo.model.User;
import com.example.demo.service.AmendmentService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequiredArgsConstructor
public class AmendmentController {
	
	private final AmendmentService amendmentService;

	/**
	 * 修正案の投稿処理
	 */
	@PostMapping("/amendments/create")
	public String createAmendment(
			@Valid @ModelAttribute("amendmentForm") AmendmentForm form,
			BindingResult bindingResult,
			HttpSession session,
			RedirectAttributes redirectAttributes) {

		User currentUser = (User) session.getAttribute("loginUser");
		if (currentUser == null) {
			return "redirect:/login";
		}

		// 入力エラーがある場合は、エラーメッセージを保持して元の法案詳細画面へリダイレクト
		if (bindingResult.hasErrors()) {
			redirectAttributes.addFlashAttribute("amendmentError", "入力内容に不備があります。タイトルと内容を確認してください。");
			return "redirect:/bills/" + form.getBillId();
		}

		try {
			amendmentService.createAmendment(form, currentUser);
			redirectAttributes.addFlashAttribute("successMessage", "修正案を提出しました。");
		} catch (Exception e) {
			redirectAttributes.addFlashAttribute("amendmentError", "修正案の提出に失敗しました: " + e.getMessage());
		}

		return "redirect:/bills/" + form.getBillId();
	}
}
