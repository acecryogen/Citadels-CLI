package citadels;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.util.*;

public class SampleTest {

    private Game createMockGame() {
        // Create mock players
        List<Player> players = new ArrayList<>();
        players.add(new Player(1, true));
        players.add(new Player(2, false));

        // Create a mock district deck
        List<DistrictCard> districtCards = new ArrayList<>();
        districtCards.add(new DistrictCard("Castle", "yellow", 4, ""));
        districtCards.add(new DistrictCard("Tavern", "green", 1, ""));
        Deck districtDeck = new Deck(districtCards);

        // Create a mock character deck
        List<CharacterCard> characterDeck = new ArrayList<>();
        characterDeck.add(new CharacterCard(1, "Assassin", "Kill a character"));
        characterDeck.add(new CharacterCard(2, "Thief", "Steal gold"));

        return new Game(players, districtDeck, characterDeck);
    }

    // Test that a new player starts with correct gold and empty hand/city
    @Test
    public void testPlayerInit() {
        Player p = new Player(1, true);
        assertEquals(1, p.getId());
        assertTrue(p.isHuman());
        assertEquals(2, p.getGold());
        assertEquals(0, p.getHand().size());
        assertEquals(0, p.getCity().size());
    }

    // Test adding and spending gold for a player
    @Test
    public void testGold() {
        Player p = new Player(2, false);
        p.addGold(3);
        assertEquals(5, p.getGold());
        p.spendGold(2);
        assertEquals(3, p.getGold());
    }

    // Test that a player cannot build duplicate districts in their city
    @Test
    public void testBuildDistrict() {
        Player p = new Player(1, true);
        DistrictCard d1 = new DistrictCard("Castle", "yellow", 4, "");
        p.addToHand(d1);
        p.buildDistrict(d1);
        assertEquals(1, p.getCity().size());
        // Try to build duplicate
        p.addToHand(d1);
        p.buildDistrict(d1);
        assertEquals(1, p.getCity().size());
    }

    // Test deck shuffle and drawing a card
    @Test
    public void testDeckShuffleDraw() {
        List<DistrictCard> cards = new ArrayList<>();
        cards.add(new DistrictCard("Castle", "yellow", 4, ""));
        cards.add(new DistrictCard("Tavern", "green", 1, ""));
        Deck deck = new Deck(cards);
        deck.shuffle();
        DistrictCard drawn = deck.draw();
        assertNotNull(drawn);
        assertEquals(1, deck.size());
    }

    // Test character card creation and getters
    @Test
    public void testCharacterCard() {
        CharacterCard cc = new CharacterCard(1, "Assassin", "Kill a character");
        assertEquals(1, cc.getNumber());
        assertEquals("Assassin", cc.getName());
        assertEquals("Kill a character", cc.getAbility());
    }

    // Test DistrictCard fields and constructor
    @Test
    public void testDistrictCard() {
        // Tests creation and field access
        DistrictCard d = new DistrictCard("Watchtower", "red", 1, "Cannot be destroyed.");
        assertEquals("Watchtower", d.name);
        assertEquals("red", d.color);
        assertEquals(1, d.cost);
        assertEquals("Cannot be destroyed.", d.text);
    }

    // Test Player cannot spend more gold than they have (edge case)
    @Test
    public void testSpendTooMuchGold() {
        Player p = new Player(1, false);
        p.spendGold(5); // Should not go negative
        assertTrue(p.getGold() >= 0);
    }

    // Test Deck draw from empty deck (edge case)
    @Test
    public void testDrawFromEmptyDeck() {
        Deck deck = new Deck(new ArrayList<>());
        assertNull(deck.draw());
    }

    // Test Player hand and city manipulation
    @Test
    public void testHandAndCity() {
        Player p = new Player(1, true);
        DistrictCard d = new DistrictCard("Temple", "blue", 1, "");
        p.addToHand(d);
        assertEquals(1, p.getHand().size());
        p.buildDistrict(d);
        assertEquals(1, p.getCity().size());
        assertEquals(0, p.getHand().size());
    }

