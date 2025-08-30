-- ユーザテーブル
SET FOREIGN_KEY_CHECKS = 0;
DROP TABLE IF EXISTS users;
SET FOREIGN_KEY_CHECKS = 1;

CREATE TABLE users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    company_id BIGINT NOT NULL,
    team_id BIGINT,
    employee_id VARCHAR(50) NOT NULL,
    username VARCHAR(50) NOT NULL UNIQUE,
    email VARCHAR(100) NOT NULL UNIQUE,
    first_name VARCHAR(50) NOT NULL,
    last_name VARCHAR(50) NOT NULL,
    first_name_kana VARCHAR(50),
    last_name_kana VARCHAR(50),
    phone VARCHAR(20),
    mobile VARCHAR(20),
    position VARCHAR(100),
    role VARCHAR(50) DEFAULT 'USER',
    password_hash VARCHAR(255),
    deleted_at TIMESTAMP NULL DEFAULT NULL,
    last_login_at TIMESTAMP NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_users_company FOREIGN KEY (company_id) REFERENCES companies(id) ON DELETE CASCADE,
    CONSTRAINT fk_users_team FOREIGN KEY (team_id) REFERENCES teams(id) ON DELETE SET NULL,
    UNIQUE KEY uk_users_company_employee_id (company_id, employee_id),
    INDEX idx_users_username (username),
    INDEX idx_users_email (email),
    INDEX idx_users_company_id (company_id),
    INDEX idx_users_team_id (team_id),
    INDEX idx_users_deleted_at (deleted_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;