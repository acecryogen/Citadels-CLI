package citadels;

import java.util.*;

public class TurnManager {
    private Game game;
    private List<Player> players;
    private Deck districtDeck;
    private Map<Integer, Player> characterToPlayer;
    private Scanner scanner;

    public TurnManager(Game game, List<Player> players, Deck districtDeck, Map<Integer, Player> characterToPlayer) {
        this.game = game;
        this.players = players;
        this.districtDeck = districtDeck;
        this.characterToPlayer = characterToPlayer;
    }

    public void setScanner(Scanner scanner) {
        this.scanner = scanner;
    }

    public void handlePlayerTurn(Player player, int killedCharacter, int robbedCharacter) {
        if (player.isHuman()) {
            handleHumanTurn(player);
        } else {
            handleAITurn(player);
        }
    }

    private void handleHumanTurn(Player player) {
        boolean turnEnded = false;
        int buildsThisTurn = 0;
        int maxBuilds = (player.getCharacter() != null &&
                player.getCharacter().getName().equals("Architect")) ? 3 : 1;

        while (!turnEnded) {
            System.out.print("> ");
            String input = scanner.nextLine().trim();
            processTurnCommand(input, player, buildsThisTurn, maxBuilds);
        }
    }

    private void handleAITurn(Player player) {
        player.addGold(2);
        for (int i = 0; i < player.getHand().size(); i++) {
            DistrictCard card = player.getHand().get(i);
            if (player.getGold() >= card.cost && !hasDistrict(player, card.name)) {
                game.build(player, i + 1);
                break;
            }
        }
        System.out.println("Player " + player.getId() + " finished their turn.");
    }

    public void processTurns() {
        for (int i = 1; i <= 8; i++) {
            Player player = characterToPlayer.get(i);
            if (player == null)
                continue;

            System.out.println("Player " + player.getId() + "'s turn (" + player.getCharacter().getName() + ")");
            if (player.isHuman()) {
                handleHumanTurn(player);
            } else {
                handleAITurn(player);
            }
        }
    }

    private void processTurnCommand(String input, Player player, int buildsThisTurn, int maxBuilds) {
        switch (input) {
            case "gold":
                player.addGold(2);
                System.out.println("You gained 2 gold.");
                break;
            case "draw":
                player.addToHand(districtDeck.draw());
                player.addToHand(districtDeck.draw());
                System.out.println("You drew 2 cards.");
                break;
            case "build":
                if (buildsThisTurn >= maxBuilds) {
                    System.out.println("You cannot build more districts this turn.");
                    break;
                }
                System.out.print("Choose a card to build (number): ");
                try {
                    int choice = Integer.parseInt(scanner.nextLine());
                    game.build(player, choice);
                    buildsThisTurn++;
                } catch (NumberFormatException e) {
                    System.out.println("Invalid input.");
                }
                break;
            case "end":
                System.out.println("Ending your turn.");
                return;
            default:
                System.out.println("Unknown command.");
        }
    }

    private boolean hasDistrict(Player player, String districtName) {
        return player.getCity().stream().anyMatch(d -> d.name.equals(districtName));
    }
}