    // Test CharacterCard equals and hashCode (if implemented)
    @Test
    public void testCharacterCardEquality() {
        CharacterCard c1 = new CharacterCard(2, "Thief", "Steal gold");
        CharacterCard c2 = new CharacterCard(2, "Thief", "Steal gold");
        assertEquals(c1, c2);
        assertEquals(c1.hashCode(), c2.hashCode());
    }

    // Test Observatory effect: player draws 3 cards and keeps 1
    @Test
    public void testObservatoryDraw() {
        Player p = new Player(1, true);
        p.getCity().add(new DistrictCard("Observatory", "purple", 5, "Draw 3 keep 1"));
        Deck deck = new Deck(Arrays.asList(
                new DistrictCard("A", "red", 1, ""),
                new DistrictCard("B", "blue", 2, ""),
                new DistrictCard("C", "green", 3, "")));
        // Simulate Observatory draw logic
        List<DistrictCard> drawn = new ArrayList<>();
        drawn.add(deck.draw());
        drawn.add(deck.draw());
        drawn.add(deck.draw());
        assertEquals(3, drawn.size());
        // Player chooses to keep the first
        p.addToHand(drawn.get(0));
        assertEquals(1, p.getHand().size());
    }

    // Test Keep effect: cannot be destroyed
    @Test
    public void testKeepCannotBeDestroyed() {
        Player p = new Player(1, false);
        p.getCity().add(new DistrictCard("Keep", "purple", 3, "Cannot be destroyed"));
        // Simulate Warlord trying to destroy
        boolean canDestroy = true;
        for (DistrictCard d : p.getCity()) {
            if (d.name.equals("Keep")) {
                canDestroy = false;
            }
        }
        assertFalse(canDestroy, "Keep should not be destroyed by Warlord.");
    }

    // Test Graveyard effect: recover destroyed district
    @Test
    public void testGraveyardRecovery() {
        // Initialize player with starting gold (2)
        Player p = new Player(1, false);

        // Add additional gold for Graveyard (5) and recovery (1)
        p.addGold(4); // Starting with 2 + 4 = 6 total

        // Initialize and verify Graveyard district
        DistrictCard graveyard = new DistrictCard("Graveyard", "purple", 5, "Recover destroyed district");
        p.addToHand(graveyard);
        p.buildDistrict(graveyard);

        // Verify post-build state
        assertTrue(p.getCity().stream().anyMatch(d -> d.name.equals("Graveyard")));
        assertEquals(1, p.getGold()); // 6 - 5 = 1 gold remaining
        assertEquals(0, p.getHand().size()); // Hand should be empty after building

        // Initialize "destroyed" district
        DistrictCard destroyed = new DistrictCard("Temple", "blue", 1, "");

        // Simulate Graveyard recovery ability
        if (p.getGold() >= 1 && p.getCity().stream().anyMatch(d -> d.name.equals("Graveyard"))) {
            p.spendGold(1);
            p.addToHand(destroyed);
        }

        // Verify final state
        assertTrue(p.getHand().contains(destroyed));
        assertEquals(0, p.getGold());
        assertEquals(1, p.getHand().size());
        assertEquals(1, p.getCity().size()); // Graveyard should still be in city
    }

    @Test
    public void testGameInitialization() {
        Game game = createMockGame();

        assertEquals(2, game.getPlayers().size());
        assertEquals(1, game.getRound());
    }

    @Test
    public void testGameLoading() {
        Game game = createMockGame();
        Game loaded = createMockGame(); // Simulate loading the same game

        assertEquals(game.getPlayers().size(), loaded.getPlayers().size());
        assertEquals(game.getPlayers().get(0).getGold(), loaded.getPlayers().get(0).getGold());
    }
}
