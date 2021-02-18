import bagel.util.Point;
import java.util.ArrayList;
import java.util.List;

/**
 * child class of slicer
 */
public class SuperSlicer extends Slicer {
    private final static String SLICER_IMG_FILEPATH = "res/images/superslicer.png";
    private static final int NUM_CHILDREN = 2;
    public final static int STARTING_HEALTH = RegularSlicer.STARTING_HEALTH;
    private final static int REWARD = 15;
    public final static double SPEED = 0.75 * RegularSlicer.SPEED;
    public final static int PENALTY = NUM_CHILDREN * RegularSlicer.PENALTY;

    /**
     * constructor
     * @param path
     */
    public SuperSlicer(List<Point> path) {
        super(path, SPEED, SLICER_IMG_FILEPATH, STARTING_HEALTH, REWARD, PENALTY);
    }

    @Override
    public List<Slicer> spawnChildren() {
        List<Slicer> children = new ArrayList<Slicer>();
        List<Point> newPath = new ArrayList<>(path.subList(pathIndex, path.size()));
        newPath.add(0, currentPosition.asPoint());
        for (int i = 0; i < NUM_CHILDREN; i++) {
            children.add(new RegularSlicer(newPath));
        }
        return children;
    }

}