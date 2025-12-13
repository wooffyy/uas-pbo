package ui;

import javax.swing.*;
import java.awt.*;


public class UIWindow extends JFrame {

    public static final String MENU_VIEW = "MenuView";
    public static final String PHASE1_VIEW = "Phase1View";
    public static final String CARD_COLLECTION_VIEW = "CardCollectionView";


    private final CardLayout cardLayout;
    private final JPanel mainPanel;

    public UIWindow() {
        setTitle("Escape from Pinjol - Life and Death Edition");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1200, 800);


        // Memuat gambar dari direktori yang sama dengan class.
        // Metode getResource() adalah cara yang aman untuk memuat resource di Java.
        Image icon = Toolkit.getDefaultToolkit().getImage(getClass().getResource("component/logoEscape.png"));
        // Mengatur ikon untuk JFrame
        setIconImage(icon);



        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);

        // Inisialisasi semua panel
        MainMenuPanel menuPanel = new MainMenuPanel(this);
        Phase1Panel phase1Panel = new Phase1Panel(this);

        mainPanel.add(menuPanel, MENU_VIEW);
        mainPanel.add(phase1Panel, PHASE1_VIEW);
        CardCollectionPanel collectionPanel = new CardCollectionPanel(this);



        add(mainPanel);
        mainPanel.add(collectionPanel, CARD_COLLECTION_VIEW);

        cardLayout.show(mainPanel, MENU_VIEW);

        setLocationRelativeTo(null);
        setVisible(true);
    }

    /**
     * Metode untuk berpindah antar tampilan.
     */
    public void switchView(String viewName) {
        cardLayout.show(mainPanel, viewName);
    }

    public static void main(String[] args) {
        // Entry point untuk menjalankan UI
        SwingUtilities.invokeLater(UIWindow::new);
    }
}
