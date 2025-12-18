package ui;

import core.GameManager;
import model.card.SpecialCard;
import model.state.GameState;

import javax.swing.*;
import java.awt.*;
import java.util.stream.Collectors;

public class GameOverPanel extends JPanel {

    private final UIWindow parentFrame;
    private final GameManager gameManager;

    private JLabel roundsSurvivedLabel;
    private JLabel finalMoneyLabel;
    private JLabel finalDebtLabel;
    private JTextArea collectedCardsArea;

    public GameOverPanel(UIWindow parentFrame, GameManager gameManager) {
        this.parentFrame = parentFrame;
        this.gameManager = gameManager;
        setLayout(new BorderLayout(20, 20));
        setBackground(new Color(10, 0, 0)); // Deep red background
        setBorder(BorderFactory.createEmptyBorder(50, 100, 50, 100));

        initComponents();
    }

    private void initComponents() {
        // Title
        JLabel titleLabel = new JLabel("YOU DIED", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Monospaced", Font.BOLD, 64));
        titleLabel.setForeground(Color.RED);
        add(titleLabel, BorderLayout.NORTH);

        // Summary Panel
        JPanel summaryPanel = new JPanel();
        summaryPanel.setLayout(new BoxLayout(summaryPanel, BoxLayout.Y_AXIS));
        summaryPanel.setOpaque(false);
        summaryPanel.setBorder(BorderFactory.createEmptyBorder(40, 0, 40, 0));

        roundsSurvivedLabel = createSummaryLabel("Rounds Survived: 0", Color.WHITE);
        finalMoneyLabel = createSummaryLabel("Final Money: $0", new Color(255, 200, 0));
        finalDebtLabel = createSummaryLabel("Final Debt: $0", Color.ORANGE);

        summaryPanel.add(roundsSurvivedLabel);
        summaryPanel.add(Box.createVerticalStrut(20));
        summaryPanel.add(finalMoneyLabel);
        summaryPanel.add(Box.createVerticalStrut(20));
        summaryPanel.add(finalDebtLabel);
        summaryPanel.add(Box.createVerticalStrut(20));

        JLabel collectedCardsTitle = createSummaryLabel("Collected Special Cards:", Color.WHITE);
        summaryPanel.add(collectedCardsTitle);
        summaryPanel.add(Box.createVerticalStrut(10));

        collectedCardsArea = new JTextArea();
        collectedCardsArea.setEditable(false);
        collectedCardsArea.setOpaque(false);
        collectedCardsArea.setForeground(Color.LIGHT_GRAY);
        collectedCardsArea.setFont(new Font("Monospaced", Font.PLAIN, 16));
        JScrollPane scrollPane = new JScrollPane(collectedCardsArea);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.setBorder(BorderFactory.createLineBorder(Color.DARK_GRAY));
        summaryPanel.add(scrollPane);


        add(summaryPanel, BorderLayout.CENTER);

        // Return Button
        JButton returnButton = new JButton("RETURN TO MENU");
        returnButton.setFont(new Font("Monospaced", Font.BOLD, 24));
        returnButton.setBackground(Color.RED.darker());
        returnButton.setForeground(Color.WHITE);
        returnButton.setFocusPainted(false);
        returnButton.setBorder(BorderFactory.createLineBorder(Color.WHITE, 2));
        returnButton.addActionListener(e -> parentFrame.switchView(UIWindow.MENU_VIEW));
        add(returnButton, BorderLayout.SOUTH);
    }

    private JLabel createSummaryLabel(String text, Color color) {
        JLabel label = new JLabel(text, SwingConstants.CENTER);
        label.setFont(new Font("Monospaced", Font.PLAIN, 28));
        label.setForeground(color);
        label.setAlignmentX(Component.CENTER_ALIGNMENT);
        return label;
    }

    public void updateSummary() {
        GameState state = gameManager.getGameState();
        roundsSurvivedLabel.setText("Rounds Survived: " + state.getRound());
        finalMoneyLabel.setText("Final Money: $" + state.getMoney());
        finalDebtLabel.setText("Final Debt: $" + state.getDebt());

        String collectedCards = state.getPlayerInventory().getCards().stream()
                .map(SpecialCard::getName)
                .collect(Collectors.joining("\n"));
        if (collectedCards.isEmpty()) {
            collectedCards = "None";
        }
        collectedCardsArea.setText(collectedCards);
    }
}
