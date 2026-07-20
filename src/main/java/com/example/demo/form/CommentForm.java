package com.example.demo.form;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CommentForm {
	
	@NotBlank(message = "コメントを入力してください")
	@Size(max = 1000, message = "コメントは1,000文字以内で入力してください")
	private String content;
}
