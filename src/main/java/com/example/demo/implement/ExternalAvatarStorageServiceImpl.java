package com.example.demo.implement;

import com.example.demo.service.AvatarStorageService;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class ExternalAvatarStorageServiceImpl implements AvatarStorageService {
	
	@Override
	public String saveAvatar(MultipartFile file, Long userId) {
		// クラウドストレージに保存する処理
		return "https://external-storage.com/avatars/default.png";
	}
}
