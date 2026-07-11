package com.example.demo;

import static org.assertj.core.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.model.Message;
import com.example.demo.model.User;
import com.example.demo.repository.MessageRepository;
import com.example.demo.repository.UserRepository;

@SpringBootTest
@Transactional // テスト毎にデータをロールバックして汚さないようにする
public class DirectMessageTests {

    @Autowired
    private MessageRepository messageRepository;

    @Autowired
    private UserRepository userRepository;

    private User userA;
    private User userB;
    private User userC;

    @BeforeEach
    void setUp() {
        // テスト用のユーザーを3人作成してDBに保存
        userA = new User();
        userA.setName("Alice");
        userA.setUserId("alice_test");
        userA.setEmail("alice@example.com");
        userA.setPassword("password");
        userRepository.save(userA);

        userB = new User();
        userB.setName("Bob");
        userB.setUserId("bob_test");
        userB.setEmail("bob@example.com");
        userB.setPassword("password");
        userRepository.save(userB);

        userC = new User();
        userC.setName("Charlie");
        userC.setUserId("charlie_test");
        userC.setEmail("charlie@example.com");
        userC.setPassword("password");
        userRepository.save(userC);
    }

    @Test
    @DisplayName("1. メッセージが正しく送信（保存）できること")
    void testSendMessage() {
        
        // AliceからBobへメッセージを作成
        Message message = new Message();
        message.setSender(userA);
        message.setRecipient(userB);
        message.setContent("こんにちは、Bob！");

        // 保存
        Message savedMessage = messageRepository.save(message);

        // 検証
        assertThat(savedMessage.getId()).isNotNull();
        assertThat(savedMessage.getContent()).isEqualTo("こんにちは、Bob！");
        assertThat(savedMessage.getSender().getName()).isEqualTo("Alice");
        assertThat(savedMessage.getRecipient().getName()).isEqualTo("Bob");
    }

    @Test
    @DisplayName("2. 二人だけのメッセージ履歴が正しく取得できること")
    void testGetChatHistory() {
        
        // Alice -> Bob のメッセージ
        Message m1 = new Message();
        m1.setSender(userA);
        m1.setRecipient(userB);
        m1.setContent("味方だよ");
        messageRepository.save(m1);
        
        // Bob -> Alice のメッセージ
        Message m2 = new Message();
        m2.setSender(userB);
        m2.setRecipient(userA);
        m2.setContent("ありがとう");
        messageRepository.save(m2);
        
        // 関係ない Charlie -> Bob のメッセージ
        Message m3 = new Message();
        m3.setSender(userC);
        m3.setRecipient(userB);
        m3.setContent("秘密の話");
        messageRepository.save(m3);

        // 【ここを今後実装する】AliceとBobの間のメッセージだけを取得するリポジトリのメソッドを呼び出す
        // 例：findChatHistory(userA.getId(), userb.getId())
        List<Message> history = messageRepository.findChatHistory(userA.getId(), userB.getId());

        // 検証：取得されたのは2件だけで、Charlieのメッセージ（m3）は含まれないこと
        assertThat(history).hasSize(2);
        assertThat(history).extracting("content").containsExactlyInAnyOrder("味方だよ", "ありがとう");
        assertThat(history).extracting("content").doesNotContain("秘密の話");
    }
}
