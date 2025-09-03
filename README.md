# Gradle Multi Module Sample

Spring Boot + Flyway + jOOQ + MySQLを使用したサンプルプロジェクト

## 概要

このプロジェクトは、以下の技術スタックを組み合わせたインフラストラクチャ共有モジュールのサンプルです：

- **Spring Boot 3.5.5** - アプリケーションフレームワーク
- **Flyway** - データベースマイグレーション管理
- **jOOQ** - 型安全なSQL DSL & コード生成
- **MySQL 8.4** - データベース（Docker Compose）
- **Kotlin** - プログラミング言語

## 特徴

✅ **Spring Boot統合** - application.ymlでの一元設定管理  
✅ **環境別設定** - dev, test, prod プロファイル対応  
✅ **べき等性** - DROP IF EXISTSによる安全なマイグレーション  
✅ **テストデータ管理** - Seeder機能でテストデータを自動投入  
✅ **型安全SQL** - jOOQによるコンパイル時チェック  
✅ **論理削除対応** - deleted_atカラムによるソフトデリート  

## プロジェクト構成

```
.
├── build.gradle.kts                    # ルートビルド設定
├── settings.gradle.kts                 # マルチモジュール設定
├── docker-compose.yml                  # MySQL環境
├── gradle.properties                   # Gradle設定
│
├── persistence-module/                 # 永続化層モジュール
│   ├── infrastructure/                 # インフラストラクチャ層
│   ├── repository/                     # リポジトリインターフェース
│   └── resources/
│       ├── db/migration/               # Flywayマイグレーション
│       └── db/seed/                    # テストデータ
│
└── app-module/                         # アプリケーション層モジュール
    ├── Application.kt                  # Spring Bootメインクラス
    ├── config/                         # 設定クラス
    ├── controller/                     # コントローラー層
    ├── service/                        # サービス層
    └── resources/
        ├── application.yml             # Spring Boot設定
        └── messages/                   # メッセージリソース
```

### モジュール依存関係

```
app-module
    └── persistence-module  (implementation依存)
```

- **app-module**: アプリケーション層（コントローラー、サービス、設定、Spring Boot本体）
- **persistence-module**: データ永続化層（リポジトリ、DB設定、マイグレーション）

## データベース設計

### テーブル構成

1. **companies** - 企業情報
   - 基本情報：名称、コード、業界、Webサイト等
   - 論理削除：deleted_at

2. **teams** - チーム情報
   - 企業との関連：company_id (FK)
   - 基本情報：名称、コード、部署、説明等
   - 論理削除：deleted_at

3. **users** - ユーザー情報
   - 企業・チームとの関連：company_id (FK), team_id (FK)
   - 基本情報：社員ID、ユーザー名、メール、氏名等
   - 論理削除：deleted_at

### 関連図

```
companies (1) -----> (*) teams (1) -----> (*) users
                                    \
                                     \---> (*) users (team未所属)
```

## セットアップ

### 1. 前提条件

- Java 21
- Docker & Docker Compose
- Gradle 8.14.3以上

### 2. プロジェクトクローン

```bash
# リポジトリのクローン
git clone <repository-url>
cd gradle-shard-module-sample
```

### 3. 初期ビルド

```bash
# プロジェクト全体のビルド
./gradlew clean build

# 各モジュールのビルド確認
./gradlew :persistence-module:build
./gradlew :app-module:build
```

### 4. データベース起動

```bash
# MySQL起動
docker-compose up -d mysql

# ログ確認
docker-compose logs -f mysql

# 接続確認
docker exec mysql-db mysql -udbuser -pdbpassword -e "SELECT 1;"
```

### 5. jOOQコード生成（persistence-module）

```bash
# データベーススキーマからjOOQクラスを生成
./gradlew :persistence-module:generateJooq

# 生成されたクラス確認
ls -la persistence-module/build/generated-src/jooq/main/
```

### 6. persistence-moduleのビルド（ローカル）

**注意**: Flywayプラグインの制約により、ローカルでは手動でマイグレーションを実行してからビルドします。

