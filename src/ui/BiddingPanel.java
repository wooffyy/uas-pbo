package ui;

import core.BiddingResult;
import core.GameManager;
import core.Phase2Game;
import java.awt.*;
import java.util.List;
import javax.swing.*;
import model.card.SpecialCard;
import model.state.GameState;
import model.state.PlayerInventory;
import ui.component.SpecialCardAbilitiesPanel;

public class BiddingPanel extends JPanel {

    private static final Color BG_DARK = new Color(25, 25, 25);
    private static final Color PANEL_GRAY = new Color(45, 45, 45);
    private static final Color TABLE_GREEN = new Color(30, 60, 30);
    private static final Color ACCENT_YELLOW = new Color(255, 200, 0);

    private final UIWindow parentFrame;
    private State currentState = State.SELECTION;

    // UI Components
    private JPanel arenaPanel;
    private JLabel moneyLabel;
    private JLabel healthLabel;
    private JLabel debtLabel;
    private JLabel interestLabel;
    private SpecialCardAbilitiesPanel abilitiesPanel;

    // Bidding Controls
    private int currentPlayerBid = 0;
    private JTextField bidValueField; // Changed from JLabel to JTextField
    private JLabel dealerBidLabel;

    enum State {
        SELECTION,
        BIDDING_WAR
    }

    public BiddingPanel(UIWindow parentFrame) {
        this.parentFrame = parentFrame;
        setLayout(new BorderLayout());
        setBackground(BG_DARK);

        add(createSidebar(), BorderLayout.WEST);

        arenaPanel = new JPanel(new BorderLayout());
        arenaPanel.setBackground(TABLE_GREEN);
        add(arenaPanel, BorderLayout.CENTER);
    }

    public void refresh() {
        // Refresh Sidebar Dynamic Data
        GameState gs = GameManager.getInstance().getGameState();
        moneyLabel.setText("$" + gs.getMoney());
        debtLabel.setText("$" + gs.getDebt());
        interestLabel.setText((int) (gs.getInterestRate() * 100) + "%");

        String hearts = "";
        for (int i = 0; i < gs.getPlayerHealth(); i++)
            hearts += "♥ ";
        healthLabel.setText(hearts.trim());

        abilitiesPanel.refresh(gs);

        // Re-render arena based on state
        // We only re-render if we are initializing or explicitly called.
        // Ideally, we keep the state, but refresh might be called on view switch.
        // If switching to view, we might want to ensure we're in SELECTION if it's a
        // fresh start.
        renderArena();
    }

    public void reset() {
        currentState = State.SELECTION;
        currentPlayerBid = 0;
        refresh();
    }

    private void renderArena() {
        arenaPanel.removeAll();

        if (currentState == State.SELECTION) {
            renderSelectionStage();
        } else if (currentState == State.BIDDING_WAR) {
            renderBiddingWarStage();
        }

        arenaPanel.revalidate();
        arenaPanel.repaint();
    }

    // ================== STATE 1: SELECTION ==================

