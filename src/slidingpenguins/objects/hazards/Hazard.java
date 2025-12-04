package slidingpenguins.objects.hazards;

import slidingpenguins.objects.ITerrainObject;

/**
 * Abstract base class for all hazards on the terrain.
 * Implements IHazard marker interface to distinguish from other objects.
 */
public abstract class Hazard implements IHazard {

    protected int x;
    protected int y;

    // Constructor: Used by subclasses to call super(true/false)
    public Hazard() {
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
     * The symbol of the hazard on the grid (HB, SL, etc.)
     */
    @Override
    public abstract String getSymbol();

    // --- FIX: Method added ---
    /**
     * Abstract method determining what happens when an object collides with this hazard.
     * All subclasses (HeavyIceBlock, SeaLion, HoleInIce, LightIceBlock) must implement this.
     * @param incomer The colliding object (usually a Penguin or another sliding object)
     */
    public abstract void onCollision(ITerrainObject incomer);
}
