package slidingpenguins.objects.hazards;

import slidingpenguins.objects.ITerrainObject;

/**
 * Interface representing a Hazard on the terrain.
 * Includes a contract for collision handling.
 */
public interface IHazard extends ITerrainObject {
    /**
     * All hazards must define a behavior when an object collides with them.
     * @param incomer The object (Penguin or sliding hazard) colliding with this hazard.
     */
    void onCollision(ITerrainObject incomer);
}
