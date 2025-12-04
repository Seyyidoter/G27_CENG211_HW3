package slidingpenguins.objects.penguins;

import java.util.ArrayList;
import java.util.List;
import slidingpenguins.objects.ISlidable;
import slidingpenguins.objects.food.Food;

/**
 * Abstract base class for all penguin types.
 * Manages position, collected food, elimination status, and stun status.
 */
public abstract class Penguin implements ISlidable {
    
    protected int x;
    protected int y;
    protected String id; // e.g., P1, P2, P3
    protected List<Food> collectedFoods;
    
    // Status flags
    protected boolean isEliminated; // True if falls into water or hole
    protected boolean isStunned;    // True if hit by LightIceBlock, skips next turn 

    /**
     * Constructor for Penguin.
     * @param id The unique identifier of the penguin (P1, P2, P3).
     */
    public Penguin(String id) {
        this.id = id;
        this.collectedFoods = new ArrayList<>();
        this.isEliminated = false;
        this.isStunned = false;
    }

    /**
     * Adds a food item to the penguin's collection.
     * @param food The food item collected.
     */
    public void addFood(Food food) {
        collectedFoods.add(food);
    }

    /**
     * Removes the food item with the lowest weight.
     * Used when colliding with a HeavyIceBlock as a penalty.
     */
    public void removeLightestFood() {
        if (collectedFoods.isEmpty()) {
            return; // Nothing to remove
        }

        Food lightest = collectedFoods.get(0);
        for (Food f : collectedFoods) {
            if (f.getWeight() < lightest.getWeight()) {
                lightest = f;
            }
        }
        
        collectedFoods.remove(lightest);
        // Logging for clarity
        System.out.println(id + " lost the lightest food item: " + lightest.getType() + " (" + lightest.getWeight() + " units)");
    }

    /**
     * Calculates total weight of collected food for the scoreboard.
     * @return Total weight units.
     */
    public int getTotalFoodWeight() {
        int total = 0;
        for (Food f : collectedFoods) {
            total += f.getWeight();
        }
        return total;
    }

    // --- Getters and Setters ---

    public String getId() {
        return id;
    }

    public boolean isEliminated() {
        return isEliminated;
    }

    public void setEliminated(boolean eliminated) {
        isEliminated = eliminated;
    }

    public boolean isStunned() {
        return isStunned;
    }

    public void setStunned(boolean stunned) {
        isStunned = stunned;
    }
    
    public List<Food> getCollectedFoods() {
        return collectedFoods;
    }

    /**
     * Abstract method for the unique action/ability of the penguin type.
     * Each subclass must implement this.
     */
    public abstract void useSpecialAbility();

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

    @Override
    public String getSymbol() {
        return id; // Display P1, P2, P3 on grid.
    }
}
