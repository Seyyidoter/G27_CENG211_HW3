package slidingpenguins.objects.penguins;

public class RockhopperPenguin extends Penguin {

    private boolean jumpPrepared;

    public RockhopperPenguin(String id) {
        super(id);
        this.jumpPrepared = false;
    }

    @Override
    public void useSpecialAbility() {
        if (hasUsedAbility()) {
            System.out.println(id + " (Rockhopper) has already used its special ability.");
            return;
        }
        this.jumpPrepared = true;
        System.out.println(id + " (Rockhopper) prepares to jump over the next hazard!");
        markAbilityUsed();
    }

    /**
     * Rockhopper Penguins are an exception to the 30% chance rule.
     * They automatically use their action the first time they decide
     * to move in the direction of a hazard. After that, the ability
     * is considered used and cannot be triggered again.
     */
    public boolean canAutoUseForHazard() {
        // Auto-use is only allowed if the ability has never been used before
        return !hasUsedAbility();
    }

    public boolean isJumpPrepared() {
        return jumpPrepared;
    }

    public void consumeJump() {
        this.jumpPrepared = false;
    }
}
