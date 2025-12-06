package slidingpenguins.util;

import java.util.List;
import slidingpenguins.core.GameConstants;
import slidingpenguins.core.IcyTerrain;
import slidingpenguins.objects.ITerrainObject;

/**
 * Utility class responsible for visualizing the game grid in the console.
 * It reads the state of the IcyTerrain and prints it in a formatted table structure
 * matching the assignment's example output.
 */
public class GridRenderer {

    /**
     * Renders the current state of the IcyTerrain grid to the console.
     * @param terrain The game terrain containing the grid data and objects.
     */
    public static void render(IcyTerrain terrain) {
        if (terrain == null) {
            System.out.println("Terrain is not initialized!");
            return;
        }

        List<List<ITerrainObject>> grid = terrain.getGrid();

        // Important: "The initial icy terrain grid:" and
        // "New state of the grid:" are printed from IcyTerrain.startGame().
        // So we do not print an extra title here.
        // Just print the grid itself like in the PDF example.

        // Print the top border of the grid
        printHorizontalBorder();

        // Loop through each row
        for (int y = 0; y < GameConstants.GRID_ROWS; y++) {
            System.out.print("|"); // Leftmost border for the row

            // Loop through each column in the row
            for (int x = 0; x < GameConstants.GRID_COLS; x++) {
                ITerrainObject obj = grid.get(y).get(x);

                String cellContent;
                if (obj == null) {
                    cellContent = GameConstants.EMPTY_CELL_SYMBOL;
                } else {
                    cellContent = String.format(" %-3s", obj.getSymbol());
                }

                System.out.print(cellContent + "|"); // Content + Vertical separator
            }
            System.out.println(); // Move to the next line

            // Print the horizontal divider after each row to create a grid effect
            printHorizontalBorder();
        }
    }

    /**
     * Helper method to print the horizontal dashed lines between rows.
     * Creates the grid border effect matching the PDF example output.
     * Dynamic length based on column count (5 dashes per column + 1).
     */
    private static void printHorizontalBorder() {
        // Note: No leading space, to match the PDF's
        // "-------------------------------------------------------------"
        for (int i = 0; i < GameConstants.GRID_COLS; i++) {
            System.out.print("-----");
        }
        System.out.println("-");
    }
}
