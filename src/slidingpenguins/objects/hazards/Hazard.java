package slidingpenguins.objects.hazards;

import slidingpenguins.objects.ITerrainObject;

/**
 * Abstract base class for all hazards on the terrain.
 * Implements IHazard marker interface to distinguish from other objects.
 */
public abstract class Hazard implements IHazard {
    
    protected int x;
    protected int y;

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
     * Each specific hazard (HeavyIceBlock, SeaLion, etc.) must define its own symbol.
     */
    @Override
    public abstract String getSymbol();
}
