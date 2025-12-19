package ui;

import core.GameManager;
import java.awt.*;
import javax.swing.*;

public class UIWindow extends JFrame {

    public static final String MENU_VIEW = "MenuView";
    public static final String STORY_VIEW = "StoryView";
    public static final String PHASE1_VIEW = "Phase1View";
    public static final String PHASE1_RESULT_VIEW = "Phase1ResultView"; // New view
    public static final String PHASE2_VIEW = "Phase2View";
    public static final String BIDDING_VIEW = "BiddingView";
    public static final String CARD_COLLECTION_VIEW = "CardCollectionView";
    public static final String GAME_OVER_VIEW = "GameOverView";

    private final CardLayout cardLayout;
    private final JPanel mainPanel;
    private GameManager gameManager;

    // View Panels
    private Phase1Panel phase1Panel;
    private Phase1ResultPanel phase1ResultPanel;
    private Phase2ShopPanel phase2Panel;
    private BiddingPanel biddingPanel;
    private CardCollectionPanel collectionPanel; // Promoted to field
    private GameOverPanel gameOverPanel;
    private StoryPanel currentStoryPanel;
    private int currentStoryScreen = 1;
    // other panels can be stored as fields if needed

    public UIWindow() {
        setTitle("Escape from Pinjol - Life and Death Edition");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1200, 800);

