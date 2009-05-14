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

    private static final double PLAYER_HIGHPASS_FILTER = 5;

    private static final int MAX_DOGS = 5;

    private static final JGColor PATH_COLOR     = JGColor.blue;
    private static final double  PATH_THICKNESS = 10;

    private static final JGPoint winTarget = new JGPoint(SIZE.x / 2, 0);

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
        setFrameRate(60, 2);

        /* Background */
        defineImage("background", "#", 0, "grass.png", "-");

        setBGImage("background");

        /* NPCs */
        defineImage("dog", "*", 3, "dog.png", "-");

        /* Player */
        defineImage("player_side", "+", 2, "player_cat_side.png", "-");

        /* Target */
        defineImage("target", ".", 1, "kuisti.png", "-");

        new JGObject("target", false, 0, 0, 3, "target");

        /* Path */
        defineImage("dot", "+", 1, "dot.png", "-");

        for (int i = 0; i < MAX_DOGS; i++)
            new Dog((int) random(0, SIZE.x), 
                    (int) random(getImage("target").getSize().y, SIZE.y - 200));

        new Player(playerStartPosition.x, playerStartPosition.y);
    }

    /** Frame logic */
    public void doFrame() {
        moveObjects(null, 0);

        checkCollision(1, 2);

        @SuppressWarnings("unchecked")
        Player player = (Player) getObject("player");

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

            if (drawingState == DrawingState.DRAWING) {
                if (path.isEmpty() 
                || (!path.isEmpty() && !previousMouse.equals(path.getLast()))) {
                    path.add(new JGPoint(previousMouse.x, previousMouse.y));
                }
            }
        }
    }

    private void win() {
        // FIXME
        System.out.println("You won!");
        System.exit(0);
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

	public void paintFrame() {
		for (JGPoint p : path) {
            drawImage(p.x, p.y, "dot");
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
            super("player", false, x, y, 2, "player_side");
        }

        public void move() {
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

        public void hit(JGObject object) {

            dbgPrint("Collision with " + object.getName());

            if (object.getName().startsWith("dog")) {
                debug("Eeeek, a dog!");
                path.clear();
                running = true;
                target = pickEscape();
            } else if (object.getName().startsWith("target")) {
                win();
            } else {
                xspeed = 0;
                yspeed = 0;
            }
        }

        private JGPoint pickEscape() {
            // Always run towards center
            return new JGPoint((int)(x + Math.signum(SIZE.x/2 - getX()) * random(runDistance/2, runDistance)), 
                               (int)(y + Math.signum(SIZE.y/2 - getY()) * random(runDistance/2, runDistance)));
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
