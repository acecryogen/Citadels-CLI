package citadels;

public class DistrictCard {
    public final String name;
    public final String color;
    public final int cost;
    public final String text;

    /**
     * 
     * @param name  - name of the district
     * @param color
     * @param cost
     * @param text
     */
    public DistrictCard(String name, String color, int cost, String text) {
        this.name = name;
        this.color = color;
        this.cost = cost;
        this.text = text;
    }

    @Override
    public String toString() {
        return name + " [" + color + cost + "]";
    }
}
