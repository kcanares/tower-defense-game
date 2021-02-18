import bagel.util.Point;

import java.util.ArrayList;
import java.util.List;

public class ApexSlicer extends Slicer {
    private final static String SLICER_IMG_FILEPATH = "res/images/apexslicer.png";
    private static final int NUM_CHILDREN = 4;
    private final static int STARTING_HEALTH = 25 * RegularSlicer.STARTING_HEALTH;
    private final static int REWARD = 150;
    private final static double SPEED = SuperSlicer.SPEED;
    public final static int PENALTY = NUM_CHILDREN * MegaSlicer.PENALTY;

    /**
     * @param path
     */
    public ApexSlicer(List<Point> path) {
        super(path, SPEED, SLICER_IMG_FILEPATH, STARTING_HEALTH, REWARD, PENALTY);
    }

    /**
     * @return children that the slicer would spawn
     */
    @Override
    public List<Slicer> spawnChildren() {
        List<Slicer> children = new ArrayList<Slicer>();
        List<Point> newPath = new ArrayList<>(path.subList(pathIndex, path.size()));
        newPath.add(0, currentPosition.asPoint());
        for (int i = 0; i < NUM_CHILDREN; i++) {
            children.add(new MegaSlicer(newPath));
        }
        return children;
    }

}