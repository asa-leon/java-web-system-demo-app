package com.example.demo.form;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BillForm {

	@NotBlank(message = "タイトルを入力してください")
	@Size(max = 100, message = "タイトルは100文字以内で入力してください")
	private String title;

	@NotBlank(message = "本文を入力してください")
	private String description;

	// 画面の<select>で選択された委員会のID
	@NotNull(message = "委員会を選択してください")
	private Long committeeId;
}
