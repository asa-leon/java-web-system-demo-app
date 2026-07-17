package com.example.demo.config;

import java.nio.file.Path;
import java.nio.file.Paths;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {
	
	@Override
	public void addResourceHandlers(ResourceHandlerRegistry registry) {
		// 保存先フォルダ（uploads/avatars）の絶対パスを取得
		Path uploadDir = Paths.get("uploads/avatars");
		String uploadPath = uploadDir.toFile().getAbsolutePath();

		// URLの「/images/avatars/**」へのアクセスを、実際のローカルフォルダ「uploads/avatars」にマッピングする
		registry.addResourceHandler("/images/avatars/**")
			.addResourceLocations("file:" + uploadPath + "/");
	}
}
