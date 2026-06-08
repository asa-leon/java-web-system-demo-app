package com.example.demo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    // これだけで save(), findAll(), delete()が使えるようになる。
    User findByEmail(String email);
}
