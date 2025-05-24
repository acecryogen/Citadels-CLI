package citadels;

import java.util.*;

public class Game {
    private List<Player> players;
    private Deck districtDeck;
    private List<CharacterCard> characterDeck;
    private int crownedPlayerIndex;
    private boolean gameOver;
    private int round = 1;
    private List<CharacterCard> availableCharacters;
    private Map<Integer, Player> characterToPlayer;
    private boolean debugMode = false;

    private CharacterAbilities characterAbilities;
    private PurpleDistrict purpleDistrict;
    private ScoreManager scoreManager;
    private SaveLoad saveLoad;
    private TurnManager turnManager;
    private CharacterSelector characterSelector;
    private GameDebugger debugger;

    private Scanner scanner;

    public Game(List<Player> players, Deck districtDeck, List<CharacterCard> characterDeck) {
        this.players = players;
        this.districtDeck = districtDeck;
        this.characterDeck = characterDeck;
        this.crownedPlayerIndex = new Random().nextInt(players.size());
        this.gameOver = false;

        // Initialize handlers
        this.characterAbilities = new CharacterAbilities(this, players, districtDeck, characterDeck, characterToPlayer);
        this.purpleDistrict = new PurpleDistrict(this, districtDeck);
        this.scoreManager = new ScoreManager(players, round);
        this.saveLoad = new SaveLoad(this, players, districtDeck, characterDeck);
        this.turnManager = new TurnManager(this, players, districtDeck, characterToPlayer);
        this.characterSelector = new CharacterSelector(this, players, characterDeck);
        this.debugger = new GameDebugger(this, players, characterDeck);

        initialDeal();
    }

    private void initialDeal() {
        for (Player p : players) {
            for (int i = 0; i < 4; i++) {
                p.addToHand(districtDeck.draw());
            }
            p.addGold(0); // already starts with 2 gold in Player constructor
        }
        players.get(crownedPlayerIndex).setCrown(true);
    }

    public boolean isOver() {
        return gameOver;
    }

    public int getRound() {
        return round;
    }

    public void setRound(int round) {
        this.round = round;
    }

    public int getCrownedPlayerIndex() {
        return crownedPlayerIndex;
    }

    public void setCrownedPlayerIndex(int index) {
        this.crownedPlayerIndex = index;
    }

    public List<Player> getPlayers() {
        return players;
    }

    public void setScanner(Scanner scanner) {
        this.scanner = scanner;
        characterAbilities.setScanner(scanner);
        purpleDistrict.setScanner(scanner);
        turnManager.setScanner(scanner);
        characterSelector.setScanner(scanner);
    }

    // === PHASE 1: CHARACTER SELECTION ===
    public void characterSelectionPhase(Scanner scanner) {
        characterSelector.setScanner(scanner);
        characterToPlayer = characterSelector.selectCharacters(crownedPlayerIndex);
    }

    // === PHASE 2: TURN PHASE ===
    public void turnPhase(Scanner scanner) {
        turnManager.setScanner(scanner);
        turnManager.processTurns();

        if (gameOver) {
            System.out.println("Game over! Calculating scores...");
            showScores();
        }
    }

    /**
     * Handles the special ability for the player's character.
     * Returns the character number affected (for Assassin/Thief), or -1 otherwise.
     */
    private int handleCharacterAbility(Player player, Scanner scanner, int killedCharacter, int robbedCharacter) {
        characterAbilities.setScanner(scanner);
        return characterAbilities.handleAbility(player, killedCharacter, robbedCharacter);
    }

    private void handlePurpleDistrictAbility(Player player, DistrictCard card, Scanner scanner) {
        purpleDistrict.setScanner(scanner);
        purpleDistrict.handleAbility(player, card);
    }

    private String characterName(int num) {
        for (CharacterCard c : characterDeck) {
            if (c.getNumber() == num)
                return c.getName();
        }
        return "Unknown";
    }

