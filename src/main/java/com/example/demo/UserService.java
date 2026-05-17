package com.example.demo;

import org.springframework.stereotype.Service;
import com.example.demo.HelloController.UserInfo;

@Service
public class UserService {
    public UserInfo getUser() {
        //  本来はここでDBから取得したり計算したりする
        return new UserInfo("higako", "Engineer", 2026);
    }
}
