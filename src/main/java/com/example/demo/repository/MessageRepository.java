package com.example.demo.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import com.example.demo.model.Message;

@Repository
public interface MessageRepository extends JpaRepository<Message, Long> {
	
	// AliceとBobの2者間のチャット履歴だけを取得するクエリ
	// （送信者がAで受信者がB、または、送信者がBで受信者がAのメッセージを、作成日時順に並べる）
	@Query("SELECT m FROM Message m WHERE " +
		"(m.sender.id = :user1Id AND m.recipient.id = :user2Id) OR " +
		"(m.sender.id = :user2Id AND m.recipient.id = :user1Id) " +
		"ORDER BY m.createdAt ASC")
		List<Message> findChatHistory(@Param("user1Id") Long user1Id, @Param("user2Id") Long user2Id);
}
