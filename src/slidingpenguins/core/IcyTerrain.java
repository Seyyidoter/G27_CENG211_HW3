package slidingpenguins.core;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Comparator;

import slidingpenguins.objects.*;
import slidingpenguins.objects.food.*;
import slidingpenguins.objects.hazards.*;
import slidingpenguins.objects.penguins.*;
import slidingpenguins.util.GridRenderer;
import slidingpenguins.util.InputHelper;
import slidingpenguins.data.ScoreBoard;

/**
 * Represents the 2D icy terrain where all objects (penguins, hazards, food) live.
 * Also contains movement and collision logic for sliding objects and the main
 * game loop / menu-related operations.
 */
public class IcyTerrain {

    private List<List<ITerrainObject>> grid;
    private List<Penguin> penguins;
    private Penguin myPenguin;
    private Random random;

    public IcyTerrain() {
        this.random = new Random();
        this.penguins = new ArrayList<>();
        initializeGrid();
        generateContent();
    }

    /**
     * Starts the game loop and handles turns, inputs and rendering.
     * This method is called from the main application class.
     */
    public void startGame() {
        System.out.println("Welcome to Sliding Penguins Puzzle Game App.");
        System.out.println("An " + GameConstants.GRID_ROWS + "x" + GameConstants.GRID_COLS
                + " icy terrain grid is being generated.");
        System.out.println("Penguins, Hazards, and Food items are also being generated.");

        // Ensure strictly P1, P2, P3 turn order
        penguins.sort(Comparator.comparing(Penguin::getId));

        // Print legend once at the beginning (similar to the PDF example)
        printLegend();

        // Initial Grid Render
        System.out.println("\nThe initial icy terrain grid:");
        GridRenderer.render(this);
        printPenguinInfo(penguins, myPenguin);

        for (int turn = 1; turn <= GameConstants.MAX_TURNS; turn++) {
            System.out.println("\n*** Turn " + turn + " ***");

            for (Penguin p : penguins) {
                // Eliminated penguins skip their turn
                if (p.isEliminated()) {
                    System.out.println(p.getId() + " is eliminated and skips turn.");
                    continue;
                }

                // Stunned penguins skip the current turn
                if (p.isStunned()) {
                    System.out.println(p.getId() + " is stunned and skips this turn!");
                    p.setStunned(false); // Remove stun for the next turn
                    continue;
                }

                System.out.println("\n--- " + p.getId() + "'s Turn ---");

                Direction chosenDir;
                boolean useAbility = false;

                // --- DECISION PHASE ---
                if (p == myPenguin) {
                    // PLAYER LOGIC
                    if (!p.hasUsedAbility()) {
                        useAbility = InputHelper.getYesNo(
                                "Will " + p.getId() + " use its special action? (Y/N): "
                        );
                        if (useAbility) {
                            handleSpecialActionPreparation(p, true);
                        }
                    } else {
                        System.out.println(p.getId() + " has already used its special action.");
                    }

                    chosenDir = InputHelper.getDirection(
                            "Which direction will " + p.getId() + " move? (U/D/L/R): "
                    );

                } else {
                    // AI LOGIC
                    chosenDir = decideAIDirection(p);

                    if (!p.hasUsedAbility()) {
                        // Rockhopper Special Rule:
                        // If moving towards a Hazard, it MUST use its ability automatically (first time).
                        if (p instanceof RockhopperPenguin) {
                            RockhopperPenguin rh = (RockhopperPenguin) p; // Cast to access Rockhopper-specific methods
                            ITerrainObject target = peekObject(p.getX(), p.getY(), chosenDir);

                            // First time seeing a hazard → auto-use
                            if (target instanceof Hazard && !(target instanceof HoleInIce) && rh.canAutoUseForHazard()) {
                                useAbility = true;
                                System.out.println(p.getId()
                                        + " (AI) sees a hazard and activates ability automatically!");
                            }
                            // Not first time or no hazard → 30% chance
                            else {
                                useAbility = random.nextInt(100) < 30;
                            }
                        } else {
                            // Other AI penguins have a flat 30% chance
                            useAbility = random.nextInt(100) < 30;
                        }
                    } else {
                        useAbility = false;
                        System.out.println(p.getId() + " has already used its special action (AI).");
                    }

                    if (useAbility) {
                        System.out.println(p.getId() + " chooses to USE its special action.");
                        handleSpecialActionPreparation(p, false);
                    } else {
                        System.out.println(p.getId() + " does NOT use its special action.");
                    }
                }

                System.out.println(p.getId() + " chooses to move " + chosenDir);

                // --- EXECUTION PHASE ---
                // Determine movement limit based on ability usage (King/Emperor)
                int limit = -1;
                if (useAbility) {
                    if (p instanceof KingPenguin) limit = 5;
                    if (p instanceof EmperorPenguin) limit = 3;
                }

                moveObject(p, chosenDir, limit);
                System.out.println("New state of the grid:");

                // Render grid state after every move
                GridRenderer.render(this);
            }
        }

        System.out.println("\nGAME OVER");
        printScoreboard(penguins, myPenguin);
    }

