package hypervisor;

import jgame.*;
import jgame.platform.*;
import java.util.*;

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
    private static final JGPoint TILE_SIZE = new JGPoint(32, 32);
    private static final JGPoint SIZE      = new JGPoint(TILES.x * TILE_SIZE.x, 
                                                         TILES.y * TILE_SIZE.y);
    private static final JGPoint START     = new JGPoint((int) (0.5 * SIZE.x), (int) (0.9 * SIZE.y));

    private static final int MAX_DOGS = 5;

    private Deque<JGPoint> path = new LinkedList<JGPoint>();

    private JGPoint previousMouse = new JGPoint(getMouseX(), getMouseY());

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
        defineImage("dog", "*", 1, "dog.png", "-");

        /* Player */
        defineImage("player_side", "+", 1, "player_cat_side.png", "-");
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
        new Player(START.x, START.y);
    }

    /** Frame logic */
    public void doFrameInGame() {
        moveObjects(null, 0);

        previousMouse = new JGPoint(getMouseX(), getMouseY());

        if (getMouseButton(1)) {
            new JGObject("pathDot", true, previousMouse.x-10, previousMouse.y-10, 0, "dog");
            path.add(new JGPoint(previousMouse.x, previousMouse.y));
        }
    }

    public class Player extends JGObject {
        private double pixPerFrame = 1; // XXX Depends on FPS

        private JGPoint target;

        Player(int x, int y) {
            super("player", false, x, y, 1, "player_side");
        }

        public void move() {
            if (target == null && !path.isEmpty())
                target = path.remove();

            if (x == target.x && y == target.y) {
                xspeed = 0;
                yspeed = 0;

                return;
            } else {
                double dirX = Math.signum(getMouseX() - target.x);
                double dirY = Math.signum(getMouseY() - target.y);

                xspeed = dirX * pixPerFrame;
                yspeed = dirY * pixPerFrame;
            }
        }
    }

    /* NPC definitions */

    /** Enemy dogs. Evil bastards. */
    public class Dog extends JGObject {
        Dog(int x, int y) {
            super("dog", true, x, y, 1, "dog");
        }

        public void move() {
            
        }
    }

}