    private void renderSelectionStage() {
        JLabel title = new JLabel("SELECT A CARD TO BID ON", SwingConstants.CENTER);
        title.setFont(new Font("Monospaced", Font.BOLD, 24));
        title.setForeground(Color.WHITE);
        title.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));
        arenaPanel.add(title, BorderLayout.NORTH);

        JPanel cardsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 40, 40));
        cardsPanel.setOpaque(false);

        Phase2Game p2 = GameManager.getInstance().getPhase2Game();
        // Ensure options are generated
        if (p2.getBiddingOptions() == null || p2.getBiddingOptions().isEmpty()) {
            p2.rollBiddingItem();
        }
        List<SpecialCard> options = p2.getBiddingOptions();

        if (options != null) {
            for (SpecialCard card : options) {
                cardsPanel.add(createSelectionCard(card));
            }
        }

        arenaPanel.add(cardsPanel, BorderLayout.CENTER);
    }

    private JPanel createSelectionCard(SpecialCard card) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setPreferredSize(new Dimension(200, 320));
        panel.setBackground(new Color(50, 50, 50));
        panel.setBorder(BorderFactory.createLineBorder(card.getRarity().getColor(), 3));

        JLabel name = new JLabel(card.getName(), SwingConstants.CENTER);
        name.setFont(new Font("Monospaced", Font.BOLD, 14));
        name.setForeground(card.getRarity().getColor());

        JLabel type = new JLabel(card.getRarity().name(), SwingConstants.CENTER);
        type.setForeground(Color.LIGHT_GRAY);

        // Resize image for selection view
        JLabel image = new JLabel(CardImageLoader.loadCardResized(card.getName(), 150, 220));
        image.setHorizontalAlignment(SwingConstants.CENTER);

        JButton selectBtn = new JButton("SELECT");
        styleButton(selectBtn, true);
        selectBtn.addActionListener(e -> selectCard(card));

        JPanel top = new JPanel(new GridLayout(2, 1));
        top.setOpaque(false);
        top.add(type);
        top.add(name);

        panel.add(top, BorderLayout.NORTH);
        panel.add(image, BorderLayout.CENTER);
        panel.add(selectBtn, BorderLayout.SOUTH);

        return panel;
    }

    private void selectCard(SpecialCard card) {
        GameManager.getInstance().getPhase2Game().selectBiddingItem(card);
        // Base bid suggestion (start at card price or 0?)
        // User image shows empty line or some value. Let's start at card price.
        currentPlayerBid = card.getPrice();
        currentState = State.BIDDING_WAR;
        renderArena();
    }

    // ================== STATE 2: BIDDING WAR ==================

    private void renderBiddingWarStage() {
        Phase2Game p2 = GameManager.getInstance().getPhase2Game();
        SpecialCard card = p2.getBiddingItem();

        // --- TOP: Title ---
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setOpaque(false);
        topPanel.setBorder(BorderFactory.createEmptyBorder(30, 0, 20, 0));
        JLabel title = new JLabel("PLACE YOUR BID", SwingConstants.CENTER);
        title.setFont(new Font("Monospaced", Font.BOLD, 24));
        title.setForeground(Color.WHITE);
        topPanel.add(title, BorderLayout.CENTER);

        dealerBidLabel = new JLabel("Dealer's Bid: $" + p2.getDealerBid(), SwingConstants.CENTER);
        dealerBidLabel.setFont(new Font("Monospaced", Font.PLAIN, 16));
        dealerBidLabel.setForeground(ACCENT_YELLOW);
        dealerBidLabel.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));
        topPanel.add(dealerBidLabel, BorderLayout.SOUTH);

        arenaPanel.add(topPanel, BorderLayout.NORTH);

        // --- CENTER: CARD ---
        JPanel centerPanel = new JPanel(new GridBagLayout());
        centerPanel.setOpaque(false);

        // Highlight border
        JPanel cardWrapper = new JPanel(new BorderLayout());
        cardWrapper.setBorder(BorderFactory.createLineBorder(Color.YELLOW, 4));
        cardWrapper.setBackground(new Color(50, 50, 50));

        JLabel cardImg = new JLabel(CardImageLoader.loadCardResized(card.getName(), 180, 280));
        cardWrapper.add(cardImg, BorderLayout.CENTER);

        centerPanel.add(cardWrapper);

        arenaPanel.add(centerPanel, BorderLayout.CENTER);

        // --- BOTTOM: CONTROLS ---
        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.setOpaque(false);
        bottomPanel.setBorder(BorderFactory.createEmptyBorder(20, 100, 50, 100));

        // Bid Input Controls
        JPanel controls = new JPanel(new FlowLayout(FlowLayout.CENTER, 30, 0));
        controls.setOpaque(false);

        // LEFT TRIANGLE with -
        JButton minus = new TriangleButton(false, "-");
        minus.addActionListener(e -> adjustBid(-10));

        // Value Layout - now using JTextField
        JPanel valuePanel = new JPanel(new BorderLayout());
        valuePanel.setOpaque(false);

        bidValueField = new JTextField(String.valueOf(currentPlayerBid));
        bidValueField.setFont(new Font("Arial", Font.BOLD, 40));
        bidValueField.setForeground(Color.WHITE);
        bidValueField.setBackground(BG_DARK);
        bidValueField.setHorizontalAlignment(JTextField.CENTER);
        bidValueField.setBorder(null);
        bidValueField.setPreferredSize(new Dimension(150, 50));
        bidValueField.setCaretColor(Color.WHITE);

        // Add action listener for Enter key
        bidValueField.addActionListener(e -> {
            try {
                int val = Integer.parseInt(bidValueField.getText());
                if (val < 0)
                    val = 0;
                currentPlayerBid = val;
            } catch (NumberFormatException ex) {
                bidValueField.setText(String.valueOf(currentPlayerBid));
            }
        });

        // Focus listener to update when user clicks away
        bidValueField.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                try {
                    int val = Integer.parseInt(bidValueField.getText());
                    if (val < 0)
                        val = 0;
                    currentPlayerBid = val;
                } catch (NumberFormatException ex) {
                    bidValueField.setText(String.valueOf(currentPlayerBid));
                }
            }
        });

        JSeparator line = new JSeparator();
        line.setForeground(Color.WHITE);
        line.setPreferredSize(new Dimension(150, 2));

        valuePanel.add(bidValueField, BorderLayout.CENTER);
        valuePanel.add(line, BorderLayout.SOUTH);

        // RIGHT TRIANGLE with +
        JButton plus = new TriangleButton(true, "+");
        plus.addActionListener(e -> adjustBid(10));

        controls.add(minus);
        controls.add(valuePanel);
        controls.add(plus);

        // Action Buttons
        JPanel actions = new JPanel(new FlowLayout(FlowLayout.CENTER));
        actions.setOpaque(false);
        actions.setBorder(BorderFactory.createEmptyBorder(20, 0, 0, 0));

        JButton bidBtn = new JButton("Bid");
        bidBtn.setFont(new Font("Arial", Font.BOLD, 16));
        bidBtn.setBackground(Color.GRAY);
        bidBtn.setForeground(Color.WHITE);
        bidBtn.setPreferredSize(new Dimension(120, 45));
        bidBtn.setFocusPainted(false);
        bidBtn.addActionListener(e -> submitBid());

        actions.add(bidBtn);

        JPanel mainControlWrapper = new JPanel(new BorderLayout());
        mainControlWrapper.setOpaque(false);
        mainControlWrapper.add(controls, BorderLayout.CENTER);
        mainControlWrapper.add(actions, BorderLayout.SOUTH);

        bottomPanel.add(mainControlWrapper, BorderLayout.CENTER);

        JButton skipBtn = new JButton("Skip");
        styleButton(skipBtn, false); // Grey button
        skipBtn.addActionListener(e -> finishBidding());

        JPanel skipWrapper = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        skipWrapper.setOpaque(false);
        skipWrapper.add(skipBtn);
        bottomPanel.add(skipWrapper, BorderLayout.SOUTH);

        arenaPanel.add(bottomPanel, BorderLayout.SOUTH);
    }

    private void adjustBid(int amount) {
        currentPlayerBid += amount;
        if (currentPlayerBid < 0)
            currentPlayerBid = 0;
        bidValueField.setText(String.valueOf(currentPlayerBid));
    }

    private void submitBid() {
        // Parse current text field value first
        try {
            int val = Integer.parseInt(bidValueField.getText());
            if (val < 0)
                val = 0;
            currentPlayerBid = val;
        } catch (NumberFormatException ex) {
            bidValueField.setText(String.valueOf(currentPlayerBid));
        }

        GameState gs = GameManager.getInstance().getGameState();
        Phase2Game p2 = GameManager.getInstance().getPhase2Game();

        // Safe insufficient funds check - just show warning, don't skip
        if (gs.getMoney() < currentPlayerBid) {
            JOptionPane.showMessageDialog(this,
                    "Uang anda kurang untuk melakukan bid ini.",
                    "Uang Kurang",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            BiddingResult result = p2.placePlayerBid(currentPlayerBid);

            if (result.getStatus() == BiddingResult.Status.WIN) {
                String message = String.format("You won the auction!\nYour bid: $%d\nDealer's bid: $%d",
                        result.getPlayerBid(), result.getDealerBid());
                JOptionPane.showMessageDialog(this, message, "Victory", JOptionPane.INFORMATION_MESSAGE);
                addCardToInventory(p2.getBiddingItem(), result.getPlayerBid());
            } else if (result.getStatus() == BiddingResult.Status.ONGOING) {
                // Dealer has re-bid, update UI to show the new state
                String message = String.format(
                        "The dealer outbid you!\nTheir new bid is $%d. Place a higher bid or skip.",
                        result.getDealerBid());
                JOptionPane.showMessageDialog(this, message, "Outbid!", JOptionPane.INFORMATION_MESSAGE);
                currentPlayerBid = result.getDealerBid() + 10; // Suggest a new bid
                bidValueField.setText(String.valueOf(currentPlayerBid));
                dealerBidLabel.setText("Dealer's Bid: $" + result.getDealerBid());
            }
            // The 'LOSE' case is handled by the player explicitly skipping.
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Bid Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void addCardToInventory(SpecialCard card, int cost) {
        GameState gs = GameManager.getInstance().getGameState();
        PlayerInventory inv = gs.getPlayerInventory();

        // Check Inventory Full
        if (inv.getCards().size() >= 6) {
            List<SpecialCard> owned = inv.getCards();
            String[] options = new String[owned.size() + 1];
            for (int i = 0; i < owned.size(); i++) {
                options[i] = owned.get(i).getName();
            }
            options[owned.size()] = "Cancel";

            int choice = JOptionPane.showOptionDialog(this,
                    "Inventory Full! Choose a card to replace:",
                    "Replace Card",
                    JOptionPane.DEFAULT_OPTION,
                    JOptionPane.WARNING_MESSAGE,
                    null,
                    options,
                    options[owned.size()]);

            if (choice >= 0 && choice < owned.size()) {
                inv.getCards().remove(choice);
                // Proceed to buy
            } else {
                // Cancelled means we forfeit the win?
                finishBidding(); // Forfeit if cancelled
                return;
            }
        }

        gs.decreaseMoney(cost);
        gs.addInventory(card);
        GameManager.getInstance().onPhase2Finish(); // Proceed to next game phase after winning
    }

    private void showOverlay(String msg) {
        Object[] options = { "Lanjut ke Stage Berikutnya" };
        int choice = JOptionPane.showOptionDialog(this,
                msg,
                "Uang Kurang",
                JOptionPane.OK_OPTION,
                JOptionPane.WARNING_MESSAGE,
                null,
                options,
                options[0]);

        if (choice == JOptionPane.OK_OPTION || choice == JOptionPane.CLOSED_OPTION) {
            finishBidding();
        }
    }

    private void finishBidding() {
        // If player skips, it's a loss.
        Phase2Game p2 = GameManager.getInstance().getPhase2Game();
        BiddingResult result = p2.playerSkips();
        String message = String.format("You skipped the auction and lost.\nYour last bid: $%d\nDealer's bid: $%d",
                result.getPlayerBid(), result.getDealerBid());
        JOptionPane.showMessageDialog(this, message, "Auction Lost", JOptionPane.INFORMATION_MESSAGE);
        GameManager.getInstance().onPhase2Finish(); // Proceed to next game phase
    }

    /* ================= SIDEBAR ================= */

    private JPanel createSidebar() {
        JPanel sidebar = new JPanel();
        sidebar.setPreferredSize(new Dimension(280, 0));
        sidebar.setBackground(new Color(30, 30, 30)); // Match Phase1
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setBorder(BorderFactory.createEmptyBorder(20, 15, 20, 15));

        JLabel title = new JLabel("BIDDING ARENA");
        title.setFont(new Font("Monospaced", Font.BOLD, 24));
        title.setForeground(Color.WHITE);
        title.setAlignmentX(Component.LEFT_ALIGNMENT);
        sidebar.add(title);

        sidebar.add(Box.createVerticalStrut(20));

        // INTEREST RATE
        sidebar.add(fixAlign(createLabel("INTEREST RATE", Color.RED)));
        interestLabel = createValueLabel((int) (GameManager.getInstance().getGameState().getInterestRate() * 100) + "%",
                ACCENT_YELLOW);
        sidebar.add(fixAlign(interestLabel));
        sidebar.add(Box.createVerticalStrut(10));

        // TOTAL DEBT
        sidebar.add(fixAlign(createLabel("TOTAL DEBT", Color.RED)));
        debtLabel = createValueLabel("$" + GameManager.getInstance().getGameState().getDebt(), ACCENT_YELLOW);
        sidebar.add(fixAlign(debtLabel));
        sidebar.add(Box.createVerticalStrut(5));

        // LAST INTEREST (Added for consistency)
        double added = GameManager.getInstance().getGameState().getLastInterestAdded();
        JLabel interestAmountLabel = new JLabel("(+$" + String.format("%.1f", added) + ")", SwingConstants.LEFT);
        interestAmountLabel.setForeground(new Color(255, 100, 100));
        interestAmountLabel.setFont(new Font("Monospaced", Font.ITALIC, 11));
        interestAmountLabel.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 0));
        interestAmountLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        sidebar.add(fixAlign(interestAmountLabel));

        sidebar.add(Box.createVerticalStrut(10));

        // PLAYER MONEY
        sidebar.add(fixAlign(createLabel("PLAYER MONEY", Color.RED)));
        moneyLabel = createValueLabel("$" + GameManager.getInstance().getGameState().getMoney(), ACCENT_YELLOW);
        sidebar.add(fixAlign(moneyLabel));
        sidebar.add(Box.createVerticalStrut(15));

        // HEALTH
        JLabel healthText = new JLabel("Health", SwingConstants.LEFT);
        healthText.setForeground(Color.GREEN);
        healthText.setAlignmentX(Component.LEFT_ALIGNMENT);
        sidebar.add(healthText);

        healthLabel = new JLabel();
        healthLabel.setFont(new Font("Monospaced", Font.BOLD, 36));
        healthLabel.setForeground(Color.RED);
        healthLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        sidebar.add(healthLabel);

        // Initial health set
        String hearts = "";
        for (int i = 0; i < GameManager.getInstance().getGameState().getPlayerHealth(); i++)
            hearts += "♥ ";
        healthLabel.setText(hearts.trim());

        sidebar.add(Box.createVerticalStrut(20));

        abilitiesPanel = new SpecialCardAbilitiesPanel();
        abilitiesPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        sidebar.add(abilitiesPanel);
        // Refresh abilities once
        abilitiesPanel.refresh(GameManager.getInstance().getGameState());

        return sidebar;
    }

    private void styleButton(JButton btn, boolean primary) {
        btn.setFont(new Font("Monospaced", Font.BOLD, 13));
        btn.setBackground(primary ? new Color(150, 0, 0) : new Color(80, 0, 0));
        btn.setForeground(Color.WHITE);
        btn.setBorder(BorderFactory.createLineBorder(Color.YELLOW, primary ? 2 : 1));
        btn.setFocusPainted(false);
        btn.setPreferredSize(new Dimension(100, 35));
    }

    private void styleControlIcon(JButton btn) {
        // No longer needed
    }

    // CUSTOM BUTTON FOR TRIANGLES
    private class TriangleButton extends JButton {
        private boolean right; // true for right (>), false for left (<)
        private String symbol; // + or -

        public TriangleButton(boolean right, String symbol) {
            this.right = right;
            this.symbol = symbol;
            setContentAreaFilled(false);
            setFocusPainted(false);
            setBorderPainted(false);
            setPreferredSize(new Dimension(50, 50));
            setCursor(new Cursor(Cursor.HAND_CURSOR));
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            // Button state
            if (getModel().isPressed()) {
                g2.setColor(new Color(100, 100, 100)); // Darker on press
            } else if (getModel().isRollover()) {
                g2.setColor(new Color(180, 180, 180)); // Lighter on hover
            } else {
                g2.setColor(Color.GRAY); // Default
            }

            int w = getWidth();
            int h = getHeight();
            int[] xPoints;
            int[] yPoints;

            if (right) {
                // Triangle pointing right >
                xPoints = new int[] { 5, w - 5, 5 };
                yPoints = new int[] { 5, h / 2, h - 5 };
            } else {
                // Triangle pointing left <
                xPoints = new int[] { w - 5, 5, w - 5 };
                yPoints = new int[] { 5, h / 2, h - 5 };
            }

            g2.fillPolygon(xPoints, yPoints, 3);

            // Draw Symbol (+ or -)
            g2.setColor(Color.WHITE);
            g2.setFont(new Font("Arial", Font.BOLD, 22));
            FontMetrics fm = g2.getFontMetrics();
            int sw = fm.stringWidth(symbol);
            int sh = fm.getAscent();

            // Center roughly based on triangle shape
            int tx = (w - sw) / 2;
            if (right)
                tx -= 3; // Shift slightly left for visual balance
            else
                tx += 3;

            int ty = (h + sh) / 2 - 4;

            g2.drawString(symbol, tx, ty);

            g2.dispose();
            super.paintComponent(g);
        }
    }

    private JLabel createLabel(String text, Color fgColor) {
        JLabel label = new JLabel(text, SwingConstants.LEFT);
        label.setForeground(fgColor);
        label.setFont(new Font("Monospaced", Font.BOLD, 12));
        label.setAlignmentX(Component.LEFT_ALIGNMENT);
        return label;
    }

    private JLabel createValueLabel(String value, Color fgColor) {
        JLabel label = new JLabel(value, SwingConstants.LEFT);
        label.setForeground(fgColor);
        label.setFont(new Font("Monospaced", Font.BOLD, 18));
        label.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 0));
        label.setAlignmentX(Component.LEFT_ALIGNMENT);
        return label;
    }

    private Component fixAlign(JComponent c) {
        c.setAlignmentX(Component.LEFT_ALIGNMENT);
        return c;
    }
}
