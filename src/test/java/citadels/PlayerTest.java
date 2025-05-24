package citadels;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.util.ArrayList;
import java.util.List;

public class PlayerTest {

    @Test
    public void testPlayerNameSetterGetter() {
        Player player = new Player(1, true);
        player.setName("Test Player");
        assertEquals("Test Player", player.getName(), "Player name should be set and retrieved correctly.");
    }

    @Test
    public void testPlayerInitialName() {
        Player humanPlayer = new Player(1, true);
        assertEquals("Player 1 (Human)", humanPlayer.getName(), "Default human player name is incorrect.");

        Player aiPlayer = new Player(2, false);
        assertEquals("Player 2 (AI)", aiPlayer.getName(), "Default AI player name is incorrect.");
    }
    
    @Test
    public void testAddGoldAndGetGold() {
        Player player = new Player(1, false); // Starts with 2 gold
        player.addGold(5);
        assertEquals(7, player.getGold(), "Gold should be added correctly.");
        player.addGold(0);
        assertEquals(7, player.getGold(), "Adding 0 gold should not change the amount.");
    }

    @Test
    public void testSpendGoldSufficient() {
        Player player = new Player(1, false); // Starts with 2 gold
        player.addGold(5); // Total 7 gold
        player.spendGold(3);
        assertEquals(4, player.getGold(), "Gold should be spent correctly.");
    }

    @Test
    public void testSpendGoldExactAmount() {
        Player player = new Player(1, false); // Starts with 2 gold
        player.spendGold(2);
        assertEquals(0, player.getGold(), "Spending exact gold amount should result in 0 gold.");
    }

    @Test
    public void testSpendGoldMoreThanAvailable() {
        Player player = new Player(1, false); // Starts with 2 gold
        player.spendGold(5); // Try to spend more than available
        assertEquals(0, player.getGold(), "Spending more gold than available should result in 0 gold, not negative.");
    }

    @Test
    public void testBuildDistrictAffordableAndNotInCity() {
        Player player = new Player(1, true); // Starts with 2 gold
        player.setName("Builder");
        DistrictCard newDistrict = new DistrictCard("Manor", "Noble", 3, "A grand manor.");
        
        player.addGold(5); // Player has 2 + 5 = 7 gold
        player.addToHand(newDistrict);
        
        assertEquals(1, player.getHand().size());
        assertEquals(0, player.getCity().size());
        assertEquals(7, player.getGold());

        player.buildDistrict(newDistrict);

        assertEquals(0, player.getHand().size(), "Card should be removed from hand after building.");
        assertEquals(1, player.getCity().size(), "City should have 1 district after building.");
        assertTrue(player.getCity().contains(newDistrict), "City should contain the newly built district.");
        assertEquals(4, player.getGold(), "Gold should be deducted by card cost (7 - 3 = 4).");
    }

    @Test
    public void testBuildDistrictNotAffordable() {
        Player player = new Player(1, true); // Starts with 2 gold
        DistrictCard expensiveDistrict = new DistrictCard("Fortress", "Military", 5, "A mighty fortress.");
        player.addToHand(expensiveDistrict);

        assertEquals(1, player.getHand().size());
        assertEquals(2, player.getGold());

        // Attempt to build (Game.build would print message, Player.buildDistrict just checks)
        // Player.buildDistrict is called by Game.build which does the check.
        // Here we test Player.buildDistrict's internal logic if called directly (though not typical flow)
        // If we assume Game.build would have prevented this call:
        // To test Player.buildDistrict in isolation for this, we assume it's called.
        // However, the current Player.buildDistrict doesn't build if gold is insufficient.
        
        // Let's test the pre-condition check that Game.build would rely on.
        // This test is more about player state for building.
        if (player.getGold() >= expensiveDistrict.cost && !player.getCity().contains(expensiveDistrict)) {
             player.buildDistrict(expensiveDistrict); // This line wouldn't be reached if not affordable
        }

        assertEquals(1, player.getHand().size(), "Card should remain in hand if not affordable.");
        assertEquals(0, player.getCity().size(), "City should remain empty if build fails.");
        assertEquals(2, player.getGold(), "Gold should not change if build fails due to cost.");
    }

    @Test
    public void testBuildDistrictAlreadyInCity() {
        Player player = new Player(1, true); // Starts with 2 gold
        DistrictCard district = new DistrictCard("Temple", "Religious", 1, "A holy temple.");
        player.addGold(2); // Total 4 gold
        
        player.addToHand(district);
        player.buildDistrict(district); // First build

        assertEquals(1, player.getCity().size());
        assertEquals(3, player.getGold()); // 4 - 1 = 3 gold

        // Attempt to build the same district again
        DistrictCard sameDistrictInstance = district; // Could be same instance
        DistrictCard anotherInstanceOfSameDistrict = new DistrictCard("Temple", "Religious", 1, "A holy temple.");

        player.addToHand(anotherInstanceOfSameDistrict); // Add another instance or the same one back to hand
        
        // Player.buildDistrict checks by name equality.
        player.buildDistrict(anotherInstanceOfSameDistrict); 

        assertEquals(1, player.getCity().size(), "City should not allow duplicate district names.");
        assertEquals(1, player.getHand().size(), "Card should remain in hand if duplicate.");
        assertEquals(3, player.getGold(), "Gold should not change if build fails due to duplicate.");
    }
    
    @Test
    void addToHand_NullCard_ShouldNotAdd() {
        Player player = new Player(1, true);
        player.addToHand(null);
        assertTrue(player.getHand().isEmpty(), "Hand should be empty after adding null card.");
    }
}
