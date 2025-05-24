package citadels;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.io.*;
import java.util.*;

public class App {

    private File cardsFile;

    public App() {
        try {
            cardsFile = new File(URLDecoder.decode(this.getClass().getResource("cards.tsv").getPath(),
                    StandardCharsets.UTF_8.name()));
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }

    public List<DistrictCard> loadDistrictCards() {
        List<DistrictCard> cards = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(cardsFile))) {
            String line = br.readLine(); // skip header
            while ((line = br.readLine()) != null) {
                String[] parts = line.split("\t");
                if (parts.length < 4)
                    continue;
                String name = parts[0];
                int qty = Integer.parseInt(parts[1]);
                String color = parts[2];
                int cost = Integer.parseInt(parts[3]);
                String text = parts.length > 4 ? parts[4] : "";
                for (int i = 0; i < qty; i++) {
                    cards.add(new DistrictCard(name, color, cost, text));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return cards;
    }

    private static Deck initializeDistrictDeck() {
        App app = new App();
        List<DistrictCard> cards = app.loadDistrictCards();
        Collections.shuffle(cards);
        return new Deck(cards);
    }

    private static List<CharacterCard> initializeCharacterDeck() {
        List<CharacterCard> deck = new ArrayList<>();
        deck.add(new CharacterCard(1, "Assassin", "Kill a character"));
        deck.add(new CharacterCard(2, "Thief", "Steal gold from another character"));
        deck.add(new CharacterCard(3, "Magician", "Exchange hands or cards with another player"));
        deck.add(new CharacterCard(4, "King", "Take crown, gain gold from noble (yellow) districts"));
        deck.add(new CharacterCard(5, "Bishop", "Protected from Warlord, gain gold from religious (blue) districts"));
        deck.add(new CharacterCard(6, "Merchant", "Get 1 extra gold, gain gold from trade (green) districts"));
        deck.add(new CharacterCard(7, "Architect", "Draw 2 extra cards, can build up to 3 districts"));
        deck.add(new CharacterCard(8, "Warlord", "Destroy a district, gain gold from military (red) districts"));
        Collections.shuffle(deck);
        return deck;
    }

    // private static void processCommand(String input, Game game, Player player) {
    //     switch (input.toLowerCase()) {
    //         case "t":
    //         case "turn":
    //             game.processTurn();
    //             break;
    //         case "hand":
    //             game.showHand(player);
    //             break;
    //         case "city":
    //             game.showCity(player);
    //             break;
    //         case "all":
    //             game.showAll();
    //             break;
    //         case "gold":
    //             System.out.println("You have " + player.getGold() + " gold.");
    //             break;
    //         case "action":
    //             game.describeAction(player);
    //             break;
    //         case "help":
    //             System.out.println("Commands: turn (t), hand, city, all, gold, action, help");
    //             break;
    //         case "save":
    //             game.saveGame("citadels_save.json");
    //             System.out.println("Game saved.");
    //             break;
    //         case "load":
    //             game.loadGame("citadels_save.json");
    //             System.out.println("Game loaded.");
    //             break;
    //         case "quit":
    //             System.exit(0);
    //             break;
    //         default:
    //             if (input.startsWith("build ")) {
    //                 try {
    //                     int cardNum = Integer.parseInt(input.substring(6));
    //                     game.build(player, cardNum);
    //                 } catch (NumberFormatException e) {
    //                     System.out.println("Invalid card number.");
    //                 }
    //             } else if (input.startsWith("info ")) {
    //                 game.showInfo(input.substring(5));
    //             } else {
    //                 System.out.println("Unknown command. Type 'help' for commands.");
    //             }
    //     }
    // }

    public static void main(String[] args) {
        // Initialize scanner with System.in
        Scanner scanner = new Scanner(System.in);

        // Get number of players with proper error handling
        int numPlayers = 0;
        while (true) {
            System.out.print("Enter how many players [4-7]: ");
            try {
                if (scanner.hasNextLine()) {
                    String input = scanner.nextLine().trim();
                    numPlayers = Integer.parseInt(input);
                    if (numPlayers >= 4 && numPlayers <= 7) {
                        break; // Exit the loop when valid input is provided
                    } else {
                        System.out.println("Please enter a number between 4 and 7.");
                    }
                } else {
                    System.out.println("No input detected. Exiting...");
                    return; // Exit the program if no input is detected
                }
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a number.");
            }
        }

        // Initialize game components
        List<Player> players = new ArrayList<>();
        players.add(new Player(1, true)); // Human player
        for (int i = 2; i <= numPlayers; i++) {
            players.add(new Player(i, false)); // AI players
        }

        // Create district and character decks
        Deck deck = initializeDistrictDeck();
        List<CharacterCard> characterDeck = initializeCharacterDeck();

        // Create and start game
        Game game = new Game(players, deck, characterDeck);
        game.setScanner(scanner); // IMPORTANT: Ensure this line is added

        // New Game loop - round-based
        while (!game.isOver()) {
            System.out.println("\n--- Round " + game.getRound() + " ---");

            // Phase 1: Character Selection
            System.out.println("Character Selection Phase...");
            game.characterSelectionPhase(scanner);

            // Phase 2: Turn Phase
            System.out.println("\nTurn Phase...");
            game.turnPhase(scanner);

            // Check for game over condition immediately after turns are processed
            // (game.isOver() might be updated during turnPhase if a win condition is met)
            if (game.isOver()) {
                // scoreManager.showScores() is often called within game.turnPhase() or game.isOver() when true
                // If not, it might be needed here. For now, assume turnPhase or isOver handles final score display.
                break; 
            }

            // End of round logic
            game.setRound(game.getRound() + 1);
            // Optional: Add a small delay or prompt to continue to next round for better UX
            // System.out.println("Press Enter to start next round...");
            // scanner.nextLine(); 
        }

        System.out.println("Game has ended.");
        // game.showScores(); // Ensure scores are shown if not handled by turnPhase when game ends.
        
        // Clean up
        scanner.close();
    }

    // ... rest of the class implementation
}
