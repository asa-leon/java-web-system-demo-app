package com.example.demo.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
// ⭕ データベース上でこのクラスを表す識別値を指定
@DiscriminatorValue("MESSAGE")
@Getter
@Setter
public class MessageNotification extends Notification {
	// 将来的に「メッセージへのリアクション通知」などを拡張したくなったら、
	// ここに特有の型（Enum）やプロパティ（紐づくMessageオブジェクトなど）を追加できる
}
