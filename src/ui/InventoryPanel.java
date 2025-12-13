package ui;

import javax.swing.*;
import java.awt.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;

// Halaman Koleksi Kartu Spesial
public class CardCollectionPanel extends JPanel {

    private static final Color BG_COLOR = new Color(20, 20, 20); // Background gelap
    private static final Color ACCENT_COLOR = new Color(255, 200, 0); // Warna aksen kuning (umum)
    private static final Color DANGER_COLOR = new Color(255, 50, 50); // Warna Merah untuk Status Locked
    private static final Color SUCCESS_COLOR = new Color(0, 200, 0); // Warna Hijau untuk Status Unlocked
    private static final Color CARD_SLOT_COLOR = new Color(40, 40, 40);

    public CardCollectionPanel(UIWindow parentFrame) {
        setLayout(new BorderLayout(10, 10));
        setBackground(BG_COLOR);
        setBorder(new EmptyBorder(20, 20, 20, 20));

        // 1. Judul dan Tombol Back
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);

        JLabel titleLabel = new JLabel("SPECIAL CARD COLLECTION", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Monospaced", Font.BOLD, 36));
        titleLabel.setForeground(ACCENT_COLOR);
        headerPanel.add(titleLabel, BorderLayout.CENTER);

        // Tombol Back
        JButton backButton = new JButton("← BACK");
        backButton.setBackground(new Color(80, 0, 0));
        backButton.setForeground(Color.WHITE);
        backButton.setFont(new Font("Monospaced", Font.BOLD, 14));
        backButton.addActionListener(e -> parentFrame.switchView(UIWindow.MENU_VIEW));

        JPanel backWrapper = new JPanel(new FlowLayout(FlowLayout.LEFT));
        backWrapper.setOpaque(false);
        backWrapper.add(backButton);
        headerPanel.add(backWrapper, BorderLayout.WEST);

        add(headerPanel, BorderLayout.NORTH);

        // 2. Area Koleksi Kartu (Menggunakan JScrollPane untuk banyak kartu)

        JPanel cardGridPanel = new JPanel(new GridLayout(0, 5, 15, 15));
        cardGridPanel.setBackground(BG_COLOR);
        cardGridPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

        // Contoh: Menambahkan 25 kartu placeholder
        for (int i = 1; i <= 25; i++) {
            cardGridPanel.add(createCardPlaceholder(i));
        }

        JScrollPane scrollPane = new JScrollPane(cardGridPanel);
        scrollPane.setBorder(null);
        scrollPane.getViewport().setBackground(BG_COLOR);
        scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

        add(scrollPane, BorderLayout.CENTER);
    }

    /**
     * Helper untuk membuat slot kartu koleksi dengan status LOCKED/UNLOCKED.
     */
    private JPanel createCardPlaceholder(int index) {

        // Logika Status: Jika ganjil -> LOCKED, Jika genap -> UNLOCKED
        boolean isLocked = (index % 2 != 0);
        String statusText = "LOCKED";

        // Tentukan warna berdasarkan status
        Color statusColor = isLocked ? DANGER_COLOR : SUCCESS_COLOR;
        Color slotBorderColor = isLocked ? DANGER_COLOR : SUCCESS_COLOR; // Border kartu UNLOCKED berwarna Hijau


        JPanel card = new JPanel(new BorderLayout());
        card.setPreferredSize(new Dimension(150, 220));
        card.setBackground(CARD_SLOT_COLOR);

        // Border slot kartu menyesuaikan status Locked/Unlocked
        card.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(slotBorderColor, 2, true),
                new EmptyBorder(5, 5, 5, 5)
        ));

        // Teks Judul Kartu ("CARD X")
        JLabel title = new JLabel("CARD " + index, SwingConstants.CENTER);
        title.setFont(new Font("Monospaced", Font.BOLD, 14));
        title.setForeground(statusColor); // Warna judul mengikuti status (Merah/Hijau)
        card.add(title, BorderLayout.NORTH);

        // Kotak Gambar Kartu
        JPanel imageBox = new JPanel(new GridBagLayout());
        imageBox.setBackground(new Color(60, 60, 60));
        imageBox.setBorder(new LineBorder(Color.DARK_GRAY));

        // --- Perubahan Utama: Hanya tambahkan label status jika LOCKED ---
        if (isLocked) {
            JLabel status = new JLabel(statusText, SwingConstants.CENTER);
            status.setFont(new Font("Monospaced", Font.PLAIN, 12));
            status.setForeground(DANGER_COLOR);
            imageBox.add(status); // Tampilkan "LOCKED" di tengah
        }
        // Jika UNLOCKED, imageBox dibiarkan kosong, seolah-olah kartu sudah ada/terlihat
        // --- End Perubahan ---

        card.add(imageBox, BorderLayout.CENTER);

        return card;
    }
}
