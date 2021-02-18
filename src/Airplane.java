import bagel.util.Point;

public class Airplane extends PassiveTower{
    private final static int SPEED = 5;
    private final static int DROP_TIME = 3;
    private final static String TOWER_IMG_FP = "res\\images\\airsupport.png";

    /**
     * constructor
     * @param clickpoint
     */
    public Airplane(Point clickpoint) {
        super(DROP_TIME, SPEED, TOWER_IMG_FP, clickpoint);
    }
}
