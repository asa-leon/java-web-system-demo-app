package com.example.demo.model;

// 保存データ取り扱いライブラリ群
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Column;

// バリデーション用ライブラリ群
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
// フォロー用（多対多）ライブラリ群
import jakarta.persistence.ManyToMany;
import jakarta.persistence.JoinTable;
import jakarta.persistence.JoinColumn;
import java.util.Set;

import java.util.HashSet;


@Entity
@Table(name = "users")
@Getter
@Setter
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // ログインID（一意制約、空文字禁止、15文字以内
    @NotBlank(message = "ユーザーIDは必須入力です")
    @Size(min = 2, max = 30, message = "ユーザーIDは2文字以上30文字以内で入力してください")
    @Column(name = "user_id", unique = true, nullable = false, length = 30)
    private String userId;

    // 空文字・スペースのみを禁止し、文字数を2~20文字に制限
    @NotBlank(message = "名前は必須入力です")
    @Size(min = 2, max = 30, message = "名前は2文字以上30文字以内で入力してください")
    @Column(nullable = false, length = 30)
    private String name;

    // 空文字を禁止し、かつ正しいメールアドレスの形式（@があるか等）かチェック
    @NotBlank(message = "メールアドレスは必須入力です")
    @Email(message = "正しいメールアドレスの形式で入力してください\"")
    @Column(unique = true, nullable = false)
    private String email;

    // ハッシュ化パスワードを格納するフィールド
    @NotBlank(message = "パスワードは必須入力です")
    private String password;

    private String icon = "🚀"; // デフォルト値を設定（画像パスの指定もできる）

    // 自分がフォローしているユーザーのリスト
    @ManyToMany
    @JoinTable(
        name = "user_follows", // 生成される中間テーブルの名前
        // follower_id（自分のLong型主キー 'id'）が、following_id（相手のLong型主キー'id'）をフォローする
        joinColumns = @JoinColumn(name = "follower_id", referencedColumnName = "id"),
        inverseJoinColumns = @JoinColumn(name = "following_id", referencedColumnName = "id") // フォロー相手のID
    )
    private Set<User> following = new HashSet<>();

    // 自分をフォローしてくれているユーザー（フォロワー）のリスト
    @ManyToMany(mappedBy = "following") // 上記の変数「following」と表裏一体であることを示す
    private Set<User> followers = new HashSet<>();

    @Column(nullable = true, length = 512) // ファイル名が長くなってもおさまる様に上限を少し上げておく
    private String avatarUrl;

    // フォローを追加する時のメソッド
    public void follow(User user) {
        this.following.add(user);
    }

    // フォローを外す時のメソッド
    public void unfollow(User user) {
        this.following.remove(user);
    };
}
