package citadels;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class GameTest {

    private Game game;
    private List<Player> players;
    private Deck districtDeck;
    private List<CharacterCard> characterDeck;

    @BeforeEach
    public void setUp() {
        // Initialize players
        players = new ArrayList<>();
        players.add(new Player(1, true)); // Human
        players.add(new Player(2, false)); // AI

        // Initialize district deck
        List<DistrictCard> districts = new ArrayList<>();
        districts.add(new DistrictCard("Manor", "Noble", 3, ""));
        districts.add(new DistrictCard("Tavern", "Trade", 1, ""));
        districtDeck = new Deck(districts);

        // Initialize character deck
        characterDeck = new ArrayList<>();
        characterDeck.add(new CharacterCard(1, "Assassin", "Kill a character"));
        characterDeck.add(new CharacterCard(4, "King", "Take the crown, gain gold from noble (yellow) districts"));
        characterDeck.add(new CharacterCard(8, "Warlord", "Destroy a district, gain gold from military (red) districts"));
        
        game = new Game(players, districtDeck, characterDeck);
    }

    @Test
    public void testGetCharacterByRank_Exists() {
        CharacterCard king = game.getCharacterByRank(4);
        assertNotNull(king, "Should find the King character by rank 4.");
        assertEquals("King", king.getName(), "The name of the character with rank 4 should be King.");

        CharacterCard assassin = game.getCharacterByRank(1);
        assertNotNull(assassin, "Should find the Assassin character by rank 1.");
        assertEquals("Assassin", assassin.getName(), "The name of the character with rank 1 should be Assassin.");
    }

    @Test
    public void testGetCharacterByRank_NotExists() {
        CharacterCard nonExistent = game.getCharacterByRank(2); // Rank 2 is not in the initial characterDeck
        assertNull(nonExistent, "Should return null for a rank that does not exist in the deck.");
    }
    
    @Test
    public void testGetCharacterByRank_EmptyDeck() {
        Game emptyGame = new Game(new ArrayList<>(), new Deck(new ArrayList<>()), new ArrayList<>());
        CharacterCard cardFromEmpty = emptyGame.getCharacterByRank(1);
        assertNull(cardFromEmpty, "Should return null if character deck is empty.");
    }

    @Test
    public void testSetAndIsOver() {
        assertFalse(game.isOver(), "Game should not be over initially.");
        game.setOver(true);
        assertTrue(game.isOver(), "Game should be over after setOver(true) is called.");
        game.setOver(false);
        assertFalse(game.isOver(), "Game should not be over after setOver(false) is called.");
    }
    
    @Test
    public void testInitialDeal() {
        // Players should start with 4 cards and 2 gold (Player constructor gives 2 gold)
        // The initialDeal in Game gives 4 cards.
        for(Player p : game.getPlayers()) {
            assertEquals(4, p.getHand().size(), "Player " + p.getId() + " should have 4 cards after initial deal.");
            assertEquals(2, p.getGold(), "Player " + p.getId() + " should have 2 gold initially.");
        }
        
        boolean onePlayerHasCrown = false;
        for(Player p : game.getPlayers()) {
            if (p.hasCrown()) {
                if (onePlayerHasCrown) {
                    fail("More than one player has the crown initially.");
                }
                onePlayerHasCrown = true;
            }
        }
        assertTrue(onePlayerHasCrown, "One player should have the crown initially.");
    }

    @Test
    public void testCheckGameOverCondition_NoWinner() {
         // Ensure no player has 7 districts initially
        for(Player p : game.getPlayers()) {
            assertTrue(p.getCity().size() < 7, "Player city size should be less than 7 initially.");
        }
        assertFalse(game.checkGameOverCondition(), "Game over condition should be false when no player has 7 districts.");
    }

    @Test
    public void testCheckGameOverCondition_WinnerExists() {
        Player winner = game.getPlayers().get(0);
        for(int i=0; i<7; i++) {
            // Add unique districts to avoid build issues, costs don't matter for this test of the condition
            winner.getCity().add(new DistrictCard("District " + i, "Color", 1, ""));
        }
        assertEquals(7, winner.getCity().size());
        assertTrue(game.checkGameOverCondition(), "Game over condition should be true when a player has 7 districts.");
    }
}
