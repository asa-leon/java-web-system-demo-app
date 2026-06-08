package com.example.demo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;



@Controller
public class HelloController {

    private final UserService userService;
    // ロガーの定義（クラスごとに作成するのが一般的）
    private static final Logger log = LoggerFactory.getLogger(HelloController.class);

    HelloController(UserService userService) {
        this.userService = userService;
    }

    // record機能（クラスを定義せずにデータを構造化できる）
    public record UserInfo(String name, String role, int experience) {}

    @GetMapping("/")
    public String hello(Model model) {
        
        log.info("リクエストを受け付けました。メインページを表示します。");

        // オブジェクトを作って渡す
        var user = new UserInfo("higako", "Software Developer", 2026);

        // 変数の値をログに出すときは{}を使う（ベストプラクティス）
        log.debug("生成されたユーザー情報: {}", user);
        
        model.addAttribute("user", user);
        model.addAttribute("message", "モダンな Java 開発環境へようこそ！");
        return "hello";
    }

    // HTML側から入力を受け取るためのエンドポイント
    @PostMapping("/submit")
    public String onSubmit(@RequestParam("username") String name, Model model) {
        log.info("フォームから名前をうけとりました： {}", name);

        // DBに保存
        userService.saveUser(name);

        // DBから全員の情報を取得
        model.addAttribute("users", userService.getAllUsers());
        model.addAttribute("message", "保存完了しました。");

        return "hello";
    }
}