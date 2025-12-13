package ui;

import javax.swing.*;
import java.awt.*;
import ui.component.SpecialCardAbilitiesPanel; // ✅ IMPORT BARU

public class Phase2ShopPanel extends JPanel {

    private static final Color BG_DARK = new Color(25, 25, 25);
    private static final Color PANEL_GRAY = new Color(45, 45, 45);
    private static final Color ACCENT_RED = new Color(150, 0, 0);
    private static final Color ACCENT_YELLOW = new Color(255, 200, 0);

    private final UIWindow parentFrame;

    public Phase2ShopPanel(UIWindow parentFrame) {
        this.parentFrame = parentFrame;
        setLayout(new BorderLayout());
        setBackground(BG_DARK);

        add(createSidebar(), BorderLayout.WEST);
        add(createShopArea(), BorderLayout.CENTER);
    }

    /* ================= SIDEBAR ================= */

    private JPanel createSidebar() {
        JPanel sidebar = new JPanel();
        sidebar.setPreferredSize(new Dimension(260, 0));
        sidebar.setBackground(PANEL_GRAY);
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));

        sidebar.add(Box.createVerticalStrut(15));

        JLabel title = new JLabel("SHOP");
        title.setFont(new Font("Monospaced", Font.BOLD, 26));
        title.setForeground(Color.WHITE);
        title.setAlignmentX(Component.CENTER_ALIGNMENT);
        sidebar.add(title);

        sidebar.add(Box.createVerticalStrut(20));
        sidebar.add(infoLabel("Current Total Debt", "$1000"));
        sidebar.add(infoLabel("Late Charge", "20%"));
        sidebar.add(infoLabel("Interest Rate", "5%"));
        sidebar.add(infoLabel("Player's Money", "$100"));
        sidebar.add(Box.createVerticalStrut(15));

        JLabel health = new JLabel("♥ ♥ ♥");
        health.setForeground(Color.RED);
        health.setFont(new Font("Monospaced", Font.BOLD, 18));
        health.setAlignmentX(Component.CENTER_ALIGNMENT);
        sidebar.add(health);

        sidebar.add(Box.createVerticalStrut(20));

        // 🔁 SLOT DIGANTI — TANPA UBAH POSISI / LAYOUT
        SpecialCardAbilitiesPanel abilitiesPanel = new SpecialCardAbilitiesPanel();
        abilitiesPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        sidebar.add(abilitiesPanel);

        return sidebar;
    }

    private JPanel infoLabel(String title, String value) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setMaximumSize(new Dimension(220, 40));
        panel.setBackground(PANEL_GRAY);

        JLabel t = new JLabel(title);
        t.setForeground(Color.LIGHT_GRAY);
        JLabel v = new JLabel(value, SwingConstants.RIGHT);
        v.setForeground(ACCENT_YELLOW);

        panel.add(t, BorderLayout.WEST);
        panel.add(v, BorderLayout.EAST);
        return panel;
    }

    /* ================= SHOP AREA ================= */

    private JPanel createShopArea() {
        JPanel shopArea = new JPanel(new BorderLayout());
        shopArea.setBackground(new Color(30, 60, 30)); // meja hijau

        JPanel grid = new JPanel(new GridLayout(2, 3, 20, 20));
        grid.setOpaque(false);
        grid.setBorder(BorderFactory.createEmptyBorder(40, 40, 40, 40));

        for (int i = 0; i < 6; i++) {
            grid.add(createShopCard("Common Effect", "$30"));
        }

        shopArea.add(grid, BorderLayout.CENTER);

        JPanel controls = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        controls.setOpaque(false);

        JButton reroll = new JButton("REROLL");
        JButton toBidding = new JButton("GO TO BIDDING ARENA");
        styleButton(toBidding, true);

        toBidding.addActionListener(e -> {
            parentFrame.switchView(UIWindow.BIDDING_VIEW);
        });

        styleButton(reroll, false);
        styleButton(toBidding, true);

        controls.add(reroll);
        controls.add(toBidding);

        shopArea.add(controls, BorderLayout.SOUTH);

        return shopArea;
    }

    private JPanel createShopCard(String name, String price) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(new Color(50, 50, 50));
        card.setBorder(BorderFactory.createLineBorder(Color.BLACK));

        JLabel title = new JLabel(name, SwingConstants.CENTER);
        title.setForeground(Color.WHITE);

        JLabel cost = new JLabel(price, SwingConstants.CENTER);
        cost.setForeground(ACCENT_YELLOW);

        JButton buy = new JButton("BUY");
        styleButton(buy, false);

        card.add(title, BorderLayout.NORTH);
        card.add(cost, BorderLayout.CENTER);
        card.add(buy, BorderLayout.SOUTH);

        return card;
    }

    private void styleButton(JButton btn, boolean highlight) {
        btn.setFont(new Font("Monospaced", Font.BOLD, 14));
        btn.setBackground(highlight ? ACCENT_RED : new Color(80, 0, 0));
        btn.setForeground(Color.WHITE);
        btn.setBorder(BorderFactory.createLineBorder(ACCENT_YELLOW, highlight ? 2 : 1));
    }
}
