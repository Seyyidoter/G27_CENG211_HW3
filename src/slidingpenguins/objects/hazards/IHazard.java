package slidingpenguins.objects.hazards;

import slidingpenguins.objects.ITerrainObject;

/**
 * Interface representing a Hazard on the terrain.
 * Used to categorize objects like IceBlocks, SeaLions, and Holes.
 */
public interface IHazard extends ITerrainObject {
    // This interface primarily serves as a type marker to distinguish 
    // Hazards from Food and Penguins during collision checks.
}
