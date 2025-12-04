package slidingpenguins.objects.hazards;

import slidingpenguins.objects.ITerrainObject;
import slidingpenguins.objects.ISlidable;
import slidingpenguins.objects.penguins.Penguin;
import slidingpenguins.core.Direction;

/**
 * Sea lion hazard.
 * It can slide on ice and has special bounce rules.
 */
public class SeaLion extends Hazard implements ISlidable {

    private boolean sliding;
    private Direction direction;

    /**
     * Sea lion can slide, so we pass true.
     */
    public SeaLion() {
        super();
        this.sliding = false;
        this.direction = null;
    }

    @Override
    public String getSymbol() {
        return "SL";
    }

    // --- ISlidable methods ---

    @Override
    public void slide() {
        System.out.println("SeaLion is sliding " + (direction != null ? direction : "") + "...");
    }

    @Override
    public boolean isMoving() {
        return sliding;
    }

    @Override
    public void setMoving(boolean moving) {
        this.sliding = moving;
        if (!moving) {
            this.direction = null;
        }
    }

    @Override
    public Direction getDirection() {
        return direction;
    }

    @Override
    public void setDirection(Direction dir) {
        this.direction = dir;
    }

    // --- Collision logic ---

    @Override
    public void onCollision(ITerrainObject incomer) {
        if (incomer == null) return;

        // Case 1: penguin hits the sea lion
        if (incomer instanceof Penguin) {
            Penguin p = (Penguin) incomer;
            Direction penguinDir = p.getDirection();

            if (penguinDir != null) {
                // sea lion slides in penguin's original direction
                this.setDirection(penguinDir);
                this.setMoving(true);

                // penguin bounces to the opposite direction
                Direction bounceDir = getOppositeDirection(penguinDir);
                p.setDirection(bounceDir);
            }
        }
        // Case 2: light ice block hits the sea lion
        else if (incomer instanceof LightIceBlock) {
            LightIceBlock block = (LightIceBlock) incomer;
            Direction blockDir = block.getDirection();

            if (blockDir != null) {
                // sea lion starts moving in block's direction
                this.setDirection(blockDir);
                this.setMoving(true);
                // block stops
                block.setMoving(false);
            }
        }
    }

    private Direction getOppositeDirection(Direction dir) {
        if (dir == null) return null;
        switch (dir) {
            case UP:    return Direction.DOWN;
            case DOWN:  return Direction.UP;
            case LEFT:  return Direction.RIGHT;
            case RIGHT: return Direction.LEFT;
            default:    return dir;
        }
    }
}
