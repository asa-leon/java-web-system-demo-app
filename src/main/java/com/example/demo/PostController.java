package com.example.demo;

import jakarta.validation.Valid;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
//import org.springframework.data.jpa.domain.Specification;
//import org.springframework.data.domain.Sort;

import org.springframework.web.bind.annotation.PathVariable;


@Controller
public class PostController {

    private final PostRepository postRepository;
    private final UserRepository userRepository;

    public PostController(PostRepository postRepository, UserRepository userRepository) {
        this.postRepository = postRepository;
        this.userRepository = userRepository;
    }

    // 投稿一覧を表示する窓口
    @GetMapping("/posts")
    public String postList(@RequestParam(name = "keyword", required = false) String keyword, Model model) {

        List<Post> posts;


        if (keyword != null && !keyword.trim().isEmpty()) {
            posts = postRepository.findByContentContainingIgnoreCaseOrderByCreatedAtDesc(keyword);
            model.addAttribute("keyword", keyword); // 画面にキーワードを保持させる
        } else {
            // キーワードがなければ、今まで通り全件を最新順で取得
            posts = postRepository.findAllByOrderByCreatedAtDesc();
        }

        model.addAttribute("posts", posts);
        
        /* ドット繋ぎで書く場合のコード
        // 1.最新順（降順）ソートの条件を用意
        Sort sort = Sort.by(Sort.Direction.DESC, "createdAt");

        // 2.Repositoryで作ったパーツをドットで繋いで「仕様書（spec）を完成させる
        Specification<Post> spec = Specification
            .where(PostRepository.containsContent(keyword)));
            // もし他にも条件があれば、ここに .and(PostRepository.isUser(1L)) の様にドットで繋いで行ける

        // 3.条件付きの findAll を発動
        List<Post> posts =postRepository.findAll(spec, sort);

        model.addAttribute("posts", posts);
        model.addAttribute("keyword", keyword);
         */

        return "post_list";
    }

    // 投稿フォーム画面を表示する
    @GetMapping("/posts/new")
    public String newPostForm(Model model) {
        model.addAttribute("post", new Post());

        // セレクトボックスで選べるように、全ユーザーの一覧を画面に渡す
        model.addAttribute("users", userRepository.findAll());

        return "post_form";
    }

    // 投稿をデータベースに保存する
    @PostMapping("/posts/create")
    public String createPost(
        @Valid Post post, 
        BindingResult bindingResult,
        @RequestParam("userId") Long userId, // 画面のセレクトボックスからuser_idを受け取る)
        Model model) {
        
        // バリデーションエラー（140文字超過など）があれば元の画面に戻す
        if (bindingResult.hasErrors()) {
            // 戻る前にもう一度ユーザー一覧をセットしてあげないと、セレクトボックスが空になる
            model.addAttribute("users", userRepository.findAll());

            return "post_form";
        }

        //MARK: 画面から選ばれたIDを使ってUserオブジェクトを取得
        User selectedUser = userRepository.findById(userId)
            .orElseThrow(() -> new IllegalArgumentException("ユーザーが見つかりません"));

        post.setUser(selectedUser);

        // データベースに保存
        postRepository.save(post);
        
        return "redirect:/posts";
    }

    // 投稿を削除する
    @PostMapping("/posts/{id}/delete")
    public String deletePost(@PathVariable("id") Long id) {
        
        // URLから受け取ったIDを使って、データベースから削除する
        postRepository.deleteById(id);
        
        return "redirect:/posts";
    }

    // 特定のユーザーの投稿一覧を表示するルート
    @GetMapping("/posts/user/{userId}")
    public String userPostList(@PathVariable("userId") Long userId, Model model) {

        // 1. 指定されたユーザーIDの投稿だけを最新順で取得
        List<Post> posts = postRepository.findByUserIdOrderByCreatedAtDesc(userId);

        // 2. 画面のタイトル等に表示するために、そのユーザーの名前も取得（任意）
        if (!posts.isEmpty()) {
            model.addAttribute("targetUser", posts.get(0).getUser());
        }

        model.addAttribute("posts", posts);
        return "user_post_list";
    }
    
    // いいね！ボタンを押したときの処理
    @PostMapping("/posts/{id}/like")
    public String likePost(@PathVariable("id") Long id) {
        // 1. いいねされた投稿をIDで探す（見つからなければエラー）
        Post post = postRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Invalid post Id:" + id));

        // 2. いいね数を+1する（エンティティ内のメソッドを呼び出す）
        post.incrementLikes();

        // 3. 状態が変わったらオブジェクトを上書き保存する（JPAのUpdate機能）
        postRepository.save(post);

        // 4. 処理が終わったら、元のタイムライン画面にリダイレクト（再表示）
        return "redirect:/posts";
    }
}