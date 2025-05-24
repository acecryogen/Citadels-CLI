package citadels;

import java.util.*;

public class Game {
    private List<Player> players;
    private Deck districtDeck;
    private List<CharacterCard> characterDeck;
    private int crownedPlayerIndex;
    private boolean gameOver;
    private int round = 1;
    // These would be set during character selection each round
    private int killedCharacterThisRound = -1; // Rank of the killed character
    private int robbedCharacterThisRound = -1; // Rank of the robbed character

    private List<CharacterCard> availableCharacters;
    private Map<Integer, Player> characterToPlayer; // This should be reset and repopulated each round
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
        this.characterToPlayer = new HashMap<>(); // Initialize the map

        // Initialize handlers
        // Pass the characterToPlayer map, which will be updated by CharacterSelector
        this.characterAbilities = new CharacterAbilities(this, players, districtDeck, characterDeck, this.characterToPlayer);
        this.purpleDistrict = new PurpleDistrict(this, districtDeck);
        this.scoreManager = new ScoreManager(players, round);
        this.saveLoad = new SaveLoad(this, players, districtDeck, characterDeck);
        this.turnManager = new TurnManager(this, players, districtDeck, this.characterToPlayer);
        this.characterSelector = new CharacterSelector(this, players, characterDeck); // Pass the main character deck
        this.debugger = new GameDebugger(this, players, characterDeck);

