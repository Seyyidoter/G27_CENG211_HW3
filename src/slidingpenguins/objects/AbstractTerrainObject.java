package slidingpenguins.objects;

/**
 * Base abstract class for all objects on the terrain.
 * Centralizes the coordinate logic (x, y) to prevent code duplication.
 */
public abstract class AbstractTerrainObject implements ITerrainObject {

    protected int x;
    protected int y;

    /**
     * Default Constructor.
     */
    public AbstractTerrainObject() {
    }

    /**
     * Copy Constructor.
     * Used by subclasses to copy coordinates safely.
     * @param other The object to copy coordinates from.
     */
    public AbstractTerrainObject(AbstractTerrainObject other) {
        if (other != null) {
            this.x = other.x;
            this.y = other.y;
        }
    }

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

    // getSymbol() is left for concrete classes or interface default.
}
