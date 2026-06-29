package com.example.demo.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.demo.model.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    // これだけで save(), findAll(), delete()が使えるようになる。
    User findByEmail(String email);
}
