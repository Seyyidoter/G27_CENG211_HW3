package slidingpenguins.objects.hazards;

import slidingpenguins.objects.ITerrainObject;
import slidingpenguins.objects.ISlidable;
import slidingpenguins.objects.penguins.Penguin;
import slidingpenguins.core.Direction;

/**
 * Light ice block.
 * It can move on ice.
 * It stuns a penguin that hits it.
 */
public class LightIceBlock extends Hazard implements ISlidable {

    private boolean sliding;
    private Direction direction;

    public LightIceBlock() {
        super();
        this.sliding = false;
        this.direction = null;
    }

    @Override
    public String getSymbol() {
        return "LB";
    }

    // --- ISlidable methods ---

    @Override
    public void slide() {
        System.out.println("LightIceBlock is sliding " + (direction != null ? direction : "") + "...");
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

        // Case 1: penguin hits this block
        if (incomer instanceof Penguin) {
            Penguin p = (Penguin) incomer;
            p.stun(); // Penguin class must have stun() method

            // block starts to slide in penguin's direction
            if (p.getDirection() != null) {
                startSliding(p.getDirection());
            }
        }
        // Case 2: another sliding object hits this block
        else if (incomer instanceof ISlidable) {
            ISlidable slider = (ISlidable) incomer;
            if (slider.isMoving()) {
                startSliding(slider.getDirection());
                slider.setMoving(false);
            }
        }
    }

    public void startSliding(Direction dir) {
        if (dir == null) return;
        this.direction = dir;
        this.sliding = true;
        slide(); // We can call slide method as a trigger
    }
}
