package slidingpenguins.objects.penguins;

import java.util.ArrayList;
import java.util.List;
import slidingpenguins.objects.ISlidable;
import slidingpenguins.objects.food.Food;
import slidingpenguins.core.Direction;

public abstract class Penguin implements ISlidable {

    protected int x;
    protected int y;
    protected String id;
    protected List<Food> collectedFoods;

    // Status flags
    protected boolean isEliminated;
    protected boolean isStunned;
    protected Direction currentDirection;
    protected boolean moving;

    // Special ability usage flag (each penguin can use it at most once)
    protected boolean abilityUsed;

    public Penguin(String id) {
        this.id = id;
        this.collectedFoods = new ArrayList<>();
        this.isEliminated = false;
        this.isStunned = false;
        this.moving = false;
        this.abilityUsed = false;
    }

    public void addFood(Food food) {
        collectedFoods.add(food);
    }

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

    public int getTotalFoodWeight() {
        int total = 0;
        for (Food f : collectedFoods) {
            total += f.getWeight();
        }
        return total;
    }

    /**
     * Returns the list of collected food items (for scoreboard).
     */
    public List<Food> getCollectedFoods() {
        List<Food> copyList = new ArrayList<>();
        for (Food f : this.collectedFoods) {
            copyList.add(new Food(f));
        }
        return copyList;
    }

    public void stun() {
        this.isStunned = true;
        System.out.println(id + " is stunned via LightIceBlock!");
    }

    public void fallIntoWater() {
        this.isEliminated = true;
        System.out.println(id + " fell into the water!");
    }

    // --- Ability usage helpers ---

    /**
     * Returns true if this penguin has already used its special ability.
     */
    public boolean hasUsedAbility() {
        return abilityUsed;
    }

    /**
     * Marks that this penguin has used its special ability.
     * Subclasses should call this once inside their useSpecialAbility() implementation.
     */
    protected void markAbilityUsed() {
        this.abilityUsed = true;
    }

    // --- Getters and Setters ---

    public String getId() {
        return id;
    }

    public boolean isEliminated() {
        return isEliminated;
    }

    public boolean isStunned() {
        return isStunned;
    }

    public void setStunned(boolean stunned) {
        this.isStunned = stunned;
    }

    @Override
    public void setDirection(Direction direction) {
        this.currentDirection = direction;
    }

    @Override
    public Direction getDirection() {
        return currentDirection;
    }

    @Override
    public boolean isMoving() {
        return moving;
    }

    @Override
    public void setMoving(boolean moving) {
        this.moving = moving;
    }

    // --- ISlidable and ITerrainObject Implementation ---

    @Override
    public void slide() {
        System.out.println(id + " is sliding...");
    }

    @Override
    public int getX() { return x; }

    @Override
    public void setX(int x) { this.x = x; }

    @Override
    public int getY() { return y; }

    @Override
    public void setY(int y) { this.y = y; }

    @Override
    public String getSymbol() { return id; }

    public abstract void useSpecialAbility();
}
