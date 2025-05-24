package citadels;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class CharacterCardTest {

    @Test
    public void testGetRank() {
        CharacterCard card = new CharacterCard(5, "Bishop", "Protected from Warlord.");
        assertEquals(5, card.getNumber(), "getNumber() should return the number passed in constructor.");
        assertEquals(5, card.getRank(), "getRank() should return the same value as getNumber().");
        assertEquals(card.getNumber(), card.getRank(), "getRank() should be an alias for getNumber().");
    }

    @Test
    public void testCharacterCardProperties() {
        CharacterCard card = new CharacterCard(1, "Assassin", "Kill another character.");
        assertEquals(1, card.getNumber());
        assertEquals("Assassin", card.getName());
        assertEquals("Kill another character.", card.getAbility());
    }

    @Test
    public void testToString() {
        CharacterCard card = new CharacterCard(4, "King", "Take the crown.");
        assertEquals("4: King - Take the crown.", card.toString());
    }

    @Test
    public void testEqualsAndHashCode() {
        CharacterCard card1 = new CharacterCard(3, "Magician", "Swap hand or cards.");
        CharacterCard card2 = new CharacterCard(3, "Magician", "Swap hand or cards.");
        CharacterCard card3 = new CharacterCard(8, "Warlord", "Destroy a district.");

        assertTrue(card1.equals(card2), "Two character cards with the same properties should be equal.");
        assertEquals(card1.hashCode(), card2.hashCode(), "Hashcodes should be equal for equal objects.");

        assertFalse(card1.equals(card3), "Character cards with different properties should not be equal.");
        assertNotEquals(card1.hashCode(), card3.hashCode(), "Hashcodes should generally differ for non-equal objects (though collisions are possible, less likely here).");
        
        assertFalse(card1.equals(null), "Character card should not be equal to null.");
        assertFalse(card1.equals(new Object()), "Character card should not be equal to an object of a different type.");
    }
}
