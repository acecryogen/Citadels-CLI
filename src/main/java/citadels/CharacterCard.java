package citadels;

import java.util.Objects;

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

    // Added getRank() as an alias for getNumber() for compatibility
    public int getRank() {
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

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        CharacterCard that = (CharacterCard) o;
        return number == that.number &&
                Objects.equals(name, that.name) &&
                Objects.equals(ability, that.ability);
    }

    @Override
    public int hashCode() {
        return Objects.hash(number, name, ability);
    }
}
