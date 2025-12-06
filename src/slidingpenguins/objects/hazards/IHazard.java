package slidingpenguins.objects.hazards;

import slidingpenguins.objects.ITerrainObject;

/**
 * Interface representing a Hazard on the terrain.
 * All hazards must implement collision behavior and inherit
 * from ITerrainObject for position tracking.
 */
public interface IHazard extends ITerrainObject {
    /**
     * All hazards must define a behavior when an object collides with them.
     * @param incomer The object (Penguin or sliding hazard) colliding with this hazard.
     */
    void onCollision(ITerrainObject incomer);
}
