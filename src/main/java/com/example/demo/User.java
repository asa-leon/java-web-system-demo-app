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

    // Getters and setters
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
}
