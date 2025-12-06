package slidingpenguins.objects.hazards;

import slidingpenguins.objects.AbstractTerrainObject;
import slidingpenguins.objects.ITerrainObject;

/**
 * Abstract base class for all hazards on the terrain.
 * Extends AbstractTerrainObject to inherit coordinates.
 */
public abstract class Hazard extends AbstractTerrainObject implements IHazard {

    public Hazard() {
        super();
    }

    // x and y are handled by AbstractTerrainObject.

    @Override
    public abstract String getSymbol();

    @Override
    public abstract void onCollision(ITerrainObject incomer);
}
