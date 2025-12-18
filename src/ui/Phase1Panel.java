package ui;

import core.GameManager;
import core.Phase1Game;
import model.card.Card;
import model.card.SpecialCard;
import model.state.GameState;
import model.state.PlayerInventory;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.text.DecimalFormat;
import java.util.List;

public class Phase1Panel extends JPanel {

    private final GameManager gameManager;
    private static final Color INFO_BG = new Color(30, 30, 30);
    private static final Color BOARD_BG = new Color(30, 60, 30);
    private static final Color ACCENT_COLOR = new Color(255, 50, 50);
    private static final Color MONEY_COLOR = new Color(255, 200, 0);

    private JPanel playerHandArea;
    private JPanel dealerHandArea;
    private JPanel trickPlayArea;

    private JLabel playerTrickPileLabel;
    private JLabel dealerTrickPileLabel;

    private JLabel healthLabel;
    private JLabel moneyLabel;
    private JLabel debtLabel;
    private JLabel targetLabel;
    private JLabel interestRateValueLabel;
    private JLabel bossNameLabel;
    private JLabel[] specialCardLabels;

    public Phase1Panel(UIWindow parentFrame, GameManager gameManager) {
        this.gameManager = gameManager;
        setLayout(new BorderLayout(10, 0));
        setBackground(Color.BLACK);

        add(createInfoPanel(parentFrame), BorderLayout.WEST);
        add(createBoardPanel(), BorderLayout.CENTER);

        updateInfoPanel();
    }

    public void refresh() {
        updateInfoPanel();
        updateBoard();
    }

    private void updateInfoPanel() {
        GameState state = gameManager.getGameState();
        Phase1Game phase1 = gameManager.getPhase1Game();
        int tricksToWin = 7;

        healthLabel.setText("♥".repeat(Math.max(0, state.getPlayerHealth())));
        moneyLabel.setText("$" + state.getMoney());
        debtLabel.setText("$" + state.getDebt());
        targetLabel.setText((tricksToWin - phase1.getTricksWon()) + " TRICKS TO WIN");

        DecimalFormat df = new DecimalFormat("0.0#");
        interestRateValueLabel.setText(df.format(state.getInterestRate() * 100) + "%");


        if (state.getCurrentDealer() != null) {
            bossNameLabel.setText(state.getCurrentDealer().getName());
        }
    }

    private void updateBoard() {
        playerHandArea.removeAll();
        dealerHandArea.removeAll();
        trickPlayArea.removeAll();

        List<Card> playerHand = gameManager.getGameState().getPlayerHand();
        for (Card card : playerHand) {
            playerHandArea.add(createHandCard(card, true));
        }

        int dealerHandSize = gameManager.getGameState().getDealerHand().size();
        for (int i = 0; i < dealerHandSize; i++) {
            dealerHandArea.add(createHandCard(null, false));
        }

        trickPlayArea.add(createTrickCard(gameManager.getGameState().getDealerPlayedCard()));
        trickPlayArea.add(createTrickCard(gameManager.getGameState().getPlayerPlayedCard()));

        Phase1Game phase1 = gameManager.getPhase1Game();
        playerTrickPileLabel.setText("PLAYER PILE : " + phase1.getTricksWon() + " TRICKS WIN");
        dealerTrickPileLabel.setText("DEALER PILE : " + phase1.getTricksLost() + " TRICKS WIN");

        updateSpecialCardSlots();

        playerHandArea.revalidate();
        playerHandArea.repaint();
        dealerHandArea.revalidate();
        dealerHandArea.repaint();
        trickPlayArea.revalidate();
        trickPlayArea.repaint();

        playerTrickPileLabel.revalidate();
        playerTrickPileLabel.repaint();
        dealerTrickPileLabel.revalidate();
        dealerTrickPileLabel.repaint();
    }

    private void updateSpecialCardSlots() {
        PlayerInventory inventory = gameManager.getGameState().getPlayerInventory();
        List<SpecialCard> cards = inventory.getCards();

        for (int i = 0; i < specialCardLabels.length; i++) {
            if (i < cards.size()) {
                SpecialCard card = cards.get(i);
                ImageIcon icon = CardImageLoader.loadCardImage(card);
                Image image = icon.getImage().getScaledInstance(80, 120, Image.SCALE_SMOOTH);
                specialCardLabels[i].setIcon(new ImageIcon(image));
            } else {
                specialCardLabels[i].setIcon(null);
            }
        }
    }

