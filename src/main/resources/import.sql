INSERT INTO users (user_id, name, email, password, icon) VALUES ('gemini', 'Gemini', 'gemini@example.com', '$2a$08$iBx1mr0xyL7UpU8qIHNaPOm6y07YKjxDgUA2CTe3HdFpgo3s4Ool6', '🚀');
INSERT INTO users (user_id, name, email, password, icon) VALUES ('higako', 'Higako', 'higako@example.com', '$2a$08$iBx1mr0xyL7UpU8qIHNaPOm6y07YKjxDgUA2CTe3HdFpgo3s4Ool6', '🐱');

INSERT INTO posts (content, user_id, created_at) VALUES ('こんにちは！', (SELECT id FROM users WHERE name = 'Gemini'), CURRENT_TIMESTAMP());
INSERT INTO posts (content, user_id, created_at) VALUES ('私はひがこです。', (SELECT id FROM users WHERE name = 'Higako'), CURRENT_TIMESTAMP());

INSERT INTO user_follows (follower_id, following_id) VALUES (2, 1);