package slidingpenguins.objects.food;

import slidingpenguins.objects.ITerrainObject;

/**
 * Represents a food item on the icy terrain.
 * Each food has a specific type and a weight.
 */
public class Food implements ITerrainObject {
    
    private int x;
    private int y;
    private final int weight; // Weight is determined at creation 
    private final FoodType type;

    /**
     * Constructor for Food.
     * @param type The type of the food (e.g., Krill, Squid).
     * @param weight The weight of the food (1-5 units).
     */
    public Food(FoodType type, int weight) {
        this.type = type;
        this.weight = weight;
    }

    public Food(Food other) {
        this(other.getType(), other.getWeight());
        this.setX(other.getX());
        this.setY(other.getY());
    }

    public int getWeight() {
        return weight;
    }

    public FoodType getType() {
        return type;
    }

    // --- ITerrainObject Implementation ---

    @Override
    public int getX() {
        return x;
    }

    @Override
    public void setX(int x) {
        this.x = x;
    }

    @Override
    public int getY() {
        return y;
    }

    @Override
    public void setY(int y) {
        this.y = y;
    }

    /**
     * Returns the short symbol of the food type (e.g., "Kr", "Ma").
     */
    @Override
    public String getSymbol() {
        return type.getShortName();
    }
}
