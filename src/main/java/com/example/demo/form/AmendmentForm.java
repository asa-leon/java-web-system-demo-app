package com.example.demo.form;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AmendmentForm {
	
	@NotBlank(message = "修正案の件名を入力してください")
	@Size(max = 100, message = "件名は100文字以内で入力してください")
	private String title;

	@NotBlank(message = "修正の理由・要旨を入力してください")
	@Size(max = 1000, message = "修正理由は1000文字以内で入力してください")
	private String reason;

	@NotBlank(message = "修正後の本文案を入力してください")
	private String content;
}
