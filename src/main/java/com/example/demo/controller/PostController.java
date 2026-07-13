package com.example.demo.controller;

import com.example.demo.repository.LikeRepository;
import jakarta.servlet.http.HttpSession;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.demo.model.Comment;
import com.example.demo.model.Post;
import com.example.demo.model.PostNotification;
import com.example.demo.model.User;
import com.example.demo.model.Tag;
import com.example.demo.repository.CommentRepository;
import com.example.demo.repository.PostRepository;
import com.example.demo.repository.TagRepository;
import com.example.demo.repository.UserRepository;
import com.example.demo.repository.VoteRepository;
import com.example.demo.repository.NotificationsRepository;

//import org.springframework.data.jpa.domain.Specification;
//import org.springframework.data.domain.Sort;

@Controller
@RequiredArgsConstructor // これを書くことでconstructor(this.xx = xx)を書かなくて済む
public class PostController {

	private final LikeRepository likeRepository;
	private final PostRepository postRepository;
	private final UserRepository userRepository;
	private final CommentRepository commentRepository;
	private final TagRepository tagRepository;

	// Vote-1: レポジトリをインジェクションしておく
	private final VoteRepository voteRepository;
	private final NotificationsRepository notificationsRepository;

	// 投稿一覧を表示する窓口
	@GetMapping("/posts")
	public String postList(
			@RequestParam(name = "keyword", required = false) String keyword,
			HttpSession session,
			HttpServletResponse response,
			Model model) {

		// ブラウザにキャッシュさせずに、チャット画面からブラウザで戻った時に必ずサーバーを叩かせる
		response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
		response.setHeader("Pragma", "no-cache");
		response.setDateHeader("Expires", 0);
		
		// ==========================================
		// 🛠【開発用ショートカット】自動ログイン処理
		// ==========================================
		if (session.getAttribute("loginUser") == null) {
			// 例：import.sql でIDが「2」のサンプルユーザー（higakoなど）を強制的に取得
			userRepository.findById(2L).ifPresent(devUser -> {
				session.setAttribute("loginUser", devUser);
			});
		}
		// ==========================================		

		// セッションからユーザー情報を取得
		User sessionUser = (User) session.getAttribute("loginUser");

		// ログインしている場合のみ、最新のユーザー情報をDBから取得してモデルに渡す
		if (sessionUser != null) {
			userRepository.findById(sessionUser.getId())
					.ifPresent(currentUser -> {
						model.addAttribute("loginUser", currentUser);

						// 全体に表示する投稿一覧を取得（これはログイン有無に関係なく実行）
						List<Post> posts;

						if (keyword != null && !keyword.trim().isEmpty()) {
							posts = postRepository.findByContentContainingIgnoreCaseOrderByCreatedAtDesc(keyword);
							model.addAttribute("keyword", keyword); // 画面にキーワードを保持させる
						} else {
							// キーワードがなければ、今まで通り全件を最新順で取得
							posts = postRepository.findAllByOrderByCreatedAtDesc();
						}

						// Vote-2: 各投稿にいいね数と、Vote数及び自分がVoteしたかの情報を付与する
						for (Post post : posts) {

							// 1-1. 総いいね数をカウントしてセット
							post.setLikeCount(likeRepository.countByPost(post));

							// 1-2. ログイン中の場合、自分がいいねしたかを判定してセット
							if (currentUser != null) {
								post.setLikedByMe(likeRepository.existsByUserAndPost(currentUser, post));
							} else {
								post.setLikedByMe(false); // 念のためユーザーがいない場合は一律false
							}

							// 総Vote数をカウントしてセット
							post.setVoteCount(voteRepository.countByPost(post));

							// ログイン中の場合、自分がVoteしたかを判定してセット
							if (currentUser != null) {
								post.setVotedByMe(voteRepository.existsByUserAndPost(currentUser, post));
							} else {
								post.setVotedByMe(false); // 念のためユーザーがいない場合は一律false
							}
						}

					});
		} else {
			// 未ログインの場合は、画面側で制御できるように null を明示するか、ログイン状態をfalseにする
			model.addAttribute("loginUser", null);
		}

		// 全体に表示する投稿一覧を取得（これはログイン有無に関係なく実行）
		List<Post> posts;

		if (keyword != null && !keyword.trim().isEmpty()) {
			posts = postRepository.findByContentContainingIgnoreCaseOrderByCreatedAtDesc(keyword);
			model.addAttribute("keyword", keyword); // 画面にキーワードを保持させる
		} else {
			// キーワードがなければ、今まで通り全件を最新順で取得
			posts = postRepository.findAllByOrderByCreatedAtDesc();
		}

		model.addAttribute("posts", posts);

		/*
		 * ドット繋ぎで書く場合のコード
		 * // 1.最新順（降順）ソートの条件を用意
		 * Sort sort = Sort.by(Sort.Direction.DESC, "createdAt");
		 * 
		 * // 2.Repositoryで作ったパーツをドットで繋いで「仕様書（spec）を完成させる
		 * Specification<Post> spec = Specification
		 * .where(PostRepository.containsContent(keyword)));
		 * // もし他にも条件があれば、ここに .and(PostRepository.isUser(1L)) の様にドットで繋いで行ける
		 * 
		 * // 3.条件付きの findAll を発動
		 * List<Post> posts =postRepository.findAll(spec, sort);
		 * 
		 * model.addAttribute("posts", posts);
		 * model.addAttribute("keyword", keyword);
		 */

		// 現在のタブの初期値をallにしておく
		model.addAttribute("currentTab", "all");

		// トレンド上位5件を画面に渡す
		model.addAttribute("trends", tagRepository.findTop5Trends());

		return "post_list";
	}

