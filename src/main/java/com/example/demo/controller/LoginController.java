package com.example.demo.controller;

import com.example.demo.model.User;
import com.example.demo.repository.UserRepository;
import com.example.demo.util.PasswordUtil;
import jakarta.servlet.http.HttpSession;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequiredArgsConstructor
public class LoginController {
	
	private final UserRepository userRepository;

	// 1. ログイン画面を表示する
	@GetMapping("/login")
	public String loginForm() {
		return "login";
	}

	// 2. ログイン処理を実行する窓口（POST）
	@PostMapping("/login")
	public String login(
		@RequestParam("loginId") String loginId,
		@RequestParam("password") String password,
		HttpSession session,
		RedirectAttributes redirectAttributes) {

		// DBからユーザーID、またはメールアドレスで検索
		User user = userRepository.findByUserIdOrEmail(loginId, loginId).orElse(null);

		// ユーザーが存在し、パスワードが一致するか検証
		if (user != null && PasswordUtil.checkPassword(password, user.getPassword())) {
			// 認証成功：セッションにログインユーザーのオブジェクトを「loginUser」という名前で丸ごと保存
			session.setAttribute("loginUser", user);
			return "redirect:/posts";
		}

		// 認証失敗：エラーメッセージを一時的に保持させてログイン画面にリダイレクト
		redirectAttributes.addFlashAttribute("error", "ユーザーID、Email、またはパスワードが正しくありません。");
		return "redirect:/login";
	}

	// 3. ログアウト処理を実行する窓口
	@GetMapping("/logout")
	public String logout(HttpSession session) {
		session.invalidate(); // 現在のセッションを完全に破棄（クリア）する
		return "redirect:/login";
	}
}
