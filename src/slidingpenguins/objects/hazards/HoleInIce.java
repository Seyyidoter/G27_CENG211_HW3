package slidingpenguins.objects.hazards;

import slidingpenguins.objects.ITerrainObject;
import slidingpenguins.objects.ISlidable;
import slidingpenguins.objects.penguins.Penguin;

/**
 * Represents a hole in the ice.
 * Penguins and sliding hazards can fall into it.
 * Sliding hazards (LightIceBlock, SeaLion) plug the hole.
 */
public class HoleInIce extends Hazard {

    private boolean plugged;

    public HoleInIce() {
        super();
        this.plugged = false;
    }

    @Override
    public String getSymbol() {
        // Returns "PH" if plugged, "HI" if open
        return plugged ? "PH" : "HI";
    }

    public boolean isPlugged() {
        return plugged;
    }

    /**
     * Plugs the hole when a sliding object (like LightIceBlock or SeaLion) falls into it.
     */
    public void plug() {
        this.plugged = true;
    }

    @Override
    public void onCollision(ITerrainObject incomer) {
        // If the hole is plugged or the incomer is null, it acts like a safe square.
        if (incomer == null || plugged) {
            return;
        }

        // Case 1: A Penguin falls into the hole.
        if (incomer instanceof Penguin) {
            Penguin p = (Penguin) incomer;
            // The penguin falls into water and is removed from the game.
            p.fallIntoWater();
        }
        // Case 2: A sliding hazard (LightIceBlock or SeaLion) falls into the hole.
        else if (incomer instanceof ISlidable) {
            // The sliding object plugs the hole and disappears (Terrain logic will not re-place it).
            this.plug();
        }
    }
}
