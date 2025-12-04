package slidingpenguins.objects;

/**
 * The base interface for all objects that can be placed on the IcyTerrain.
 * Includes Penguins, Food items, and Hazards.
 */
public interface ITerrainObject {
    
    /**
     * Gets the X coordinate (column) of the object on the grid.
     * @return int coordinate
     */
    int getX();

    /**
     * Sets the X coordinate (column) of the object on the grid.
     * @param x new X coordinate
     */
    void setX(int x);

    /**
     * Gets the Y coordinate (row) of the object on the grid.
     * @return int coordinate
     */
    int getY();

    /**
     * Sets the Y coordinate (row) of the object on the grid.
     * @param y new Y coordinate
     */
    void setY(int y);

    /**
     * Returns the string symbol to be displayed on the grid (e.g., "P1", "HB").
     * Essential for the GridRenderer
     * @return String representation
     */
    String getSymbol();
}
