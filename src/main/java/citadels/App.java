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

    public static void main(String[] args) {
        App app = new App();
        List<DistrictCard> cards = app.loadDistrictCards();
        Deck deck = new Deck(cards);

        // Example character cards (should be expanded with all 8 and their abilities)
        List<CharacterCard> characterDeck = Arrays.asList(
                new CharacterCard(1, "Assassin", "Kill a character."),
                new CharacterCard(2, "Thief", "Steal from a character."),
                new CharacterCard(3, "Magician", "Swap or redraw cards."),
                new CharacterCard(4, "King", "Gain gold for yellow districts."),
                new CharacterCard(5, "Bishop", "Gain gold for blue districts."),
                new CharacterCard(6, "Merchant", "Gain gold for green districts."),
                new CharacterCard(7, "Architect", "Draw extra cards, build up to 3."),
                new CharacterCard(8, "Warlord", "Destroy a district."));

        Scanner scanner = new Scanner(System.in);
        System.out.println("Enter how many players [4-7]:");
        int numPlayers = 0;
        while (numPlayers < 4 || numPlayers > 7) {
            try {
                numPlayers = Integer.parseInt(scanner.nextLine());
                if (numPlayers < 4 || numPlayers > 7) {
                    System.out.println("Please enter a number between 4 and 7:");
                }
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a number between 4 and 7:");
            }
        }

        List<Player> players = new ArrayList<>();
        players.add(new Player(1, true)); // Human player
        for (int i = 2; i <= numPlayers; i++) {
            players.add(new Player(i, false));
        }

        Game game = new Game(players, deck, characterDeck);

        System.out.println("Starting Citadels with " + numPlayers + " players...");
        System.out.println("You are player 1");

        while (!game.isOver()) {
            System.out.print("> ");
            String input = scanner.nextLine().trim();
            String[] tokens = input.split("\\s+");
            Player human = players.get(0);

            switch (tokens[0]) {
                case "t":
                    game.processTurn();
                    break;
                case "hand":
                    game.showHand(human);
                    break;
                case "gold":
                    System.out.println("You have " + human.getGold() + " gold.");
                    break;
                case "build":
                    if (tokens.length > 1) {
                        try {
                            int idx = Integer.parseInt(tokens[1]);
                            game.build(human, idx);
                        } catch (NumberFormatException e) {
                            System.out.println("Usage: build <card number>");
                        }
                    } else {
                        System.out.println("Usage: build <card number>");
                    }
                    break;
                case "citadel":
                case "city":
                case "list":
                    if (tokens.length > 1) {
                        try {
                            int pid = Integer.parseInt(tokens[1]);
                            if (pid >= 1 && pid <= players.size()) {
                                game.showCity(players.get(pid - 1));
                            } else {
                                System.out.println("No such player.");
                            }
                        } catch (NumberFormatException e) {
                            System.out.println("Usage: " + tokens[0] + " [player number]");
                        }
                    } else {
                        game.showCity(human);
                    }
                    break;
                case "all":
                    game.showAll();
                    break;
                case "help":
                    System.out.println("Available commands:");
                    System.out.println("t : process turns");
                    System.out.println("hand : show your hand");
                    System.out.println("gold : show your gold");
                    System.out.println("build <n> : build card number n from your hand");
                    System.out.println("citadel/list/city [p] : show city of player p (default you)");
                    System.out.println("all : show all players");
                    System.out.println("help : show this help message");
                    break;
                case "end":
                    System.out.println("You ended your turn.");
                    break;
                default:
                    System.out.println("Unknown command. Type 'help' for options.");
            }
        }
        System.out.println("Game over!");
        // game.showScores(); // Implement scoring and call here
    }
}
