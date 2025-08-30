# Gradle Shard Module Sample

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
├── build.gradle.kts                    # ビルド設定
├── docker-compose.yml                 # MySQL環境
├── src/main/
│   ├── kotlin/com/masakaya/
│   │   ├── Application.kt              # Spring Bootメインクラス
│   │   ├── service/                    # Service Layer
│   │   │   ├── CompanyService.kt       #   ビジネスロジック
│   │   │   └── repository/             #   リポジトリインターフェース
│   │   │       └── CompanyRepository.kt
│   │   └── infrastructure/             # Infrastructure Layer
│   │       └── persistence/            #   データアクセス実装
│   │           └── CompanyRepositoryImpl.kt
│   └── resources/
│       ├── application.yml             # 環境別設定
│       ├── db/migration/               # Flywayマイグレーション
│       │   ├── V1__create_companies_table.sql
│       │   ├── V2__create_teams_table.sql
│       │   └── V3__create_users_table.sql
│       └── db/seed/                    # テストデータ
│           ├── R__01_insert_test_companies.sql
│           ├── R__02_insert_test_teams.sql
│           └── R__03_insert_test_users.sql
└── build/generated-src/jooq/           # jOOQ自動生成クラス
    └── main/com/masakaya/jooq/generated/
        ├── tables/                     # テーブル定義
        │   ├── Companies.kt
        │   ├── Users.kt
        │   ├── Teams.kt
        │   ├── records/                # Recordクラス
        │   └── pojos/                  # POJOクラス
        └── Mydb.kt                     # スキーマ定義
```

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

### 2. データベース起動

```bash
# MySQL起動
docker-compose up -d mysql

# ログ確認
docker-compose logs -f mysql
```

### 3. アプリケーション起動

```bash
# 開発環境で起動（マイグレーション + テストデータ投入）
./gradlew bootRun --args='--spring.profiles.active=dev'

# 本番環境で起動（マイグレーションのみ）
./gradlew bootRun --args='--spring.profiles.active=prod'

# デフォルト環境で起動
./gradlew bootRun
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
# データベースからjOOQクラスを生成
./gradlew generateJooq

# 生成されたクラス確認
ls -la build/generated-src/jooq/main/com/masakaya/jooq/generated/tables/
```

### テストデータ

開発環境（`--spring.profiles.active=dev`）で起動すると自動でテストデータが投入されます。

#### 投入されるデータ：
- **企業**: 3社（テックイノベーション、グローバルソリューションズ、デジタルクリエイティブ）
- **チーム**: 9チーム（各企業に3チームずつ）
- **ユーザー**: 13名（各チームにメンバー + 論理削除済みユーザー1名）

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

1. `src/main/resources/db/migration/` に `V4__description.sql` を作成
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

1. `src/main/resources/db/seed/` に `R__description.sql` を作成
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
│     Service Layer           │  ← ビジネスロジック
│   service/CompanyService.kt │
│   service/repository/       │    (リポジトリインターフェース)
│     CompanyRepository.kt    │
├─────────────────────────────┤
│   Infrastructure Layer      │  ← データアクセス実装
│ infrastructure/persistence/ │
│  CompanyRepositoryImpl.kt   │
│      ↓ uses jOOQ            │
│   DSLContext + Tables       │
└─────────────────────────────┘
```

### レイヤー別責務

- **Service Layer**: ビジネスロジック、トランザクション制御、リポジトリインターフェース定義
- **Infrastructure Layer**: jOOQを使った具体的なデータアクセス実装

### コード例

#### 1. Repository Interface (Service Layer)
```kotlin
// src/main/kotlin/com/masakaya/service/repository/CompanyRepository.kt
interface CompanyRepository {
    fun findAll(): List<Companies>
    fun findById(id: Long): Companies?
    fun findByCode(code: String): Companies?
    fun findActiveCompanies(): List<Companies>
    fun create(name: String, code: String, ...): Companies
    fun update(company: Companies): Companies
    fun softDelete(id: Long): Boolean
}
```

#### 2. Repository Implementation (Infrastructure Layer)
```kotlin
// src/main/kotlin/com/masakaya/infrastructure/persistence/CompanyRepositoryImpl.kt
@Repository
class CompanyRepositoryImpl(private val dsl: DSLContext) : CompanyRepository {
    
    override fun findActiveCompanies(): List<Companies> {
        return dsl.selectFrom(COMPANIES)
            .where(COMPANIES.DELETED_AT.isNull)
            .orderBy(COMPANIES.CREATED_AT.desc())
            .fetchInto(Companies::class.java)
    }
    
    override fun create(name: String, code: String, ...): Companies {
        return dsl.insertInto(COMPANIES)
            .set(COMPANIES.NAME, name)
            .set(COMPANIES.CODE, code)
            // ... 他のフィールド
            .returning()
            .fetchOneInto(Companies::class.java)!!
    }
    
    override fun softDelete(id: Long): Boolean {
        return dsl.update(COMPANIES)
            .set(COMPANIES.DELETED_AT, LocalDateTime.now())
            .where(COMPANIES.ID.eq(id))
            .execute() > 0
    }
}
```

#### 3. Service (Service Layer)
```kotlin
// src/main/kotlin/com/masakaya/service/CompanyService.kt
@Service
@Transactional
class CompanyService(private val companyRepository: CompanyRepository) {
    
    @Transactional(readOnly = true)
    fun getActiveCompanies(): List<Companies> {
        return companyRepository.findActiveCompanies()
    }
    
    fun createCompany(name: String, code: String, ...): Companies {
        // ビジネスルール: 企業コードの重複チェック
        val existingCompany = companyRepository.findByCode(code)
        if (existingCompany != null) {
            throw IllegalArgumentException("企業コード '$code' は既に使用されています")
        }
        
        return companyRepository.create(name, code, ...)
    }
}
```

## トラブルシューティング

### データベース接続エラー
```bash
# MySQL起動確認
docker-compose ps

# MySQL再起動
docker-compose restart mysql

# ポート確認
netstat -tlnp | grep 3306
```

### マイグレーション失敗
```bash
# マイグレーション履歴確認
docker exec mysql-db mysql -udbuser -pdbpassword mydb -e "SELECT * FROM flyway_schema_history ORDER BY installed_on;"

# データベースリセット（開発環境のみ）
docker exec mysql-db mysql -uroot -prootpassword -e "DROP DATABASE mydb; CREATE DATABASE mydb;"
```

### jOOQ生成エラー
```bash
# データベース接続確認
docker exec mysql-db mysql -udbuser -pdbpassword -e "SELECT 1;"

# 手動生成
./gradlew clean generateJooq
```

## ライセンス

MIT License

## 貢献

1. Fork the repository
2. Create your feature branch (`git checkout -b feature/amazing-feature`)  
3. Commit your changes (`git commit -m 'Add some amazing feature'`)
4. Push to the branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request