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

    // --- WARNA RARITY BARU ---
    private static final Color RARE_GREEN = new Color(0, 180, 0); // Hijau untuk Rare
    private static final Color SUPER_RARE_PURPLE = new Color(170, 0, 200); // Ungu untuk Super Rare
    private static final Color LEGENDARY_YELLOW = new Color(255, 200, 0); // Kuning untuk Legendary
    // COMMON_GRAY tidak diperlukan karena Common tanpa border tebal

    // Konfigurasi Rarity (Total 18)
    private static final int[] RARITY_COUNTS = {7, 5, 3, 3}; // Common, Rare, Super Rare, Legendary
    private static final String[] RARITY_NAMES = {"COMMON", "RARE", "SUPER RARE", "LEGENDARY"};
    private static final Color[] RARITY_BORDERS = {null, RARE_GREEN, SUPER_RARE_PURPLE, LEGENDARY_YELLOW};


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

        // Ubah GridLayout ke 5 kolom (karena 18 kartu terlihat baik di 5 kolom)
        JPanel cardGridPanel = new JPanel(new GridLayout(0, 5, 15, 15));
        cardGridPanel.setBackground(BG_COLOR);
        cardGridPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

        // --- Menambahkan 18 kartu dengan rarity berbeda ---

        int cardIndex = 1;
        // Iterasi berdasarkan Rarity
        for (int r = 0; r < RARITY_COUNTS.length; r++) {
            for (int i = 0; i < RARITY_COUNTS[r]; i++) {
                // Logika Status: Ganjil -> LOCKED, Genap -> UNLOCKED (contoh)
                boolean isLocked = (cardIndex % 2 != 0);

                cardGridPanel.add(createCardPlaceholder(
                        cardIndex,
                        isLocked,
                        RARITY_NAMES[r],
                        RARITY_BORDERS[r]
                ));
                cardIndex++;
            }
        }
        // --- End Perubahan Kartu ---

        JScrollPane scrollPane = new JScrollPane(cardGridPanel);
        scrollPane.setBorder(null);
        scrollPane.getViewport().setBackground(BG_COLOR);
        scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

        add(scrollPane, BorderLayout.CENTER);
    }

    /**
     * Helper untuk membuat slot kartu koleksi dengan status LOCKED/UNLOCKED
     * dan Rarity Border yang spesifik.
     */
    private JPanel createCardPlaceholder(int index, boolean isLocked, String rarity, Color rarityBorderColor) {

        String statusText = isLocked ? "LOCKED" : "UNLOCKED";

        // Tentukan warna teks status/judul berdasarkan status (Merah/Hijau)
        Color statusTextColor = isLocked ? DANGER_COLOR : SUCCESS_COLOR;

        // Tentukan warna BORDER AKHIR.
        // Untuk Rare, Super Rare, Legendary, border harus warna rarity-nya, terlepas dari status.
        Color finalBorderColor;

        if (rarityBorderColor != null) {
            // JIKA RARE/SR/LEGENDARY: Gunakan warna rarity yang diminta (UNGU/KUNING/HIJAU)
            finalBorderColor = rarityBorderColor;
        } else {
            // JIKA COMMON: Border akan mengikuti status (Merah/Hijau)
            finalBorderColor = isLocked ? DANGER_COLOR : SUCCESS_COLOR;
        }


        JPanel card = new JPanel(new BorderLayout());
        card.setPreferredSize(new Dimension(150, 220));
        card.setBackground(CARD_SLOT_COLOR);

        // --- Atur Border Sesuai Rarity ---
        if (rarityBorderColor == null) {
            // 1. COMMON: Tidak ada border luar yang tebal (Hanya padding)
            card.setBorder(BorderFactory.createCompoundBorder(
                    new EmptyBorder(3, 3, 3, 3), // Tidak ada border luar yang tebal
                    new EmptyBorder(4, 4, 4, 4) // Padding internal
            ));
        } else {
            // 2. RARE/SR/LEGENDARY: Border tebal sesuai warna rarity (finalBorderColor = rarityBorderColor)
            card.setBorder(BorderFactory.createCompoundBorder(
                    new LineBorder(finalBorderColor, 3, true), // Border sesuai warna rarity
                    new EmptyBorder(4, 4, 4, 4) // Padding internal
            ));
        }

        // Teks Judul Kartu ("CARD X - RARITY")
        JLabel title = new JLabel(rarity + " #" + index, SwingConstants.CENTER);
        title.setFont(new Font("Monospaced", Font.BOLD, 14));
        title.setForeground(statusTextColor); // Warna judul mengikuti status (Merah/Hijau)
        card.add(title, BorderLayout.NORTH);

        // Kotak Gambar Kartu
        JPanel imageBox = new JPanel(new GridBagLayout());
        imageBox.setBackground(new Color(60, 60, 60));

        // Untuk kartu Common, kita tetap memberikan indikasi status border di image box.
        if (rarityBorderColor == null) {
            imageBox.setBorder(new LineBorder(finalBorderColor, 1)); // Border Merah/Hijau untuk Common
        } else {
            imageBox.setBorder(new LineBorder(Color.DARK_GRAY, 1)); // Border default untuk Rare/SR/Legendary
        }


        // --- Tampilkan label status jika LOCKED ---
        if (isLocked) {
            JLabel status = new JLabel(statusText, SwingConstants.CENTER);
            status.setFont(new Font("Monospaced", Font.PLAIN, 12));
            status.setForeground(DANGER_COLOR);
            imageBox.add(status); // Tampilkan "LOCKED" di tengah
        }

        card.add(imageBox, BorderLayout.CENTER);

        return card;
    }
}