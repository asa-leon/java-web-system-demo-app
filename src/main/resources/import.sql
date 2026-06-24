INSERT INTO users (name, email) VALUES ('Gemini', 'gemini@example.com');
INSERT INTO users (name, email) VALUES ('Higako', 'higako@example.com');

INSERT INTO posts (content, user_id, created_at, likes) VALUES ('こんにちは！', (SELECT id FROM users WHERE name = 'Gemini'), CURRENT_TIMESTAMP(), 0);
INSERT INTO posts (content, user_id, created_at, likes) VALUES ('私はひがこです。', (SELECT id FROM users WHERE name = 'Higako'), CURRENT_TIMESTAMP(), 0);
