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
        setFrameRate(35,2);
        defineImage("grass","-",0,"grass.png","-");

        setTiles(0, 0, MAP);
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
    }

    public void paintFrameInGame() {
        // display instructions
        drawString("Cat game", pfWidth()/2,180,0);
    }

}
