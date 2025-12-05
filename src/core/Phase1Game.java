package core;

import model.state.GameState;
import model.card.Card;
import model.card.Deck;
import model.entity.Player;
import model.entity.Dealer;

// Logic Trick-taking (German Whist-style)
public class Phase1Game {
    private final GameState gameState;
    private Player player;
    private Dealer dealer;
    private Deck deck;

    private int tricksWon;
    pivate int tricksLost;

    public Phase1Game(GameState gameState) {
        this.gameState = gameState;
    }

    public void start(){
        deck = new Deck();
        deck.shuffle(gameState.getSeed());

        player = new Player();
        dealer = gameState.getCurrentDealer();

        deck.deal(player.getHand(), dealer.getHand());
    }

    public TrickResult playCard(Card playerCard){
        Card dealerCard = dealer.chooseCard(playerCard, player.getHand());
        boolean playerWin = Rules.isPlayerWin(playerCard, dealerCard);
        if (playerWin) {
            tricksWon++;
            gameState.addPhase1score(Rules.scoreCard(playerCard) + Rules.scoreCard(dealerCard));
        } else {
            tricksLost++;
        }

        return new TrickResult(playerWin, playerCard, dealerCard);
    }

    public boolean isWin() {
        return tricksWon > tricksLost;
    }
}
