package slidingpenguins.objects.penguins;

public class KingPenguin extends Penguin {

    public KingPenguin(String id) {
        super(id);
    }

    @Override
    public void useSpecialAbility() {
        if (hasUsedAbility()) {
            System.out.println(id + " (King) has already used its special ability.");
            return;
        }
        System.out.println(id + " (King) is prepared to stop at the 5th square if needed.");
        markAbilityUsed();
    }
}