	// 投稿フォーム画面を表示する
	@GetMapping("/posts/new")
	public String showNewPostForm(Model model) {
		model.addAttribute("post", new Post());

		// セレクトボックスで選べるように、全ユーザーの一覧を画面に渡す
		model.addAttribute("users", userRepository.findAll());

		// データベースからすべてのタグを取得して、画面に渡す
		List<Tag> allTags = tagRepository.findAll();
		model.addAttribute("allTags", allTags);

		return "post_form";
	}

	// 投稿をデータベースに保存する
	@PostMapping("/posts/create")
	public String createPost(
			@Valid Post post,
			BindingResult bindingResult,
			HttpSession session,
			Model model) {

		// セッションからログインユーザーを取得
		User currentUser = (User) session.getAttribute("loginUser");
		if (currentUser == null) {
			return "redirect:/login";
		}

		// バリデーションエラー（140文字超過など）があれば元の画面に戻す
		if (bindingResult.hasErrors()) {
			// エラーで戻った時も、タグ一覧を再セットしてあげる
			model.addAttribute("allTags", tagRepository.findAll());

			return "post_form";
		}

		post.setUser(currentUser);

		// MARK: ハッシュタグ抽出ロジック
		String content = post.getContent();
		List<Tag> tagList = new ArrayList<>();

		if (content != null && !content.isEmpty()) {
			// 正規表現で「#」から始まる単語を抽出（全角・半角対応）
			Pattern pattern = Pattern.compile("[#＃][A-Za-z0-9ぁ-んァ-ヶ一-龠ー_]+");
			Matcher matcher = pattern.matcher(content);

			while (matcher.find()) {
				// 先頭の「#」を消して、アルファベットは小文字に統一
				String tagName = matcher.group().substring(1).toLowerCase();

				// データベースに既存のタグがあるか探し、なければ新しく保存して取得
				Tag tag = tagRepository.findByName(tagName)
						.orElseGet(() -> {
							Tag newTag = new Tag();
							newTag.setName(tagName);
							return tagRepository.save(newTag);
						});

				// リスト内での重複を防いで追加
				if (!tagList.contains(tag)) {
					tagList.add(tag);
				}
			}
		}

		// 投稿オブジェクトに、抽出したタグのリストをセット（これで中間テーブルに自動保存される）
		post.setTags(tagList);

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
	public String userPostList(
			@PathVariable("userId") Long userId,
			HttpSession session,
			Model model) {

		// 1. 指定されたユーザーIDの投稿だけを最新順で取得
		List<Post> posts = postRepository.findByUserIdOrderByCreatedAtDesc(userId);

		// 2. セッションからユーザー情報を取得
		User sessionUser = (User) session.getAttribute("loginUser");

		// タイムラインと同じ構造：ログインしている場合のみ最新のユーザー情報をDBから取得してモデルに渡す
		if (sessionUser != null) {

			userRepository.findById(sessionUser.getId())
				.ifPresent(currentUser -> {
					model.addAttribute("loginUser", currentUser);

					// 各投稿にいいね・Vote情報を付与
					for (Post post : posts) {
						post.setLikeCount(likeRepository.countByPost(post));
						post.setLikedByMe(likeRepository.existsByUserAndPost(currentUser, post));

						post.setVoteCount(voteRepository.countByPost(post));
						post.setVotedByMe(voteRepository.existsByUserAndPost(currentUser, post));
					}
				});
		} else {

			// 未ログインの場合はnullを明示し、投稿の自分のアクションフラグを一律falseにする
			model.addAttribute("loginUser", null);
			for (Post post : posts) {
				post.setLikeCount(likeRepository.countByPost(post));
				post.setLikedByMe(false);
				post.setVoteCount(voteRepository.countByPost(post));
				post.setVotedByMe(false);
			}
		}

		// 2. 画面のタイトル等に表示するために、そのユーザーの名前も取得（任意）
		if (!posts.isEmpty()) {
			model.addAttribute("targetUser", posts.get(0).getUser());
		} else {
			// 投稿が空の場合でも動くように、ユーザー自身を直接取得してモデルに入れると安全
			model.addAttribute("targetUser", userRepository.findById(userId).orElse(null));
		}

		model.addAttribute("posts", posts);
		return "user_post_list";
	}

	// フォローしているユーザーの投稿だけを表示するタイムライン
	@GetMapping("/posts/following")
	public String followingPostList(HttpSession session, Model model) {

		// 1. セッションからログインユーザーを取得
		User me = (User) session.getAttribute("loginUser");
		if (me == null) {
			return "redirect:/login";
		}

		// 常に最新の状態のフォローリストを参照する為、念の為DBから引き直す
		me = userRepository.findById(me.getId()).orElse(me);

		// 2. 自分がフォローしているユーザーの「IDのリスト」を作る
		List<Long> followingUserIds = me.getFollowing().stream()
				.map(User::getId)
				.toList();

		List<Post> posts;
		if (followingUserIds.isEmpty()) {
			// まだ誰もフォローしていない場合は、からのリストを返す（エラー回避）
			posts = new java.util.ArrayList<>();
		} else {
			// 3. フォローしている人のIDリストをリポジトリに渡して、投稿を取得する
			posts = postRepository.findByUserIdInOrderByCreatedAtDesc(followingUserIds);
		}

		// 4. 画面にデータを渡す
		model.addAttribute("posts", posts);
		model.addAttribute("loginUser", me);
		model.addAttribute("currentTab", "following"); // 今どっちのタブにいるかを判定するためのフラグ

		// トレンド上位5件を画面に渡す
		model.addAttribute("trends", tagRepository.findTop5Trends());

		return "post_list"; // 画面は新しく作らず、既存の post_list.html を使いまわす。
	}

	// 特定の投稿の詳細画面を表示する
	@GetMapping("/posts/{id}")
	public String postDetail(
			@PathVariable("id") Long id,
			HttpSession session,
			Model model) {

		// 1. 対象の投稿を取得
		Post post = postRepository.findById(id)
				.orElseThrow(() -> new IllegalArgumentException("指定された投稿が見つかりません:" + id));

		// セッションからログインユーザーを取得（最新情報をDBから引き直す）
		User sessionUser = (User) session.getAttribute("loginUser");
		User currentUser = null;
		if (sessionUser != null) {
			currentUser = userRepository.findById(sessionUser.getId()).orElse(null);
		}

		// 取得した1件の投稿にいいね数と投票数の情報を詰め込む
		post.setLikeCount(likeRepository.countByPost(post));
		post.setVoteCount(voteRepository.countByPost(post));

		if (currentUser != null) {
			post.setLikedByMe(likeRepository.existsByUserAndPost(currentUser, post));
			post.setVotedByMe(voteRepository.existsByUserAndPost(currentUser, post));
			model.addAttribute("loginUser", currentUser); // 常に最新のユーザーを渡す
		} else {
			post.setLikedByMe(false);
			post.setVotedByMe(false);
			model.addAttribute("loginUser", null);
		}

		// 2. 画面にデータを渡す
		model.addAttribute("post", post);
		// Post.java に @OneToMany を書いたので、JPAが自動で紐づくコメントを一緒に持ってきてくれる
		model.addAttribute("comments", post.getComments());

		return "post_detail";
	}

	// コメントを投稿する処理
	@PostMapping("/posts/{id}/comments")
	public String createComment(
			@PathVariable("id") Long id,
			@RequestParam("content") String content,
			HttpSession session) {

		// 1. セッションからは「箱」としてユーザーを取得
		User sessionUser = (User) session.getAttribute("loginUser");
		// セッション自体が空、またはDBから最新のユーザーが取得できない場合はログインへ
		if (sessionUser == null) {
			return "redirect:/login";
		}

		// 最新のユーザー情報をDBから引き直す（Lazy/セッション不整合対策）
		User me = userRepository.findById(sessionUser.getId())
			.orElseThrow(() -> new IllegalArgumentException("User not found"));

		// 2. どの投稿に対するコメントか、親を取得
		Post post = postRepository.findById(id)
				.orElseThrow(() -> new IllegalArgumentException("投稿が見つかりません"));

		// 3. コメントオブジェクトを生成してデータをセット
		Comment comment = new Comment();
		comment.setContent(content);
		comment.setPost(post);
		comment.setUser(me); // 最新のユーザーオブジェクトを紐づける

		// 4. データベースに保存
		commentRepository.save(comment);

		// 5. 通知機能：自作自演（自分の投稿に自分でコメント）でなければ、投稿の作者宛にコメント通知を作成して保存
		if (!post.getUser().getId().equals(me.getId())) {
			PostNotification notification = new PostNotification();
			notification.setType(PostNotification.PostNotificationType.COMMENT);
			notification.setSender(me);
			notification.setReceiver(post.getUser());
			notification.setPost(post);

			notificationsRepository.save(notification);
		}

		// 6. 書き込みが終わったら、元の詳細画面にリダイレクトで戻る
		return "redirect:/posts/" + id;
	}

	// タグに基づく投稿を取得して画面に渡す処理
	@GetMapping("/tags/{tagName}")
	public String showPostsByTag(@PathVariable("tagName") String tagName, Model model) {
		// 1. 指定されたタグ名がついている投稿だけをリポジトリから取得
		List<Post> taggedPosts = postRepository.findByTagsName(tagName);

		// 2. タイムライン（post_list.html）と同じ変数名「posts」で画面に渡す
		model.addAttribute("posts", taggedPosts);

		// 3. 今何のタグで絞り込んでいるかを画面に表示するために、タグ名も渡しておく
		model.addAttribute("currentTag", tagName);

		// 4. 新しい画面を作らず、既存の「post_list.html」をそのまま使いまわす

		// 割り込み：トレンド上位5件を画面に渡す
		model.addAttribute("trends", tagRepository.findTop5Trends());

		return "post_list";
	}
}