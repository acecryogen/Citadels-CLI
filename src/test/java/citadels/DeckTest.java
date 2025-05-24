package citadels;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DeckTest {

    @Test
    public void testDeckInitializationAndSize() {
        DistrictCard card1 = new DistrictCard("Manor", "Noble", 3, "");
        DistrictCard card2 = new DistrictCard("Tavern", "Trade", 1, "");
        List<DistrictCard> initialCards = new ArrayList<>(Arrays.asList(card1, card2));
        
        Deck deck = new Deck(initialCards);
        assertEquals(2, deck.size(), "Deck size should match the number of initial cards.");
        assertEquals(2, deck.cardsLeft(), "cardsLeft() should also match the number of initial cards.");
    }

    @Test
    public void testDrawFromDeck() {
        DistrictCard card1 = new DistrictCard("Manor", "Noble", 3, "");
        DistrictCard card2 = new DistrictCard("Tavern", "Trade", 1, "");
        List<DistrictCard> initialCards = new ArrayList<>(Arrays.asList(card1, card2));
        Deck deck = new Deck(initialCards);

        assertEquals(2, deck.size());
        DistrictCard drawnCard = deck.draw();
        assertNotNull(drawnCard, "Drawn card should not be null when deck is not empty.");
        assertEquals(1, deck.size(), "Deck size should decrease by 1 after drawing.");
        assertEquals(1, deck.cardsLeft(), "cardsLeft() should decrease by 1 after drawing.");

        DistrictCard secondDrawnCard = deck.draw();
        assertNotNull(secondDrawnCard, "Second drawn card should not be null.");
        assertEquals(0, deck.size(), "Deck should be empty after drawing all cards.");
        
        DistrictCard thirdDrawnCard = deck.draw();
        assertNull(thirdDrawnCard, "Drawing from an empty deck should return null.");
        assertEquals(0, deck.size(), "Deck size should remain 0 when drawing from empty deck.");
    }
    
    @Test
    public void testShuffle() {
        // It's hard to test randomness perfectly, but we can check if the order changes
        // or if elements are still present.
        DistrictCard card1 = new DistrictCard("A", "color", 1, "");
        DistrictCard card2 = new DistrictCard("B", "color", 1, "");
        DistrictCard card3 = new DistrictCard("C", "color", 1, "");
        DistrictCard card4 = new DistrictCard("D", "color", 1, "");
        DistrictCard card5 = new DistrictCard("E", "color", 1, "");
        List<DistrictCard> initialCards = new ArrayList<>(Arrays.asList(card1, card2, card3, card4, card5));
        Deck deck = new Deck(new ArrayList<>(initialCards)); // Pass a copy for comparison
        
        // Store original order (first few elements)
        List<DistrictCard> originalOrderSample = new ArrayList<>();
        for(int i=0; i<Math.min(3, deck.size()); i++) {
            originalOrderSample.add(deck.getCards().get(i)); // Assuming getCards() returns current internal list
        }

        deck.shuffle();
        
        assertEquals(initialCards.size(), deck.size(), "Shuffling should not change the deck size.");
        for(DistrictCard card : initialCards) {
            assertTrue(deck.getCards().contains(card), "All original cards should still be in the deck after shuffle.");
        }

        // Check if the order of the sample has likely changed (not a guaranteed test for shuffle quality)
        if (initialCards.size() > 1) { // Only meaningful if there's more than one card
            boolean orderChanged = false;
            if (deck.size() >= originalOrderSample.size()) {
                for(int i=0; i<originalOrderSample.size(); i++) {
                    if (!originalOrderSample.get(i).equals(deck.getCards().get(i))) {
                        orderChanged = true;
                        break;
                    }
                }
            } else {
                orderChanged = true; // Size changed unexpectedly, consider order changed
            }
             // This assertion is probabilistic. For a small deck, it might occasionally fail
             // if shuffle results in the same order. For a larger deck, it's more reliable.
             // For a very robust test, one might compare against multiple shuffles or statistical properties.
             // For this context, we'll assume if the list isn't identical, it's shuffled.
            if (initialCards.size() >= 3) { // More likely to see order change with more cards
                 assertTrue(orderChanged, "Deck order should likely change after shuffle (this test is probabilistic). If it fails, re-run; consistent failure might indicate an issue.");
            }
        }
    }

    @Test
    public void testAddCard() {
        Deck deck = new Deck(new ArrayList<>());
        assertEquals(0, deck.size());
        DistrictCard card = new DistrictCard("New Card", "color", 1, "");
        deck.addCard(card);
        assertEquals(1, deck.size());
        assertTrue(deck.getCards().contains(card));
    }
}
