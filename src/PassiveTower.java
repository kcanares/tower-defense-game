import bagel.DrawOptions;
import bagel.Image;
import bagel.util.Point;
import java.util.*;

/**
 * represents passive towers in the game (the ones that don't aim and shoot but just drop bombs)
 */
abstract public class PassiveTower {
    private final static int ANGLE_TO_FACE_RIGHT = 90; // in degrees
    private final static int ANGLE_TO_FACE_DOWN = 180;  // in degrees
    private final static int FRAMES_PER_SECOND = 60;
    private Point spawnPoint;
    private int maxDropTime;
    private int speed;
    private Image towerImg;
    private DrawOptions rotation;
    private Point currentPos;
    private int flightNum;
    private int latestDropFrame;
    private List<Bomb> bombs;

    public String getFlightStatus() {
        return flightStatus;
    }

    private String flightStatus;

    /**
     * constructor
     * @param maxDropTime
     * @param speed
     * @param imgFP
     * @param spawnPoint
     */
    public PassiveTower(int maxDropTime, int speed, String imgFP, Point spawnPoint)
    {
        this.speed = speed;
        this.maxDropTime = maxDropTime;
        this.spawnPoint = spawnPoint;

        towerImg = new Image(imgFP);

        rotation = new DrawOptions();
        rotation.setRotation(Math.toRadians(ANGLE_TO_FACE_RIGHT));

        flightNum = 0;  // number of times the plane has flown across the screen
        currentPos = new Point(0, spawnPoint.y);
        this.flightStatus = "in flight";
        updateDropFrame();

        bombs = new ArrayList<Bomb>();
    }

    /**
     * general main driver for updating all the drawings associated with the passive tower
     */
    public void updateDraw() {
        if (flightNum == 0 || flightNum == 2) {
            flyHorizontal();
        }
        else if (flightNum == 1) {
            flyVertical();
        }
        else if (flightNum == 3) {
            flightStatus = "finished";
            return;
        }

        drawProjectiles();

        towerImg.draw(currentPos.x, currentPos.y, rotation);
    }

    /**
     * handles when to generate random drop times and drawings for the projectile. Also removes bombs when they've
     * finished detonating
     */
    private void drawProjectiles() {
        // add bomb if framesElapsed has passed the chosen randomised dropFrame
        if (ShadowDefend.getFramesElapsed() >= latestDropFrame) {
            bombs.add(new Bomb(currentPos, latestDropFrame));
            updateDropFrame();
        }

        Iterator iter = bombs.iterator();
        while (iter.hasNext()) {
            Bomb bomb = (Bomb) iter.next();
            bomb.updateDraw();
            if (bomb.hasDetonated()) {
                iter.remove();
            }
        }
    }

    /**
     * updates plane position every frame so it flies horizontally from the left of the game window (at the y coordinate
     * where it was spawned) down to the right out of the game window again.
     */
    private void flyHorizontal() {

        if (currentPos.x < ShadowDefend.getWindowWidth()) {
            currentPos = new Point(currentPos.x + speed * StatusPanel.getTimescale(), currentPos.y);
        }
        else {
            flightNum++;
            currentPos = new Point(spawnPoint.x, 0);
            rotation.setRotation(Math.toRadians(ANGLE_TO_FACE_DOWN));
        }
    }

    /**
     * updates plane position every frame so it flies vertically from the top of the game window (at the x coordinate
     * where it was spawned) down to the bottom out of the game window again.
     */
    private void flyVertical() {
        if (currentPos.y < ShadowDefend.getWindowHeight()) {
            currentPos = new Point(currentPos.x, currentPos.y + speed * StatusPanel.getTimescale());
        }
        else {
            flightNum++;
            currentPos = new Point(0, spawnPoint.y);
            rotation.setRotation(Math.toRadians(ANGLE_TO_FACE_RIGHT));
        }
    }

    /**
     * chooses a random integer between 0 and max drop time for when to drop a projectile. Thus, updates the dropFrame
     * (frame at which projectile drops).
     */
    private void updateDropFrame() {
        Random random = new Random();
        latestDropFrame = (int) (ShadowDefend.getFramesElapsed()
                + random.nextDouble() * maxDropTime * FRAMES_PER_SECOND);
    }

}
