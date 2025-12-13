package ui;

import javax.swing.*;
import java.awt.*;
import ui.component.SpecialCardAbilitiesPanel; // ✅ REUSE SLOT

public class BiddingPanel extends JPanel {

    private static final Color BG_DARK = new Color(25, 25, 25);
    private static final Color PANEL_GRAY = new Color(45, 45, 45);
    private static final Color TABLE_GREEN = new Color(30, 60, 30);

    private static final Color RARE_GREEN = new Color(0, 180, 0);
    private static final Color SUPER_RARE_PURPLE = new Color(170, 0, 200);
    private static final Color LEGENDARY_YELLOW = new Color(255, 200, 0);

    private final UIWindow parentFrame;

    public BiddingPanel(UIWindow parentFrame) {
        this.parentFrame = parentFrame;
        setLayout(new BorderLayout());
        setBackground(BG_DARK);

        add(createSidebar(), BorderLayout.WEST);
        add(createArena(), BorderLayout.CENTER);
    }

    /* ================= SIDEBAR ================= */

    private JPanel createSidebar() {
        JPanel sidebar = new JPanel();
        sidebar.setPreferredSize(new Dimension(260, 0));
        sidebar.setBackground(PANEL_GRAY);
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));

        sidebar.add(Box.createVerticalStrut(15));

        JLabel title = new JLabel("BIDDING ARENA");
        title.setFont(new Font("Monospaced", Font.BOLD, 22));
        title.setForeground(Color.WHITE);
        title.setAlignmentX(Component.CENTER_ALIGNMENT);
        sidebar.add(title);

        sidebar.add(Box.createVerticalStrut(20));
        sidebar.add(infoBox("Current Total Debt", "$1000"));
        sidebar.add(infoBox("Late Charge", "20%"));
        sidebar.add(infoBox("Interest Rate", "5%"));
        sidebar.add(infoBox("Player's Money", "$80"));

        sidebar.add(Box.createVerticalStrut(15));

        JLabel health = new JLabel("♥ ♥ ♥");
        health.setFont(new Font("Monospaced", Font.BOLD, 18));
        health.setForeground(Color.RED);
        health.setAlignmentX(Component.CENTER_ALIGNMENT);
        sidebar.add(health);

        sidebar.add(Box.createVerticalStrut(20));

        // 🔁 SLOT DIGANTI — IDENTIK DENGAN PHASE 1 & 2
        SpecialCardAbilitiesPanel abilitiesPanel = new SpecialCardAbilitiesPanel();
        abilitiesPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        sidebar.add(abilitiesPanel);

        return sidebar;
    }

    private JPanel infoBox(String title, String value) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setMaximumSize(new Dimension(220, 38));
        panel.setBackground(PANEL_GRAY);

        JLabel l = new JLabel(title);
        l.setForeground(Color.LIGHT_GRAY);
        JLabel v = new JLabel(value, SwingConstants.RIGHT);
        v.setForeground(Color.YELLOW);

        panel.add(l, BorderLayout.WEST);
        panel.add(v, BorderLayout.EAST);
        return panel;
    }

    /* ================= ARENA ================= */

    private JPanel createArena() {
        JPanel arena = new JPanel(new BorderLayout());
        arena.setBackground(TABLE_GREEN);

        // ===== TOP AREA : DEALER BID AREA =====
        JPanel dealerArea = new JPanel();
        dealerArea.setPreferredSize(new Dimension(0, 110));
        dealerArea.setBackground(new Color(200, 200, 200));
        dealerArea.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));

        JLabel dealerLabel = new JLabel("DEALER BID AREA");
        dealerLabel.setFont(new Font("Monospaced", Font.BOLD, 14));
        dealerLabel.setForeground(Color.DARK_GRAY);
        dealerArea.add(dealerLabel);

        arena.add(dealerArea, BorderLayout.NORTH);

        // ===== CENTER : BID CARDS =====
        JPanel cardRow = new JPanel(new GridLayout(1, 3, 40, 0));
        cardRow.setOpaque(false);
        cardRow.setBorder(BorderFactory.createEmptyBorder(60, 120, 60, 120));

        cardRow.add(createBidCard("RARE", RARE_GREEN));
        cardRow.add(createBidCard("SUPER RARE", SUPER_RARE_PURPLE));
        cardRow.add(createBidCard("LEGENDARY", LEGENDARY_YELLOW));

        arena.add(cardRow, BorderLayout.CENTER);

        // ===== BOTTOM AREA : PLAYER BID AREA + SKIP =====
        JPanel playerArea = new JPanel(new BorderLayout());
        playerArea.setPreferredSize(new Dimension(0, 130));
        playerArea.setBackground(new Color(200, 200, 200));
        playerArea.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));

        JLabel playerLabel = new JLabel("PLAYER BID AREA", SwingConstants.CENTER);
        playerLabel.setFont(new Font("Monospaced", Font.BOLD, 14));
        playerLabel.setForeground(Color.DARK_GRAY);

// panel tengah khusus label
        JPanel centerLabelPanel = new JPanel(new BorderLayout());
        centerLabelPanel.setOpaque(false);
        centerLabelPanel.add(playerLabel, BorderLayout.CENTER);

        JButton skip = new JButton("SKIP");
        styleButton(skip, false);
        skip.addActionListener(e -> {
            parentFrame.switchView(UIWindow.PHASE1_VIEW);
        });

        playerArea.add(centerLabelPanel, BorderLayout.CENTER);
        playerArea.add(skip, BorderLayout.EAST);

        arena.add(playerArea, BorderLayout.SOUTH);

        return arena;
    }

    private JPanel createBidCard(String rarity, Color borderColor) {
        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setOpaque(false);

        JPanel card = new JPanel();
        card.setPreferredSize(new Dimension(140, 200));
        card.setBackground(new Color(70, 70, 70));
        card.setBorder(BorderFactory.createLineBorder(borderColor, 3));

        JLabel label = new JLabel(rarity, SwingConstants.CENTER);
        label.setForeground(Color.WHITE);
        label.setFont(new Font("Monospaced", Font.BOLD, 14));
        card.setLayout(new BorderLayout());
        card.add(label, BorderLayout.CENTER);

        JButton bidBtn = new JButton("BID");
        styleButton(bidBtn, true);

        wrapper.add(card, BorderLayout.CENTER);
        wrapper.add(bidBtn, BorderLayout.SOUTH);

        return wrapper;
    }

    private void styleButton(JButton btn, boolean primary) {
        btn.setFont(new Font("Monospaced", Font.BOLD, 13));
        btn.setBackground(primary ? new Color(150, 0, 0) : new Color(80, 0, 0));
        btn.setForeground(Color.WHITE);
        btn.setBorder(BorderFactory.createLineBorder(Color.YELLOW, primary ? 2 : 1));
    }
}
