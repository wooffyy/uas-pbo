-- ============================================================
--  DB SCHEMA FOR CARD GAME PROJECT (COLLECTION ONLY)
--  Refactored: Only stores Special Card Data and User Collection
-- ============================================================

-- 1) CREATE DATABASE
CREATE DATABASE IF NOT EXISTS card_game;
USE card_game;

-- ============================================================
-- 2) CREATE TABLES
-- ============================================================

-- Tabel special_card_data
-- Defines all available special cards in the game
CREATE TABLE IF NOT EXISTS special_card_data (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    effect_type VARCHAR(50) NOT NULL,
    effect_trigger VARCHAR(50) NOT NULL, -- Added missing column
    price INT NOT NULL,
    rarity ENUM('COMMON', 'RARE', 'SUPER RARE', 'LEGENDARY') NOT NULL,
    description TEXT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Tabel unlocked_cards (replacing inventory)
-- Stores which cards the player has unlocked/collected.
-- Once unlocked, it stays unlocked forever (Collection).
CREATE TABLE IF NOT EXISTS unlocked_cards (
    id INT AUTO_INCREMENT PRIMARY KEY,
    special_card_id INT NOT NULL,
    acquired_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_collection_card
        FOREIGN KEY (special_card_id)
        REFERENCES special_card_data(id)
        ON UPDATE CASCADE
        ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ============================================================
-- 3) INITIAL DATA (Optional seed if needed)
-- ============================================================
-- You can add INSERT statements here if you want to pre-populate cards