    /**
     * Initializes the 2D grid with null (empty) cells.
     */
    private void initializeGrid() {
        grid = new ArrayList<>();
        for (int i = 0; i < GameConstants.GRID_ROWS; i++) {
            List<ITerrainObject> row = new ArrayList<>();
            for (int j = 0; j < GameConstants.GRID_COLS; j++) {
                row.add(null);
            }
            grid.add(row);
        }
    }

    /**
     * Generates penguins, hazards and food items on the grid.
     */
    private void generateContent() {
        generatePenguins();
        generateHazards();
        generateFoods();
    }

    /**
     * Randomly creates and places P1, P2, P3 on the edges of the grid.
     */
    private void generatePenguins() {
        for (int i = 1; i <= GameConstants.PENGUIN_COUNT; i++) {
            String pId = "P" + i;
            Penguin p = createRandomPenguin(pId);
            placeOnRandomEdge(p);
            penguins.add(p);
        }
        // Select one of them as "myPenguin"
        this.myPenguin = penguins.get(random.nextInt(penguins.size()));
    }

    /**
     * Randomly chooses one of the four penguin types.
     */
    private Penguin createRandomPenguin(String id) {
        int type = random.nextInt(4);
        switch (type) {
            case 0: return new KingPenguin(id);
            case 1: return new EmperorPenguin(id);
            case 2: return new RoyalPenguin(id);
            default: return new RockhopperPenguin(id);
        }
    }

    /**
     * Randomly creates and places hazards on empty squares.
     */
    private void generateHazards() {
        for (int i = 0; i < GameConstants.HAZARD_COUNT; i++) {
            Hazard h = createRandomHazard();
            placeOnRandomEmptySquare(h);
        }
    }

    /**
     * Randomly chooses one of the hazard types.
     */
    private Hazard createRandomHazard() {
        int type = random.nextInt(4);
        switch (type) {
            case 0: return new LightIceBlock();
            case 1: return new HeavyIceBlock();
            case 2: return new SeaLion();
            default: return new HoleInIce();
        }
    }

    /**
     * Randomly creates and places food items on empty squares.
     */
    private void generateFoods() {
        for (int i = 0; i < GameConstants.FOOD_COUNT; i++) {
            FoodType[] types = FoodType.values();
            FoodType type = types[random.nextInt(types.length)];
            int weight = random.nextInt(5) + 1;
            Food f = new Food(type, weight);
            placeOnRandomEmptySquare(f);
        }
    }

    /**
     * Places an object randomly on one of the edges of the grid.
     */
    private void placeOnRandomEdge(ITerrainObject obj) {
        int x, y;
        do {
            int edge = random.nextInt(4);
            if (edge == 0) { // top edge
                x = random.nextInt(GameConstants.GRID_COLS);
                y = 0;
            } else if (edge == 1) { // bottom edge
                x = random.nextInt(GameConstants.GRID_COLS);
                y = GameConstants.GRID_ROWS - 1;
            } else if (edge == 2) { // left edge
                x = 0;
                y = random.nextInt(GameConstants.GRID_ROWS);
            } else { // right edge
                x = GameConstants.GRID_COLS - 1;
                y = random.nextInt(GameConstants.GRID_ROWS);
            }
        } while (getObjectAt(x, y) != null);
        placeObjectOnGrid(obj, x, y);
    }

