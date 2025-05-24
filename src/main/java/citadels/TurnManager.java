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
        this.characterToPlayer = characterToPlayer; // This map is passed in, potentially null initially
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
        System.out.println("\nIt's your turn, Player " + player.getId() +
                           (player.getCharacter() != null ? " (" + player.getCharacter().getName() + ")" : "") + ".");
        
        boolean turnOver = false;
        boolean hasTakenMainAction = false; // True if player took gold or drew cards
        int buildsDoneThisTurn = 0; // Actual successful builds
        int maxBuilds = 1; // Default
        if (player.getCharacter() != null && "Architect".equals(player.getCharacter().getName())) {
            maxBuilds = 3;
        }

        while (!turnOver) {
            System.out.print((player.getCharacter() != null ? player.getCharacter().getName() : "Player " + player.getId()) + " > ");
            String input = "";
            if (scanner != null && scanner.hasNextLine()) {
                input = scanner.nextLine().trim();
            } else { 
                System.out.println("No input detected or scanner closed. Ending turn by default.");
                turnOver = true; 
                continue;
            }

            if (input.equalsIgnoreCase("end") || input.equalsIgnoreCase("done")) {
                System.out.println("Ending your turn.");
                turnOver = true;
                continue;
            }

            switch (input.toLowerCase()) {
                case "gold":
                    if (!hasTakenMainAction) {
                        player.addGold(2);
                        System.out.println("You took 2 gold. You now have " + player.getGold() + " gold.");
                        hasTakenMainAction = true;
                    } else {
                        System.out.println("You have already taken your main action (gold or cards).");
                    }
                    break;
                case "draw":
                    if (!hasTakenMainAction) {
                        DistrictCard drawnCard1 = districtDeck.draw();
                        if (drawnCard1 != null) {
                            player.addToHand(drawnCard1);
                            System.out.println("You drew a card: " + drawnCard1.name + " (" + drawnCard1.cost + " gold, " + drawnCard1.color + ").");
                            hasTakenMainAction = true;
                        } else {
                            System.out.println("District deck is empty.");
                        }
                    } else {
                        System.out.println("You have already taken your main action (gold or cards).");
                    }
                    break;
                case "build": 
                    System.out.println("To build, type 'build <number_from_hand>'. Type 'hand' to see your cards and their numbers.");
                    break;
                case "action":
                    System.out.println("Attempting to use character ability (" + (player.getCharacter() != null ? player.getCharacter().getName() : "No character") + ")...");
                    if (player.getCharacter() != null && game.getCharacterAbilities() != null) { // Assuming getCharacterAbilities exists
                        game.getCharacterAbilities().handleAbility(player, game.getKilledCharacterThisRound(), game.getRobbedCharacterThisRound());
                        // System.out.println(player.getCharacter().getName() + "'s ability was attempted.");
                    } else if (player.getCharacter() == null) {
                        System.out.println("You do not have a character to use an ability.");
                    } else {
                         System.out.println("CharacterAbilities not available in Game object.");
                    }
                    break;
                case "hand":
                    game.showHand(player);
                    break;
                case "city":
                    game.showCity(player);
                    break;
                case "mygold":
                    System.out.println("You have " + player.getGold() + " gold.");
                    break;
                case "help":
                    System.out.println("Available commands: gold, draw, build <num>, action, hand, city, mygold, end/done.");
                    System.out.println("Special commands for Architect: can build up to 3 districts.");
                    break;
                default:
                    if (input.startsWith("build ")) {
                        if (!hasTakenMainAction) {
                            System.out.println("You must take your main action (gold or draw) before building.");
                            break;
                        }
                        if (buildsDoneThisTurn < maxBuilds) {
                            try {
                                int cardNum = Integer.parseInt(input.substring(6).trim());
                                int citySizeBefore = player.getCity().size();
                                game.build(player, cardNum); // game.build is void
                                if (player.getCity().size() > citySizeBefore) {
                                    buildsDoneThisTurn++;
                                    System.out.println("District built. Builds done this turn: " + buildsDoneThisTurn + "/" + maxBuilds);
                                } else {
                                    // Build failed, message should come from game.build()
                                    System.out.println("Build may have failed or card was not buildable. Check messages above.");
                                }
                            } catch (NumberFormatException e) {
                                System.out.println("Invalid card number. Use 'build <number_from_hand>'.");
                            } catch (IndexOutOfBoundsException e) {
                                System.out.println("Invalid card number. Type 'hand' to see your cards and their numbers.");
                            }
                        } else {
                            System.out.println("You have already built your maximum of " + maxBuilds + " district(s) this turn.");
                        }
                    } else {
                        System.out.println("Unknown command. Type 'help' for options.");
                    }
                    break;
            }
        }
    }

    private void handleAITurn(Player player) {
        System.out.println("Player " + player.getId() + 
                           (player.getCharacter() != null ? " (" + player.getCharacter().getName() + ")" : "") + 
                           " is an AI and is taking its turn.");

        if (player.getGold() < 3 || player.getHand().size() < 2 && districtDeck.cardsLeft() > 0) { // AI prefers gold if poor or hand is small (and can draw)
            player.addGold(2);
            System.out.println("Player " + player.getId() + " took 2 gold.");
        } else if (districtDeck.cardsLeft() > 0) {
            DistrictCard drawn = districtDeck.draw();
            if (drawn != null) { // Should always be true if cardsLeft > 0
                player.addToHand(drawn);
                System.out.println("Player " + player.getId() + " drew: " + drawn.name);
            }
        } else { // No cards to draw, take gold
             player.addGold(2);
             System.out.println("Player " + player.getId() + " took 2 gold as deck is empty.");
        }
        
        int maxBuildsAI = (player.getCharacter() != null && "Architect".equals(player.getCharacter().getName())) ? 3 : 1;
        for (int i = 0; i < maxBuildsAI; i++) {
            DistrictCard toBuild = null;
            int cardIndexToBuild = -1; 
            for (int j = 0; j < player.getHand().size(); j++) {
                DistrictCard card = player.getHand().get(j);
                if (player.getGold() >= card.cost && !hasDistrict(player, card.name)) { 
                    if (toBuild == null || card.cost < toBuild.cost) {
                        toBuild = card;
                        cardIndexToBuild = j + 1; 
                    }
                }
            }

            if (toBuild != null) {
                int citySizeBefore = player.getCity().size();
                game.build(player, cardIndexToBuild);
                if(player.getCity().size() > citySizeBefore) {
                     System.out.println("Player " + player.getId() + " built " + toBuild.name + ".");
                } else {
                    break; 
                }
            } else {
                break;
            }
        }
        
        if (player.getCharacter() != null && game.getCharacterAbilities() != null) {
             System.out.println("Player " + player.getId() + " (" + player.getCharacter().getName() + ") considers using ability.");
             game.getCharacterAbilities().handleAbilityAI(player, game.getKilledCharacterThisRound(), game.getRobbedCharacterThisRound());
        }

        System.out.println("Player " + player.getId() + " finished their turn.");
    }

    public void processTurns() {
        if (characterToPlayer == null) {
            System.out.println("Error: Character to Player map not initialized in TurnManager. Skipping turn processing.");
            return;
        }
        for (int rank = 1; rank <= 8; rank++) {
            Player currentPlayer = characterToPlayer.get(rank);

            if (currentPlayer == null) { 
                CharacterCard c = game.getCharacterByRank(rank); 
                String characterName = (c != null) ? c.getName() : "Character Rank " + rank;
                if (game.getKilledCharacterThisRound() == rank) {
                     System.out.println(characterName + " was killed and cannot take a turn.");
                }
                continue;
            }
            
            if (currentPlayer.getCharacter() == null) { // Should not happen if currentPlayer is not null
                 System.out.println("Error: Player " + currentPlayer.getId() + " has no character assigned. Skipping turn.");
                 continue;
            }

            if (game.getKilledCharacterThisRound() == currentPlayer.getCharacter().getRank()) {
                System.out.println(currentPlayer.getCharacter().getName() + " (Player " + currentPlayer.getId() + ") was killed and skips their turn.");
                continue;
            }

            // System.out.println("\nNow taking turn: " + currentPlayer.getCharacter().getName() +
            //                    " (Player " + currentPlayer.getId() + (currentPlayer.isHuman() ? ", Human" : ", AI") + ")");
            handlePlayerTurn(currentPlayer, game.getKilledCharacterThisRound(), game.getRobbedCharacterThisRound());
            
            if (game.checkGameOverCondition()) { 
                game.setOver(true); 
                System.out.println("Game over condition met after Player " + currentPlayer.getId() + "'s turn.");
                break; 
            }
        }
    }

    /*
    private void processTurnCommand(String input, Player player, int buildsThisTurn, int maxBuilds) {
        // This method is now commented out as its logic is integrated into handleHumanTurn
        // switch (input) {
        //     case "gold":
        //         player.addGold(2);
        //         System.out.println("You gained 2 gold.");
        //         break;
        //     case "draw":
        //         player.addToHand(districtDeck.draw());
        //         player.addToHand(districtDeck.draw());
        //         System.out.println("You drew 2 cards.");
        //         break;
        //     case "build":
        //         if (buildsThisTurn >= maxBuilds) {
        //             System.out.println("You cannot build more districts this turn.");
        //             break;
        //         }
        //         System.out.print("Choose a card to build (number): ");
        //         try {
        //             int choice = Integer.parseInt(scanner.nextLine());
        //             game.build(player, choice);
        //             buildsThisTurn++;
        //         } catch (NumberFormatException e) {
        //             System.out.println("Invalid input.");
        //         }
        //         break;
        //     case "end":
        //         System.out.println("Ending your turn.");
        //         return; // This was problematic as it didn't set a flag for the loop in handleHumanTurn
        //     default:
        //         System.out.println("Unknown command.");
        // }
    }
    */

    private boolean hasDistrict(Player player, String districtName) {
        return player.getCity().stream().anyMatch(d -> d.name.equals(districtName));
    }
}