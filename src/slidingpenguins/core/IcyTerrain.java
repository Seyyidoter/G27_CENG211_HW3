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

    /**
     * Default Constructor: Creates a random game (No seed).
     */
    public IcyTerrain() {
        this.random = new Random();
        initialize();
    }

    /**
     * Seeded Constructor: Creates a game with a specific seed.
     * Useful for testing or replicating scenarios (deterministic behavior).
     * @param seed Random seed (e.g., 42)
     */
    public IcyTerrain(int seed) {
        // Implicit casting from int to long handles the seed correctly.
        this.random = new Random(seed);
        initialize();
    }

    /**
     * Common initialization method to prevent code duplication in constructors.
     */
    private void initialize() {
        this.penguins = new ArrayList<>();
        initializeGrid();
        generateContent();
    }

    /**
     * Starts the game loop and handles turns, inputs and rendering.
     */
    public void startGame() {
        System.out.println("Welcome to Sliding Penguins Puzzle Game App.");
        System.out.println("An " + GameConstants.GRID_ROWS + "x" + GameConstants.GRID_COLS
                + " icy terrain grid is being generated.");
        System.out.println("Penguins, Hazards, and Food items are also being generated.");

        // Ensure strictly P1, P2, P3 turn order
        penguins.sort(Comparator.comparing(Penguin::getId));

        printLegend();

        System.out.println("\nThe initial icy terrain grid:");
        GridRenderer.render(this);
        printPenguinInfo(penguins, myPenguin);

        for (int turn = 1; turn <= GameConstants.MAX_TURNS; turn++) {
            System.out.println("\n*** Turn " + turn + " ***");

            for (Penguin p : penguins) {
                if (p.isEliminated()) {
                    System.out.println(p.getId() + " is eliminated and skips turn.");
                    continue;
                }

                if (p.isStunned()) {
                    System.out.println(p.getId() + " is stunned and skips this turn!");
                    p.setStunned(false);
                    continue;
                }


                 System.out.print("\n--- " + p.getId() + "'s Turn ---");

                Direction chosenDir;
                boolean useAbility = false;

                // --- DECISION PHASE ---
                if (p == myPenguin) {
                    System.out.println(" (Your Penguin)\n");
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

                    if (p instanceof RockhopperPenguin) {
                        // Rockhopper: no 30% random chance.
                        // They automatically use their action the first time they
                        // decide to move in the direction of a hazard.
                        RockhopperPenguin rh = (RockhopperPenguin) p;
                        ITerrainObject target = peekObject(p.getX(), p.getY(), chosenDir);

                        if (!p.hasUsedAbility()
                                && target instanceof Hazard
                                && !(target instanceof HoleInIce)
                                && rh.canAutoUseForHazard()) {

                            useAbility = true;
                            System.out.println("\n" + p.getId()
                                    + " (AI) sees a hazard and automatically uses its special action!");
                        } else {
                            useAbility = false;
                        }

                    } else {
                        // Other AI penguins keep the 30% chance rule
                        if (!p.hasUsedAbility()) {
                            useAbility = random.nextInt(100) < GameConstants.AI_ABILITY_USE_CHANCE;
                        } else {
                            useAbility = false;
                            System.out.println("\n" + p.getId() + " has already used its special action (AI).");
                        }
                    }

                    if (useAbility) {
                        System.out.println("\n" + p.getId() + " chooses to USE its special action.");
                        handleSpecialActionPreparation(p, false);
                    } else {
                        System.out.println("\n" + p.getId() + " does NOT use its special action.");
                    }
                }

                System.out.println(p.getId() + " chooses to move " + chosenDir);

                // --- EXECUTION PHASE ---
                int limit = -1;
                if (useAbility) {
                    if (p instanceof KingPenguin) limit = 5;
                    if (p instanceof EmperorPenguin) limit = 3;
                }

                moveObject(p, chosenDir, limit);
                System.out.println("New state of the grid:");
                GridRenderer.render(this);
            }
        }

        System.out.println("\nGAME OVER");
        printScoreboard(penguins, myPenguin);
    }

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

    private void generateContent() {
        generatePenguins();
        generateHazards();
        generateFoods();
    }

    /**
     * Generates all penguins and places them on the grid edges.
     * One penguin is randomly assigned to the player.
     */
    private void generatePenguins() {
        for (int i = 1; i <= GameConstants.PENGUIN_COUNT; i++) {
            String pId = "P" + i;
            Penguin p = createRandomPenguin(pId);
            placeOnRandomEdge(p);
            penguins.add(p);
        }
        this.myPenguin = penguins.get(random.nextInt(penguins.size()));
    }

    /**
     * Creates a random penguin of one of the four types.
     * @param id The penguin ID (e.g., "P1", "P2", "P3")
     * @return A new Penguin instance
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
     * Generates all hazards and places them on empty grid squares.
     */
    private void generateHazards() {
        for (int i = 0; i < GameConstants.HAZARD_COUNT; i++) {
            Hazard h = createRandomHazard();
            placeOnRandomEmptySquare(h);
        }
    }

    /**
     * Creates a random hazard of one of the four types.
     * @return A new Hazard instance
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
     * Generates all food items with random types and weights.
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

    private void placeOnRandomEdge(ITerrainObject obj) {
        int x, y;
        do {
            int edge = random.nextInt(4);
            if (edge == 0) { // top
                x = random.nextInt(GameConstants.GRID_COLS);
                y = 0;
            } else if (edge == 1) { // bottom
                x = random.nextInt(GameConstants.GRID_COLS);
                y = GameConstants.GRID_ROWS - 1;
            } else if (edge == 2) { // left
                x = 0;
                y = random.nextInt(GameConstants.GRID_ROWS);
            } else { // right
                x = GameConstants.GRID_COLS - 1;
                y = random.nextInt(GameConstants.GRID_ROWS);
            }
        } while (getObjectAt(x, y) != null);
        placeObjectOnGrid(obj, x, y);
    }

    private void placeOnRandomEmptySquare(ITerrainObject obj) {
        int x, y;

        while (true) {
            x = random.nextInt(GameConstants.GRID_COLS);
            y = random.nextInt(GameConstants.GRID_ROWS);

            ITerrainObject existing = getObjectAt(x, y);

            // 1) Hazards → can only spawn on a completely empty cell.
            //    They should never overlap with penguins or food.
            if (obj instanceof Hazard) {
                if (existing == null) break;
            }

            // 2) Food → must also spawn on empty cells only.
            //    Initial food should NOT appear on the same square as penguins.
            //    Food being on the same cell with a penguin only happens during movement
            //    and is consumed instantly through collision logic — not during generation.
            else if (obj instanceof Food) {
                if (existing == null) break;
            }

            // 3) Penguins & other objects → must also only be placed on empty cells.
            else {
                if (existing == null) break;
            }
        }

        placeObjectOnGrid(obj, x, y);
    }

    // --- Helper Logic ---

    public ITerrainObject peekObject(int currentX, int currentY, Direction dir) {
        int nextX = currentX;
        int nextY = currentY;
        switch (dir) {
            case UP:    nextY--; break;
            case DOWN:  nextY++; break;
            case LEFT:  nextX--; break;
            case RIGHT: nextX++; break;
        }
        return getObjectAt(nextX, nextY);
    }

    public boolean isSafeMove(int currentX, int currentY, Direction dir) {
        int nextX = currentX;
        int nextY = currentY;
        switch (dir) {
            case UP:    nextY--; break;
            case DOWN:  nextY++; break;
            case LEFT:  nextX--; break;
            case RIGHT: nextX++; break;
        }

        if (isOutOfBounds(nextX, nextY)) return false;

        ITerrainObject obj = getObjectAt(nextX, nextY);
        if (obj instanceof HoleInIce) {
            if (((HoleInIce) obj).isPlugged()) return true;
        }
        if (obj instanceof Hazard) return false;

        return true;
    }

    // --- Movement & Collision ---

    public void moveObject(ISlidable slidable, Direction direction, int stopLimit) {
        if (slidable == null || direction == null) return;
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

            if (isOutOfBounds(nextX, nextY)) {
                handleFallingIntoWater(slidable);
                slidable.setMoving(false);
                return;
            }

            ITerrainObject target = getObjectAt(nextX, nextY);

            if (target == null) {
                currentX = nextX;
                currentY = nextY;
                stepsTaken++;
            } else {
                // Rockhopper jump
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

                if (target instanceof HoleInIce) {
                    HoleInIce hole = (HoleInIce) target;
                    if (!hole.isPlugged()) {
                        hole.onCollision((ITerrainObject) slidable);
                        if (slidable instanceof Penguin) {
                            slidable.setMoving(false);
                            return;
                        }
                        if (slidable instanceof LightIceBlock || slidable instanceof SeaLion) {
                            slidable.setMoving(false);
                            return;
                        }
                    } else {
                        currentX = nextX;
                        currentY = nextY;
                        stepsTaken++;
                    }
                } else {
                    boolean stopMovement = handleCollision(slidable, target, direction);

                    if (target instanceof Food && slidable instanceof Penguin) {
                        currentX = nextX;
                        currentY = nextY;
                        break;
                    }

                    if (stopMovement) {
                        break;
                    }

                    Direction newDir = slidable.getDirection();
                    if (newDir != null && newDir != direction) {
                        direction = newDir;
                        continue;
                    }
                    
                    currentX = nextX;
                    currentY = nextY;
                    stepsTaken++;
                }
            }
        }

        slidable.setMoving(false);
        if (slidable instanceof RockhopperPenguin) {
            ((RockhopperPenguin) slidable).consumeJump();
        }

        if (slidable instanceof Penguin && ((Penguin) slidable).isEliminated()) {
            return;
        }

        placeObjectOnGrid(slidable, currentX, currentY);
    }

    private boolean handleCollision(ISlidable slider, ITerrainObject target, Direction dir) {
        System.out.println(slider.getSymbol() + " collided with " + target.getSymbol());

        if (slider instanceof Penguin && target instanceof Food) {
            ((Penguin) slider).addFood((Food) target);
            System.out.println(slider.getSymbol() + " takes the " + ((Food)target).getType()
                    + " on the ground. (Weight=" + ((Food)target).getWeight() + " units)");
            removeObjectFromGrid(target);
            return true;
        }

        if (!(slider instanceof Penguin) && target instanceof Food) {
            removeObjectFromGrid(target);
            System.out.println("Food " + target.getSymbol() + " was removed by a sliding hazard.");
            return false;
        }

        if (slider instanceof Penguin && target instanceof Penguin) {
            moveObject((Penguin) target, dir, -1);
            return true;
        }

        if (target instanceof Hazard) {
            Hazard h = (Hazard) target;
            if (h instanceof HoleInIce) return true;

            h.onCollision((ITerrainObject) slider);

            if (h instanceof SeaLion) {
                SeaLion seaLion = (SeaLion) h;
                if (seaLion.isMoving() && seaLion.getDirection() != null) {
                    moveObject(seaLion, seaLion.getDirection(), -1);
                }
                if (slider instanceof Penguin) return false;
                else if (slider instanceof LightIceBlock) return true;
            }

            if (h instanceof LightIceBlock) {
                LightIceBlock block = (LightIceBlock) h;
                Direction blockDir = block.getDirection();
                if (blockDir == null) blockDir = dir;
                moveObject(block, blockDir, -1);
                return true;
            }
            return true;
        }
        return true;
    }

    private void handleFallingIntoWater(ISlidable obj) {
        if (obj instanceof Penguin) {
            ((Penguin) obj).fallIntoWater();
            System.out.println(obj.getSymbol() + " removed from game.");
        } else if (obj instanceof LightIceBlock || obj instanceof SeaLion) {
            System.out.println(obj.getSymbol() + " fell into water and is gone.");
        }
    }

    public void removeObjectFromGrid(ITerrainObject obj) {
        if (obj == null) return;
        int x = obj.getX();
        int y = obj.getY();
        if (y >= 0 && y < GameConstants.GRID_ROWS && x >= 0 && x < GameConstants.GRID_COLS) {
            grid.get(y).set(x, null);
        }
    }

    public void placeObjectOnGrid(ITerrainObject obj, int x, int y) {
        obj.setX(x);
        obj.setY(y);
        grid.get(y).set(x, obj);
    }

    public boolean isOutOfBounds(int x, int y) {
        return x < 0 || x >= GameConstants.GRID_COLS || y < 0 || y >= GameConstants.GRID_ROWS;
    }

    public ITerrainObject getObjectAt(int x, int y) {
        if (isOutOfBounds(x, y)) return null;
        return grid.get(y).get(x);
    }

    /**
     * Prevents the removal of penguins from the main game list.
     */
    public List<Penguin> getPenguins() {
        return List.copyOf(this.penguins);
    }

    /**
     * Returns the user's penguin for read-only purposes.
     */
    public Penguin getMyPenguin() {
        return myPenguin;
    }

    /**
     * Returns a copy of the grid (copying each row).
     * Protects the structure of the game grid from external modifications.
     */
    public List<List<ITerrainObject>> getGrid() {
        List<List<ITerrainObject>> copyGrid = new ArrayList<>();
        for (List<ITerrainObject> row : this.grid) {
            copyGrid.add(new ArrayList<>(row));
        }
        return copyGrid;
    }

    // --- Helpers ---


    /**
     * Handles the preparation phase for special abilities.
     * For Royal Penguins, executes the one-step movement.
     * @param p The penguin using the ability
     * @param isPlayer Whether this is the player's penguin
     */
    private void handleSpecialActionPreparation(Penguin p, boolean isPlayer) {
        p.useSpecialAbility();
        if (p instanceof RoyalPenguin && !p.isEliminated()) {
            Direction moveDir;
            if (isPlayer) {
                moveDir = InputHelper.getDirection("Royal Ability: Choose direction to step 1 square (U/D/L/R): ");
            } else {
                moveDir = decideSafeOneStep(p);
            }
            System.out.println(p.getId() + " takes a safe step " + moveDir);
            moveObject(p, moveDir, 1);
        }
    }

    /**
     * AI decision logic for choosing a movement direction.
     * Prioritizes: food > safe moves > hazards > random
     * @param p The AI-controlled penguin
     * @return The chosen direction
     */
    private Direction decideAIDirection(Penguin p) {
        Direction[] dirs = Direction.values();
        for (Direction d : dirs) {
            ITerrainObject obj = peekObject(p.getX(), p.getY(), d);
            if (obj instanceof Food) return d;
        }
        for (Direction d : dirs) {
            if (isSafeMove(p.getX(), p.getY(), d)) return d;
        }
        for (Direction d : dirs) {
            ITerrainObject obj = peekObject(p.getX(), p.getY(), d);
            if (obj instanceof Hazard && !(obj instanceof HoleInIce)) return d;
        }
        return Direction.values()[random.nextInt(dirs.length)];
    }

    /**
     * AI decision logic for Royal Penguin's one-step ability.
     * Chooses a safe adjacent square if possible.
     * @param p The Royal Penguin
     * @return A safe direction or random if no safe options
     */
    private Direction decideSafeOneStep(Penguin p) {
        Direction[] dirs = Direction.values();
        List<Direction> safeDirs = new ArrayList<>();
        for (Direction d : dirs) {
            if (isSafeMove(p.getX(), p.getY(), d)) safeDirs.add(d);
        }
        if (!safeDirs.isEmpty()) return safeDirs.get(random.nextInt(safeDirs.size()));
        return dirs[random.nextInt(dirs.length)];
    }

    /**
     * Returns the penguin type name as a readable string.
     * Used for displaying penguin information to the user.
     * @param p The penguin to get the type name for
     * @return Human-readable type name (e.g., "King Penguin")
     */
    private String getPenguinTypeName(Penguin p) {
        if (p instanceof KingPenguin) return "King Penguin";
        if (p instanceof EmperorPenguin) return "Emperor Penguin";
        if (p instanceof RoyalPenguin) return "Royal Penguin";
        if (p instanceof RockhopperPenguin) return "Rockhopper Penguin";
        return p.getClass().getSimpleName();
    }

    /**
     * Prints information about all penguins in the game.
     * Marks the user's penguin with a special indicator.
     * @param list List of all penguins
     * @param mine The user's penguin
     */
    private void printPenguinInfo(List<Penguin> list, Penguin mine) {
        System.out.println("\nThese are the penguins on the icy terrain:");
        for (Penguin p : list) {
            String suffix = (p == mine) ? " ---> YOUR PENGUIN" : "";
            String typeName = getPenguinTypeName(p);
            System.out.println("- Penguin " + p.getId().substring(1) + " (" + p.getId() + "): " + typeName + suffix);
        }
    }

    /**
     * Prints the legend explaining all symbols used in the grid.
     * Called once at the start of the game.
     */
    private void printLegend() {
        System.out.println("\nLegend for Icy Terrain Grid Menu Notations:");
        System.out.println("Penguins : P1, P2, P3");
        System.out.println("Food items : Kr (Krill), Cr (Crustacean), An (Anchovy), Sq (Squid), Ma (Mackerel)");
        System.out.println("Hazards : LB (LightIceBlock), HB (HeavyIceBlock), SL (SeaLion), HI (HoleInIce)");
        System.out.println("Special : PH (Plugged HoleInIce)");
    }

    /**
     * Displays the final scoreboard using the ScoreBoard class.
     * @param penguins List of all penguins
     * @param mine The user's penguin
     */
    private void printScoreboard(List<Penguin> penguins, Penguin mine) {
        new ScoreBoard().displayScoreBoard(penguins, mine);
    }
}