    /**
     * Places an object randomly on any empty square inside the grid.
     */
    private void placeOnRandomEmptySquare(ITerrainObject obj) {
        int x, y;
        do {
            x = random.nextInt(GameConstants.GRID_COLS);
            y = random.nextInt(GameConstants.GRID_ROWS);
        } while (getObjectAt(x, y) != null);
        placeObjectOnGrid(obj, x, y);
    }

    // --- Helpers used by AI ---

    /**
     * Inspects the object in the immediate next square in a given direction without moving.
     */
    public ITerrainObject peekObject(int currentX, int currentY, Direction dir) {
        int nextX = currentX;
        int nextY = currentY;
        switch (dir) {
            case UP:    nextY--; break;
            case DOWN:  nextY++; break;
            case LEFT:  nextX--; break;
            case RIGHT: nextX++; break;
        }
        return getObjectAt(nextX, nextY); // Returns null if out-of-bounds or empty
    }

    /**
     * Checks if a move in a specific direction is safe (no water, no active hazard).
     * Plugged HoleInIce (PH) is treated as a safe square.
     */
    public boolean isSafeMove(int currentX, int currentY, Direction dir) {
        int nextX = currentX;
        int nextY = currentY;
        switch (dir) {
            case UP:    nextY--; break;
            case DOWN:  nextY++; break;
            case LEFT:  nextX--; break;
            case RIGHT: nextX++; break;
        }

        // Check water (out of bounds)
        if (isOutOfBounds(nextX, nextY)) return false;

        // Check hazards
        ITerrainObject obj = getObjectAt(nextX, nextY);
        if (obj instanceof HoleInIce) {
            HoleInIce hole = (HoleInIce) obj;
            // Plugged hole is safe to step on / slide over
            if (hole.isPlugged()) {
                return true;
            }
        }
        if (obj instanceof Hazard) {
            // Any non-plugged hazard is unsafe
            return false;
        }

        // Empty or food is safe
        return true;
    }

    // --- Movement Logic ---

