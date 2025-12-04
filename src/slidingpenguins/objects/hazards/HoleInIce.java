package slidingpenguins.objects.hazards;

import slidingpenguins.objects.ITerrainObject;
import slidingpenguins.objects.penguins.Penguin;

/**
 * Hole in ice.
 * If it is open, it is dangerous.
 * If it is plugged, it is safe to pass.
 */
public class HoleInIce extends Hazard {

    private boolean plugged;

    /**
     * Hole cannot slide, so we pass false.
     * At start it is open (dangerous).
     */
    public HoleInIce() {
        super(false);
        this.plugged = false;
    }

    /**
     * "HI" for open hole, "PH" for plugged hole.
     */
    @Override
    public String getSymbol() {
        return plugged ? "PH" : "HI";
    }

    public boolean isPlugged() {
        return plugged;
    }

    /**
     * Plug the hole.
     * Used when a sliding hazard falls into it.
     */
    public void plug() {
        this.plugged = true;
    }

    /**
     * What happens when something comes to this cell.
     */
    @Override
    public void onCollision(ITerrainObject incomer) {
        if (incomer == null) {
            return;
        }

        // If plugged, it is just like normal ice.
        if (plugged) {
            return;
        }

        // Case 1: a penguin falls into the hole
        if (incomer instanceof Penguin) {
            Penguin p = (Penguin) incomer;
            // penguin is removed from the game (falls into water)
            p.fallIntoWater(); // TODO: your teammate should add this method
        }
        // Case 2: a sliding hazard (LightIceBlock or SeaLion) falls into it
        else if (incomer instanceof LightIceBlock || incomer instanceof SeaLion) {
            // sliding object plugs the hole
            this.plug();
            // engine should remove the hazard from the grid
            // (for example: IcyTerrain or TerrainGrid will handle it)
        }
    }
}
