package slidingpenguins.objects;

import slidingpenguins.core.Direction;

/**
 * Interface for objects that have the ability to slide on the icy terrain.
 * Implemented by Penguins and certain Hazards (LightIceBlock, SeaLion).
 */
public interface ISlidable extends ITerrainObject {

    /**
     * Defines the behavior when the object starts sliding.
     */
    void slide();

    boolean isMoving();

    void setMoving(boolean moving);

    Direction getDirection();

    void setDirection(Direction direction);
}
