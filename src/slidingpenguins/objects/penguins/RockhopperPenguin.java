package slidingpenguins.objects.penguins;

public class RockhopperPenguin extends Penguin {

    private boolean jumpPrepared;
    private boolean firstHazardAutoUsed = false; // NEW: Flag to track "first time" auto-use

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
        this.firstHazardAutoUsed = true; // NEW: Auto-use has been consumed
        System.out.println(id + " (Rockhopper) prepares to jump over the next hazard!");
        markAbilityUsed();
    }

    /**
     * NEW: Check if this Rockhopper can auto-use ability when seeing a hazard.
     * Returns true only if ability has never been used AND this is the first time seeing a hazard.
     * @return true if auto-use is allowed, false otherwise
     */
    public boolean canAutoUseForHazard() {
        return !hasUsedAbility() && !firstHazardAutoUsed;
    }

    public boolean isJumpPrepared() {
        return jumpPrepared;
    }

    public void consumeJump() {
        this.jumpPrepared = false;
    }
}
