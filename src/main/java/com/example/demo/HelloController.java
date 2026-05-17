package com.example.demo;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HelloController {

    // record機能（クラスを定義せずにデータを構造化できる）
    public record UserInfo(String name, String role, int experience) {}

    @GetMapping("/")
    public String hello(Model model) {

        // オブジェクトを作って渡す
        var user = new UserInfo("higako", "Software Developer", 2026);

        model.addAttribute("user", user);
        model.addAttribute("message", "モダンな Java 開発環境へようこそ！");
        return "hello";
    }
}