    /**
     * Slides a slidable object in the given direction until it stops or reaches a limit.
     *
     * @param slidable  The object to move
     * @param direction Initial direction of movement
     * @param stopLimit If positive, maximum number of squares it can move in this turn
     *                  (-1 means no limit)
     */
    public void moveObject(ISlidable slidable, Direction direction, int stopLimit) {
        if (slidable == null || direction == null) return;

        // Remove from grid before starting movement
        removeObjectFromGrid(slidable);

        int currentX = slidable.getX();
        int currentY = slidable.getY();
        int stepsTaken = 0;
        boolean keepsSliding = true;

        slidable.setDirection(direction);
        slidable.setMoving(true);

        boolean canJump = false;
        if (slidable instanceof RockhopperPenguin) {
            canJump = ((RockhopperPenguin) slidable).isJumpPrepared();
        }

        while (keepsSliding) {
            // Ability-based movement limit (King / Emperor)
            if (stopLimit != -1 && stepsTaken >= stopLimit) {
                System.out.println(slidable.getSymbol() + " stopped due to ability limit.");
                break;
            }

            int nextX = currentX;
            int nextY = currentY;

            switch (direction) {
                case UP:    nextY--; break;
                case DOWN:  nextY++; break;
                case LEFT:  nextX--; break;
                case RIGHT: nextX++; break;
            }

            // Handle falling into water (off the edges)
            if (isOutOfBounds(nextX, nextY)) {
                handleFallingIntoWater(slidable);
                slidable.setMoving(false);
                return; // Object is removed from the game
            }

            ITerrainObject target = getObjectAt(nextX, nextY);

            if (target == null) {
                // Empty ice, continue sliding
                currentX = nextX;
                currentY = nextY;
                stepsTaken++;
            } else {
                // Rockhopper jump logic over a single hazard (not HoleInIce)
                if (canJump && target instanceof Hazard && !(target instanceof HoleInIce)) {
                    int jumpX = nextX + (nextX - currentX);
                    int jumpY = nextY + (nextY - currentY);
                    if (!isOutOfBounds(jumpX, jumpY) && getObjectAt(jumpX, jumpY) == null) {
                        System.out.println("Rockhopper jumped over " + target.getSymbol());
                        currentX = jumpX;
                        currentY = jumpY;
                        stepsTaken += 2;
                        canJump = false;
                        ((RockhopperPenguin) slidable).consumeJump();
                        continue;
                    } else {
                        System.out.println("Rockhopper failed to jump!");
                        canJump = false;
                    }
                }

                // Special handling for HoleInIce according to the assignment rules
                if (target instanceof HoleInIce) {
                    HoleInIce hole = (HoleInIce) target;

                    if (!hole.isPlugged()) {
                        hole.onCollision((ITerrainObject) slidable);

                        if (slidable instanceof Penguin) {
                            slidable.setMoving(false);
                            return; // Penguin eliminated - don't place back on grid
                        }

                        if (slidable instanceof LightIceBlock || slidable instanceof SeaLion) {
                            slidable.setMoving(false);
                            return; // Hazard plugs hole and disappears - don't place back
                        }
                    } else {
                        // Plugged hole = normal ice, pass through
                        currentX = nextX;
                        currentY = nextY;
                        stepsTaken++;
                    }
                } else {
                    // General collision handling for other objects
                    boolean stopMovement = handleCollision(slidable, target, direction);

                    // If penguin is moving onto a Food, we stop exactly on that square
                    if (target instanceof Food && slidable instanceof Penguin) {
                        currentX = nextX;
                        currentY = nextY;
                        break;
                    }

                    // Check if direction changed (e.g., bounce from SeaLion)
                    if (slidable.getDirection() != direction) {
                        direction = slidable.getDirection();
                        // Do not advance this iteration; continue with new direction
                        continue;
                    }

                    if (stopMovement) {
                        break;
                    }

                    // If not stopping, then slider continues to next cell
                    currentX = nextX;
                    currentY = nextY;
                    stepsTaken++;
                }
            }
        }

        slidable.setMoving(false);

        // IMPORTANT: Rockhopper's jump is only valid for the current turn.
        // Even if no hazard was encountered, the prepared jump must be cleared,
        // so that the ability can be "wasted" as required by the assignment.
        if (slidable instanceof RockhopperPenguin) {
            ((RockhopperPenguin) slidable).consumeJump();
        }

        // If penguin was eliminated during movement logic, do not place it back
        if (slidable instanceof Penguin && ((Penguin) slidable).isEliminated()) {
            return;
        }

        // Place the object back onto the grid at its final position
        placeObjectOnGrid(slidable, currentX, currentY);
    }

