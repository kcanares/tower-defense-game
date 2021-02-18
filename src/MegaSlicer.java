import bagel.util.Point;
import java.util.ArrayList;
import java.util.List;

/**
 * represents megaslicer - a child class of slicer
 */
public class MegaSlicer extends Slicer {
    private final static String SLICER_IMG_FILEPATH = "res/images/megaslicer.png";
    private static final int NUM_CHILDREN = 2;
    private final static int STARTING_HEALTH = 2 * SuperSlicer.STARTING_HEALTH;
    private final static int REWARD = 10;
    private final static double SPEED = SuperSlicer.SPEED;
    public final static int PENALTY = NUM_CHILDREN * SuperSlicer.PENALTY;

    public MegaSlicer(List<Point> path) {
        super(path, SPEED, SLICER_IMG_FILEPATH, STARTING_HEALTH, REWARD, PENALTY);
    }

    @Override
    public List<Slicer> spawnChildren() {
        List<Slicer> children = new ArrayList<Slicer>();
        List<Point> newPath = new ArrayList<>(path.subList(pathIndex, path.size()));
        newPath.add(0, currentPosition.asPoint());
        for (int i = 0; i < NUM_CHILDREN; i++) {
            children.add(new SuperSlicer(newPath));
        }
        return children;
    }

}