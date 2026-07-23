package com.example.demo.form;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CommentForm {
	
	@NotBlank(message = "コメントを入力してください")
	@Size(max = 1000, message = "コメントは1,000文字以内で入力してください")
	private String content;

	// 質疑通告として送信するかどうか
	private boolean question = false;

	// 答弁（返信）対象の親コメントID（通常の投稿時はnull）
	private Long parentId;
}
