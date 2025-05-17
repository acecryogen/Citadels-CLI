package citadels;

public class CharacterCard {
    private final int number; // 1-8 for turn order
    private final String name;
    private final String ability;

    public CharacterCard(int number, String name, String ability) {
        this.number = number;
        this.name = name;
        this.ability = ability;
    }

    public int getNumber() {
        return number;
    }

    public String getName() {
        return name;
    }

    public String getAbility() {
        return ability;
    }

    @Override
    public String toString() {
        return number + ": " + name + " - " + ability;
    }
}
