package ui.component;

import javax.swing.*;
import javax.swing.border.TitledBorder;

import model.card.SpecialCard;
import model.state.GameState;
import ui.CardImageLoader;

import java.awt.*;

public class SpecialCardAbilitiesPanel extends JPanel {

    private static final Color INFO_BG = new Color(30, 30, 30);
    private static final Color SLOT_BG = new Color(60, 60, 60);
    private static final Color TITLE_COLOR = new Color(255, 200, 0);

    public SpecialCardAbilitiesPanel() {
        setLayout(new GridLayout(2, 3, 5, 5)); // 2 rows, 3 columns, smaller gap
        setBackground(INFO_BG);

        setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(Color.GRAY.darker()),
                "SPECIAL CARDS", // Match Phase 1 Title
                TitledBorder.CENTER,
                TitledBorder.TOP,
                new Font("Monospaced", Font.BOLD, 14),
                TITLE_COLOR));

        Dimension slotSize = new Dimension(80, 120); // Match Phase 1 size (2:3 aspect roughly)

        for (int i = 0; i < 6; i++) {
            JLabel slot = new JLabel();
            slot.setPreferredSize(slotSize);
            slot.setBorder(BorderFactory.createLineBorder(Color.WHITE, 1)); // Match Phase 1 Border
            slot.setHorizontalAlignment(SwingConstants.CENTER);
            add(slot);
        }

        setMaximumSize(new Dimension(280, 300)); // Adjusted size for sidebar
        setAlignmentX(Component.CENTER_ALIGNMENT);
    }

    public void refresh(GameState state) {
        removeAll();

        java.util.List<SpecialCard> cards = state.getPlayerInventory().getCards();

        for (int i = 0; i < 6; i++) {
            JLabel slot = new JLabel();
            slot.setPreferredSize(new Dimension(80, 120));
            slot.setBorder(BorderFactory.createLineBorder(Color.WHITE, 1));
            slot.setHorizontalAlignment(SwingConstants.CENTER);

            if (i < cards.size()) {
                SpecialCard card = cards.get(i);
                // Use CardImageLoader but scale to 80x120
                ImageIcon icon = CardImageLoader.loadCardImage(card);
                Image image = icon.getImage().getScaledInstance(80, 120, Image.SCALE_SMOOTH);
                slot.setIcon(new ImageIcon(image));
            } else {
                // Empty slot styling
                slot.setIcon(null);
            }
            add(slot);
        }

        revalidate();
        repaint();
    }
}
