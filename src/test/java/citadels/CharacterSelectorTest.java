package citadels;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CharacterSelectorTest {

    private CharacterSelector characterSelector;
    private List<Player> players;
    private List<CharacterCard> characterDeck;
    private Game mockGame; // Mock Game if CharacterSelector needs it

    @BeforeEach
    public void setUp() {
        players = new ArrayList<>();
        players.add(new Player(1, true)); // P1
        players.add(new Player(2, false)); // P2
        players.add(new Player(3, false)); // P3
        players.add(new Player(4, false)); // P4

        characterDeck = new ArrayList<>(Arrays.asList(
            new CharacterCard(1, "Assassin", ""),
            new CharacterCard(2, "Thief", ""),
            new CharacterCard(3, "Magician", ""),
            new CharacterCard(4, "King", ""),
            new CharacterCard(5, "Bishop", ""),
            new CharacterCard(6, "Merchant", ""),
            new CharacterCard(7, "Architect", ""),
            new CharacterCard(8, "Warlord", "")
        ));
        
        // CharacterSelector might need a Game instance, but we can test some methods without it
        // For determinePickOrder, only players list is needed conceptually
        // If Game is strictly required by constructor, a simplified mock or real Game might be needed.
        // For this test, we'll assume CharacterSelector can be instantiated for specific tests
        // or we pass null if Game methods aren't called by the tested method.
        // The constructor requires a Game object. Let's create a minimal one.
        Deck mockDistrictDeck = new Deck(new ArrayList<>());
        mockGame = new Game(players, mockDistrictDeck, characterDeck); // Game needs all three

        characterSelector = new CharacterSelector(mockGame, players, characterDeck);
    }

    @Test
    public void testDeterminePickOrder_CrownedPlayerFirst() {
        // Crowned player is P1 (index 0)
        List<Player> pickOrder = characterSelector.determinePickOrder(0);
        assertEquals(4, pickOrder.size());
        assertEquals(players.get(0), pickOrder.get(0), "P1 should be first.");
        assertEquals(players.get(1), pickOrder.get(1), "P2 should be second.");
        assertEquals(players.get(2), pickOrder.get(2), "P3 should be third.");
        assertEquals(players.get(3), pickOrder.get(3), "P4 should be fourth.");
    }

    @Test
    public void testDeterminePickOrder_CrownedPlayerInMiddle() {
        // Crowned player is P3 (index 2)
        List<Player> pickOrder = characterSelector.determinePickOrder(2);
        assertEquals(4, pickOrder.size());
        assertEquals(players.get(2), pickOrder.get(0), "P3 should be first.");
        assertEquals(players.get(3), pickOrder.get(1), "P4 should be second.");
        assertEquals(players.get(0), pickOrder.get(2), "P1 should be third.");
        assertEquals(players.get(1), pickOrder.get(3), "P2 should be fourth.");
    }
    
    @Test
    public void testDeterminePickOrder_CrownedPlayerLast() {
        // Crowned player is P4 (index 3)
        List<Player> pickOrder = characterSelector.determinePickOrder(3);
        assertEquals(4, pickOrder.size());
        assertEquals(players.get(3), pickOrder.get(0), "P4 should be first.");
        assertEquals(players.get(0), pickOrder.get(1), "P1 should be second.");
        assertEquals(players.get(1), pickOrder.get(2), "P2 should be third.");
        assertEquals(players.get(2), pickOrder.get(3), "P3 should be fourth.");
    }

    @Test
    public void testGetKilledAndRobbedRankPlaceholders() {
        // These methods in the current implementation are just placeholders
        assertEquals(-1, characterSelector.getKilledCharacterRank(), "Placeholder for killed rank should be -1.");
        assertEquals(-1, characterSelector.getRobbedCharacterRank(), "Placeholder for robbed rank should be -1.");
    }
}
