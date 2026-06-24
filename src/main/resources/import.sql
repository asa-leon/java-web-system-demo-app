INSERT INTO users (name, email) VALUES ('Gemini', 'gemini@example.com');
INSERT INTO users (name, email) VALUES ('Higako', 'higako@example.com');

INSERT INTO posts (content, user_id) VALUES ('こんにちは！', (SELECT id FROM users WHERE name = 'Gemini'));
INSERT INTO posts (content, user_id) VALUES ('私はひがこです。', (SELECT id FROM users WHERE name = 'Higako'));