    /**
     * Handles collision between a sliding object and a target object (excluding HoleInIce).
     *
     * @return true if the slider should stop at its current position; false if it should continue sliding.
     */
    private boolean handleCollision(ISlidable slider, ITerrainObject target, Direction dir) {
        System.out.println(slider.getSymbol() + " collided with " + target.getSymbol());

        // Case 1: penguin eats food
        if (slider instanceof Penguin && target instanceof Food) {
            Penguin p = (Penguin) slider;
            Food f = (Food) target;
            p.addFood(f);
            System.out.println(p.getId() + " ate " + f.getType());
            removeObjectFromGrid(f);
            // Penguin should stop at the food square
            return true;
        }

        // Case 2: sliding hazard passes over food and removes it, continuing movement
        if (!(slider instanceof Penguin) && target instanceof Food) {
            removeObjectFromGrid(target);
            System.out.println("Food " + target.getSymbol() + " was removed by a sliding hazard.");
            // Hazard continues sliding
            return false;
        }

        // Case 3: penguin pushes another penguin
        if (slider instanceof Penguin && target instanceof Penguin) {
            Penguin stationary = (Penguin) target;
            moveObject(stationary, dir, -1);
            return true;
        }

        // Case 4: collisions with hazards (except HoleInIce, handled in moveObject)
        if (target instanceof Hazard) {
            Hazard h = (Hazard) target;

            // HoleInIce is handled separately in moveObject
            if (h instanceof HoleInIce) {
                return true;
            }

            h.onCollision((ITerrainObject) slider); // Trigger hazard effect

            // SeaLion special bounce behavior
            if (h instanceof SeaLion) {
                SeaLion seaLion = (SeaLion) h;

                // If the SeaLion started moving, slide it as well
                if (seaLion.isMoving() && seaLion.getDirection() != null) {
                    moveObject(seaLion, seaLion.getDirection(), -1);
                }

                if (slider instanceof Penguin) {
                    // Penguin continues sliding in the new (bounced) direction
                    return false;
                } else if (slider instanceof LightIceBlock) {
                    // Movement transferred to SeaLion; block stops
                    return true;
                }
            }

            // LightIceBlock: when hit, it starts sliding and the slider stops
            if (h instanceof LightIceBlock) {
                LightIceBlock block = (LightIceBlock) h;
                Direction blockDir = block.getDirection();
                if (blockDir == null) {
                    blockDir = dir;
                }
                moveObject(block, blockDir, -1);
                // The original slider (penguin or hazard) stops
                return true;
            }

            // Other hazards (e.g., HeavyIceBlock) simply stop the slider
            return true;
        }

        // Default: stop movement
        return true;
    }

    /**
     * Handles falling into water when an object leaves the grid.
     */
    private void handleFallingIntoWater(ISlidable obj) {
        if (obj instanceof Penguin) {
            ((Penguin) obj).fallIntoWater();
            System.out.println(obj.getSymbol() + " removed from game.");
        } else if (obj instanceof LightIceBlock || obj instanceof SeaLion) {
            System.out.println(obj.getSymbol() + " fell into water and is gone.");
        }
    }

    /**
     * Removes the given object from the grid based on its (x, y).
     */
    public void removeObjectFromGrid(ITerrainObject obj) {
        if (obj == null) return;
        int x = obj.getX();
        int y = obj.getY();
        if (y >= 0 && y < GameConstants.GRID_ROWS &&
                x >= 0 && x < GameConstants.GRID_COLS) {
            grid.get(y).set(x, null);
        }
    }

    /**
     * Places an object at the given coordinates and updates its internal position.
     */
    public void placeObjectOnGrid(ITerrainObject obj, int x, int y) {
        obj.setX(x);
        obj.setY(y);
        grid.get(y).set(x, obj);
    }

    /**
     * Returns true if the coordinates are outside the grid.
     */
    public boolean isOutOfBounds(int x, int y) {
        return x < 0 || x >= GameConstants.GRID_COLS || y < 0 || y >= GameConstants.GRID_ROWS;
    }

    /**
     * Returns the object at the given coordinates, or null if empty or out of bounds.
     */
    public ITerrainObject getObjectAt(int x, int y) {
        if (isOutOfBounds(x, y)) return null;
        return grid.get(y).get(x);
    }

    public List<Penguin> getPenguins() {
        return penguins;
    }

    public Penguin getMyPenguin() {
        return myPenguin;
    }

    public List<List<ITerrainObject>> getGrid() {
        return grid;
    }

    // ----------------------
    // Helper methods for game loop (moved from SlidingPuzzleApp)
    // ----------------------

    /**
     * Handles the preparation phase of special abilities (setting flags or Royal's pre-step).
     */
    private void handleSpecialActionPreparation(Penguin p, boolean isPlayer) {
        // This call will internally check if the ability was already used.
        p.useSpecialAbility();

        // RoyalPenguin performs a 1-step move before sliding
        if (p instanceof RoyalPenguin && !p.isEliminated()) {
            Direction moveDir;
            if (isPlayer) {
                moveDir = InputHelper.getDirection(
                        "Royal Ability: Choose direction to step 1 square (U/D/L/R): "
                );
            } else {
                // AI Royal: Takes a safe step (random safe direction if possible)
                moveDir = decideSafeOneStep(p);
            }
            System.out.println(p.getId() + " takes a safe step " + moveDir);
            moveObject(p, moveDir, 1); // Move exactly 1 step
        }
    }

