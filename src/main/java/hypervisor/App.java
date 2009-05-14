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
    private static final JGPoint playerStartPosition     = new JGPoint((int) (0.5 * SIZE.x), (int) (0.9 * SIZE.y));

    private static final JGPoint playerWinAreaSize = new JGPoint(40, 40);
    private static final JGRectangle playerWinArea = new JGRectangle((SIZE.x - playerWinAreaSize.x) / 2,
                                                                     playerWinAreaSize.y + 20,
                                                                     playerWinAreaSize.x,
                                                                     playerWinAreaSize.y);

    private static final double PLAYER_HIGHPASS_FILTER = 5;

    private static final int MAX_DOGS = 5;

    private static final JGColor PATH_COLOR     = JGColor.blue;
    private static final double  PATH_THICKNESS = 10;

    private boolean DEBUG = false;

    public enum DrawingState { NEW, DRAWING, STOPPED }

    private DrawingState drawingState = DrawingState.NEW;

    // Concrete type since we need both .listIterator() and .remove()
    private final LinkedList<JGPoint> path = new LinkedList<JGPoint>();

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
                    (int) random(0, SIZE.y - 200));

        new Player(playerStartPosition.x, playerStartPosition.y);
    }

    /** Frame logic */
    public void paintFrame() {
        moveObjects(null, 0);

        @SuppressWarnings("unchecked")
        Player player = (Player) getObject("player");

        if (player.getBBox().intersects(playerWinArea)) {
            win();
        }

        previousMouse = new JGPoint(getMouseX(), getMouseY());

        if (!player.isRunning()) {
            if (getMouseButton(1)) {
                if (drawingState == DrawingState.NEW || drawingState == DrawingState.STOPPED) {
                    if (cursorOnPlayer()) {
                        drawingState = DrawingState.DRAWING;
                        path.clear();
                    } else if (cursoronRouteEnd())  {
                        drawingState = DrawingState.DRAWING;
                    }
                }
            } else if (drawingState == DrawingState.DRAWING) {
                drawingState = DrawingState.STOPPED;
            }

            if (drawingState == DrawingState.DRAWING)
                path.add(new JGPoint(previousMouse.x, previousMouse.y));
        }

        paintPath();

        drawVictoryArea();
    }

    private void win() {
        // FIXME
        System.out.println("You won!");
        System.exit(0);
    }

    private void drawVictoryArea() {
        // TODO
        setColor(JGColor.red);
        drawRect(playerWinArea.x, playerWinArea.y, playerWinArea.width, playerWinArea.height, true, false);
    }

    private boolean cursorOnPlayer() {
        @SuppressWarnings("unchecked")
        Player player = (Player) getObject("player");

        return Math.abs(player.getX() - previousMouse.x) < 30 && Math.abs(player.getY() - previousMouse.y) < 30;
    }
	private boolean cursoronRouteEnd() {
		if (!path.isEmpty()) {
			JGPoint p = path.getLast();

			return Math.abs(p.x - previousMouse.x ) < 30 && Math.abs(p.y - previousMouse.y) < 30;
		} else {
			return false;
		}

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
        private final double pixPerFrame = 2; // XXX Depends on FPS
        private final double dist = 10;
        private final double scaredDistance = 80;

        private final double runningSpeed = 5;
        private final double runDistance = 80;

        private boolean running = false;

        private JGPoint target;

        Player(int x, int y) {
            super("player", false, x, y, 1, "player_side");
        }

        public void move() {
            checkDogs();

            if (running) {
                debug("Running!");
            } else {
                debug("Phew, safe.");
            }

            if (target == null && !path.isEmpty())
                target = path.remove();

            if (target == null && path.isEmpty())
                return;

            if (DEBUG) {
                drawLine(getX(), getY(), target.x, target.y, 2, JGColor.cyan);
            }

            if (Math.abs(getX() - target.x) <= dist && Math.abs(getY() - target.y) <= dist) {
                running = false;

                target = null;

                xspeed = yspeed = 0;
            } else {
                double distX = target.x - getX();
                double distY = target.y - getY();

                double dirX = Math.signum(distX);
                double dirY = Math.signum(distY);

                if (Math.abs(distX) > PLAYER_HIGHPASS_FILTER) {
                    xspeed = dirX * pixPerFrame;
                } else {
                    xspeed = 0;
                }

                if (Math.abs(distY) > PLAYER_HIGHPASS_FILTER) {
                    yspeed = dirY * pixPerFrame;
                } else {
                    yspeed = 0;
                }
            }
        }

        public boolean isRunning() {
            return running;
        }

        public double getX() {
            return x + getBBox().width / 2;
        }

        public double getY() {
            return y + getBBox().height / 2;
        }

        private void checkDogs() {
            if (running)
                return;

            debug("Looking for dogs");

            List<Dog> dogs = getDogs();

            for (Dog dog : dogs) {
                if (distanceTo(dog) < scaredDistance) {
                    debug("Eeeek, a dog!");
                    path.clear();
                    running = true;
                    target = pickEscape();
                }
            }
        }

        private double distanceTo(JGObject object) {
            JGRectangle target = object.getBBox();

            double targetX = target.x + target.width / 2;
            double targetY = target.y + target.height / 2;

            if (DEBUG) {
                drawLine(getX(), getY(), targetX, targetY, 2, JGColor.white);
            }

            return Math.sqrt(Math.pow(targetX - getX(), 2) 
                           + Math.pow(targetY - getY(), 2));
        }

        private JGPoint pickEscape() {
            // Always run towards center
            return new JGPoint((int)(x + Math.signum(SIZE.x/2 - getX()) * random(runDistance/2, runDistance)), 
                               (int)(y + Math.signum(SIZE.y/2 - getY()) * random(runDistance/2, runDistance)));
        }

        private List<Dog> getDogs() {
            return getObjects("dog", 1, false, new JGRectangle(0, 0, SIZE.x, SIZE.y));
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
            if (Math.random() < 0.01)
                direction = -direction;

            xspeed = pixPerFrame * direction;
        }
    }

    private void debug(String message) {
        if (DEBUG)
            dbgPrint(message);
    }

}
