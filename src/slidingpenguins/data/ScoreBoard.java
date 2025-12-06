package slidingpenguins.data;

import java.util.Comparator;
import java.util.List;

import slidingpenguins.objects.food.Food;
import slidingpenguins.objects.penguins.Penguin;

/**
 * Utility class for displaying the final game scoreboard.
 * Sorts penguins by total food weight and formats the results.
 */
public class ScoreBoard {

    /**
     * Displays the game over scoreboard to the console in the format specified in the PDF.
     * @param penguins List of all penguins in the game
     * @param myPenguin The penguin controlled by the user (for marking purposes)
     */
    public void displayScoreBoard(List<Penguin> penguins, Penguin myPenguin) {
        System.out.println("\n\n***** GAME OVER *****");
        System.out.println("***** SCOREBOARD FOR THE PENGUINS *****\n");

        // Sort penguins by total food weight (descending order)
        penguins.sort(Comparator.comparingInt(Penguin::getTotalFoodWeight).reversed());

        int rank = 1;
        for (Penguin p : penguins) {
            // Determine rank suffixes (1st, 2nd, 3rd)
            String rankSuffix;
            if (rank == 1) rankSuffix = "st";
            else if (rank == 2) rankSuffix = "nd";
            else if (rank == 3) rankSuffix = "rd";
            else rankSuffix = "th";

            // Mark the user's penguin
            String ownerInfo = (p == myPenguin) ? " (Your Penguin)" : "";

            // Example: * 1st place: P2 (Your Penguin)
            System.out.println("* " + rank + rankSuffix + " place: " + p.getId() + ownerInfo);

            // List collected food items: E.g. -> Food items: Kr (3 units), Sq (2 units)
            System.out.print(" |---> Food items: ");
            List<Food> foods = p.getCollectedFoods();

            if (foods.isEmpty()) {
                System.out.println("None");
            } else {
                for (int i = 0; i < foods.size(); i++) {
                    Food f = foods.get(i);
                    System.out.print(f.getSymbol() + " (" + f.getWeight() + " units)");
                    if (i < foods.size() - 1) {
                        System.out.print(", ");
                    }
                }
                System.out.println(); // End of line
            }

            // Print total weight
            System.out.println(" |---> Total weight: " + p.getTotalFoodWeight() + " units\n");

            rank++;
        }
    }
}
