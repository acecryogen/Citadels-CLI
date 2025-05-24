package citadels;

import java.util.*;

public class Deck {
    private List<DistrictCard> cards;

    public Deck(List<DistrictCard> cards) {
        this.cards = new ArrayList<>(cards);
        shuffle();
    }

    public void shuffle() {
        Collections.shuffle(cards);
    }

    public DistrictCard draw() {
        if (cards.isEmpty())
            return null;
        return cards.remove(0);
    }

    public int size() {
        return cards.size();
    }

    public List<DistrictCard> getCards() {
        return cards;
    }

    public void addCard(DistrictCard card) {
        cards.add(card);
    }
}
