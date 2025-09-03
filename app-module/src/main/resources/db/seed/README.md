# Seeder（テストデータ）管理

## 概要
このディレクトリには、開発・テスト環境用のシードデータ（初期データ）を管理するSQLファイルが格納されています。

## ファイル命名規則

### R__プレフィックス（Repeatable Migration）
- `R__`で始まるファイルは「再実行可能なマイグレーション」
- ファイルの内容が変更されるたびに自動的に再実行される
- べき等性を保つため、TRUNCATE/DELETE文を含めること

### 命名例
- `R__01_insert_test_companies.sql` - 企業データ
- `R__02_insert_test_teams.sql` - チームデータ
- `R__03_insert_test_users.sql` - ユーザーデータ

## 使い方

### 1. テストデータを投入
```bash
./gradlew flywaySeeder
```

### 2. テストデータをクリーン（構造は残す）
```bash
./gradlew cleanSeedData
```

### 3. マイグレーション + シーダー実行
```bash
./gradlew flywayMigrate flywaySeeder
```

## 環境別の管理

### 開発環境のみでシーダーを実行
```gradle
tasks.register("setupDev") {
    dependsOn("flywayMigrate")
    if (project.hasProperty("dev")) {
        dependsOn("flywaySeeder")
    }
}
```

### 実行例
```bash
# 開発環境（シーダー含む）
./gradlew setupDev -Pdev

# 本番環境（マイグレーションのみ）
./gradlew flywayMigrate
```

## 注意事項

1. **本番環境では実行しない**
   - シーダーは開発・テスト環境専用
   - 本番デプロイ時は `flywayMigrate` のみ実行

2. **べき等性の確保**
   - 必ずTRUNCATE/DELETEで既存データをクリーン
   - 固定IDを使用してデータの一貫性を保つ

3. **依存関係の順序**
   - 外部キー制約がある場合は、親テーブルから順に投入
   - 例: companies → teams → users

4. **パスワード管理**
   - テスト用の固定パスワードのみ使用
   - 本番環境では適切なハッシュ化を行う

## 履歴管理
シーダーの実行履歴は `flyway_seed_history` テーブルで管理されます（通常のマイグレーション履歴とは別管理）。