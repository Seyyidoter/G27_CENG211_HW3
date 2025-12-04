package slidingpenguins.objects;

/**
 * Interface for objects that have the ability to slide on the icy terrain.
 * Implemented by Penguins and certain Hazards (LightIceBlock, SeaLion).
 */
public interface ISlidable extends ITerrainObject {
    
    /**
     * Defines the behavior when the object starts sliding.
     * Once slidable objects start moving, they keep going.
     */
    void slide(); 
}