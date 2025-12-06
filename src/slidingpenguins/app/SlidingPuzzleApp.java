package slidingpenguins.app;

import slidingpenguins.core.IcyTerrain;

/**
 * Main application class that runs the Sliding Penguins Puzzle Game.
 * According to the assignment, the main method should only initialize
 * an IcyTerrain object and delegate the game/menu logic to it.
 */
public class SlidingPuzzleApp {

    public static void main(String[] args) {
        // Create the terrain (this will generate penguins, hazards and food)
        IcyTerrain terrain = new IcyTerrain(); // You can also test with random seed
        // Delegate the game loop and menu logic to IcyTerrain
        terrain.startGame();
    }
}
