package citadels;

import java.util.*;

public class Player {
    private final int id;
    private final boolean isHuman;
    private int gold;
    private List<DistrictCard> hand;
    private List<DistrictCard> city;
    private CharacterCard character;
    private boolean hasCrown;

    public Player(int id, boolean isHuman) {
        this.id = id;
        this.isHuman = isHuman;
        this.gold = 2;
        this.hand = new ArrayList<>();
        this.city = new ArrayList<>();
        this.character = null;
        this.hasCrown = false;
    }

    public int getId() {
        return id;
    }

    public boolean isHuman() {
        return isHuman;
    }

    public int getGold() {
        return gold;
    }

    public void addGold(int amount) {
        gold += amount;
    }

    public void spendGold(int amount) {
        this.gold = Math.max(0, this.gold - amount);
    }

    public List<DistrictCard> getHand() {
        return hand;
    }

    public List<DistrictCard> getCity() {
        return city;
    }

    public CharacterCard getCharacter() {
        return character;
    }

    public void setCharacter(CharacterCard character) {
        this.character = character;
    }

    public boolean hasCrown() {
        return hasCrown;
    }

    public void setCrown(boolean hasCrown) {
        this.hasCrown = hasCrown;
    }

    public void removeFromHand(DistrictCard card) {
        hand.remove(card);
    }

    public void addToHand(DistrictCard card) {
        if (hand == null) {
            hand = new ArrayList<>();
        }
        hand.add(card);
    }

    public void buildDistrict(DistrictCard card) {
        // Check if district already exists in city
        if (city.stream().anyMatch(d -> d.name.equals(card.name))) {
            return;
        }

        // Remove from hand if present
        hand.remove(card);

        // Add to city and spend gold
        city.add(card);
        spendGold(card.cost);
    }

    @Override
    public String toString() {
        return "Player " + id + (isHuman ? " (you)" : "") +
                ": cards=" + hand.size() +
                " gold=" + gold +
                " city=" + city;
    }
}
