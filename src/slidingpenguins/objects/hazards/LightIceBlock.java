package slidingpenguins.objects.hazards;

import slidingpenguins.objects.ITerrainObject;
import slidingpenguins.objects.ISlidable;
import slidingpenguins.objects.penguins.Penguin;
import slidingpenguins.core.Directions;

/**
 * Light ice block.
 * It can move on ice.
 * It stuns a penguin that hits it.
 */
public class LightIceBlock extends Hazard implements ISlidable {

    private boolean sliding;
    private Directions direction;

    /**
     * This block can slide, so we pass true.
     */
    public LightIceBlock() {
        super(true);
        this.sliding = false;
        this.direction = null;
    }

    @Override
    public String getSymbol() {
        return "LB";
    }

    // --- ISlidable methods (adapt names to your interface if needed) ---

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
     * What happens when something hits this block.
     */
    @Override
    public void onCollision(ITerrainObject incomer) {
        if (incomer == null) {
            return;
        }

        // Case 1: penguin hits this block
        if (incomer instanceof Penguin) {
            Penguin p = (Penguin) incomer;

            // penguin is stunned for one turn
            p.stun(); // this method should exist in Penguin

            // block starts to slide in penguin's direction
            if (p.getDirection() != null) {
                startSliding(p.getDirection());
            }
        }
        // Case 2: another sliding object hits this block (for example SeaLion)
        else if (incomer instanceof ISlidable) {
            ISlidable slider = (ISlidable) incomer;

            if (slider.isMoving()) {
                // take its direction
                startSliding(slider.getDirection());
                // the other object can stop now
                slider.setMoving(false);
            }
        }
    }

    /**
     * Start sliding in a direction.
     */
    public void startSliding(Directions dir) {
        if (dir == null) {
            return;
        }
        this.direction = dir;
        this.sliding = true;
    }

    /**
     * Stop sliding (for example: fell off or plugged a hole).
     */
    public void stopSliding() {
        this.sliding = false;
        this.direction = null;
    }
}
