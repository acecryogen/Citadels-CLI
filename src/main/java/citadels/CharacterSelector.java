package citadels;

import java.util.*;

public class CharacterSelector {
    private Game game;
    private List<Player> players;
    private List<CharacterCard> characterDeck;
    private Scanner scanner;
    private int killedCharacterRank = -1; // Placeholder for actual logic
    private int robbedCharacterRank = -1; // Placeholder for actual logic

    public CharacterSelector(Game game, List<Player> players, List<CharacterCard> characterDeck) {
        this.game = game;
        this.players = players;
        this.characterDeck = characterDeck;
    }

    public void setScanner(Scanner scanner) {
        this.scanner = scanner;
    }

    // Added placeholder getter
    public int getKilledCharacterRank() {
        // In a real implementation, this would be determined by Assassin's choice
        return killedCharacterRank; 
    }
    
    // Added placeholder getter
    public int getRobbedCharacterRank() {
        // In a real implementation, this would be determined by Thief's choice
        return robbedCharacterRank;
    }

    public Map<Integer, Player> selectCharacters(int crownedPlayerIndex) {
        Map<Integer, Player> characterToPlayer = new HashMap<>();
        // Reset killed/robbed status at the start of selection
        this.killedCharacterRank = -1;
        this.robbedCharacterRank = -1;
        List<CharacterCard> availableCharacters = setupAvailableCharacters();
        List<Player> pickOrder = determinePickOrder(crownedPlayerIndex);

        for (Player p : pickOrder) {
            if (p.isHuman()) {
                handleHumanSelection(p, availableCharacters, characterToPlayer);
            } else {
                handleAISelection(p, availableCharacters, characterToPlayer);
            }
        }

        return characterToPlayer;
    }

    private List<CharacterCard> setupAvailableCharacters() {
        List<CharacterCard> available = new ArrayList<>(characterDeck);
        Collections.shuffle(available);

        // Remove face down card
        available.remove(0);
        System.out.println("A mystery character was removed.");

        // Remove face up cards based on player count
        removeFaceUpCards(available);

        return available;
    }

    private void removeFaceUpCards(List<CharacterCard> available) {
        int faceUpToRemove = players.size() == 4 ? 2 : (players.size() == 5 ? 1 : 0);
        for (int i = 0; i < faceUpToRemove; i++) {
            CharacterCard removed = available.remove(0);
            if (removed.getName().equals("King")) {
                available.add(removed);
                Collections.shuffle(available);
                removed = available.remove(0);
            }
            System.out.println(removed.getName() + " was removed.");
        }
    }

    private List<Player> determinePickOrder(int crownedPlayerIndex) {
        List<Player> pickOrder = new ArrayList<>();

        // Add players starting from the crowned player
        for (int i = crownedPlayerIndex; i < players.size(); i++) {
            pickOrder.add(players.get(i));
        }

        // Add players before the crowned player
        for (int i = 0; i < crownedPlayerIndex; i++) {
            pickOrder.add(players.get(i));
        }

        return pickOrder;
    }

    private void handleHumanSelection(Player player, List<CharacterCard> availableCharacters,
            Map<Integer, Player> characterToPlayer) {
        System.out.println("Available characters:");
        for (int i = 0; i < availableCharacters.size(); i++) {
            System.out.println((i + 1) + ": " + availableCharacters.get(i).getName());
        }

        System.out.print("Choose a character (number): ");
        int choice = -1;
        while (choice < 1 || choice > availableCharacters.size()) {
            try {
                choice = Integer.parseInt(scanner.nextLine());
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a number.");
            }
        }

        CharacterCard chosen = availableCharacters.remove(choice - 1);
        characterToPlayer.put(chosen.getNumber(), player);
        player.setCharacter(chosen);
        System.out.println("You chose: " + chosen.getName());
    }

    private void handleAISelection(Player player, List<CharacterCard> availableCharacters,
            Map<Integer, Player> characterToPlayer) {
        CharacterCard chosen = availableCharacters.remove(0); // AI picks the first available character
        characterToPlayer.put(chosen.getNumber(), player);
        player.setCharacter(chosen);
        System.out.println("Player " + player.getId() + " chose: " + chosen.getName());
    }
}