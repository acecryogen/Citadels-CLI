package citadels;

import java.util.*;

public class CharacterAbilities {
    private Game game;
    private List<Player> players;
    private Deck districtDeck;
    private List<CharacterCard> characterDeck;
    private Map<Integer, Player> characterToPlayer;
    private Scanner scanner;

    public CharacterAbilities(Game game, List<Player> players, Deck districtDeck,
            List<CharacterCard> characterDeck, Map<Integer, Player> characterToPlayer) {
        this.game = game;
        this.players = players;
        this.districtDeck = districtDeck;
        this.characterDeck = characterDeck;
        this.characterToPlayer = characterToPlayer;
    }

    public void setScanner(Scanner scanner) {
        this.scanner = scanner;
    }

    public int handleAbility(Player player, int killedCharacter, int robbedCharacter) {
        CharacterCard character = player.getCharacter();
        if (character == null)
            return -1;

        switch (character.getName()) {
            case "Assassin":
                return handleAssassin(player);
            case "Thief":
                return handleThief(player);
            case "Magician":
                handleMagician(player);
                break;
            case "King":
                handleKing(player);
                break;
            case "Bishop":
                handleBishop(player);
                break;
            case "Merchant":
                handleMerchant(player);
                break;
            case "Architect":
                handleArchitect(player);
                break;
            case "Warlord":
                handleWarlord(player, killedCharacter);
                break;
        }
        return -1;
    }

    private int handleAssassin(Player player) {
        if (scanner != null) {
            System.out.println(
                    "Who do you want to kill? (2-Thief, 3-Magician, 4-King, 5-Bishop, 6-Merchant, 7-Architect, 8-Warlord)");
            int target = -1;
            while (target < 2 || target > 8) {
                System.out.print("Enter character number to kill: ");
                try {
                    target = Integer.parseInt(scanner.nextLine());
                } catch (Exception e) {
                    target = -1;
                }
            }
            System.out.println("You chose to kill: " + getCharacterName(target));
            return target;
        }
        return 2 + new Random().nextInt(7); // AI: pick random valid target
    }

    private int handleThief(Player player) {
        if (scanner != null) {
            System.out.println(
                    "Who do you want to steal from? (3-Magician, 4-King, 5-Bishop, 6-Merchant, 7-Architect, 8-Warlord)");
            int target = -1;
            while (target < 3 || target > 8) {
                System.out.print("Enter character number to steal from: ");
                try {
                    target = Integer.parseInt(scanner.nextLine());
                } catch (Exception e) {
                    target = -1;
                }
            }
            System.out.println("You chose to steal from: " + getCharacterName(target));
            return target;
        }
        return 3 + new Random().nextInt(6); // AI: pick random valid target
    }

    private void handleMagician(Player player) {
        if (scanner != null) {
            System.out.println("Magician: swap hand with another player or redraw cards.");
            System.out.print("Type 'swap <player number>' or 'redraw <card numbers,comma separated>': ");
            String action = scanner.nextLine().trim();
            handleMagicianAction(player, action);
        }
    }

    private void handleKing(Player player) {
        int yellow = (int) player.getCity().stream().filter(c -> c.color.equals("yellow")).count();
        if (yellow > 0) {
            player.addGold(yellow);
            System.out.println("King: You gained " + yellow + " gold for yellow districts.");
        }
        game.setCrownedPlayerIndex(players.indexOf(player));
        players.forEach(p -> p.setCrown(false));
        player.setCrown(true);
    }

    private void handleBishop(Player player) {
        int blueDistricts = (int) player.getCity().stream().filter(c -> c.color.equals("blue")).count();
        if (blueDistricts > 0) {
            player.addGold(blueDistricts);
            System.out.println("Bishop: You gained " + blueDistricts + " gold for blue districts.");
        }
    }

    private void handleMerchant(Player player) {
        int greenDistricts = (int) player.getCity().stream().filter(c -> c.color.equals("green")).count();
        if (greenDistricts > 0) {
            player.addGold(greenDistricts);
            System.out.println("Merchant: You gained " + greenDistricts + " gold for green districts.");
        }
        player.addGold(1); // Merchant always gains 1 extra gold
        System.out.println("Merchant: You gained 1 extra gold.");
    }

    private void handleArchitect(Player player) {
        for (int i = 0; i < 2; i++) {
            DistrictCard card = districtDeck.draw();
            if (card != null) {
                player.addToHand(card);
            }
        }
        System.out.println("Architect: You drew 2 extra cards.");
    }

    private void handleWarlord(Player player, int killedCharacter) {
        System.out.println("Warlord: Destroy a district for gold.");
        // Add logic for destroying a district
    }

    private String getCharacterName(int characterNumber) {
        for (CharacterCard card : characterDeck) {
            if (card.getNumber() == characterNumber) {
                return card.getName();
            }
        }
        return "Unknown";
    }

    private void handleMagicianAction(Player player, String action) {
        if (action.startsWith("swap")) {
            int targetPlayerId = Integer.parseInt(action.split(" ")[1]);
            Player targetPlayer = players.stream().filter(p -> p.getId() == targetPlayerId).findFirst().orElse(null);
            if (targetPlayer != null) {
                List<DistrictCard> temp = player.getHand();
                player.setHand(targetPlayer.getHand());
                targetPlayer.setHand(temp);
                System.out.println("You swapped hands with Player " + targetPlayerId);
            } else {
                System.out.println("Invalid player number.");
            }
        } else if (action.startsWith("redraw")) {
            System.out.println("Redraw logic not implemented yet.");
        } else {
            System.out.println("Invalid action.");
        }
    }

    // Placeholder for AI character ability logic
    public void handleAbilityAI(Player player, int killedCharacter, int robbedCharacter) {
        CharacterCard character = player.getCharacter();
        if (character == null)
            return;

        // AI doesn't use scanner, makes decisions programmatically
        System.out.println(
                "Player " + player.getId() + " (" + character.getName() + ") is considering its AI ability logic.");
        // Basic AI logic placeholders:
        switch (character.getName()) {
            case "Assassin":
                // AI Assassin might pick a random player/character to "kill"
                // This would typically involve updating game state (e.g.
                // game.setKilledCharacterThisRound(targetRank))
                System.out.println("AI Assassin: Deciding who to kill (not fully implemented).");
                break;
            case "Thief":
                // AI Thief might pick a random player/character to "rob"
                System.out.println("AI Thief: Deciding who to rob (not fully implemented).");
                break;
            case "Magician":
                // AI Magician might swap with poorest player or redraw if hand is bad
                System.out.println("AI Magician: Deciding on magic action (not fully implemented).");
                break;
            // King, Bishop, Merchant abilities are passive income, already handled by their
            // specific methods if called during turn.
            // Architect draws cards, already handled.
            case "Warlord":
                // AI Warlord might destroy cheapest district of richest player
                System.out.println("AI Warlord: Deciding what to destroy (not fully implemented).");
                break;
            default:
                // System.out.println("AI " + character.getName() + ": No specific AI ability
                // action defined here.");
                break;
        }
    }
}