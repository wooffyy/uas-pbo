package ui;

import javax.swing.*;
import javax.swing.border.TitledBorder; // Import ini mungkin diperlukan jika belum ada
import java.awt.*;

// UI Trick-taking (Board Permainan)
public class Phase1Panel extends JPanel {

    private static final Color INFO_BG = new Color(30, 30, 30);
    private static final Color BOARD_BG = new Color(30, 60, 30);
    private static final Color TEXT_COLOR = Color.WHITE;
    private static final Color ACCENT_COLOR = new Color(255, 50, 50);
    private static final Color MONEY_COLOR = new Color(255, 200, 0);

    // Warna Latar Belakang Nilai (VALUE_BG) tidak terpakai lagi karena kotak dihapus

    public Phase1Panel(UIWindow parentFrame) {
        setLayout(new BorderLayout(10, 0));
        setBackground(Color.BLACK);

        add(createInfoPanel(parentFrame), BorderLayout.WEST);
        add(createBoardPanel(), BorderLayout.CENTER);
    }

    private JPanel createInfoPanel(UIWindow parentFrame) {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setPreferredSize(new Dimension(280, 0));
        panel.setBackground(INFO_BG);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 15, 20, 15)); // Ubah supaya flush kiri

        // FORCE LEFT ALIGNMENT
        final float LEFT = Component.LEFT_ALIGNMENT;


        JButton nextPhase = new JButton(" Back to menu ");
        nextPhase.setAlignmentX(LEFT);
        nextPhase.setBackground(ACCENT_COLOR.darker());
        nextPhase.setForeground(Color.WHITE);
        nextPhase.addActionListener(e -> parentFrame.switchView(UIWindow.MENU_VIEW));


        panel.add(nextPhase);
        panel.add(Box.createVerticalStrut(15));

        JLabel bossName = new JLabel("BOSS:", SwingConstants.LEFT);
        JLabel bossName2 = new JLabel("ELITE ENFORCER", SwingConstants.LEFT);

        bossName.setFont(new Font("Monospaced", Font.BOLD, 24));
        bossName2.setFont(new Font("Monospaced", Font.BOLD, 24));

        bossName.setForeground(ACCENT_COLOR);
        bossName2.setForeground(ACCENT_COLOR);

        bossName.setAlignmentX(LEFT);
        bossName2.setAlignmentX(LEFT);

        panel.add(bossName);
        panel.add(bossName2);
        panel.add(Box.createVerticalStrut(15));

        panel.add(fixAlign(createLabel("TARGET", Color.RED)));
        panel.add(fixAlign(createValueLabel("5 TRICKS", Color.YELLOW)));
        panel.add(Box.createVerticalStrut(10));

        panel.add(fixAlign(createLabel("TOTAL DEBT", Color.RED)));
        panel.add(fixAlign(createValueLabel("$1000", MONEY_COLOR)));
        panel.add(Box.createVerticalStrut(10));

        JPanel debtDetails = new JPanel(new GridLayout(1, 2, 5, 0));
        debtDetails.setBackground(INFO_BG);
        debtDetails.add(createUniformInfoBox("LATE CHARGE: 20%", Color.YELLOW));
        debtDetails.add(createUniformInfoBox("RATE: 5%", Color.YELLOW));
        debtDetails.setAlignmentX(LEFT);
        panel.add(debtDetails);
        panel.add(Box.createVerticalStrut(10));

        panel.add(fixAlign(createLabel("PLAYER'S MONEY", MONEY_COLOR)));
        panel.add(fixAlign(createValueLabel("$100", MONEY_COLOR)));
        panel.add(Box.createVerticalStrut(15));

        JLabel HealthText = new JLabel("Health", SwingConstants.LEFT); // Perbaikan constructor
        HealthText.setForeground(Color.GREEN); // Atur warna
        HealthText.setAlignmentX(Component.LEFT_ALIGNMENT); // Tambahkan alignment

        JLabel healthLabel = new JLabel("♥♥♥", SwingConstants.LEFT);
        healthLabel.setFont(new Font("Monospaced", Font.BOLD, 36));
        healthLabel.setForeground(ACCENT_COLOR);
        healthLabel.setAlignmentX(Component.LEFT_ALIGNMENT); // Perbaikan LEFT

        panel.add(HealthText);
        panel.add(healthLabel);

        panel.add(Box.createVerticalStrut(20));

        // Keep slot centered but not shifting upper content
        panel.add(Box.createVerticalGlue());
        JPanel slot = createSpecialCardSlot();
        slot.setAlignmentX(LEFT);
        panel.add(slot);
        panel.add(Box.createVerticalGlue());

        return panel;
    }
    private Component fixAlign(JComponent c) {
        c.setAlignmentX(Component.LEFT_ALIGNMENT);
        return c;
    }



    private JPanel createBoardPanel() {
        JPanel panel = new JPanel(new BorderLayout(5, 5));
        panel.setBackground(BOARD_BG);

        // TOP: Dealer's Hand Area (13 cards)
        JPanel dealerHandArea = createCardAreaPlaceholder("DEALER'S HAND (13 CARDS UNKNOWN)", 140, false, BOARD_BG.brighter());
        panel.add(dealerHandArea, BorderLayout.NORTH);

        // EAST: Trick Pile
        JPanel trickPilePanel = new JPanel(new GridLayout(2, 1, 5, 5));
        trickPilePanel.setOpaque(false);
        // PERUBAHAN DI SINI: Ganti nama label menjadi dua baris menggunakan spasi lebar
        trickPilePanel.add(createAreaBox("DEALER PILE (TRICKS LOST)", new Dimension(140, 180), ACCENT_COLOR.darker()));
        trickPilePanel.add(createAreaBox("PLAYER PILE (TRICKS WON)", new Dimension(140, 180), new Color(0, 150, 0)));
        panel.add(trickPilePanel, BorderLayout.EAST);

        // ... (kode createBoardPanel lainnya tetap)

        // CENTER: Area Bermain Kartu & Hand Pemain
        // Ganti BorderLayout dengan BoxLayout
        JPanel centerArea = new JPanel();
        centerArea.setLayout(new BoxLayout(centerArea, BoxLayout.Y_AXIS)); // Diubah ke Y_AXIS
        centerArea.setOpaque(false);

        // **PERUBAHAN UTAMA DI SINI:** Tambahkan Vertical Glue (Spacer)
        // Ini akan mendorong komponen di bawahnya (trickPlay) ke tengah.
        centerArea.add(Box.createVerticalGlue());

        // Tengah: Kartu yang dimainkan saat ini
        JPanel trickPlay = new JPanel(new FlowLayout(FlowLayout.CENTER, 50, 50));
        trickPlay.setOpaque(false);
        trickPlay.add(createTrickCardPlaceholder(false));
        trickPlay.add(createTrickCardPlaceholder(true));
        // Atur agar trickPlay berada di tengah secara horizontal
        trickPlay.setAlignmentX(Component.CENTER_ALIGNMENT);
        centerArea.add(trickPlay);

        // **Opsional:** Tambahkan Vertical Glue lagi jika ingin menengahkan trickPlay
        // secara sempurna di antara atas dan bawah centerArea (jika tidak ada komponen lain).
        // centerArea.add(Box.createVerticalGlue());

        // BOTTOM: Player's Hand Area (13 cards)
        JPanel playerHandArea = createCardAreaPlaceholder("PLAYER HAND (13 CARDS YOUR FATE)", 140, true, new Color(40, 70, 40));
        panel.add(playerHandArea, BorderLayout.SOUTH);

        panel.add(centerArea, BorderLayout.CENTER);
        return panel;
    }

    // --- Helper Methods ---

    /**
     * Membuat Label Status (Judul) yang diletakkan di luar kotak nilai.
     */
    private JLabel createLabel(String label, Color fgColor) {
        JLabel statusLabel = new JLabel(label, SwingConstants.LEFT);
        statusLabel.setForeground(fgColor);
        statusLabel.setFont(new Font("Monospaced", Font.BOLD, 12));
        statusLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        return statusLabel;
    }

    /**
     * Membuat Label Nilai yang ukurannya menyesuaikan isi, tanpa kotak solid.
     * Teks diinden 10px secara visual agar terstruktur di bawah label judul.
     */
    private JLabel createValueLabel(String value, Color fgColor) {
        JLabel valueLabel = new JLabel(value, SwingConstants.LEFT);
        valueLabel.setForeground(fgColor);
        valueLabel.setFont(new Font("Monospaced", Font.BOLD, 18));

        // Inden visual untuk tampilan yang lebih rapi
        valueLabel.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 0));
        valueLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        return valueLabel;
    }

    /**
     * Membuat kotak info kecil yang seragam (misal: Late Charge).
     */
    private JPanel createUniformInfoBox(String label, Color fgColor) {
        JPanel box = new JPanel(new GridLayout(1, 1));
        box.setBorder(BorderFactory.createLineBorder(Color.GRAY.darker()));
        box.setPreferredSize(new Dimension(220, 50));
        box.setBackground(new Color(50, 50, 50));

        JLabel statusLabel = new JLabel(label, SwingConstants.CENTER);
        statusLabel.setForeground(fgColor);
        statusLabel.setFont(new Font("Monospaced", Font.BOLD, 12));
        box.add(statusLabel);
        box.setAlignmentX(Component.LEFT_ALIGNMENT);
        return box;
    }

    /**
     * Membuat slot untuk Special Card Abilities (Inventory).
     */
    private JPanel createSpecialCardSlot() {
        JPanel panel = new JPanel(new GridLayout(2, 3, 15, 15));
        panel.setBackground(INFO_BG);

        panel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(Color.GRAY.darker()),
                "SPECIAL CARD ABILITIES",
                TitledBorder.CENTER, TitledBorder.TOP,
                new Font("Monospaced", Font.BOLD, 14),
                Color.ORANGE
        ));

        // Membuat kotak lebih besar dan lebih lebar
        Dimension slotSize = new Dimension(120, 120);

        for (int i = 0; i < 6; i++) {
            JPanel cardSlot = new JPanel();
            cardSlot.setBackground(new Color(60, 60, 60));
            cardSlot.setPreferredSize(slotSize);
            cardSlot.setBorder(BorderFactory.createLineBorder(Color.DARK_GRAY, 2));

            // Biar kartu bisa terlihat "slot" UI, sedikit rounded look
            cardSlot.setLayout(new BorderLayout());
            JLabel placeholder = new JLabel("EMPTY", SwingConstants.CENTER);
            placeholder.setForeground(Color.GRAY);
            placeholder.setFont(new Font("Monospaced", Font.PLAIN, 11));
            cardSlot.add(placeholder, BorderLayout.CENTER);

            panel.add(cardSlot);
        }

        // Agar benar-benar posisi tengah
        panel.setMaximumSize(new Dimension(400, 300));
        panel.setAlignmentX(Component.CENTER_ALIGNMENT);

        return panel;
    }




    private JPanel createCardAreaPlaceholder(String label, int height, boolean clickable, Color bgColor) {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 3, 3));
        panel.setPreferredSize(new Dimension(getWidth(), height));
        panel.setBackground(bgColor);
        panel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(Color.DARK_GRAY),
                label,
                0,
                0,
                new Font("Monospaced", Font.PLAIN, 10),
                Color.GRAY
        ));

        for (int i = 0; i < 13; i++) {
            JPanel card = createHandCardPlaceholder(clickable);
            panel.add(card);
        }
        return panel;
    }

    private JPanel createHandCardPlaceholder(boolean faceUp) {
        JPanel card = new JPanel();
        card.setPreferredSize(new Dimension(80, 120));
        card.setBorder(BorderFactory.createLineBorder(Color.WHITE, 1));
        card.setBackground(faceUp ? Color.WHITE : Color.BLACK);
        if (faceUp) {
            JLabel label = new JLabel("", SwingConstants.CENTER);
            label.setFont(new Font("Serif", Font.BOLD, 24));
            label.setForeground(Color.BLACK);
            card.add(label);
        }
        return card;
    }

    private JPanel createTrickCardPlaceholder(boolean faceUp) {
        JPanel card = new JPanel();
        card.setPreferredSize(new Dimension(100, 150));
        card.setBorder(BorderFactory.createLineBorder(Color.WHITE, 2));
        card.setBackground(faceUp ? Color.WHITE : Color.BLACK);
        if (faceUp) {
            JLabel label = new JLabel("A♠", SwingConstants.CENTER);
            label.setFont(new Font("Serif", Font.BOLD, 30));
            label.setForeground(Color.BLACK);
            card.add(label);
        }
        return card;
    }

    private JPanel createAreaBox(String label, Dimension dim, Color accent) {
        JPanel box = new JPanel(new BorderLayout());
        box.setPreferredSize(dim);
        box.setBackground(new Color(20, 20, 20));

        // Tulis teks TITLE di atas, subtext di bawah
        String[] splitText = label.split("\\(");
        String mainTitle = splitText[0].trim();
        String subText = "(" + splitText[1];

        TitledBorder title = BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(accent, 2),
                mainTitle,
                TitledBorder.CENTER,
                TitledBorder.TOP,
                new Font("Monospaced", Font.BOLD, 11),
                accent
        );
        box.setBorder(title);

        JLabel subLabel = new JLabel(subText, SwingConstants.CENTER);
        subLabel.setForeground(new Color(200, 200, 200));
        subLabel.setFont(new Font("Monospaced", Font.PLAIN, 10));
        box.add(subLabel, BorderLayout.SOUTH);

        return box;
    }

}
