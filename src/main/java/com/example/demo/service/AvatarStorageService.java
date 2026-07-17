package com.example.demo.service;

import org.springframework.web.multipart.MultipartFile;

// 1. まずは「画像を保存して、そのURLを返す」という約束事「インターフェース」を作る
public interface AvatarStorageService {
	String saveAvatar(MultipartFile file, Long userId); 
}