package hypervisor;

import jgame.*;
import jgame.platform.*;
import java.util.*;

/**
 * Hello world!
 *
 */
public class App extends JGEngine {
    
    private static final JGPoint TILES     = new JGPoint(20, 20);
    private static final JGPoint TILE_SIZE = new JGPoint(32, 32);
    private static final JGPoint SIZE      = new JGPoint(TILES.x * TILE_SIZE.x, 
                                                         TILES.y * TILE_SIZE.y);
    private static final JGPoint START     = new JGPoint((int) (0.5 * SIZE.x), (int) (0.9 * SIZE.y));

    private static final int MAX_DOGS = 5;

    private static final JGColor PATH_COLOR     = JGColor.blue;
    private static final double  PATH_THICKNESS = 10;

    public enum DrawingState { NEW, DRAWING, STOPPED }

    private DrawingState drawingState = DrawingState.NEW;

    // Concrete type since we need both .listIterator() and .remove()
    private LinkedList<JGPoint> path = new LinkedList<JGPoint>();

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

        /* Background */
        defineImage("background", "#", 0, "grass.png", "-");

        setBGImage("background");

        /* NPCs */
        defineImage("dog", "*", 1, "dog.png", "-");

        /* Player */
        defineImage("player_side", "+", 1, "player_cat_side.png", "-");

        /* Path */
        defineImage("dot", "+", 1, "mouse.png", "-");

        for (int i = 0; i < MAX_DOGS; i++)
            new Dog((int) random(0, SIZE.x), 
                    (int) random(0, SIZE.y));

        new Player(START.x, START.y);
    }

    /** Frame logic */
    public void paintFrame() {
        moveObjects(null, 0);

        previousMouse = new JGPoint(getMouseX(), getMouseY());

        if (getMouseButton(1)) {
            if (drawingState == DrawingState.NEW || drawingState == DrawingState.STOPPED) {
                if (cursorOnPlayer()) {
                    dbgPrint("Yes.");
                    drawingState = DrawingState.DRAWING;
                    path.clear();
                } else {
                    dbgPrint("No.");
                }
            }
        }  

        if (getKey(KeyShift) && drawingState == DrawingState.DRAWING) {
            drawingState = DrawingState.STOPPED;
        }

        if (drawingState == DrawingState.DRAWING)
            path.add(new JGPoint(previousMouse.x, previousMouse.y));

        paintPath();
    }

    private boolean cursorOnPlayer() {
        @SuppressWarnings("unchecked")
        Player player = (Player) getObject("player");

        dbgPrint("Checking if cursor is on player");

        return Math.abs(player.getX() - previousMouse.x) < 30 && Math.abs(player.getY() - previousMouse.y) < 30;
    }

    private void paintPath() {
        ListIterator<JGPoint> points = path.listIterator();

        if (!points.hasNext())
            return;

        @SuppressWarnings("unchecked")
        Player player = (Player) getObject("player");
        JGPoint prev = points.next();

        drawLine(player.getX(), player.getY(), prev.x, prev.y, PATH_THICKNESS, PATH_COLOR);

        while (points.hasNext()) {
            JGPoint next = points.next();

            drawLine(prev.x, prev.y, next.x, next.y, PATH_THICKNESS, PATH_COLOR);

            if (!points.hasNext())
                break;

            prev = points.next();
        }
    }

    public class Player extends JGObject {
        private double pixPerFrame = 1; // XXX Depends on FPS

        private JGPoint target;

        private double dist = 10;

        Player(int x, int y) {
            super("player", false, x, y, 1, "player_side");
        }

        public void move() {
            if (target == null && !path.isEmpty())
                target = path.remove();

            if (target == null && path.isEmpty())
                return;

            if (Math.abs(getX() - target.x) <= dist && Math.abs(getY() - target.y) <= dist) {
                target = null;

                xspeed = yspeed = 0;
            } else {
                double dirX = Math.signum(target.x - getX());
                double dirY = Math.signum(target.y - getY());

                xspeed = dirX * pixPerFrame;
                yspeed = dirY * pixPerFrame;
            }
        }

        public double getX() {
            return x + 32;
        }

        public double getY() {
            return y + 32;
        }
    }

    /* NPC definitions */

    /** Enemy dogs. Evil bastards. */
    public class Dog extends JGObject {
        private int direction = 1;
        private double pixPerFrame = 0.3;

        Dog(int x, int y) {
            super("dog", true, x, y, 1, "dog");
        }

        public void move() {
            if (Math.random() < 0.1)
                direction = -direction;

            xspeed = pixPerFrame * direction;
        }
    }

}
