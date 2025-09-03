-- テスト用企業データ
-- R__プレフィックスは再実行可能なマイグレーション（Repeatable Migration）

-- 既存データをクリーン（べき等性のため）
SET FOREIGN_KEY_CHECKS = 0;
TRUNCATE TABLE users;
TRUNCATE TABLE teams;
TRUNCATE TABLE companies;
SET FOREIGN_KEY_CHECKS = 1;

-- 企業データ挿入
INSERT INTO companies (id, name, code, industry, website, phone, address, established_date, employee_count) VALUES
(1, '株式会社テックイノベーション', 'TECH001', 'IT・ソフトウェア', 'https://tech-innovation.example.com', '03-1234-5678', '東京都渋谷区渋谷1-2-3 テックビル', '2010-04-01', 250),
(2, 'グローバルソリューションズ株式会社', 'GLOB001', 'コンサルティング', 'https://global-solutions.example.com', '03-2345-6789', '東京都千代田区丸の内2-3-4 グローバルタワー', '2005-09-15', 500),
(3, 'デジタルクリエイティブ株式会社', 'DIGI001', 'デザイン・広告', 'https://digital-creative.example.com', '03-3456-7890', '東京都港区六本木3-4-5 クリエイティブビル', '2015-01-20', 120);