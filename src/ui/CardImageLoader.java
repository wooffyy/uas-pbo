package ui;

import model.card.Card;
import model.card.Rank;
import model.card.Suit;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.net.URL;

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
            case TWO: rankStr = "2"; break;
            case THREE: rankStr = "3"; break;
            case FOUR: rankStr = "4"; break;
            case FIVE: rankStr = "5"; break;
            case SIX: rankStr = "6"; break;
            case SEVEN: rankStr = "7"; break;
            case EIGHT: rankStr = "8"; break;
            case NINE: rankStr = "9"; break;
            case TEN: rankStr = "10"; break;
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
}
