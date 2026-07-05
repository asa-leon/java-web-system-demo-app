INSERT INTO users (name, email, icon) VALUES ('Gemini', 'gemini@example.com', '🚀');
INSERT INTO users (name, email, icon) VALUES ('Higako', 'higako@example.com', '🐱');

INSERT INTO posts (content, user_id, created_at) VALUES ('こんにちは！', (SELECT id FROM users WHERE name = 'Gemini'), CURRENT_TIMESTAMP());
INSERT INTO posts (content, user_id, created_at) VALUES ('私はひがこです。', (SELECT id FROM users WHERE name = 'Higako'), CURRENT_TIMESTAMP());

INSERT INTO user_follows (user_id, follow_id) VALUES (2, 1);