package com.example.demo.form;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AmendmentForm {
	
	@NotNull(message = "対象の法案IDは必須です")
	private Long billId;

	@NotBlank(message = "修正案のタイトルを入力してください")
	@Size(max = 100, message = "タイトルは100文字以内で入力してください")
	private String title;

	@NotBlank(message = "修正内容・提案理由を入力してください")
	@Size(max = 2000, message = "内容は2,000文字以内で入力してください")
	private String description;
}
