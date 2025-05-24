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
    private String name; // Added name field

    public Player(int id, boolean isHuman) {
        this.id = id;
        this.isHuman = isHuman;
        this.gold = 2;
        this.hand = new ArrayList<>();
        this.city = new ArrayList<>();
        this.character = null;
        this.hasCrown = false;
        this.name = isHuman ? "Player " + id + " (Human)" : "Player " + id + " (AI)"; // Default name
    }

    // Added getName method
    public String getName() {
        return name;
    }

    // Added setName method
    public void setName(String name) {
        this.name = name;
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
        if (card != null) { // Prevent adding null cards
           hand.add(card);
        }
    }

    public void buildDistrict(DistrictCard card) {
        // Check if district already exists in city
        if (city.stream().anyMatch(d -> d.name.equals(card.name))) {
            // System.out.println("Cannot build " + card.name + ": already in city."); // Message handled by Game.build
            return;
        }
        if (gold < card.cost) {
            // System.out.println("Cannot build " + card.name + ": not enough gold."); // Message handled by Game.build
            return;
        }

        // Remove from hand if present
        boolean removed = hand.remove(card); // Ensure it was actually in hand
        if (!removed) {
            // This case should ideally not happen if card validation is done before calling.
            // System.out.println("Cannot build " + card.name + ": not in hand (or different instance).");
            // For now, if it's not in hand, don't build. Game.build() checks hand by index.
            // This direct buildDistrict method is mostly for internal setup or AI that has direct card reference.
        }


        // Add to city and spend gold
        city.add(card);
        spendGold(card.cost);
    }

    @Override
    public String toString() {
        return name + ": cards=" + hand.size() +
                " gold=" + gold +
                " city=" + city.size() + " districts.";
    }
}
