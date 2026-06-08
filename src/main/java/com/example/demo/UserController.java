package com.example.demo;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PathVariable; // URLの可変部分（パス）を扱うために必要
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestBody;




@Controller
public class UserController {

    private final UserRepository userRepository;

    // @Autowiredを使って、SpringにUserRepositoryの準備を任せる（依存性の注入：ID）
    @Autowired
    public UserController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @GetMapping("/users")
    public String showUserList(Model model) {
        
        // DBから全権取得（SQLは自動発行される）
        List<User> userList = userRepository.findAll();

        // ターミナルにデバッグ用に中身を出力してみる
        for (User user : userList) {
            System.out.println("ユーザ名： " + user.getName() + ", メール： " + user.getEmail());
        }

        // Thymeleafの画面にデータを渡す
        model.addAttribute("users", userList);

        return "user_list"; // user_list.htmlを表示する

    }

    // 1. 登録フォーム画面を表示する（GETリクエスト）
    @GetMapping("/users/new")
    public String showCreationForm(Model model) {
        // フォームと紐づけるための中身が空のUserオブジェクトを渡す
        model.addAttribute("user", new User());
        return "user_form";
    }

    // 2. 新規登録用の保存窓口：フォームから送信されたデータを保存する（POSTリクエスト）
    @PostMapping("/users/create")
    public String createUser(User user) {
        
        // 防衛策：もし画面から意図しないIDが送られてきて、かつDBに既に存在していたらブロックする
        if (user.getId() != null && userRepository.existsById(user.getId())) {
            throw new IllegalStateException("エラー：新規登録ですが、既に存在するID（"+ user.getId() + "）が指定されたため処理を中断しました");
        }

        userRepository.save(user);
        
        // 保存が終わったら、ユーザー一覧画面（/users）にリダイレクトさせる
        return "redirect:/users";
    }

    // 3. 編集・更新用の保存窓口
    @PostMapping("/users/update")
    public String updateUser(User user) {
        
        // 防御策：逆に、編集なのに「DBに存在しないID」だったら不正アクセスとして弾く
        if (user.getId() == null || !userRepository.existsById(user.getId())) {
            throw new IllegalArgumentException("エラー：存在しないユーザーの更新はできません。");
        }
        
        userRepository.save(user);
        return "redirect:/users";
    }
    
    // 4. 編集画面を表示する（URLの末尾にあるIDを @PathVariable で受け取る）
    @GetMapping("/users/edit/{id}")
    public String showEditForm(@PathVariable("id") Long id, Model model) {
        // IDを元に、データベースから既存のユーザー情報を1件だけ取得
        // 見つからなかった場合は例外を投げる（今回は簡易的に例外処理を記述）
        User user = userRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Invalid user Id:" + id));

        // 取得した「データが入っているUserオブジェクト」を画面に渡す
        model.addAttribute("user", user);
            
        return "user_form"; // -> 新規登録で使ったフォーム（user_form.html）を再利用
    }

    // 5. 削除用の窓口
    @PostMapping("/users/delete/{id}")
    public String deleteUser(@PathVariable("id") Long id) {
        
        // 安全対策：念のため、本当に存在するIDかどうかチェック
        if (!userRepository.existsById(id)) {
            throw new IllegalArgumentException("エラー：存在しないユーザーは削除できません（ID: " + id + "）");
        }

        // 指定されたIDのデータを削除
        userRepository.deleteById(id);
        
        // 削除が終わったら、一覧画面にリダイレクトして最新の状態にする
        return "redirect:/users";
    }
}
