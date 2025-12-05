package core;

// Custom exception (invalid bid, illegal move, dll.)
public class GameException extends Exception {
    public GameException(String message) { super(message); }
}
