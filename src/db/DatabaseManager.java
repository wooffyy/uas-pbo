package db;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import model.card.EffectTrigger;
import model.card.EffectType;
import model.card.Rarity;
import model.card.SpecialCard;
import core.shop.ShopCardPool;

public class DatabaseManager {

    private static final String DB_URL = "jdbc:mysql://localhost:3306/card_game";
    private static final String DB_USER = "root";
    private static final String DB_PASS = "";

    private static Connection connection;

    public static void connect() throws SQLException {
        if (connection == null || connection.isClosed()) {
            try {
                Class.forName("com.mysql.cj.jdbc.Driver");
            } catch (ClassNotFoundException e) {
                // Try legacy driver if new one fails
                try {
                    Class.forName("com.mysql.jdbc.Driver");
                } catch (ClassNotFoundException ex) {
                    throw new SQLException(
                            "MySQL JDBC Driver not found. Please ensure the mysql-connector library is added to your project classpath.",
                            ex);
                }
            }
            connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
        }
    }

    public static void disconnect() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
            }
        } catch (SQLException ignored) {
        }
    }

    private static Connection getConnection() throws SQLException {
        connect();
        seedSpecialCardData();
        return connection;
    }

    private static void seedSpecialCardData() {
        if (connection == null)
            return;

        System.out.println("Seeding/Syncing special_card_data table from ShopCardPool...");
        List<SpecialCard> allCards = ShopCardPool.getAllCards();

        String sql = """
                    INSERT INTO special_card_data (id, name, effect_type, effect_trigger, price, rarity, description)
                    VALUES (?, ?, ?, ?, ?, ?, ?)
                    ON DUPLICATE KEY UPDATE
                    name=VALUES(name),
                    effect_type=VALUES(effect_type),
                    effect_trigger=VALUES(effect_trigger),
                    price=VALUES(price),
                    rarity=VALUES(rarity),
                    description=VALUES(description)
                """;

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            for (SpecialCard card : allCards) {
                ps.setInt(1, card.getId());
                ps.setString(2, card.getName());
                ps.setString(3, card.getEffectType().name());
                ps.setString(4, card.getEffectTrigger() != null ? card.getEffectTrigger().name() : "ON_ROUND");
                ps.setInt(5, card.getPrice());
                ps.setString(6, card.getRarity().name().replace("_", " "));
                ps.setString(7, card.getDescription());
                ps.addBatch();
            }
            ps.executeBatch();
            System.out.println("Seeding complete. Synced " + allCards.size() + " cards.");
        } catch (SQLException e) {
            System.err.println("Error seeding special_card_data: " + e.getMessage());
        }
    }

    /**
     * Unlocks a special card in the database.
     */
    public static void unlockCard(int specialCardId) {
        try {
            seedSpecialCardData();
        } catch (Exception e) {
            e.printStackTrace();
        }

        String sql = "INSERT IGNORE INTO unlocked_cards (special_card_id) VALUES (?)";

        try (PreparedStatement ps = getConnection().prepareStatement(sql)) {
            ps.setInt(1, specialCardId);
            int rows = ps.executeUpdate();
            if (rows > 0) {
                System.out.println("Unlocked card ID: " + specialCardId + " (NEW)");
            } else {
                System.out.println("Unlocked card ID: " + specialCardId + " (ALREADY OWNED)");
            }
        } catch (SQLException e) {
            System.err.println("Error unlocking card: " + e.getMessage());
        }
    }

    /**
     * Retrieves all unlocked special cards.
     */
    public static List<SpecialCard> getUnlockedCards() {
        List<SpecialCard> unlockedCards = new ArrayList<>();
        String sql = """
                    SELECT s.id, s.name, s.effect_type, s.effect_trigger, s.price, s.rarity, s.description
                    FROM unlocked_cards u
                    JOIN special_card_data s ON u.special_card_id = s.id
                    ORDER BY s.id ASC
                """;

        try (Statement stmt = getConnection().createStatement();
                ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                SpecialCard card = new SpecialCard(
                        rs.getInt("id"),
                        rs.getString("name"),
                        EffectType.valueOf(rs.getString("effect_type")),
                        EffectTrigger.valueOf(rs.getString("effect_trigger")),
                        rs.getInt("price"),
                        Rarity.valueOf(rs.getString("rarity").replace(" ", "_")),
                        rs.getString("description"));
                unlockedCards.add(card);
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving unlocked cards: " + e.getMessage());
        }

        return unlockedCards;
    }
}
