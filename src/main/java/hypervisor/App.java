package hypervisor;

import jgame.*;
import jgame.platform.*;

/**
 * Hello world!
 *
 */
public class App extends StdGame {
    
    private static String[] MAP = new String[] {
        "----------------------------------------",
        "----------------------------------------",
        "----------------------------------------",
        "----------------------------------------",
        "----------------------------------------",
        "----------------------------------------",
        "----------------------------------------",
        "----------------------------------------",
        "----------------------------------------",
        "----------------------------------------",
        "----------------------------------------",
        "----------------------------------------",
        "----------------------------------------",
        "----------------------------------------",
        "----------------------------------------",
        "----------------------------------------",
        "----------------------------------------",
        "----------------------------------------",
        "----------------------------------------",
        "----------------------------------------",
        "----------------------------------------",
        "----------------------------------------",
        "----------------------------------------",
        "----------------------------------------",
        "----------------------------------------",
        "----------------------------------------",
        "----------------------------------------",
        "----------------------------------------",
        "----------------------------------------",
        "----------------------------------------",
        "----------------------------------------",
        "----------------------------------------",
        "----------------------------------------",
        "----------------------------------------",
        "----------------------------------------",
        "----------------------------------------",
        "----------------------------------------",
        "----------------------------------------",
        "----------------------------------------",
        "----------------------------------------",
    };

    private static final JGPoint TILES     = new JGPoint(20, 20);
    private static final JGPoint TILE_SIZE = new JGPoint(64, 64);
    private static final JGPoint SIZE      = new JGPoint(TILES.x * TILE_SIZE.x, 
                                                         TILES.y * TILE_SIZE.y);

    private static final int MAX_DOGS = 20;

    /* Application entry point */
    public static void main( String[] args ) {
        new App(SIZE);
    }

    App(JGPoint size) {
        initEngine(size.x, size.y);
    }

    App() {
        initEngineApplet();
    }

    public void initCanvas() {
        setCanvasSettings(TILES.x, TILES.y, TILE_SIZE.x, TILE_SIZE.y, null, null, null);
    }

    public void initGame() {
        setFrameRate(35, 2);

        /* Tiles */
        defineImage("grass", "-", 0, "grass.png", "-");

        setTiles(0, 0, MAP);

        /* NPCs */
        defineImage("dog", "*", 1, "dog.png", "+");
    }

    /** Called when a new level is started. */
    public void defineLevel() {
        // remove any remaining objects
        removeObjects(null, 0);

        for (int i = 0; i < MAX_DOGS; i++)
            new Dog((int) Math.round(Math.random() * SIZE.x), 
                    (int) Math.round(Math.random() * SIZE.y));
    }

    /** Called when a new life is introduced (that is, at the beginning of the
     * game and every time the player dies. */
    public void initNewLife() {
        // ... initialise player sprite ...
    }

    /** This is the most important method you have to fill in in StdGame. */
    public void doFrameInGame() {
        moveObjects(null, 0);

        if (getMouseButton(1))
            new JGObject("pathDot", true, getMouseX(), getMouseY(), 0, "dog");
    }

    public void paintFrameInGame() { }

    /* NPC definitions */
    public class Dog extends JGObject {
        Dog(int x, int y) {
            super("dog", true, x, y, 1, "dog");
        }

        public void move() {
            // TODO
        }
    }

}
