package slidingpenguins.objects.penguins;

import java.util.ArrayList;
import java.util.List;
import slidingpenguins.objects.AbstractTerrainObject;
import slidingpenguins.objects.ISlidable;
import slidingpenguins.objects.food.Food;
import slidingpenguins.core.Direction;

public abstract class Penguin extends AbstractTerrainObject implements ISlidable {

    protected String id;
    protected List<Food> collectedFoods;

    // Status flags
    protected boolean isEliminated;
    protected boolean isStunned;
    protected Direction currentDirection;
    protected boolean moving;
    protected boolean abilityUsed;

    public Penguin(String id) {
        super();
        this.id = id;
        this.collectedFoods = new ArrayList<>();
        this.isEliminated = false;
        this.isStunned = false;
        this.moving = false;
        this.abilityUsed = false;
    }

    /**
     * Copy Constructor for Penguin.
     * Useful if we need to clone a penguin state.
     */
    public Penguin(Penguin other) {
        super(other); // Copies x and y
        this.id = other.id;
        this.isEliminated = other.isEliminated;
        this.isStunned = other.isStunned;
        this.abilityUsed = other.abilityUsed;

        // Deep copy of the food list
        this.collectedFoods = new ArrayList<>();
        for (Food f : other.collectedFoods) {
            this.collectedFoods.add(new Food(f));
        }
    }

    /**
     * Adds a food item to the penguin's collection.
     * @param food The food item to add
     */
    public void addFood(Food food) {
        collectedFoods.add(food);
    }

    /**
     * Removes the lightest food item from the penguin's collection.
     * Called when hitting a HeavyIceBlock. Does nothing if no food is carried.
     */
    public void dropLightestFood() {
        if (collectedFoods.isEmpty()) return;

        Food lightest = collectedFoods.get(0);
        for (Food f : collectedFoods) {
            if (f.getWeight() < lightest.getWeight()) {
                lightest = f;
            }
        }
        collectedFoods.remove(lightest);
        System.out.println(id + " lost the lightest food item: " + lightest.getType());
    }

    /**
     * Calculates the total weight of all collected food.
     * @return The sum of all food weights
     */
    public int getTotalFoodWeight() {
        int total = 0;
        for (Food f : collectedFoods) {
            total += f.getWeight();
        }
        return total;
    }

    /**
     * Returns a DEEP COPY of the collected foods list.
     * This prevents privacy leaks; external classes cannot modify the penguin's actual stomach.
     */
    public List<Food> getCollectedFoods() {
        List<Food> copyList = new ArrayList<>();
        for (Food f : this.collectedFoods) {
            // Use the copy constructor of Food
            copyList.add(new Food(f));
        }
        return copyList;
    }

    /**
     * Stuns the penguin, causing them to skip their next turn.
     * Called when colliding with a LightIceBlock.
     */
    public void stun() {
        this.isStunned = true;
        System.out.println(id + " is stunned via LightIceBlock!");
    }

    /**
     * Eliminates the penguin from active play.
     * Called when falling into water or a HoleInIce.
     */
    public void fallIntoWater() {
        this.isEliminated = true;
        System.out.println(id + " fell into the water!");
    }

    /**
     * Checks if the penguin has used their special ability.
     * @return true if ability has been used, false otherwise
     */
    public boolean hasUsedAbility() {
        return abilityUsed;
    }

    /**
     * Marks the penguin's special ability as used.
     * Called by subclasses when activating their ability.
     */
    protected void markAbilityUsed() {
        this.abilityUsed = true;
    }

    // Getters and Setters
    public String getId() { return id; }
    public boolean isEliminated() { return isEliminated; }
    public boolean isStunned() { return isStunned; }
    public void setStunned(boolean stunned) { this.isStunned = stunned; }

    @Override
    public void setDirection(Direction direction) { this.currentDirection = direction; }
    @Override
    public Direction getDirection() { return currentDirection; }
    @Override
    public boolean isMoving() { return moving; }
    @Override
    public void setMoving(boolean moving) { this.moving = moving; }

    @Override
    public void slide() {
        System.out.println(id + " is sliding...");
    }

    @Override
    public String getSymbol() { return id; }

    public abstract void useSpecialAbility();
}
