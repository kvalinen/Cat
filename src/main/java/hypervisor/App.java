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
    };

    public static void main( String[] args ) {
        new App(new JGPoint(640, 480));
    }

    App(JGPoint size) {
        initEngine(size.x, size.y);
    }

    App() {
        initEngineApplet();
    }

    public void initCanvas() {
        setCanvasSettings(40, 30, 64, 64, null, null, null);
    }

    public void initGame() {
        setFrameRate(35, 2);

        /* Tiles */
        defineImage("grass", "-", 0, "grass.png", "-");

        setTiles(0, 0, MAP);

        /* NPCs */
        defineImage("dog", "*", 1, "dog.png", "-");

        new Dog(1, 1);
        new Dog(200, 100);
    }

    /** Called when a new level is started. */
    public void defineLevel() {
        // remove any remaining objects
        removeObjects(null, 0);
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