    /**
     * AI Logic based on assignment rules:
     * 1. Prioritize Food.
     * 2. Prioritize Safe Moves (avoid Water/Hazards).
     * 3. Go towards Hazard (except HoleInIce) if necessary.
     * 4. Fall into water (Last resort).
     */
    private Direction decideAIDirection(Penguin p) {
        Direction[] dirs = Direction.values();

        // Priority 1: Check for Food in direct path
        for (Direction d : dirs) {
            ITerrainObject obj = peekObject(p.getX(), p.getY(), d);
            if (obj instanceof Food) return d;
        }

        // Priority 2: Safe Move (Not Water, Not Hazard)
        for (Direction d : dirs) {
            if (isSafeMove(p.getX(), p.getY(), d)) return d;
        }

        // Priority 3: Move towards a Hazard (except HoleInIce)
        for (Direction d : dirs) {
            ITerrainObject obj = peekObject(p.getX(), p.getY(), d);
            if (obj instanceof Hazard && !(obj instanceof HoleInIce)) return d;
        }

        // Priority 4: Last resort (Random / Falling into water)
        return Direction.values()[random.nextInt(dirs.length)];
    }

    /**
     * Helper for Royal Penguin AI to find a safe adjacent square.
     * It chooses a random safe direction if possible; if there is no safe
     * direction (all lead to hazards or water), it picks a random direction.
     */
    private Direction decideSafeOneStep(Penguin p) {
        Direction[] dirs = Direction.values();
        List<Direction> safeDirs = new ArrayList<>();

        // Collect all safe directions first
        for (Direction d : dirs) {
            if (isSafeMove(p.getX(), p.getY(), d)) {
                safeDirs.add(d);
            }
        }

        // If there is at least one safe direction, pick one randomly
        if (!safeDirs.isEmpty()) {
            return safeDirs.get(random.nextInt(safeDirs.size()));
        }

        // If no safe move exists, choose a completely random direction
        return dirs[random.nextInt(dirs.length)];
    }

    /**
     * Prints the list of penguins and marks the user's penguin.
     */
    private void printPenguinInfo(List<Penguin> list, Penguin mine) {
        System.out.println("\nThese are the penguins on the icy terrain:");
        for (Penguin p : list) {
            String suffix = (p == mine) ? " ---> YOUR PENGUIN" : "";
            String penguinType = getPenguinTypeName(p);
            System.out.println("- Penguin " + p.getId().substring(1) + " (" + p.getId() + "): " + penguinType + suffix);
        }
    }

    private String getPenguinTypeName(Penguin p) {
        if (p instanceof KingPenguin) return "King Penguin";
        if (p instanceof EmperorPenguin) return "Emperor Penguin";
        if (p instanceof RoyalPenguin) return "Royal Penguin";
        if (p instanceof RockhopperPenguin) return "Rockhopper Penguin";
        return p.getClass().getSimpleName();
    }

    /**
     * Prints the legend for grid notations similar to the assignment example.
     */
    private void printLegend() {
        System.out.println("\nLegend for Icy Terrain Grid Menu Notations:");
        System.out.println("Penguins : P1, P2, P3");
        System.out.println("Food items : Kr (Krill), Cr (Crustacean), An (Anchovy), Sq (Squid), Ma (Mackerel)");
        System.out.println("Hazards : LB (LightIceBlock), HB (HeavyIceBlock), SL (SeaLion), HI (HoleInIce)");
        System.out.println("Special : PH (Plugged HoleInIce)");
    }

    /**
     * Delegates to ScoreBoard class to print final scores.
     */
    private void printScoreboard(List<Penguin> penguins, Penguin mine) {
        new ScoreBoard().displayScoreBoard(penguins, mine);
    }
}
