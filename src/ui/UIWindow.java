package ui;
import core.GameManager;
import java.awt.*;
import javax.swing.*;

public class UIWindow extends JFrame {

    public static final String MENU_VIEW = "MenuView";
    public static final String PHASE1_VIEW = "Phase1View";
    public static final String PHASE2_VIEW = "Phase2View";
    public static final String BIDDING_VIEW = "BiddingView";
    public static final String CARD_COLLECTION_VIEW = "CardCollectionView";

    private final CardLayout cardLayout;
    private final JPanel mainPanel;
    private GameManager gameManager;

    public UIWindow() {
        setTitle("Escape from Pinjol - Life and Death Edition");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1200, 800);

        Image icon = Toolkit.getDefaultToolkit()
                .getImage(getClass().getResource("component/logoEscape.png"));
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
        Phase1Panel phase1Panel = new Phase1Panel(this);
        Phase2ShopPanel phase2Panel = new Phase2ShopPanel(this);
        BiddingPanel biddingPanel = new BiddingPanel(this);
        CardCollectionPanel collectionPanel = new CardCollectionPanel(this);

        // ================= REGISTER VIEWS =================
        mainPanel.add(menuPanel, MENU_VIEW);
        mainPanel.add(phase1Panel, PHASE1_VIEW);
        mainPanel.add(phase2Panel, PHASE2_VIEW);
        mainPanel.add(biddingPanel, BIDDING_VIEW);
        mainPanel.add(collectionPanel, CARD_COLLECTION_VIEW);

        // ================= FINAL SETUP =================
        cardLayout.show(mainPanel, MENU_VIEW);
        setVisible(true);
    }

    /** Pindah antar view */
    public void switchView(String viewName) {
        cardLayout.show(mainPanel, viewName);
    }
}
