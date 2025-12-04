package slidingpenguins.objects.hazards;

import slidingpenguins.objects.ITerrainObject;
import slidingpenguins.objects.ISlidable;
import slidingpenguins.objects.penguins.Penguin;
import slidingpenguins.core.Directions;

/**
 * Sea lion hazard.
 * It can slide on ice.
 * It has special bounce rules.
 */
public class SeaLion extends Hazard implements ISlidable {

    private boolean sliding;
    private Directions direction;

    /**
     * Sea lion can slide, so we pass true.
     */
    public SeaLion() {
        super(true);
        this.sliding = false;
        this.direction = null;
    }

    @Override
    public String getSymbol() {
        return "SL";
    }

    // --- ISlidable methods (names should match your interface) ---

    public boolean isMoving() {
        return sliding;
    }

    public void setMoving(boolean moving) {
        this.sliding = moving;
        if (!moving) {
            this.direction = null;
        }
    }

    public Directions getDirection() {
        return direction;
    }

    public void setDirection(Directions dir) {
        this.direction = dir;
    }

    // --- Collision logic ---

    /**
     * What happens when something hits this sea lion.
     */
    @Override
    public void onCollision(ITerrainObject incomer) {
        if (incomer == null) {
            return;
        }

        // Case 1: penguin hits the sea lion
        if (incomer instanceof Penguin) {
            Penguin p = (Penguin) incomer;
            Directions penguinDir = p.getDirection();

            if (penguinDir != null) {
                // sea lion slides in penguin's original direction
                this.setDirection(penguinDir);
                this.setMoving(true);

                // penguin bounces to the opposite direction
                Directions bounceDir = getOppositeDirection(penguinDir);
                p.setDirection(bounceDir); // needs setDirection in Penguin
            }
        }
        // Case 2: light ice block hits the sea lion
        else if (incomer instanceof LightIceBlock) {
            LightIceBlock block = (LightIceBlock) incomer;
            Directions blockDir = block.getDirection();

            if (blockDir != null) {
                // take block direction
                this.setDirection(blockDir);
                this.setMoving(true);

                // block stops
                block.setMoving(false);
            }
        }
    }

    /**
     * Get opposite direction for bounce.
     */
    private Directions getOppositeDirection(Directions dir) {
        if (dir == null) {
            return null;
        }
        switch (dir) {
            case UP:    return Directions.DOWN;
            case DOWN:  return Directions.UP;
            case LEFT:  return Directions.RIGHT;
            case RIGHT: return Directions.LEFT;
            default:    return dir;
        }
    }
}
