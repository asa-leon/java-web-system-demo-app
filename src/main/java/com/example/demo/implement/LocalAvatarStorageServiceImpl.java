package com.example.demo.implement;

import com.example.demo.service.AvatarStorageService;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@Primary
public class LocalAvatarStorageServiceImpl implements AvatarStorageService {

	// 保存先ディレクトリ
	private final String uploadDir = "uploads/avatars";

	@Override
	public String saveAvatar(MultipartFile file, Long userId) {

		if (file.isEmpty()) {
			throw new IllegalArgumentException("ファイルが空です。");
		}

		try {
			// 1. 保存先フォルダが存在しない場合は作成する
			Path uploadPath = Paths.get(uploadDir);
			if (!Files.exists(uploadPath)) {
				Files.createDirectories(uploadPath);
			}

			// 2. 元のファイル名から拡張子（.pngや.jpg）を取得する
			String originalFilename = file.getOriginalFilename();
			String extension = "";
			if (originalFilename != null && originalFilename.contains(".")) {
				extension = originalFilename.substring(originalFilename.lastIndexOf("."));
			}

			// 3. 重複しないユニークなファイル名を生成する（例：ユーザーID + ランダム文字列 + 拡張子）
			String filename = "user_" + userId + "_" + UUID.randomUUID().toString() + extension;

			// 4. 実際にファイルをディスクに保存する
			Path filePath = uploadPath.resolve(filename);
			Files.copy(file.getInputStream(), filePath);

			// 5. フロントエンドからアクセスするためのURLパスを返す
			return "/images/avatars/" + filename;

		} catch (IOException e) {
			throw new RuntimeException("ファイルの保存に失敗しました。", e);
		}
	}
}