```bash
# 手動でマイグレーション実行
docker exec -i mysql-db mysql -uroot -prootpassword mydb < persistence-module/src/main/resources/db/migration/V1__create_companies_table.sql
docker exec -i mysql-db mysql -uroot -prootpassword mydb < persistence-module/src/main/resources/db/migration/V2__create_teams_table.sql  
docker exec -i mysql-db mysql -uroot -prootpassword mydb < persistence-module/src/main/resources/db/migration/V3__create_users_table.sql

# jOOQクラス生成（Flywayスキップ）
./gradlew :persistence-module:generateJooq -x flywayMigrate

# persistence-moduleビルド（Flywayスキップ）
./gradlew :persistence-module:build -x flywayMigrate

# ローカルパブリッシュテスト
./gradlew :persistence-module:publishToMavenLocal -x flywayMigrate
```

### 7. アプリケーション起動

```bash
# app-moduleから起動（開発環境：マイグレーション + テストデータ投入）
./gradlew :app-module:bootRun --args='--spring.profiles.active=dev'

# 本番環境で起動（マイグレーションのみ）
./gradlew :app-module:bootRun --args='--spring.profiles.active=prod'

# デフォルト環境で起動
./gradlew :app-module:bootRun
```

### 8. 動作確認

```bash
# APIエンドポイント確認
curl http://localhost:8080/api/companies

# ヘルスチェック
curl http://localhost:8080/actuator/health
```

## ヘルスチェック

Spring Boot Actuatorによるヘルスチェックエンドポイントが利用可能です：

### 基本ヘルスチェック
```bash
# ヘルスステータス確認
curl http://localhost:8080/actuator/health

# 詳細情報付きヘルスチェック（開発環境）
curl http://localhost:8080/actuator/health | jq .
```

### レスポンス例
```json
{
  "status": "UP",
  "components": {
    "db": {
      "status": "UP",
      "details": {
        "database": "MySQL",
        "validationQuery": "isValid()"
      }
    },
    "diskSpace": {
      "status": "UP",
      "details": {
        "total": 499963174912,
        "free": 411612934144,
        "threshold": 10485760,
        "exists": true
      }
    },
    "ping": {
      "status": "UP"
    }
  }
}
```

### その他のActuatorエンドポイント
```bash
# 利用可能なエンドポイント一覧
curl http://localhost:8080/actuator

# アプリケーション情報
curl http://localhost:8080/actuator/info

# メトリクス情報
curl http://localhost:8080/actuator/metrics

# 環境変数・設定情報（開発環境のみ）
curl http://localhost:8080/actuator/env
```

## Swagger/OpenAPI Documentation

このプロジェクトではSwagger UIによるAPI仕様の可視化をサポートしています。

### Swagger UIアクセス

アプリケーション起動後、以下のURLでSwagger UIにアクセスできます：

```
http://localhost:8080/swagger-ui/index.html
```

### OpenAPI仕様書

OpenAPI 3.0形式のAPI仕様書は以下のエンドポイントで取得できます：

```bash
# JSON形式
curl http://localhost:8080/v3/api-docs

# YAML形式
curl http://localhost:8080/v3/api-docs.yaml
```

### Swagger設定

`application.yml`でSwaggerの設定をカスタマイズできます：

```yaml
springdoc:
  api-docs:
    enabled: true
    path: /v3/api-docs
  swagger-ui:
    enabled: true
    path: /swagger-ui.html
    tags-sorter: alpha
    operations-sorter: method
  packages-to-scan: com.masakaya.controller
  paths-to-match: /api/**
```

### 環境別設定

本番環境ではSwagger UIを無効化することを推奨します：

```yaml
# application-prod.yml
springdoc:
  swagger-ui:
    enabled: false
  api-docs:
    enabled: false
```

### API仕様の自動生成

コントローラーに適切なアノテーションを追加することで、Swagger仕様を充実させることができます：

```kotlin
@RestController
@RequestMapping("/api/companies")
@Tag(name = "Company API", description = "企業情報管理API")
class CompanyController {
    
    @GetMapping
    @Operation(
        summary = "企業一覧取得",
        description = "論理削除されていないアクティブな企業の一覧を取得します"
    )
    @ApiResponses(
        ApiResponse(responseCode = "200", description = "成功"),
        ApiResponse(responseCode = "500", description = "サーバーエラー")
    )
    fun getCompanies(): List<Company> {
        // 実装
    }
}
```