        Image icon = Toolkit.getDefaultToolkit().getImage(getClass().getResource("component/logoEscape.png"));
        setIconImage(icon);

        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);
        add(mainPanel);

        setLocationRelativeTo(null);
    }

    public void initComponents(GameManager gameManager) {
        this.gameManager = gameManager;

        // ================= INIT ALL VIEWS =================
        MainMenuPanel menuPanel = new MainMenuPanel(this, gameManager);
        phase1Panel = new Phase1Panel(this, gameManager);
        phase1ResultPanel = new Phase1ResultPanel(this, gameManager);
        phase2Panel = new Phase2ShopPanel(this);
        biddingPanel = new BiddingPanel(this);
        collectionPanel = new CardCollectionPanel(this);
        gameOverPanel = new GameOverPanel(this, gameManager);

        // ================= REGISTER VIEWS =================
        mainPanel.add(menuPanel, MENU_VIEW);
        mainPanel.add(phase1Panel, PHASE1_VIEW);
        mainPanel.add(phase1ResultPanel, PHASE1_RESULT_VIEW);
        mainPanel.add(phase2Panel, PHASE2_VIEW);
        mainPanel.add(biddingPanel, BIDDING_VIEW);
        mainPanel.add(collectionPanel, CARD_COLLECTION_VIEW);
        mainPanel.add(gameOverPanel, GAME_OVER_VIEW);

        // ================= FINAL SETUP =================
        cardLayout.show(mainPanel, MENU_VIEW);

        // Start BGM immediately on main menu
        ui.util.SoundManager.getInstance().play("MainMenu");

        setVisible(true);
    }

    /** Pindah antar view */
    public void switchView(String viewName) {
        cardLayout.show(mainPanel, viewName);

        // Handle BGM Switching
        ui.util.SoundManager sm = ui.util.SoundManager.getInstance();

        switch (viewName) {
            case MENU_VIEW:
            case CARD_COLLECTION_VIEW:
            case STORY_VIEW: // Story screens use MainMenu BGM
                sm.play("MainMenu");
                break;
            case PHASE2_VIEW: // Shop
            case BIDDING_VIEW:
                sm.play("Shop");
                break;
            case PHASE1_VIEW:
                // Check current round/dealer to determine BGM
                if (gameManager != null) {
                    int round = gameManager.getGameState().getRound();
                    if (round == 1)
                        sm.play("IntroBoss");
                    else if (round == 2)
                        sm.play("Tactician");
                    else if (round == 3)
                        sm.play("Economic");
                    else
                        sm.play("FinalBoss");
                }
                break;
            case PHASE1_RESULT_VIEW:
                sm.play("Death"); // "Menang atau kalah" -> Result screen
                break;
            case GAME_OVER_VIEW:
                sm.play("Death");
                break;
            default:
                // For any other views not explicitly handled, stop BGM or keep previous
                // For now, let's assume we stop or keep previous if no specific BGM is
                // assigned.
                // sm.stopBGM(); // Or do nothing to keep current BGM
                break;
        }

        // Refresh Phase1Panel whenever it's shown
        if (viewName.equals(PHASE1_VIEW) && phase1Panel != null) {
            phase1Panel.refresh();
        } else if (viewName.equals(PHASE1_RESULT_VIEW) && phase1ResultPanel != null) {
            phase1ResultPanel.updateResults(); // Update results when showing this panel
        } else if (viewName.equals(PHASE2_VIEW) && phase2Panel != null) {
            phase2Panel.refresh();
        } else if (viewName.equals(BIDDING_VIEW) && biddingPanel != null) {
            biddingPanel.reset();
        } else if (viewName.equals(CARD_COLLECTION_VIEW) && collectionPanel != null) {
            collectionPanel.refresh();
        } else if (viewName.equals(GAME_OVER_VIEW) && gameOverPanel != null) {
            gameOverPanel.updateSummary();
        }
    }

    public Phase1Panel getPhase1Panel() {
        return phase1Panel;
    }

    public Phase1ResultPanel getPhase1ResultPanel() {
        return phase1ResultPanel;
    }

    public Phase2ShopPanel getPhase2ShopPanel() {
        return phase2Panel;
    }

    public GameOverPanel getGameOverPanel() {
        return gameOverPanel;
    }

    public void showNotification(String message) {
        JOptionPane.showMessageDialog(this, message, "Game Notification", JOptionPane.INFORMATION_MESSAGE);
    }

    /**
     * Start the story sequence from screen 1
     */
    private String[] currentStorySequence;

    /**
     * Start the story sequence from screen 1 (Intro)
     */
    public void startStorySequence() {
        currentStorySequence = StoryPanel.INTRO_STORY_TEXTS;
        currentStoryScreen = 1;
        showStoryScreen(currentStoryScreen);
    }

    /**
     * Start the Stage 2 story sequence (Before Tactician)
     */
    public void startStage2StorySequence() {
        currentStorySequence = StoryPanel.STAGE_2_STORY_TEXTS;
        currentStoryScreen = 1;
        showStoryScreen(currentStoryScreen);
    }

    /**
     * Start the Stage 3 story sequence (Before Ricky Chindo)
     */
    public void startStage3StorySequence() {
        currentStorySequence = StoryPanel.STAGE_3_STORY_TEXTS;
        currentStoryScreen = 1;
        showStoryScreen(currentStoryScreen);
    }

    /**
     * Start the Stage 4 story sequence (Final Boss - Future Alit)
     */
    public void startStage4StorySequence() {
        currentStorySequence = StoryPanel.STAGE_4_STORY_TEXTS;
        currentStoryScreen = 1;
        showStoryScreen(currentStoryScreen);
    }

    /**
     * Start the Ending sequence
     * 
     * @param win true if player won the final battle (but still loses
     *            systemically), false if died
     */
    public void startEndingSequence(boolean win) {
        if (win) {
            currentStorySequence = StoryPanel.ENDING_WIN_BUT_LOSE_TEXT;
        } else {
            currentStorySequence = StoryPanel.ENDING_LOSE_TEXT;
        }
        currentStoryScreen = 1;
        showStoryScreen(currentStoryScreen);
    }

    private void showEpilogue() {
        currentStorySequence = StoryPanel.EPILOGUE_TEXT;
        currentStoryScreen = 1;
        showStoryScreen(currentStoryScreen);
    }

    /**
     * Show a specific story screen
     */
    private void showStoryScreen(int screenNumber) {
        // Remove previous story panel if exists
        if (currentStoryPanel != null) {
            mainPanel.remove(currentStoryPanel);
        }

        // Check if we reached the end of the current sequence
        if (screenNumber > currentStorySequence.length) {
            // End of story
            currentStoryPanel = null;

            // Logic to determine where to go next
            if (currentStorySequence == StoryPanel.INTRO_STORY_TEXTS) {
                gameManager.startRun(); // Start Stage 1
            } else if (currentStorySequence == StoryPanel.STAGE_2_STORY_TEXTS ||
                    currentStorySequence == StoryPanel.STAGE_3_STORY_TEXTS ||
                    currentStorySequence == StoryPanel.STAGE_4_STORY_TEXTS) {
                gameManager.resumePhase2Finish(); // Resume phases
            } else if (currentStorySequence == StoryPanel.ENDING_LOSE_TEXT ||
                    currentStorySequence == StoryPanel.ENDING_WIN_BUT_LOSE_TEXT) {
                // After Ending Screen 31, always show Epilogue
                showEpilogue();
            } else if (currentStorySequence == StoryPanel.EPILOGUE_TEXT) {
                // After Epilogue, return to Main Menu
                switchView(MENU_VIEW);
            }
            return;
        }

        // Show new screen
        currentStoryPanel = new StoryPanel(currentStorySequence[screenNumber - 1], screenNumber, () -> {
            currentStoryScreen++;
            showStoryScreen(currentStoryScreen);
        });

        mainPanel.add(currentStoryPanel, STORY_VIEW);
        switchView(STORY_VIEW);
    }
}
