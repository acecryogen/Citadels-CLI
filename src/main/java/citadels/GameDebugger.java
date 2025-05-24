package citadels;

import java.util.*;

public class GameDebugger {
    private Game game;
    private List<Player> players;
    private List<CharacterCard> characterDeck;
    private boolean debugMode = false;

    public GameDebugger(Game game, List<Player> players, List<CharacterCard> characterDeck) {
        this.game = game;
        this.players = players;
        this.characterDeck = characterDeck;
    }

    public void toggleDebug() {
        debugMode = !debugMode;
        System.out.println("Debug mode " + (debugMode ? "enabled" : "disabled") + ".");
    }

    public void showInfo(String arg) {
        System.out.println("Debug info: " + arg);
    }

    public void describeAction(Player player) {
        // ...existing describeAction logic...
    }

    public void initTestGame() {
        // ...existing initTestGame logic...
    }
}