package slidingpenguins.objects.food;

/**
 * Enumeration for the different types of food items found on the terrain.
 * Each type has a short notation used for display on the grid.
 */
public enum FoodType {
    KRILL("Kr"),        // Krill
    CRUSTACEAN("Cr"),   // Crustacean
    ANCHOVY("An"),      // Anchovy
    SQUID("Sq"),        // Squid
    MACKEREL("Ma");     // Mackerel

    private final String shortName;

    FoodType(String shortName) {
        this.shortName = shortName;
    }

    /**
     * Returns the short notation for the menu legend.
     * @return Short string representation (e.g., "Kr")
     */
    public String getShortName() {
        return shortName;
    }
}
