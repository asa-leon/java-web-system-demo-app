package com.example.demo.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.demo.model.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    // これだけで save(), findAll(), delete()が使えるようになる。
    User findByEmail(String email);

    // ユーザーIDまたはメールアドレスで検索する（安全のためOptionalで返す）
    Optional<User> findByUserIdOrEmail(String userId, String email);
}
