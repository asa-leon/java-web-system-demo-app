package com.example.demo;

// 保存データ取り扱いライブラリ群
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

// バリデーション用ライブラリ群
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

// フォロー用（多対多）ライブラリ群
import jakarta.persistence.ManyToMany;
import jakarta.persistence.JoinTable;
import jakarta.persistence.JoinColumn;
import java.util.Set;
import java.util.HashSet;


@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 空文字・スペースのみを禁止し、文字数を2~20文字に制限
    @NotBlank(message = "名前は必須入力です")
    @Size(min = 2, max = 20, message = "名前は2文字以上20文字以内で入力してください")
    private String name;

    // 空文字を禁止し、かつ正しいメールアドレスの形式（@があるか等）かチェック
    @NotBlank(message = "メールアドレスは必須入力です")
    @Email(message = "正しいメールアドレスの形式で入力してください\"")
    private String email;

    private String icon = "🚀"; // デフォルト値を設定（画像パスの指定もできる）

    // 自分がフォローしているユーザーのリスト
    @ManyToMany
    @JoinTable(
        name = "user_follows", // 生成される中間テーブルの名前
        joinColumns = @JoinColumn(name = "user_id"), // 自分自身のID
        inverseJoinColumns = @JoinColumn(name = "follow_id") // フォロー相手のID
    )
    private Set<User> following = new HashSet<>();

    // 自分をフォローしてくれているユーザー（フォロワー）のリスト
    @ManyToMany(mappedBy = "following") // 上記の変数「following」と表裏一体であることを示す
    private Set<User> followers = new HashSet<>();


    // --- ゲッターとセッター ---
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }   

    public Set<User> getFollowing() {
        return following;
    }

    public void setFollowing(Set<User> following) {
        this.following = following;
    }

    public Set<User> getFollowers() {
        return followers;
    }

    public void setFollowers(Set<User> followers) {
        this.followers = followers;
    }

    // フォローを追加する時のメソッド
    public void follow(User user) {
        this.following.add(user);
    }

    // フォローを外す時のメソッド
    public void unfollow(User user) {
        this.following.remove(user);
    };
}