        initialDeal();
    }

    private void initialDeal() {
        for (Player p : players) {
            for (int i = 0; i < 4; i++) {
                if (districtDeck.cardsLeft() > 0) { // Check if deck has cards
                    p.addToHand(districtDeck.draw());
                }
            }
            // p.addGold(0); // Players start with 2 gold by default in Player constructor
        }
        if (!players.isEmpty()) {
            players.get(crownedPlayerIndex % players.size()).setCrown(true); // Ensure crownedPlayerIndex is valid
        }
    }

    public boolean isOver() {
        return gameOver;
    }

    // Added setter for gameOver state
    public void setOver(boolean gameOver) {
        this.gameOver = gameOver;
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
        // Update crown status among players
        for (int i = 0; i < players.size(); i++) {
            players.get(i).setCrown(i == index);
        }
    }

    public List<Player> getPlayers() {
        return players;
    }
    
    public CharacterAbilities getCharacterAbilities() {
        return characterAbilities;
    }

    public int getKilledCharacterThisRound() {
        // This would be set by CharacterSelector or Assassin's ability logic
        return characterSelector.getKilledCharacterRank(); // Delegate to CharacterSelector
    }

    public void setKilledCharacterThisRound(int rank) {
        this.killedCharacterThisRound = rank;
    }

    public int getRobbedCharacterThisRound() {
        // This would be set by Thief's ability logic
        return characterSelector.getRobbedCharacterRank(); // Delegate to CharacterSelector
    }
    
    public void setRobbedCharacterThisRound(int rank) {
        this.robbedCharacterThisRound = rank;
    }


    public CharacterCard getCharacterByRank(int rank) {
        for (CharacterCard card : characterDeck) { // Assuming characterDeck holds all base characters
            if (card.getRank() == rank) {
                return card;
            }
        }
        return null; // Or throw an exception if a card for a rank is always expected
    }

    public boolean checkGameOverCondition() {
        // Game ends if a player has built 7 districts at the end of a round.
        // The game then completes that round.
        if (gameOver) return true; // If already set by Warlord destroying 7th district etc.
        for (Player player : players) {
            if (player.getCity().size() >= 7) { // Typically 7 districts to trigger game end
                // this.gameOver = true; // The game should finish the current round.
                // The actual setting of gameOver might happen after all turns in the round are done.
                System.out.println("Player " + player.getId() + " has reached 7 districts. Game will end after this round.");
                return true; // Condition to end the game (after round) is met
            }
        }
        return false;
    }


    public void setScanner(Scanner scanner) {
        this.scanner = scanner;
        if(characterAbilities != null) characterAbilities.setScanner(scanner);
        if(purpleDistrict != null) purpleDistrict.setScanner(scanner);
        if(turnManager != null) turnManager.setScanner(scanner);
        if(characterSelector != null) characterSelector.setScanner(scanner);
    }

    // === PHASE 1: CHARACTER SELECTION ===
    public void characterSelectionPhase(Scanner scanner) {
        characterSelector.setScanner(scanner); // Ensure selector has the scanner
        // characterToPlayer map is updated directly by characterSelector
        this.characterToPlayer = characterSelector.selectCharacters(crownedPlayerIndex);
        // Update TurnManager's map reference if it was created with an old/empty one
        this.turnManager = new TurnManager(this, players, districtDeck, this.characterToPlayer);
        this.turnManager.setScanner(scanner); // Ensure new TurnManager also has scanner
         // Update CharacterAbilities' map reference
        this.characterAbilities = new CharacterAbilities(this, players, districtDeck, characterDeck, this.characterToPlayer);
        this.characterAbilities.setScanner(scanner);

    }

    // === PHASE 2: TURN PHASE ===
    public void turnPhase(Scanner scanner) {
        turnManager.setScanner(scanner); // Ensure turnManager has the scanner
        turnManager.processTurns(); // This will use the characterToPlayer map

        // After all turns, if game over condition was met, truly set gameOver.
        if (checkGameOverCondition() && !gameOver) { // Check again, in case it was triggered during a turn
            this.gameOver = true; // Now officially set it
             System.out.println("The round has ended and the game is now over!");
        }

        if (gameOver) {
            System.out.println("Calculating final scores...");
            showScores();
        }
    }

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

    public void showHand(Player player) {
        System.out.println(player.getName() + "'s hand:"); // Assuming Player has getName()
        for (int i = 0; i < player.getHand().size(); i++) {
            DistrictCard card = player.getHand().get(i);
            System.out.println((i + 1) + ": " + card.name + " (" + card.cost + " gold, " + card.color + ")");
        }
    }

    public void build(Player player, int handIndex) {
        if (handIndex < 1 || handIndex > player.getHand().size()) {
            System.out.println("Invalid card number.");
            return;
        }
        DistrictCard card = player.getHand().get(handIndex - 1);
        if (player.getGold() < card.cost) {
            System.out.println("Not enough gold to build " + card.name + " (Cost: " + card.cost + ", Your Gold: " + player.getGold() + ").");
            return;
        }
        for (DistrictCard built : player.getCity()) {
            if (built.name.equals(card.name)) {
                System.out.println("You already have " + card.name + " in your city.");
                return;
            }
        }
        player.buildDistrict(card); // This method should remove card from hand and gold from player
        System.out.println(player.getName() + " built " + card.name + ".");
        if (purpleDistrict != null) { // Check if purpleDistrict handler is initialized
            purpleDistrict.handleAbility(player, card);
        }
    }

    public void processTurn() {
        turnManager.processTurns();
    }

    public void showCity(Player player) {
        System.out.println(player.getName() + "'s city:");
        if (player.getCity().isEmpty()) {
            System.out.println("  (empty)");
        }
        for (DistrictCard card : player.getCity()) {
            System.out.println("  " + card.name + " (" + card.color + ", " + card.cost + " gold)");
        }
        System.out.println("  Total districts: " + player.getCity().size());
    }


    private void showScores() {
        scoreManager.showScores();
    }

    public void saveGame(String filename) {
        saveLoad.saveGame(filename);
    }

    public void loadGame(String filename) {
        saveLoad.loadGame(filename);
         // After loading, ensure current TurnManager and CharacterAbilities have the correct characterToPlayer map
        this.turnManager = new TurnManager(this, this.players, this.districtDeck, this.characterToPlayer);
        if (this.scanner != null) this.turnManager.setScanner(this.scanner);
        this.characterAbilities = new CharacterAbilities(this, this.players, this.districtDeck, this.characterDeck, this.characterToPlayer);
         if (this.scanner != null) this.characterAbilities.setScanner(this.scanner);
    }
    
    public Map<Integer, Player> getCharacterToPlayerMap() {
        return characterToPlayer;
    }

    public void setCharacterToPlayerMap(Map<Integer, Player> map) {
        this.characterToPlayer = map;
    }


    public void toggleDebug() {
        debugMode = !debugMode;
        System.out.println("Debug mode " + (debugMode ? "enabled" : "disabled") + ".");
    }

    public void showInfo(String arg) {
        // Try to parse as card number in hand
        try {
            int idx = Integer.parseInt(arg);
            Player human = null; // Find the human player
            for(Player p : players) { if(p.isHuman()) { human = p; break; } }

            if (human != null && idx >= 1 && idx <= human.getHand().size()) {
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
        System.out.println("No such card or character, or no human player found for hand check.");
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
        this.characterToPlayer.clear();


        // Create test players
        Player p1 = new Player(1, true); // Human player
        p1.setName("Player 1 (Human)");
        p1.addGold(5);

        Player p2 = new Player(2, false); // AI player
        p2.setName("Player 2 (AI)");
        p2.addGold(3);
        
        this.players.add(p1);
        this.players.add(p2);


        // Reset deck
        List<DistrictCard> cards = new ArrayList<>();
        cards.add(new DistrictCard("Temple", "blue", 1, "Temple Text"));
        cards.add(new DistrictCard("Watchtower", "red", 1, "Watchtower Text"));
        cards.add(new DistrictCard("Castle", "yellow", 4, "Castle Text"));
        cards.add(new DistrictCard("Tavern", "green", 1, "Tavern Text"));
        cards.add(new DistrictCard("Market", "green", 2, "Market Text"));
        cards.add(new DistrictCard("Palace", "yellow", 5, "Palace Text"));
        this.districtDeck = new Deck(cards);
        
        p1.addToHand(districtDeck.draw()); // Castle
        p1.addToHand(districtDeck.draw()); // Tavern
        // p1.buildDistrict(new DistrictCard("Manor", "purple", 3, ""));


        // Reset character deck (or ensure it's properly initialized)
        this.characterDeck.clear();
        this.characterDeck.add(new CharacterCard(1, "Assassin", "Kill a character"));
        this.characterDeck.add(new CharacterCard(2, "Thief", "Steal gold from another character"));
        this.characterDeck.add(new CharacterCard(3, "Magician", "Swap hand or discard"));
        this.characterDeck.add(new CharacterCard(4, "King", "Take crown, get gold for yellow"));


        this.round = 1;
        this.crownedPlayerIndex = 0;
        players.get(0).setCrown(true);
        this.gameOver = false;
    }

    public void showAll() {
        for (Player player : players) {
            showCity(player);
        }
    }
}
