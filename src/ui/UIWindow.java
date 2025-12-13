package ui;

import javax.swing.*;
import java.awt.*;

public class UIWindow extends JFrame {

    public static final String MENU_VIEW = "MenuView";
    public static final String PHASE1_VIEW = "Phase1View";
    public static final String PHASE2_VIEW = "Phase2View";
    public static final String BIDDING_VIEW = "BiddingView";
    public static final String CARD_COLLECTION_VIEW = "CardCollectionView";

    private final CardLayout cardLayout;
    private final JPanel mainPanel;

    public UIWindow() {
        setTitle("Escape from Pinjol - Life and Death Edition");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1200, 800);

        Image icon = Toolkit.getDefaultToolkit()
                .getImage(getClass().getResource("component/logoEscape.png"));
        setIconImage(icon);

        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);

        // ================= INIT ALL VIEWS =================
        MainMenuPanel menuPanel = new MainMenuPanel(this);
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
        add(mainPanel);
        cardLayout.show(mainPanel, MENU_VIEW);

        setLocationRelativeTo(null);
        setVisible(true);
    }

    /** Pindah antar view */
    public void switchView(String viewName) {
        cardLayout.show(mainPanel, viewName);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(UIWindow::new);
    }
}
