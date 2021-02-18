import bagel.util.Point;
import java.util.List;

/**
 * RegularSlicer is the very basic slicer and a child class of Slicer
 */
public class RegularSlicer extends Slicer {
    private final static String SLICER_IMG_FILEPATH = "res/images/slicer.png";
    public final static int STARTING_HEALTH = 1;
    private final static int REWARD = 2;
    public final static int SPEED = 2;
    private final static int NUM_CHILDREN = 0;
    public final static int PENALTY = 1;

    /**
     * constructor
     * @param path
     */
    public RegularSlicer(List<Point> path) {
        super(path, SPEED, SLICER_IMG_FILEPATH, STARTING_HEALTH, REWARD, PENALTY);
    }

    @Override
    public List<Slicer> spawnChildren() {
        return null;
    }
}