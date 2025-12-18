package ui;

import core.GameManager;
import core.Phase1Game;
import model.state.GameState;

import javax.swing.*;
import java.awt.*;

public class Phase1ResultPanel extends JPanel {

    private final UIWindow parentFrame;
    private final GameManager gameManager;

    private JLabel resultTitleLabel;
    private JLabel tricksWonLabel;
    private JLabel moneyFromTricksLabel;
    private JLabel totalMoneyLabel;
    private JLabel healthStatusLabel;
    private JLabel debtInterestLabel;
    private JLabel debtStatusLabel;
    private JButton continueButton;

    public Phase1ResultPanel(UIWindow parentFrame, GameManager gameManager) {
        this.parentFrame = parentFrame;
        this.gameManager = gameManager;
        setLayout(new BorderLayout(20, 20));
        setBackground(new Color(20, 20, 20)); // Dark background
        setBorder(BorderFactory.createEmptyBorder(50, 100, 50, 100));

        initComponents();
        addListeners();
    }

    private void initComponents() {
        // Title
        resultTitleLabel = new JLabel("RESULTS", SwingConstants.CENTER);
        resultTitleLabel.setFont(new Font("Monospaced", Font.BOLD, 48));
        resultTitleLabel.setForeground(new Color(255, 200, 0)); // Gold color
        add(resultTitleLabel, BorderLayout.NORTH);

        // Results Panel
        JPanel resultsPanel = new JPanel();
        resultsPanel.setLayout(new BoxLayout(resultsPanel, BoxLayout.Y_AXIS));
        resultsPanel.setOpaque(false); // Make it transparent
        resultsPanel.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));

        tricksWonLabel = createResultLabel("Tricks Won: 0", Color.WHITE);
        moneyFromTricksLabel = createResultLabel("Money from Tricks: $0", new Color(0, 200, 0));
        totalMoneyLabel = createResultLabel("Total Player Money: $0", new Color(255, 200, 0));
        healthStatusLabel = createResultLabel("Health: ♥♥♥", Color.RED);
        debtInterestLabel = createResultLabel("Debt from Interest: $0", Color.ORANGE);
        debtStatusLabel = createResultLabel("New Total Debt: $0", Color.ORANGE);

        resultsPanel.add(tricksWonLabel);
        resultsPanel.add(Box.createVerticalStrut(15));
        resultsPanel.add(moneyFromTricksLabel);
        resultsPanel.add(totalMoneyLabel);
        resultsPanel.add(Box.createVerticalStrut(15));
        resultsPanel.add(healthStatusLabel);
        resultsPanel.add(Box.createVerticalStrut(15));
        resultsPanel.add(debtInterestLabel);
        resultsPanel.add(Box.createVerticalStrut(5));
        resultsPanel.add(debtStatusLabel);

        add(resultsPanel, BorderLayout.CENTER);

        // Continue Button
        continueButton = new JButton("CONTINUE");
        continueButton.setFont(new Font("Monospaced", Font.BOLD, 24));
        continueButton.setBackground(new Color(50, 150, 50)); // Greenish button
        continueButton.setForeground(Color.WHITE);
        continueButton.setFocusPainted(false);
        continueButton.setBorder(BorderFactory.createLineBorder(Color.GREEN.darker(), 2));
        add(continueButton, BorderLayout.SOUTH);
    }

    private JLabel createResultLabel(String text, Color color) {
        JLabel label = new JLabel(text, SwingConstants.CENTER);
        label.setFont(new Font("Monospaced", Font.PLAIN, 24));
        label.setForeground(color);
        label.setAlignmentX(Component.CENTER_ALIGNMENT);
        return label;
    }

    private void addListeners() {
        continueButton.addActionListener(e -> gameManager.continueToPhase2());
    }

    public void updateResults() {
        GameState state = gameManager.getGameState();
        Phase1Game phase1 = gameManager.getPhase1Game();

        resultTitleLabel.setText(phase1.isWin() ? "YOU WIN!" : "YOU LOSE!");
        resultTitleLabel.setForeground(phase1.isWin() ? new Color(0, 200, 0) : Color.RED);

        tricksWonLabel.setText("Tricks Won: " + phase1.getTricksWon() + " / 13");
        moneyFromTricksLabel.setText("Money from Tricks: $" + state.getMoneyFromTricks());
        totalMoneyLabel.setText("Total Player Money: $" + state.getMoney());
        healthStatusLabel.setText("Health: " + "♥".repeat(Math.max(0, state.getPlayerHealth())));
        healthStatusLabel.setForeground(state.getPlayerHealth() > 1 ? Color.GREEN : Color.RED);
        
        debtInterestLabel.setText("Debt from Interest: $" + state.getLastInterestAdded());
        debtStatusLabel.setText("New Total Debt: $" + state.getDebt());
        
        if (state.isDead()) {
            continueButton.setText("GAME OVER - BACK TO MENU");
            continueButton.setBackground(Color.RED.darker());
            continueButton.addActionListener(e -> parentFrame.switchView(UIWindow.MENU_VIEW));
        } else {
            continueButton.setText("CONTINUE");
            continueButton.setBackground(new Color(50, 150, 50));
        }
    }
}
