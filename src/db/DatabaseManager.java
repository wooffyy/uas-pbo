// Connection (JDBC), CRUD untuk game state & inventory
package db;

import model.state.GameState;
import model.state.PlayerInventory;
import model.card.SpecialCard;
import model.card.EffectType;
import model.card.Rarity;

import java.sql.*;
import java.util.List;

public class DatabaseManager {

    private static final String DB_URL  = "jdbc:mysql://localhost:3306/card_game";
    private static final String DB_USER = "root";
    private static final String DB_PASS = "";

    private static Connection connection;

    // =========================================================
    // 1. METHOD WAJIB (TIDAK DIUBAH)
    // =========================================================

    public static void connect() throws SQLException {
        if (connection == null || connection.isClosed()) {
            connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
        }
    }

    public static void disconnect() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
            }
        } catch (SQLException ignored) {}
    }

    public static ResultSet executeQuerry(String sql) throws SQLException {
        connect();
        Statement stmt = connection.createStatement();
        return stmt.executeQuery(sql);
    }

    public static int executeUpdate(String sql) throws SQLException {
        connect();
        Statement stmt = connection.createStatement();
        return stmt.executeUpdate(sql);
    }

    private static Connection getConnection() throws SQLException {
        connect();
        return connection;
    }

    // =========================================================
    // 2. SAVE GAME STATE
    // =========================================================

    public static void saveState(GameState state) throws SQLException {
        connect();

        String sqlPlayer = """
            UPDATE player_state
            SET money = ?, health = ?, debt = ?, interest_rate = ?, round = ?, seed = ?, current_dealer = ?
            WHERE id = 1
        """;

        try (PreparedStatement ps = getConnection().prepareStatement(sqlPlayer)) {
            ps.setInt(1, state.getMoney());
            ps.setInt(2, state.getPlayerHealth());
            ps.setInt(3, state.getDebt());
            ps.setDouble(4, state.getInterestRate());
            ps.setInt(5, state.getRound());
            ps.setLong(6, state.getSeed());
            ps.setString(7, state.getCurrentDealer().getName());
            ps.executeUpdate();
        }

        // reset inventory
        executeUpdate("DELETE FROM inventory");

        String sqlInv = "INSERT INTO inventory (special_card_id, quantity) VALUES (?, ?)";

        PlayerInventory inventory = state.getInventory();
        if (inventory == null) return;

        try (PreparedStatement ps = getConnection().prepareStatement(sqlInv)) {
            for (SpecialCard card : inventory.getCards()) {
                ps.setInt(1, card.getId());
                ps.setInt(2, 1); // quantity FIXED = 1 (inventory list handles ownership)
                ps.addBatch();
            }
            ps.executeBatch();
        }
    }

    // =========================================================
    // 3. LOAD GAME STATE
    // =========================================================

    public static GameState loadState() throws SQLException {
        connect();

        GameState state = new GameState(); 

        // ---- player_state
        try (ResultSet rs = executeQuerry("SELECT * FROM player_state WHERE id = 1")) {
            if (rs.next()) {
                state.setMoney(rs.getInt("money"));
                state.setPlayerHealth(rs.getInt("health"));
                state.setDebt(rs.getInt("debt"));
                state.setInterestRate(rs.getDouble("interest_rate"));
                state.setRound(rs.getInt("round"));
                state.setSeed(rs.getLong("seed"));

                // dealer akan di-resolve oleh GameManager / factory
                // state.setCurrentDealer(...)
            }
        }

        // ---- inventory
        PlayerInventory inventory = new PlayerInventory();

        String sqlInventory = """
            SELECT s.id, s.name, s.effect_type, s.price, s.rarity, s.description
            FROM inventory i
            JOIN special_card_data s ON i.special_card_id = s.id
        """;

        try (ResultSet rs = executeQuerry(sqlInventory)) {
            while (rs.next()) {
                SpecialCard card = new SpecialCard(
                    rs.getInt("id"),
                    rs.getString("name"),
                    EffectType.valueOf(rs.getString("effect_type")),
                    rs.getInt("price"),
                    Rarity.valueOf(rs.getString("rarity")),
                    rs.getString("description")
                );
                inventory.add(card);
            }
        }

        state.setInventory(inventory);
        return state;
    }
}