    private JPanel createInfoPanel(UIWindow parentFrame) {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setPreferredSize(new Dimension(280, 0));
        panel.setBackground(INFO_BG);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 15, 20, 15));

        final float LEFT = Component.LEFT_ALIGNMENT;

        JButton backButton = new JButton(" Back to menu ");
        backButton.setAlignmentX(LEFT);
        backButton.setBackground(ACCENT_COLOR.darker());
        backButton.setForeground(Color.WHITE);
        backButton.addActionListener(e -> parentFrame.switchView(UIWindow.MENU_VIEW));
        panel.add(backButton);
        panel.add(Box.createVerticalStrut(15));

        JLabel bossLabel = new JLabel("BOSS:", SwingConstants.LEFT);
        bossNameLabel = new JLabel("ELITE ENFORCER", SwingConstants.LEFT);
        bossLabel.setFont(new Font("Monospaced", Font.BOLD, 24));
        bossNameLabel.setFont(new Font("Monospaced", Font.BOLD, 24));
        bossLabel.setForeground(ACCENT_COLOR);
        bossNameLabel.setForeground(ACCENT_COLOR);
        bossLabel.setAlignmentX(LEFT);
        bossNameLabel.setAlignmentX(LEFT);
        panel.add(bossLabel);
        panel.add(bossNameLabel);
        panel.add(Box.createVerticalStrut(15));

        panel.add(fixAlign(createLabel("TARGET", Color.RED)));
        targetLabel = createValueLabel("7 TRICKS", Color.YELLOW);
        panel.add(fixAlign(targetLabel));
        panel.add(Box.createVerticalStrut(10));

        panel.add(fixAlign(createLabel("INTEREST RATE", Color.RED)));
        interestRateValueLabel = createValueLabel("0.1%", Color.YELLOW);
        panel.add(fixAlign(interestRateValueLabel));
        panel.add(Box.createVerticalStrut(10));

        panel.add(fixAlign(createLabel("TOTAL DEBT", Color.RED)));
        debtLabel = createValueLabel("$1000", MONEY_COLOR);
        panel.add(fixAlign(debtLabel));
        panel.add(Box.createVerticalStrut(10));

        panel.add(fixAlign(createLabel("PLAYER'S MONEY", Color.RED)));
        moneyLabel = createValueLabel("$100", MONEY_COLOR);
        panel.add(fixAlign(moneyLabel));
        panel.add(Box.createVerticalStrut(15));

        JLabel healthText = new JLabel("Health", SwingConstants.LEFT);
        healthText.setForeground(Color.GREEN);
        healthText.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(healthText);

        healthLabel = new JLabel("♥♥♥", SwingConstants.LEFT);
        healthLabel.setFont(new Font("Monospaced", Font.BOLD, 36));
        healthLabel.setForeground(ACCENT_COLOR);
        healthLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(healthLabel);
        panel.add(Box.createVerticalStrut(20));

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

        dealerHandArea = createCardAreaPanel("DEALER'S HAND", 140, BOARD_BG.brighter());
        panel.add(dealerHandArea, BorderLayout.NORTH);

        // Panel to hold center area and right-side piles
        JPanel gameBoardCenterPanel = new JPanel(new BorderLayout(5, 5));
        gameBoardCenterPanel.setOpaque(false);

        // Center Area (trick play area)
        JPanel centerArea = new JPanel();
        centerArea.setLayout(new BoxLayout(centerArea, BoxLayout.Y_AXIS));
        centerArea.setOpaque(false);
        centerArea.add(Box.createVerticalGlue());

        trickPlayArea = new JPanel(new FlowLayout(FlowLayout.CENTER, 50, 50));
        trickPlayArea.setOpaque(false);
        centerArea.add(trickPlayArea);
        centerArea.add(Box.createVerticalGlue());
        gameBoardCenterPanel.add(centerArea, BorderLayout.CENTER);

        // Right column container to push piles to the bottom
        JPanel rightColumnContainer = new JPanel(new BorderLayout());
        rightColumnContainer.setOpaque(false);

        // Stack Dealer Pile and Player Pile vertically
        JPanel pilesStackPanel = new JPanel();
        pilesStackPanel.setLayout(new BoxLayout(pilesStackPanel, BoxLayout.Y_AXIS));
        pilesStackPanel.setOpaque(false);

        // Dealer Pile
        JPanel dealerPileWrapper = new JPanel(new FlowLayout(FlowLayout.CENTER));
        dealerPileWrapper.setOpaque(false);
        dealerTrickPileLabel = createTrickPileLabel("DEALER PILE : 0 TRICKS WIN", ACCENT_COLOR.darker());
        dealerPileWrapper.add(dealerTrickPileLabel);
        pilesStackPanel.add(dealerPileWrapper);

        // Player Pile
        JPanel playerPileWrapper = new JPanel(new FlowLayout(FlowLayout.CENTER));
        playerPileWrapper.setOpaque(false);
        playerTrickPileLabel = createTrickPileLabel("PLAYER PILE : 0 TRICKS WIN", new Color(0, 150, 0));
        playerPileWrapper.add(playerTrickPileLabel);
        pilesStackPanel.add(playerPileWrapper);

        rightColumnContainer.add(pilesStackPanel, BorderLayout.SOUTH); // Piles stacked at the SOUTH of rightColumnContainer
        gameBoardCenterPanel.add(rightColumnContainer, BorderLayout.EAST); // Add rightColumnContainer to the EAST of gameBoardCenterPanel
        panel.add(gameBoardCenterPanel, BorderLayout.CENTER); // Add gameBoardCenterPanel to the main panel's CENTER

        // Player Hand Area (at the bottom)
        playerHandArea = createCardAreaPanel("PLAYER HAND", 140, new Color(40, 70, 40));
        panel.add(playerHandArea, BorderLayout.SOUTH);

        updateBoard();

        return panel;
    }

    private JPanel createCardAreaPanel(String label, int height, Color bgColor) {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 3, 3));
        panel.setPreferredSize(new Dimension(getWidth(), height));
        panel.setBackground(bgColor);
        panel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(Color.DARK_GRAY),
                label, 0, 0, new Font("Monospaced", Font.PLAIN, 10), Color.GRAY));
        return panel;
    }

    private JLabel createHandCard(Card card, boolean isPlayer) {
        ImageIcon icon = CardImageLoader.loadCardImage(isPlayer ? card : null);
        Image image = icon.getImage().getScaledInstance(80, 120, Image.SCALE_SMOOTH);
        JLabel cardLabel = new JLabel(new ImageIcon(image));
        cardLabel.setBorder(BorderFactory.createLineBorder(Color.WHITE, 1));

        if (isPlayer && card != null) {
            cardLabel.setCursor(new Cursor(Cursor.HAND_CURSOR));
            cardLabel.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    gameManager.playCard(card);
                }
            });
        }
        return cardLabel;
    }

    private JLabel createTrickCard(Card card) {
        if (card == null) {
            JLabel emptyLabel = new JLabel();
            emptyLabel.setPreferredSize(new Dimension(100, 150));
            return emptyLabel;
        }
        ImageIcon icon = CardImageLoader.loadCardImage(card);
        Image image = icon.getImage().getScaledInstance(100, 150, Image.SCALE_SMOOTH);
        JLabel cardLabel = new JLabel(new ImageIcon(image));
        cardLabel.setBorder(BorderFactory.createLineBorder(Color.WHITE, 2));
        return cardLabel;
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

    private JPanel createSpecialCardSlot() {
        JPanel panel = new JPanel(new BorderLayout()); // Main container for special card area
        panel.setBackground(INFO_BG);
        panel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(Color.GRAY.darker()),
                "SPECIAL CARDS", TitledBorder.CENTER, TitledBorder.TOP,
                new Font("Monospaced", Font.BOLD, 14), Color.ORANGE));

        // Grid for the 6 card slots
        JPanel cardGrid = new JPanel(new GridLayout(2, 3, 5, 5)); // 2 rows, 3 columns, with gaps
        cardGrid.setOpaque(false);
        specialCardLabels = new JLabel[6];

        for (int i = 0; i < 6; i++) {
            specialCardLabels[i] = new JLabel();
            specialCardLabels[i].setPreferredSize(new Dimension(80, 120)); // Keep original card dimensions
            specialCardLabels[i].setBorder(BorderFactory.createLineBorder(Color.WHITE, 1));
            specialCardLabels[i].setHorizontalAlignment(SwingConstants.CENTER);
            cardGrid.add(specialCardLabels[i]);
        }

        panel.add(cardGrid, BorderLayout.CENTER);
        panel.setMaximumSize(new Dimension(280, 300)); // Adjusted size
        panel.setAlignmentX(Component.CENTER_ALIGNMENT);
        return panel;
    }

    // New helper method to create the simplified trick pile labels
    private JLabel createTrickPileLabel(String text, Color accentColor) {
        JLabel label = new JLabel(text, SwingConstants.CENTER);
        // label.setPreferredSize(new Dimension(140, 180)); // REMOVED: Let it size itself
        label.setForeground(new Color(200, 200, 200));
        label.setFont(new Font("Monospaced", Font.BOLD, 11));
        label.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(accentColor, 2), // Outer border
                new EmptyBorder(5, 5, 5, 5) // Inner padding to give text some breathing room
        ));
        label.setBackground(new Color(20, 20, 20)); // Background for the label itself
        label.setOpaque(true); // Make background visible
        return label;
    }
}
