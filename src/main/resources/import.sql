-- 1. 親テーブル（users, committees）のデータをID固定で投入
INSERT INTO users (id, user_id, name, email, password, avatar_url) VALUES (1, 'gemini', 'Gemini', 'gemini@example.com', '$2a$08$iBx1mr0xyL7UpU8qIHNaPOm6y07YKjxDgUA2CTe3HdFpgo3s4Ool6', '/images/avatars/user_1_574582b9-7827-4533-ae20-b82008a72e74.png');
INSERT INTO users (id, user_id, name, email, password) VALUES (2, 'higako', 'Higako', 'higako@example.com', '$2a$08$iBx1mr0xyL7UpU8qIHNaPOm6y07YKjxDgUA2CTe3HdFpgo3s4Ool6');

INSERT INTO committees (id, name, description, icon) VALUES (1, '内閣・総務委員会', '祝日の追加や、ネットのルール、行政に関する法案を審議します。', '🏛️');

-- 2. 子テーブル・中間テーブル（user_follows, bills）のデータを投入（固定したID 1 と 2 を安全に参照）
INSERT INTO user_follows (follower_id, following_id) VALUES (2, 1);

INSERT INTO bills (title, description, status, committee_id, user_id, created_at, updated_at) VALUES ('AI活用による政務自動化法案', '生成AIを活用し、議事録の作成や法案の下書きを自動化することで、議員の公務効率化を推進する。 #AI改革 #業務効率化', 'UNDER_DELIBERATION', 1, 1, '2026-07-18 22:30:00', '2026-07-18 22:30:00');
INSERT INTO bills (title, description, status, committee_id, user_id, created_at, updated_at) VALUES ('開発環境（WSL）普及促進法案', 'すべての開発者に快適なWSL環境を提供し、ローカル開発とプロダクション環境（Coreserver）の連携を円滑にする。 #WSL #デベロッパー', 'PASSED', 1, 2, '2026-07-18 22:30:00', '2026-07-18 22:30:00');