package slidingpenguins.objects.penguins;

public class EmperorPenguin extends Penguin {

    public EmperorPenguin(String id) {
        super(id);
    }

    @Override
    public void useSpecialAbility() {
        if (hasUsedAbility()) {
            System.out.println(id + " (Emperor) has already used its special ability.");
            return;
        }
        System.out.println(id + " (Emperor) is prepared to stop at the 3rd square if needed.");
        markAbilityUsed();
    }
}
