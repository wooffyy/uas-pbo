package ui;

import core.GameManager;
import core.shop.ShopCardPool;
import model.card.SpecialCard;
import model.state.GameState;
import model.state.PlayerInventory;
import ui.component.SpecialCardAbilitiesPanel;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class Phase2ShopPanel extends JPanel {

    private static final Color BG_DARK = new Color(25, 25, 25);
    private static final Color PANEL_GRAY = new Color(45, 45, 45);
    private static final Color TABLE_GREEN = new Color(30, 60, 30);
    private static final Color ACCENT_RED = new Color(150, 0, 0);
    private static final Color ACCENT_YELLOW = new Color(255, 200, 0);

    private final UIWindow parentFrame;

    // ===== SHOP STATE =====
    // Use an array to keep fixed slots. Null means sold/empty.
    private SpecialCard[] shopSlots = new SpecialCard[6];
    private boolean rerollUsed = false;

    // ===== UI REFS =====
    private JPanel shopGrid;
    private JButton rerollBtn;
    private SpecialCardAbilitiesPanel abilitiesPanel;

    // Dynamic Labels
    private JLabel moneyLabel;
    private JLabel healthLabel;
    private JLabel debtLabel;
    private JLabel interestLabel;

    public Phase2ShopPanel(UIWindow parentFrame) {
        this.parentFrame = parentFrame;

        setLayout(new BorderLayout());
        setBackground(BG_DARK);

        add(createSidebar(), BorderLayout.WEST);
        add(createShopArea(), BorderLayout.CENTER);

        // ===== INIT SHOP =====
        rollShop();
        renderShop();
    }

    private void rollShop() {
        List<SpecialCard> fresh = ShopCardPool.roll(6);
        for (int i = 0; i < 6; i++) {
            if (i < fresh.size())
                shopSlots[i] = fresh.get(i);
            else
                shopSlots[i] = null;
        }
    }

    public void refresh() {
        if (moneyLabel != null) {
            moneyLabel.setText("$" + GameManager.getInstance().getGameState().getMoney());
        }
        if (healthLabel != null) {
            String hearts = "";
            int hp = GameManager.getInstance().getGameState().getPlayerHealth();
            for (int i = 0; i < hp; i++)
                hearts += "♥ ";
            healthLabel.setText(hearts.trim());
        }
        if (debtLabel != null) {
            debtLabel.setText("$" + GameManager.getInstance().getGameState().getDebt());
        }
        if (abilitiesPanel != null) {
            abilitiesPanel.refresh(GameManager.getInstance().getGameState());
        }
        renderShop(); // Refresh shop to update button states if needed
    }

    /* ===================== SIDEBAR ===================== */

    private JPanel createSidebar() {
        JPanel sidebar = new JPanel();
        sidebar.setPreferredSize(new Dimension(280, 0));
        sidebar.setBackground(PANEL_GRAY);
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JLabel title = new JLabel("SHOP");
        title.setFont(new Font("Monospaced", Font.BOLD, 26));
        title.setForeground(Color.WHITE);
        title.setAlignmentX(Component.CENTER_ALIGNMENT);

        sidebar.add(title);
        sidebar.add(Box.createVerticalStrut(20));

        debtLabel = new JLabel("$" + GameManager.getInstance().getGameState().getDebt(), SwingConstants.RIGHT);
        debtLabel.setForeground(ACCENT_YELLOW);
        sidebar.add(infoLabel("TOTAL DEBT", debtLabel));

        interestLabel = new JLabel((int) (GameManager.getInstance().getGameState().getInterestRate() * 100) + "%",
                SwingConstants.RIGHT);
        interestLabel.setForeground(ACCENT_YELLOW);
        sidebar.add(infoLabel("INTEREST RATE", interestLabel));

        moneyLabel = new JLabel("$" + GameManager.getInstance().getGameState().getMoney(), SwingConstants.RIGHT);
        moneyLabel.setForeground(ACCENT_YELLOW);
        sidebar.add(infoLabel("PLAYER MONEY", moneyLabel));

        sidebar.add(Box.createVerticalStrut(15));

        healthLabel = new JLabel();
        healthLabel.setFont(new Font("Monospaced", Font.BOLD, 18));
        healthLabel.setForeground(Color.RED);
        healthLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        sidebar.add(healthLabel);

        // Initial refresh
        String hearts = "";
        int hp = GameManager.getInstance().getGameState().getPlayerHealth();
        for (int i = 0; i < hp; i++)
            hearts += "♥ ";
        healthLabel.setText(hearts.trim());

        sidebar.add(Box.createVerticalStrut(20));

        abilitiesPanel = new SpecialCardAbilitiesPanel();
        abilitiesPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        abilitiesPanel.refresh(GameManager.getInstance().getGameState());
        sidebar.add(abilitiesPanel);

        return sidebar;
    }

    private JPanel infoLabel(String label, JLabel valueLabel) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setMaximumSize(new Dimension(240, 40));
        panel.setBackground(PANEL_GRAY);

        JLabel l = new JLabel(label);
        l.setForeground(Color.LIGHT_GRAY);

        if (valueLabel.getForeground() != ACCENT_YELLOW) {
            valueLabel.setForeground(ACCENT_YELLOW);
        }

        panel.add(l, BorderLayout.WEST);
        panel.add(valueLabel, BorderLayout.EAST);
        return panel;
    }

    /* ===================== SHOP AREA ===================== */

    private JPanel createShopArea() {
        JPanel area = new JPanel(new BorderLayout());
        area.setBackground(TABLE_GREEN);

        shopGrid = new JPanel(new GridLayout(2, 3, 20, 20));
        shopGrid.setOpaque(false);
        shopGrid.setBorder(BorderFactory.createEmptyBorder(40, 40, 40, 40));

        area.add(shopGrid, BorderLayout.CENTER);

        // ===== CONTROLS =====
        JPanel controls = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        controls.setOpaque(false);

        rerollBtn = new JButton("REROLL (FREE)");
        styleButton(rerollBtn, false);
        rerollBtn.addActionListener(e -> reroll());

        JButton continueBtn = new JButton("GO TO BIDDING");
        styleButton(continueBtn, true);
        continueBtn.addActionListener(e -> GameManager.getInstance().startBidding());

        controls.add(rerollBtn);
        controls.add(continueBtn);

        area.add(controls, BorderLayout.SOUTH);

        return area;
    }

    /* ===================== SHOP LOGIC ===================== */

    private void renderShop() {
        shopGrid.removeAll();

        for (int i = 0; i < 6; i++) {
            SpecialCard card = shopSlots[i];
            if (card == null) {
                // Empty slot (sold)
                JPanel placeholder = new JPanel();
                placeholder.setOpaque(false);
                placeholder.setBorder(BorderFactory.createLineBorder(new Color(255, 255, 255, 50), 2));

                JLabel sold = new JLabel("SOLD / EMPTY");
                sold.setForeground(Color.GRAY);
                placeholder.add(sold);

                shopGrid.add(placeholder);
            } else {
                shopGrid.add(createShopCard(card, i));
            }
        }

        shopGrid.revalidate();
        shopGrid.repaint();
    }

    private JPanel createShopCard(SpecialCard card, int index) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(50, 50, 50));
        panel.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));

        JLabel name = new JLabel(card.getName(), SwingConstants.CENTER);
        name.setFont(new Font("Monospaced", Font.BOLD, 12));
        name.setForeground(card.getRarity().getColor());

        // Resize to 9:16 aspect ratio (approx 135x240)
        JLabel image = new JLabel(CardImageLoader.loadCardResized(card.getName(), 135, 240));
        image.setHorizontalAlignment(SwingConstants.CENTER);

        // Price display inside/beside image
        JLabel price = new JLabel("$" + card.getPrice(), SwingConstants.CENTER);
        price.setFont(new Font("Arial", Font.BOLD, 16));
        price.setForeground(ACCENT_YELLOW);

        JButton buy = new JButton("BUY $" + card.getPrice());
        styleButton(buy, false);

        GameState gs = GameManager.getInstance().getGameState();
        boolean canAfford = gs.getMoney() >= card.getPrice();

        // Check if already owned
        boolean alreadyOwned = false;
        for (SpecialCard c : gs.getPlayerInventory().getCards()) {
            if (c.getName().equals(card.getName())) {
                alreadyOwned = true;
                break;
            }
        }

        if (alreadyOwned) {
            buy.setEnabled(false);
            buy.setText("OWNED");
            buy.setToolTipText("You already have this card!");
        } else if (!canAfford) {
            buy.setEnabled(false);
            buy.setToolTipText("Not enough money!");
        } else {
            buy.addActionListener(e -> buyCard(card, index));
        }

        panel.add(name, BorderLayout.NORTH);
        panel.add(image, BorderLayout.CENTER);
        panel.add(price, BorderLayout.SOUTH);
        panel.add(buy, BorderLayout.PAGE_END);

        return panel;
    }

    private void buyCard(SpecialCard card, int index) {
        GameState gs = GameManager.getInstance().getGameState();
        PlayerInventory inv = gs.getPlayerInventory();

        if (gs.getMoney() < card.getPrice())
            return;

        // Double check ownership
        for (SpecialCard c : inv.getCards()) {
            if (c.getName().equals(card.getName())) {
                return;
            }
        }

        // Check inventory size
        if (inv.getCards().size() >= 6) {
            // Replacement Logic
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
                // Remove chosen card
                inv.getCards().remove(choice);
                // Continue to buy
            } else {
                // Cancelled
                return;
            }
        }

        gs.decreaseMoney(card.getPrice());
        gs.getPlayerInventory().addCard(card);

        // Mark slot as sold (null) instead of shifting
        shopSlots[index] = null;

        // UNLOCK PERMANENTLY IN COLLECTION
        db.DatabaseManager.unlockCard(card.getId());
        System.out.println("Shop purchase: Unlocking card " + card.getName() + " (ID: " + card.getId() + ")");

        refresh(); // Refresh labels and abilities panel
    }

    private void reroll() {
        if (rerollUsed)
            return;

        rollShop();

        rerollUsed = true;
        rerollBtn.setEnabled(false);
        refresh();
    }

    /* ===================== STYLE ===================== */

    private void styleButton(JButton btn, boolean primary) {
        btn.setFont(new Font("Monospaced", Font.BOLD, 14));
        btn.setBackground(primary ? ACCENT_RED : new Color(80, 0, 0));
        btn.setForeground(Color.WHITE);
        btn.setBorder(BorderFactory.createLineBorder(ACCENT_YELLOW, primary ? 2 : 1));
    }
}
