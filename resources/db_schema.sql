-- ============================================================
--  DB SCHEMA FOR CARD GAME PROJECT
--  This file contains everything needed to build the database
-- ============================================================

-- 1) CREATE DATABASE
CREATE DATABASE IF NOT EXISTS card_game;
USE card_game;

-- ============================================================
-- 2) CREATE TABLES
-- ============================================================

-- Tabel player_state
CREATE TABLE IF NOT EXISTS player_state (
    id INT PRIMARY KEY,
    money INT NOT NULL,
    health INT NOT NULL,
    debt INT NOT NULL,
    interest_rate DECIMAL(5,4) NOT NULL,
    round INT NOT NULL,
    seed BIGINT NOT NULL,
    current_dealer VARCHAR(100)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Tabel special_card_data
CREATE TABLE IF NOT EXISTS special_card_data (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    effect_type VARCHAR(50) NOT NULL,
    price INT NOT NULL,
    rarity ENUM('COMMON', 'RARE', 'SUPER RARE', 'LEGENDARY') NOT NULL,
    description TEXT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Tabel inventory
CREATE TABLE IF NOT EXISTS inventory (
    id INT AUTO_INCREMENT PRIMARY KEY,
    special_card_id INT NOT NULL,
    quantity INT NOT NULL DEFAULT 1,
    CONSTRAINT fk_inventory_card
        FOREIGN KEY (special_card_id)
        REFERENCES special_card_data(id)
        ON UPDATE CASCADE
        ON DELETE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Tabel run_history
CREATE TABLE IF NOT EXISTS run_history (
    id INT AUTO_INCREMENT PRIMARY KEY,
    finished BOOLEAN NOT NULL,
    final_round INT,
    final_money INT,
    timestamp DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Tabel logs
CREATE TABLE IF NOT EXISTS logs (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    event_type VARCHAR(50) NOT NULL,
    message TEXT NOT NULL,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

