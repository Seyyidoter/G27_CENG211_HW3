package slidingpenguins.objects.hazards;

import slidingpenguins.objects.penguins.Penguin;
import slidingpenguins.objects.ITerrainObject;

/**
 * Heavy ice block.
 * It does not move.
 * When a penguin hits it, the penguin loses the lightest food.
 */
public class HeavyIceBlock extends Hazard {

    /**
     * This block cannot slide, so we pass false.
     */
    public HeavyIceBlock() {
        super();
    }

    /**
     * Code used on the grid.
     */
    @Override
    public String getSymbol() {
        return "HB";
    }

    /**
     * What happens when something hits this block.
     */
    @Override
    public void onCollision(ITerrainObject incomer) {
        if (incomer == null) {
            return;
        }

        // only penguin has a special rule here
        if (incomer instanceof Penguin) {
            Penguin p = (Penguin) incomer;
            p.dropLightestFood();
        }
        // other objects just stop; engine handles that part
    }
}
