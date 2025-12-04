package slidingpenguins.core;

/**
 * Contains constant values used throughout the Sliding Penguins Puzzle Game.
 * This ensures easier maintenance and avoidance of "magic numbers".
 */
public class GameConstants {
    
    // Grid dimensions
    public static final int GRID_ROWS = 10;
    public static final int GRID_COLS = 10;
    
    // Game Entity Counts
    public static final int PENGUIN_COUNT = 3;
    public static final int HAZARD_COUNT = 15;
    public static final int FOOD_COUNT = 20;
    
    // Game Rules
    public static final int MAX_TURNS = 4;
    
    // Visualization constants (can be used for formatting)
    public static final String EMPTY_CELL_SYMBOL = "   "; // 3 spaces for alignment
}
