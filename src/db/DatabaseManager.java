// Connection (JDBC), CRUD untuk game state & inventory
package db;

import model.state.GameState;
import model.state.PlayerInventory;
import model.card.SpecialCard;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Kelas helper untuk koneksi JDBC + operasi save/load game state.
 * Method wajib: connect(), disconnect(), executeQuerry(), executeUpdate().
 */
public class DatabaseManager {

    // TODO: sesuaikan nama DB, user, dan password dengan MySQL/XAMPP kamu
    private static final String DB_URL  = "jdbc:mysql://localhost:3306/card_game";
    private static final String DB_USER = "root";
    private static final String DB_PASS = "";

    private static Connection connection;

    // =========================================================
    // 1. METHOD WAJIB
    // =========================================================

    /**
     * Membuka koneksi ke database jika belum terbuka.
     */
    public static void connect() throws SQLException {
        if (connection == null || connection.isClosed()) {
            connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
        }
    }

    /**
     * Menutup koneksi database jika masih terbuka.
     */
    public static void disconnect() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
            }
        } catch (SQLException ignored) {
        }
    }

    /**
     * Menjalankan perintah SELECT (query) dan mengembalikan ResultSet.
     * Nama method mengikuti spesifikasi: executeQuerry (2x r).
     */
    public static ResultSet executeQuerry(String sql) throws SQLException {
        connect();
        Statement stmt = connection.createStatement();
        return stmt.executeQuery(sql);
    }

    /**
     * Menjalankan perintah INSERT / UPDATE / DELETE.
     * Mengembalikan jumlah baris yang terpengaruh.
     */
    public static int executeUpdate(String sql) throws SQLException {
        connect();
        Statement stmt = connection.createStatement();
        return stmt.executeUpdate(sql);
    }

    // Helper internal kalau butuh akses Connection (misal untuk PreparedStatement)
    private static Connection getConnection() throws SQLException {
        connect();
        return connection;
    }

    // =========================================================
    // 2. SAVE GAME STATE KE DATABASE
    // =========================================================

    /**
     * Menyimpan GameState ke tabel player_state dan inventory.
     * Pastikan GameState punya getter yang sesuai.
     */
    public static void saveState(GameState state) throws SQLException {
        connect();

        // --- 2.1 UPDATE player_state ---
        String sqlPlayer = """
                UPDATE player_state
                SET money = ?, health = ?, debt = ?, interest_rate = ?, round = ?, seed = ?, current_dealer = ?
                WHERE id = 1
                """;

        try (PreparedStatement ps = getConnection().prepareStatement(sqlPlayer)) {
            // TODO: sesuaikan getter ini dengan GameState kamu
            ps.setInt(1, state.getPlayerMoney());        // atau getMoney()
            ps.setInt(2, state.getPlayer().getHealth()); // kalau player punya health
            ps.setInt(3, state.getDebt());
            ps.setDouble(4, state.getInterestRate());    // tambahkan field ini di GameState kalau belum ada
            ps.setInt(5, state.getRound());
            ps.setLong(6, state.getSeed());              // tambahkan field seed di GameState kalau belum
            ps.setString(7, state.getCurrentDealer().getName()); // atau method lain untuk nama dealer

            ps.executeUpdate();
        }

        // --- 2.2 HAPUS inventory lama ---
        executeUpdate("DELETE FROM inventory");

        // --- 2.3 INSERT inventory baru ---
        String sqlInv = "INSERT INTO inventory (special_card_id, quantity) VALUES (?, ?)";

        // Asumsi PlayerInventory punya method getCards()
        PlayerInventory inv = state.getInventory();
        List<SpecialCard> cards = new ArrayList<>();
        if (inv != null) {
            cards.addAll(inv.getCards()); // pastikan PlayerInventory.getCards() mengembalikan List<SpecialCard>
        }

        try (PreparedStatement ps = getConnection().prepareStatement(sqlInv)) {
            for (SpecialCard card : cards) {
                ps.setInt(1, card.getId());         // id di special_card_data
                ps.setInt(2, card.getQuantity());   // kalau selalu 1, bisa hardcode 1
                ps.addBatch();
            }
            ps.executeBatch();
        }
    }

    // =========================================================
    // 3. LOAD GAME STATE DARI DATABASE
    // =========================================================

    /**
     * Membaca GameState dari database (player_state + inventory).
     */
    public static GameState loadState() throws SQLException {
        connect();

        GameState state = new GameState();

        // --- 3.1 LOAD player_state ---
        String sqlPlayer = "SELECT * FROM player_state WHERE id = 1";
        try (ResultSet rs = executeQuerry(sqlPlayer)) {
            if (rs.next()) {
                // TODO: sesuaikan setter dengan GameState kamu
                state.setPlayerMoney(rs.getInt("money"));      // atau setMoney()
                state.getPlayer().setHealth(rs.getInt("health"));
                state.setDebt(rs.getInt("debt"));
                state.setInterestRate(rs.getDouble("interest_rate"));
                state.setRound(rs.getInt("round"));
                state.setSeed(rs.getLong("seed"));

                String dealerName = rs.getString("current_dealer");
                // Kalau kamu punya factory/lookup dealer, panggil di sini.
                // Misal: state.setCurrentDealer(DealerRegistry.getDealerByName(dealerName));
            }
        }

        // --- 3.2 LOAD inventory (join dengan special_card_data) ---
        String sqlInventory = """
                SELECT i.special_card_id, i.quantity,
                       s.name, s.effect_type, s.price, s.rarity, s.description
                FROM inventory i
                JOIN special_card_data s ON i.special_card_id = s.id
                """;

        PlayerInventory inventory = new PlayerInventory();

        try (ResultSet rsInv = executeQuerry(sqlInventory)) {
            while (rsInv.next()) {
                SpecialCard card = new SpecialCard();

                card.setId(rsInv.getInt("special_card_id"));
                card.setQuantity(rsInv.getInt("quantity"));
                card.setName(rsInv.getString("name"));
                card.setEffectType(rsInv.getString("effect_type"));
                card.setPrice(rsInv.getInt("price"));
                card.setRarity(rsInv.getString("rarity"));
                card.setDescription(rsInv.getString("description"));

                inventory.add(card);
            }
        }

        state.setInventory(inventory);
        return state;
    }
}
