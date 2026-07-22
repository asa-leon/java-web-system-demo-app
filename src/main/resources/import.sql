-- 1. 親テーブル（users, committees）のデータをID固定で投入
INSERT INTO users (id, user_id, name, email, password, avatar_url) VALUES (1, 'gemini', 'Gemini', 'gemini@example.com', '$2a$08$iBx1mr0xyL7UpU8qIHNaPOm6y07YKjxDgUA2CTe3HdFpgo3s4Ool6', '/images/avatars/user_1_574582b9-7827-4533-ae20-b82008a72e74.png');
INSERT INTO users (id, user_id, name, email, password) VALUES (2, 'higako', 'Higako', 'higako@example.com', '$2a$08$iBx1mr0xyL7UpU8qIHNaPOm6y07YKjxDgUA2CTe3HdFpgo3s4Ool6');

INSERT INTO committees (id, name, description, icon) VALUES (1, '内閣・行政改革委員会', '国家機構の改革、デジタル庁・行政手続きの効率化、公務員制度や統治機構の刷新に関する法案を審議・議論します。', 'bi-building-fill-gear');
INSERT INTO committees (id, name, description, icon) VALUES (2, '経済・財政・産業委員会', '税制改正、金融政策、スタートアップ支援、労働環境の改善、エネルギー産業育成など経済成長に関わる法案を扱います。', 'bi-graph-up-arrow');
INSERT INTO committees (id, name, description, icon) VALUES (3, '外交・安全保障委員会', '国際関係の強化、防衛政策、安全保障体制、サイバーセキュリティ対策や国際協力に関する法案を審議します。', 'bi-globe-americas');
INSERT INTO committees (id, name, description, icon) VALUES (4, '厚生・教育・社会保障委員会', '子育て支援、少子化対策、年金・医療制度の維持、教育改革および科学技術振興に関する法案を重点的に取り扱います。', 'bi-heart-pulse-fill');
INSERT INTO committees (id, name, description, icon) VALUES (5, '環境・国土インフラ委員会', '脱炭素・エネルギー政策、防災・減災対策、都市計画やインフラ老朽化対策に関する法案を審議・提案します。', 'bi-tree-fill');

-- 2. 子テーブル・中間テーブル（user_follows, bills）のデータを投入（固定したID 1 と 2 を安全に参照）
INSERT INTO user_follows (follower_id, following_id) VALUES (2, 1);

INSERT INTO bills (title, description, status, committee_id, user_id, created_at, updated_at) VALUES ('AI活用による政務自動化法案', '生成AIを活用し、議事録の作成や法案の下書きを自動化することで、議員の公務効率化を推進する。 #AI改革 #業務効率化', 'UNDER_DELIBERATION', 1, 1, '2026-07-18 22:30:00', '2026-07-18 22:30:00');
INSERT INTO bills (title, description, status, committee_id, user_id, created_at, updated_at) VALUES ('開発環境（WSL）普及促進法案', 'すべての開発者に快適なWSL環境を提供し、ローカル開発とプロダクション環境（Coreserver）の連携を円滑にする。 #WSL #デベロッパー', 'PASSED', 1, 2, '2026-07-18 22:30:00', '2026-07-18 22:30:00');