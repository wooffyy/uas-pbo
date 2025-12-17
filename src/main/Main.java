package main;

import core.GameManager;
import model.card.EffectTrigger;
import model.card.EffectType;
import model.card.Rarity;
import model.card.SpecialCard;
import model.state.GameState;
import ui.UIWindow;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;

/**
 * The main entry point for the application.
 * This class initializes the game logic (GameManager), the UI (UIWindow),
 * and links them together.
 */
public class Main {

    public static void main(String[] args) {
        // Use SwingUtilities.invokeLater to ensure UI updates are on the Event Dispatch
        // Thread.
        SwingUtilities.invokeLater(() -> {
            // 1. Initialize Game State
            GameState gameState = new GameState();

            // 2. Create a pool of available special cards for the shop and bidding.
            // (This would ideally be loaded from a database or file).
            List<SpecialCard> itemPool = core.shop.ShopCardPool.getAllCards();

            // 3. Initialize UI.
            // We create the UI first so we can pass it to the GameManager.
            UIWindow ui = new UIWindow();

            // 4. Initialize Game Manager and link it with the UI and GameState.
            GameManager gameManager = new GameManager(gameState, ui, itemPool);

            // 5. Initialize UI components and start the game.
            // This is called after gameManager is created to solve circular dependency.
            ui.initComponents(gameManager);
        });
    }
}