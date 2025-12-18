package ui;

import javax.swing.*;
import java.awt.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import db.DatabaseManager;
import model.card.SpecialCard;
import model.card.Rarity;
import core.shop.ShopCardPool;

// Halaman Koleksi Kartu Spesial
public class CardCollectionPanel extends JPanel {

    private static final Color BG_COLOR = new Color(20, 20, 20); // Background gelap
    private static final Color ACCENT_COLOR = new Color(255, 200, 0); // Warna aksen kuning (umum)
    private static final Color DANGER_COLOR = new Color(255, 50, 50); // Warna Merah untuk Status Locked
    private static final Color SUCCESS_COLOR = new Color(0, 200, 0); // Warna Hijau untuk Status Unlocked
    private static final Color CARD_SLOT_COLOR = new Color(40, 40, 40);

    // --- WARNA RARITY ---
    private static final Color RARE_GREEN = new Color(0, 180, 0);
    private static final Color SUPER_RARE_PURPLE = new Color(170, 0, 200);
    private static final Color LEGENDARY_YELLOW = new Color(255, 200, 0);

    private JPanel cardGridPanel;

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

        // 4. Grid Panel Holder
        cardGridPanel = new JPanel(new GridLayout(0, 5, 15, 15));
        cardGridPanel.setBackground(BG_COLOR);
        cardGridPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

        JScrollPane scrollPane = new JScrollPane(cardGridPanel);
        scrollPane.setBorder(null);
        scrollPane.getViewport().setBackground(BG_COLOR);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        add(scrollPane, BorderLayout.CENTER);

        refresh(); // Populates data
    }

    public void refresh() {
        cardGridPanel.removeAll();

        // 2. Load Data from DB
        List<SpecialCard> unlockedList = DatabaseManager.getUnlockedCards();
        Set<Integer> unlockedIds = unlockedList.stream()
                .map(SpecialCard::getId)
                .collect(Collectors.toSet());

        // 3. Get All Available Cards from Pool
        List<SpecialCard> allCards = ShopCardPool.getAllCards();

        for (SpecialCard card : allCards) {
            boolean isUnlocked = unlockedIds.contains(card.getId());
            cardGridPanel.add(createCardSlot(card, isUnlocked));
        }

        cardGridPanel.revalidate();
        cardGridPanel.repaint();
    }

    private JPanel createCardSlot(SpecialCard cardData, boolean isUnlocked) {
        String statusText = isUnlocked ? "UNLOCKED" : "LOCKED";
        Color statusColor = isUnlocked ? SUCCESS_COLOR : DANGER_COLOR;

        // Determine border color based on Rarity (if unlocked) or Status (if
        // locked/common)
        Color borderColor;
        if (cardData.getRarity() == Rarity.COMMON) {
            borderColor = statusColor;
        } else {
            borderColor = getRarityColor(cardData.getRarity());
        }

        JPanel cardPanel = new JPanel(new BorderLayout());
        cardPanel.setPreferredSize(new Dimension(150, 250)); // Adjusted height for image
        cardPanel.setBackground(CARD_SLOT_COLOR);

        // Border Logic
        if (isUnlocked && cardData.getRarity() != Rarity.COMMON) {
            cardPanel.setBorder(BorderFactory.createCompoundBorder(
                    new LineBorder(borderColor, 3, true),
                    new EmptyBorder(4, 4, 4, 4)));
        } else {
            // For locked or common, simpler border
            cardPanel.setBorder(BorderFactory.createCompoundBorder(
                    new LineBorder(statusColor, 1),
                    new EmptyBorder(5, 5, 5, 5)));
        }

        // Title
        String rarityLabel = cardData.getRarity().name().replace("_", " ");
        JLabel title = new JLabel(rarityLabel + " #" + cardData.getId(), SwingConstants.CENTER);
        title.setFont(new Font("Monospaced", Font.BOLD, 12));
        title.setForeground(statusColor);
        cardPanel.add(title, BorderLayout.NORTH);

        // Image Box
        JLabel imageLabel = new JLabel();
        imageLabel.setHorizontalAlignment(SwingConstants.CENTER);
        imageLabel.setVerticalAlignment(SwingConstants.CENTER);
        imageLabel.setOpaque(true);
        imageLabel.setBackground(new Color(60, 60, 60));

        if (isUnlocked) {
            // Load actual image
            // Load resized image (9:16 aspect ratio reference: 150x266 approx)
            // Let's use simpler fitting dimension, e.g. 130x200
            ImageIcon icon = CardImageLoader.loadCardResized(cardData.getName(), 130, 200);
            imageLabel.setIcon(icon);
            imageLabel.setText("");
        } else {
            imageLabel.setIcon(null);
            imageLabel.setText(statusText);
            imageLabel.setForeground(DANGER_COLOR);
            imageLabel.setFont(new Font("Monospaced", Font.BOLD, 16));
        }

        cardPanel.add(imageLabel, BorderLayout.CENTER);

        // Name (Bottom)
        JLabel nameLabel = new JLabel(cardData.getName(), SwingConstants.CENTER);
        nameLabel.setFont(new Font("SansSerif", Font.PLAIN, 12));
        nameLabel.setForeground(Color.LIGHT_GRAY);
        nameLabel.setBorder(new EmptyBorder(4, 0, 0, 0));
        cardPanel.add(nameLabel, BorderLayout.SOUTH);

        return cardPanel;
    }

    private Color getRarityColor(Rarity rarity) {
        return switch (rarity) {
            case RARE -> RARE_GREEN;
            case SUPER_RARE -> SUPER_RARE_PURPLE;
            case LEGENDARY -> LEGENDARY_YELLOW;
            default -> Color.GRAY;
        };
    }
}
