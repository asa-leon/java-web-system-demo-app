package com.example.demo;

import org.springframework.stereotype.Service;
import lombok.extern.slf4j.Slf4j;
import java.util.List;

@Slf4j
@Service
public class UserService {
    
    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    // 保存する処理
    public void saveUser(String name) {
        User user = new User();
        user.setName(name);
        userRepository.save(user);
        log.info("DBに保存しました: {}", name);
    }
    
    // 全員取得する処理
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }
}
