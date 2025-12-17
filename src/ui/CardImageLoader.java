package ui;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.net.URL;
import javax.swing.*;
import model.card.Card;
import model.card.Rank;
import model.card.Suit;

public class CardImageLoader {

    private static final String CARD_IMAGE_PATH = "cards.pixel/";
    private static final String CARD_BACK_FILENAME = "back.png";

    public static ImageIcon loadCardImage(Card card) {
        String imageFileName;
        if (card == null) {
            imageFileName = CARD_BACK_FILENAME;
        } else {
            imageFileName = getCardImageFilename(card.getSuit(), card.getRank());
        }

        URL imageUrl = CardImageLoader.class.getResource(CARD_IMAGE_PATH + imageFileName);
        if (imageUrl == null) {
            System.err.println("Could not find card image: " + imageFileName);
            return createPlaceholderImage(imageFileName);
        }
        return new ImageIcon(imageUrl);
    }

    private static String getCardImageFilename(Suit suit, Rank rank) {
        String suitStr = suit.name().toLowerCase();
        String rankStr;

        switch (rank) {
            case TWO:
                rankStr = "2";
                break;
            case THREE:
                rankStr = "3";
                break;
            case FOUR:
                rankStr = "4";
                break;
            case FIVE:
                rankStr = "5";
                break;
            case SIX:
                rankStr = "6";
                break;
            case SEVEN:
                rankStr = "7";
                break;
            case EIGHT:
                rankStr = "8";
                break;
            case NINE:
                rankStr = "9";
                break;
            case TEN:
                rankStr = "10";
                break;
            default:
                rankStr = rank.name().toLowerCase();
                break;
        }

        return rankStr + "_of_" + suitStr + ".png";
    }

    private static ImageIcon createPlaceholderImage(String text) {
        BufferedImage image = new BufferedImage(80, 120, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = image.createGraphics();

        g2d.setColor(Color.WHITE);
        g2d.fillRect(0, 0, 80, 120);
        g2d.setColor(Color.BLACK);
        g2d.drawRect(0, 0, 79, 119);

        g2d.setFont(new Font("Monospaced", Font.PLAIN, 10));
        g2d.drawString(text, 5, 60);

        g2d.dispose();
        return new ImageIcon(image);
    }

    // ui/CardImageLoader.java
    public static ImageIcon loadCard(String cardName) {
        String fileName = cardName.replace(" ", "");

        // Manual fix for naming inconsistencies
        if (fileName.equals("ClubCrusher"))
            fileName = "ClubRusher"; // File on disk is ClubRusher.png

        // Try PNG first
        URL url = CardImageLoader.class.getResource("/ui/cards/Joker/" + fileName + ".png");

        // Try JPEG/JPG if PNG not found
        if (url == null) {
            url = CardImageLoader.class.getResource("/ui/cards/Joker/" + fileName + ".jpeg");
        }
        if (url == null) {
            url = CardImageLoader.class.getResource("/ui/cards/Joker/" + fileName + ".jpg");
        }

        // Fallback: Try the general 'cards' directory (not specific 'Joker' subfolder)
        if (url == null) {
            url = CardImageLoader.class.getResource("/ui/cards/" + fileName + ".png");
        }
        if (url == null) {
            url = CardImageLoader.class.getResource("/ui/cards/" + fileName + ".jpeg");
        }
        if (url == null) {
            url = CardImageLoader.class.getResource("/ui/cards/" + fileName + ".jpg");
        }

        if (url == null) {
            System.err.println("Card image not found: " + fileName); // debug
            return createPlaceholderImage(cardName);
        }
        return new ImageIcon(url);
    }

    public static ImageIcon loadCardResized(String cardName, int width, int height) {
        ImageIcon original = loadCard(cardName);
        Image img = original.getImage();
        Image resized = img.getScaledInstance(width, height, Image.SCALE_SMOOTH);
        return new ImageIcon(resized);
    }
}
