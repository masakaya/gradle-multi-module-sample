-- テスト用ユーザーデータ

-- パスワードは全て 'password123' のハッシュ値（実際の環境では適切なハッシュ化が必要）
SET @password_hash = '$2a$10$YourHashedPasswordHere';

-- ユーザーデータ挿入
INSERT INTO users (company_id, team_id, employee_id, username, email, first_name, last_name, first_name_kana, last_name_kana, phone, mobile, position, role, password_hash) VALUES
-- テックイノベーションの社員
(1, 1, 'TI001', 'yamada.taro', 'yamada.taro@tech-innovation.example.com', '太郎', '山田', 'タロウ', 'ヤマダ', '03-1234-5678', '090-1111-2222', 'シニアエンジニア', 'USER', @password_hash),
(1, 1, 'TI002', 'suzuki.hanako', 'suzuki.hanako@tech-innovation.example.com', '花子', '鈴木', 'ハナコ', 'スズキ', '03-1234-5679', '090-2222-3333', 'プロダクトマネージャー', 'ADMIN', @password_hash),
(1, 2, 'TI003', 'sato.ichiro', 'sato.ichiro@tech-innovation.example.com', '一郎', '佐藤', 'イチロウ', 'サトウ', '03-1234-5680', '090-3333-4444', 'インフラエンジニア', 'USER', @password_hash),
(1, 3, 'TI004', 'tanaka.yuki', 'tanaka.yuki@tech-innovation.example.com', 'ゆき', '田中', 'ユキ', 'タナカ', '03-1234-5681', '090-4444-5555', 'データサイエンティスト', 'USER', @password_hash),

-- グローバルソリューションズの社員
(2, 4, 'GS001', 'kobayashi.ken', 'kobayashi.ken@global-solutions.example.com', '健', '小林', 'ケン', 'コバヤシ', '03-2345-6789', '090-5555-6666', 'シニアコンサルタント', 'USER', @password_hash),
(2, 4, 'GS002', 'watanabe.mai', 'watanabe.mai@global-solutions.example.com', '舞', '渡辺', 'マイ', 'ワタナベ', '03-2345-6790', '090-6666-7777', 'マネージャー', 'ADMIN', @password_hash),
(2, 5, 'GS003', 'ito.takeshi', 'ito.takeshi@global-solutions.example.com', '武', '伊藤', 'タケシ', 'イトウ', '03-2345-6791', '090-7777-8888', 'DXスペシャリスト', 'USER', @password_hash),
(2, 6, 'GS004', 'nakamura.rina', 'nakamura.rina@global-solutions.example.com', '里奈', '中村', 'リナ', 'ナカムラ', '03-2345-6792', '090-8888-9999', 'PMO リーダー', 'USER', @password_hash),

-- デジタルクリエイティブの社員
(3, 7, 'DC001', 'yamamoto.shin', 'yamamoto.shin@digital-creative.example.com', '慎', '山本', 'シン', 'ヤマモト', '03-3456-7890', '090-9999-0000', 'アートディレクター', 'USER', @password_hash),
(3, 7, 'DC002', 'mori.aoi', 'mori.aoi@digital-creative.example.com', '葵', '森', 'アオイ', 'モリ', '03-3456-7891', '090-0000-1111', 'UIデザイナー', 'USER', @password_hash),
(3, 8, 'DC003', 'hayashi.sota', 'hayashi.sota@digital-creative.example.com', '蒼太', '林', 'ソウタ', 'ハヤシ', '03-3456-7892', '090-1111-2222', 'マーケティングマネージャー', 'ADMIN', @password_hash),
(3, 9, 'DC004', 'matsumoto.miku', 'matsumoto.miku@digital-creative.example.com', '未来', '松本', 'ミク', 'マツモト', '03-3456-7893', '090-2222-3333', 'コンテンツディレクター', 'USER', @password_hash);

-- 論理削除されたユーザー（テスト用）
INSERT INTO users (company_id, team_id, employee_id, username, email, first_name, last_name, first_name_kana, last_name_kana, position, role, password_hash, deleted_at) VALUES
(1, 1, 'TI999', 'deleted.user', 'deleted@tech-innovation.example.com', '削除済', 'ユーザー', 'サクジョズミ', 'ユーザー', '退職者', 'USER', @password_hash, NOW());