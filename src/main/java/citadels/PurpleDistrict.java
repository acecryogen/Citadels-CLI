package citadels;

import java.util.*;

public class PurpleDistrict {
    private Game game;
    private Deck districtDeck;
    private Scanner scanner;

    public PurpleDistrict(Game game, Deck districtDeck) {
        this.game = game;
        this.districtDeck = districtDeck;
    }

    public void setScanner(Scanner scanner) {
        this.scanner = scanner;
    }

    public void handleAbility(Player player, DistrictCard card) {
        if (!"purple".equals(card.color))
            return;

        switch (card.name) {
            case "Laboratory":
                handleLaboratory(player);
                break;
            case "Smithy":
                handleSmithy(player);
                break;
            case "Graveyard":
                System.out
                        .println("Graveyard: If one of your districts is destroyed, you may pay 1 gold to recover it.");
                break;
            case "Haunted City":
            case "School of Magic":
                System.out.println(card.name + ": Counts as any color for color bonus.");
                break;
            case "Keep":
                System.out.println("Keep: This district cannot be destroyed.");
                break;
            case "Observatory":
                System.out.println("Observatory: When you draw cards, draw 3 and keep 1.");
                break;
            case "Dragon Gate":
            case "University":
                System.out.println(card.name + ": Worth 8 points instead of 6.");
                break;
            default:
                System.out.println("Purple district ability: " + card.text);
        }
    }

    private void handleLaboratory(Player player) {
        if (player.getHand().isEmpty()) {
            System.out.println("Laboratory: No cards to discard.");
            return;
        }
        System.out.println("Laboratory: Discard a card from your hand to gain 1 gold? [yes/no]");
        String labAns = scanner.nextLine().trim().toLowerCase();
        if (labAns.equals("yes")) {
            game.showHand(player);
            System.out.print("Which card to discard? (number): ");
            try {
                int idx = Integer.parseInt(scanner.nextLine());
                if (idx >= 1 && idx <= player.getHand().size()) {
                    player.addGold(1);
                    player.removeFromHand(player.getHand().get(idx - 1));
                    System.out.println("Gained 1 gold.");
                }
            } catch (Exception e) {
                System.out.println("Invalid input.");
            }
        }
    }

    private void handleSmithy(Player player) {
        if (player.getGold() < 2) {
            System.out.println("Smithy: Not enough gold to use ability.");
            return;
        }
        System.out.println("Smithy: Pay 2 gold to draw 3 cards? [yes/no]");
        String smithyAns = scanner.nextLine().trim().toLowerCase();
        if (smithyAns.equals("yes")) {
            player.spendGold(2);
            for (int i = 0; i < 3; i++) {
                player.addToHand(districtDeck.draw());
            }
            System.out.println("You drew 3 cards.");
        }
    }

    // Add other purple district handling methods...
}