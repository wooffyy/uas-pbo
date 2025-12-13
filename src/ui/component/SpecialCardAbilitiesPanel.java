package ui.component;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;

public class SpecialCardAbilitiesPanel extends JPanel {

    private static final Color INFO_BG = new Color(30, 30, 30);
    private static final Color SLOT_BG = new Color(60, 60, 60);
    private static final Color TITLE_COLOR = new Color(255, 200, 0);

    public SpecialCardAbilitiesPanel() {
        setLayout(new GridLayout(2, 3, 15, 15));
        setBackground(INFO_BG);

        setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(Color.GRAY.darker()),
                "SPECIAL CARD ABILITIES",
                TitledBorder.CENTER,
                TitledBorder.TOP,
                new Font("Monospaced", Font.BOLD, 14),
                TITLE_COLOR
        ));

        Dimension slotSize = new Dimension(120, 120);

        for (int i = 0; i < 6; i++) {
            JPanel cardSlot = new JPanel(new BorderLayout());
            cardSlot.setBackground(SLOT_BG);
            cardSlot.setPreferredSize(slotSize);
            cardSlot.setBorder(BorderFactory.createLineBorder(Color.DARK_GRAY, 2));

            JLabel placeholder = new JLabel("EMPTY", SwingConstants.CENTER);
            placeholder.setForeground(Color.GRAY);
            placeholder.setFont(new Font("Monospaced", Font.PLAIN, 11));

            cardSlot.add(placeholder, BorderLayout.CENTER);
            add(cardSlot);
        }

        setMaximumSize(new Dimension(400, 300));
        setAlignmentX(Component.CENTER_ALIGNMENT);
    }
}