## 使い方

### Flywayマイグレーション

Spring Bootの起動時に自動実行されます。手動実行も可能：

```bash
# マイグレーション状態確認
docker exec mysql-db mysql -udbuser -pdbpassword mydb -e "SELECT * FROM flyway_schema_history;"

# テーブル確認
docker exec mysql-db mysql -udbuser -pdbpassword mydb -e "SHOW TABLES;"
```

### jOOQコード生成

```bash
# persistence-moduleでデータベースからjOOQクラスを生成
./gradlew :persistence-module:generateJooq

# 生成されたクラス確認
ls -la persistence-module/build/generated-src/jooq/main/com/masakaya/jooq/generated/tables/
```

### テストデータ

開発環境（`--spring.profiles.active=dev`）で起動すると自動でテストデータが投入されます。

## 環境設定

### プロファイル別設定

#### 開発環境 (`dev`)
```yaml
spring:
  profiles: dev
  flyway:
    locations:
      - classpath:db/migration    # スキーマ
      - classpath:db/seed         # テストデータ
    clean-disabled: false
```

#### テスト環境 (`test`)
```yaml
spring:
  profiles: test
  datasource:
    url: jdbc:mysql://localhost:3306/mydb_test
  flyway:
    locations:
      - classpath:db/migration
      - classpath:db/seed
```

#### 本番環境 (`prod`)
```yaml
spring:
  profiles: prod
  datasource:
    url: ${DB_URL}              # 環境変数から取得
    username: ${DB_USER}
    password: ${DB_PASSWORD}
  flyway:
    locations:
      - classpath:db/migration  # スキーマのみ
```

### 本番環境での起動例

```bash
export DB_URL=jdbc:mysql://prod-server:3306/mydb
export DB_USER=produser  
export DB_PASSWORD=prodpassword
./gradlew bootRun --args='--spring.profiles.active=prod'
```

## 開発ガイド

### 新しいマイグレーションの追加

1. `persistence-module/src/main/resources/db/migration/` に `V4__description.sql` を作成
2. べき等性を保つため `DROP TABLE IF EXISTS` を含める
3. アプリケーション再起動で自動適用

```sql
-- V4__add_departments_table.sql
DROP TABLE IF EXISTS departments;

CREATE TABLE departments (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    -- ...
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
```

### テストデータの追加

1. `persistence-module/src/main/resources/db/seed/` に `R__description.sql` を作成
2. `R__`プレフィックスは再実行可能（ファイル変更時に自動再実行）
3. 必ずTRUNCATE文でデータをクリア

```sql
-- R__04_insert_departments.sql
-- 既存データクリア（べき等性）
TRUNCATE TABLE departments;

-- データ挿入
INSERT INTO departments (name) VALUES 
('開発部'), ('営業部'), ('管理部');
```

## アーキテクチャ

### レイヤー構成

```
┌─────────────────────────────┐
│     App Module              │
│   ├── Controller Layer      │  ← HTTPエンドポイント
│   │   CompanyController.kt  │
│   └── Service Layer         │  ← ビジネスロジック
│       CompanyService.kt     │
├─────────────────────────────┤
│   Persistence Module        │
│   ├── Repository Interface  │  ← リポジトリインターフェース
│   │   CompanyRepository.kt  │
│   └── Infrastructure Layer  │  ← データアクセス実装
│       CompanyRepositoryImpl │
│         ↓ uses jOOQ         │
│       DSLContext + Tables   │
└─────────────────────────────┘
```

### レイヤー別責務

- **App Module**
  - Controller Layer: HTTPエンドポイント、リクエスト/レスポンス処理
  - Service Layer: ビジネスロジック、トランザクション制御
- **Persistence Module**
  - Repository Interface: データアクセス抽象化
  - Infrastructure Layer: jOOQを使った具体的なデータアクセス実装

## ライセンス

MIT License

## 貢献

1. Fork the repository
2. Create your feature branch (`git checkout -b feature/amazing-feature`)  
3. Commit your changes (`git commit -m 'Add some amazing feature'`)
4. Push to the branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request