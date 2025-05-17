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
    private Map<Integer, Player> characterToPlayer; // character number -> player

    public Game(List<Player> players, Deck districtDeck, List<CharacterCard> characterDeck) {
        this.players = players;
        this.districtDeck = districtDeck;
        this.characterDeck = characterDeck;
        this.crownedPlayerIndex = new Random().nextInt(players.size());
        this.gameOver = false;
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

    // === PHASE 1: CHARACTER SELECTION ===
    public void characterSelectionPhase(Scanner scanner) {
        System.out.println("================================");
        System.out.println("SELECTION PHASE (Round " + round + ")");
        System.out.println("================================");

        // 1. Shuffle character deck and discard as per rules
        availableCharacters = new ArrayList<>(characterDeck);
        Collections.shuffle(availableCharacters);

        // Discard 1 face down
        CharacterCard mystery = availableCharacters.remove(0);
        System.out.println("A mystery character was removed.");

        // Discard face up as per rules (for 4-7 players)
        int faceUpToRemove = players.size() == 4 ? 2 : (players.size() == 5 ? 1 : 0);
        for (int i = 0; i < faceUpToRemove; i++) {
            CharacterCard removed = availableCharacters.remove(0);
            if (removed.getName().equals("King")) {
                // King cannot be removed face up, put back and remove another
                availableCharacters.add(removed);
                Collections.shuffle(availableCharacters);
                removed = availableCharacters.remove(0);
            }
            System.out.println(removed.getName() + " was removed.");
        }

        // 2. Players pick characters in order, starting with crowned player
        characterToPlayer = new HashMap<>();
        int n = players.size();
        int start = crownedPlayerIndex;
        List<Player> pickOrder = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            pickOrder.add(players.get((start + i) % n));
        }

        for (Player p : pickOrder) {
            if (p.isHuman()) {
                System.out.println("Available characters:");
                for (int i = 0; i < availableCharacters.size(); i++) {
                    System.out.println((i + 1) + ". " + availableCharacters.get(i).getName());
                }
                int choice = -1;
                while (choice < 1 || choice > availableCharacters.size()) {
                    System.out.print("Choose your character [1-" + availableCharacters.size() + "]: ");
                    try {
                        choice = Integer.parseInt(scanner.nextLine());
                    } catch (Exception e) {
                        choice = -1;
                    }
                }
                CharacterCard chosen = availableCharacters.remove(choice - 1);
                p.setCharacter(chosen);
                characterToPlayer.put(chosen.getNumber(), p);
                System.out.println("You chose: " + chosen.getName());
            } else {
                // AI: pick random character
                CharacterCard chosen = availableCharacters.remove(0);
                p.setCharacter(chosen);
                characterToPlayer.put(chosen.getNumber(), p);
                System.out.println("Player " + p.getId() + " chose a character.");
            }
        }
    }

    // === PHASE 2: TURN PHASE ===
    public void turnPhase(Scanner scanner) {
        System.out.println("================================");
        System.out.println("TURN PHASE (Round " + round + ")");
        System.out.println("================================");

        // Characters act in order 1-8
        for (int num = 1; num <= 8; num++) {
            Player p = characterToPlayer.get(num);
            if (p == null) {
                System.out.println(num + ": " + characterName(num));
                System.out.println("No one is the " + characterName(num));
                continue;
            }
            System.out.println(num + ": " + p.getCharacter().getName());
            if (p.isHuman()) {
                System.out.println("Your turn.");
                // Example: let user collect gold or draw cards
                boolean turnEnded = false;
                while (!turnEnded) {
                    System.out.print("> ");
                    String input = scanner.nextLine().trim();
                    switch (input) {
                        case "gold":
                            p.addGold(2);
                            System.out.println("You received 2 gold.");
                            break;
                        case "hand":
                            showHand(p);
                            break;
                        case "build":
                            showHand(p);
                            System.out.print("Which card to build? (number): ");
                            try {
                                int idx = Integer.parseInt(scanner.nextLine());
                                build(p, idx);
                            } catch (Exception e) {
                                System.out.println("Invalid input.");
                            }
                            break;
                        case "end":
                            turnEnded = true;
                            break;
                        default:
                            System.out.println("Commands: gold, hand, build, end");
                    }
                }
            } else {
                // Simple AI: always take gold, build if possible
                p.addGold(2);
                for (int i = 0; i < p.getHand().size(); i++) {
                    DistrictCard card = p.getHand().get(i);
                    if (p.getGold() >= card.cost && !hasDistrict(p, card.name)) {
                        build(p, i + 1);
                        break;
                    }
                }
                System.out.println("Player " + p.getId() + " finished their turn.");
            }
            // Check for 8 districts
            if (p.getCity().size() >= 8) {
                gameOver = true;
            }
        }
        round++;
    }

    private boolean hasDistrict(Player p, String name) {
        for (DistrictCard d : p.getCity()) {
            if (d.name.equals(name))
                return true;
        }
        return false;
    }

    private String characterName(int num) {
        for (CharacterCard c : characterDeck) {
            if (c.getNumber() == num)
                return c.getName();
        }
        return "Unknown";
    }

    public void processTurn() {
        // This method is called from App when "t" is pressed
        Scanner scanner = new Scanner(System.in);
        characterSelectionPhase(scanner);
        turnPhase(scanner);
        if (gameOver) {
            System.out.println("Game over! (Scoring not implemented)");
        }
    }

    public void showHand(Player player) {
        System.out.println("You have " + player.getGold() + " gold. Cards in hand:");
        int i = 1;
        for (DistrictCard card : player.getHand()) {
            System.out.println(i + ". " + card);
            i++;
        }
    }

    public void build(Player player, int handIndex) {
        if (handIndex < 1 || handIndex > player.getHand().size()) {
            System.out.println("Invalid card position.");
            return;
        }
        DistrictCard card = player.getHand().get(handIndex - 1);
        if (player.getGold() < card.cost) {
            System.out.println("Not enough gold to build " + card.name + ".");
            return;
        }
        // Check for duplicate in city
        for (DistrictCard built : player.getCity()) {
            if (built.name.equals(card.name)) {
                System.out.println("You already have " + card.name + " in your city.");
                return;
            }
        }
        player.buildDistrict(card);
        System.out.println("Built " + card);
    }

    public void showCity(Player player) {
        System.out.println("Player " + player.getId() + (player.isHuman() ? " (you)" : "") + " has built:");
        if (player.getCity().isEmpty()) {
            System.out.println("No districts built yet.");
        } else {
            for (DistrictCard card : player.getCity()) {
                System.out.println(card + ", points: " + card.cost);
            }
        }
    }

    public void showAll() {
        for (Player p : players) {
            System.out.println(p);
        }
    }

    // TODO: Add methods for scoring, saving/loading, and special abilities
}
