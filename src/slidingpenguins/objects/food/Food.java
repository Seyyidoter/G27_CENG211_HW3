package slidingpenguins.objects.food;

import slidingpenguins.objects.AbstractTerrainObject;

/**
 * Represents a food item on the icy terrain.
 * Each food has a specific type and a weight.
 */
public class Food extends AbstractTerrainObject {

    private final int weight;
    private final FoodType type;

    /**
     * Constructor for Food.
     * Creates a food item with the specified type and weight.
     * @param type The type of the food (e.g., KRILL, SQUID)
     * @param weight The weight of the food (typically 1-5 units)
     */
    public Food(FoodType type, int weight) {
        super();
        this.type = type;
        this.weight = weight;
    }

    /**
     * Copy Constructor: Creates a new Food object as a deep copy of another.
     * This is essential for preventing privacy leaks when returning lists.
     * @param other The food object to copy.
     */
    public Food(Food other) {
        super(other); // Copies x and y from the abstract parent
        this.type = other.getType();
        this.weight = other.getWeight();
    }

    public int getWeight() {
        return weight;
    }

    public FoodType getType() {
        return type;
    }

    @Override
    public String getSymbol() {
        return type.getShortName();
    }
}
