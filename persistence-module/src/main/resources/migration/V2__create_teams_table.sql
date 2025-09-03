-- チームテーブル
SET FOREIGN_KEY_CHECKS = 0;
DROP TABLE IF EXISTS teams;
SET FOREIGN_KEY_CHECKS = 1;

CREATE TABLE teams (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    company_id BIGINT NOT NULL,
    name VARCHAR(100) NOT NULL,
    code VARCHAR(50) NOT NULL,
    department VARCHAR(100),
    description TEXT,
    member_count INT DEFAULT 0,
    deleted_at TIMESTAMP NULL DEFAULT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_teams_company FOREIGN KEY (company_id) REFERENCES companies(id) ON DELETE CASCADE,
    UNIQUE KEY uk_teams_company_code (company_id, code),
    INDEX idx_teams_company_id (company_id),
    INDEX idx_teams_deleted_at (deleted_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;