    private boolean hasDistrict(Player p, String name) {
        for (DistrictCard d : p.getCity()) {
            if (d.name.equals(name))
                return true;
        }
        return false;
    }

    // Show the cards in a player's hand
    public void showHand(Player player) {
        System.out.println("Your hand:");
        for (int i = 0; i < player.getHand().size(); i++) {
            System.out.println((i + 1) + ": " + player.getHand().get(i));
        }
    }

    // Build a district from the player's hand (by index, 1-based)
    public void build(Player player, int handIndex) {
        if (handIndex < 1 || handIndex > player.getHand().size()) {
            System.out.println("Invalid card number.");
            return;
        }
        DistrictCard card = player.getHand().get(handIndex - 1);
        if (player.getGold() < card.cost) {
            System.out.println("Not enough gold to build " + card.name + ".");
            return;
        }
        for (DistrictCard built : player.getCity()) {
            if (built.name.equals(card.name)) {
                System.out.println("You already have " + card.name + " in your city.");
                return;
            }
        }
        player.buildDistrict(card);
        System.out.println("Built " + card.name + ".");
        purpleDistrict.handleAbility(player, card);
    }

    public void processTurn() {
        turnManager.processTurns();
    }

    public void showCity(Player player) {
        System.out.println("Player " + player.getId() + "'s city:");
        for (DistrictCard card : player.getCity()) {
            System.out.println(card.name + " (" + card.color + ", " + card.cost + " gold)");
        }
    }

    // --- SCORING ---

    private void showScores() {
        scoreManager.showScores();
    }

    public void saveGame(String filename) {
        saveLoad.saveGame(filename);
    }

    public void loadGame(String filename) {
        saveLoad.loadGame(filename);
    }

    public void toggleDebug() {
        debugMode = !debugMode;
        System.out.println("Debug mode " + (debugMode ? "enabled" : "disabled") + ".");
    }

    public void showInfo(String arg) {
        // Try to parse as card number in hand
        try {
            int idx = Integer.parseInt(arg);
            Player human = players.get(0);
            if (idx >= 1 && idx <= human.getHand().size()) {
                DistrictCard card = human.getHand().get(idx - 1);
                System.out.println(card.name + ": " + card.text);
                return;
            }
        } catch (NumberFormatException e) {
            // Not a number, treat as character name
            for (CharacterCard cc : characterDeck) {
                if (cc.getName().equalsIgnoreCase(arg)) {
                    System.out.println(cc);
                    return;
                }
            }
        }
        System.out.println("No such card or character.");
    }

    public void describeAction(Player player) {
        CharacterCard character = player.getCharacter();
        if (character == null) {
            System.out.println("You have no character this round.");
            return;
        }
        System.out.println("Your character: " + character.getName());
        System.out.println("Ability: " + character.getAbility());
        System.out.println("To use your ability, type 'action' during your turn.");
    }

    public void initTestGame() {
        // Clear existing state
        this.players.clear();

        // Create test players
        Player p1 = new Player(1, true);
        p1.addGold(5);
        p1.addToHand(new DistrictCard("Castle", "yellow", 4, ""));
        p1.buildDistrict(new DistrictCard("Tavern", "green", 1, ""));
        this.players.add(p1);

        Player p2 = new Player(2, false);
        p2.addGold(3);
        this.players.add(p2);

        // Reset deck
        List<DistrictCard> cards = new ArrayList<>();
        cards.add(new DistrictCard("Temple", "blue", 1, ""));
        cards.add(new DistrictCard("Watchtower", "red", 1, ""));
        this.districtDeck = new Deck(cards);

        // Reset character deck
        this.characterDeck.clear();
        this.characterDeck.add(new CharacterCard(1, "Assassin", "Kill a character"));
        this.characterDeck.add(new CharacterCard(2, "Thief", "Steal gold"));

        this.round = 1;
        this.crownedPlayerIndex = 0;
    }

    public void showAll() {
        for (Player player : players) {
            showCity(player);
        }
    }
}
