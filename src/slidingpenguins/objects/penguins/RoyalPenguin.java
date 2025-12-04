package slidingpenguins.objects.penguins;

public class RoyalPenguin extends Penguin {

    public RoyalPenguin(String id) {
        super(id);
    }

    @Override
    public void useSpecialAbility() {
        if (hasUsedAbility()) {
            System.out.println(id + " (Royal) has already used its special ability.");
            return;
        }
        System.out.println(id + " (Royal) can move to an adjacent square before sliding.");
        markAbilityUsed();
    }
}
