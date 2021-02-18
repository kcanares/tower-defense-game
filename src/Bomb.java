import bagel.Image;
import bagel.util.Point;
import bagel.util.Rectangle;
import java.util.ArrayList;
import java.util.List;

/**
 * represents bomb class that passive towers can drop
 */
public class Bomb {
    private static final int DETONATION_TIME = 2;
    private static final int FRAMES_PER_SECOND = 60;
    private static final String IMG_FP = "res/images/explosive.png";
    private Point pos;
    private static final int EFFECT_RADIUS = 200;
    private final static int DAMAGE = 500;

    public boolean hasDetonated() {
        return detonated;
    }

    private boolean detonated;
    private double detonationFrame;
    private Image projectileImg;

    /**
     * constructor
     * @param pos
     * @param frameDropped
     */
    public Bomb(Point pos, double frameDropped) {
        projectileImg = new Image(IMG_FP);
        detonationFrame = frameDropped + DETONATION_TIME * FRAMES_PER_SECOND;
        this.pos = pos;
        detonated = false;
    }

    /**
     * updates drawn representation in the game. So, if it's time to detonate, it will not be drawn, the detonation
     * status updated to true, and damage is dealt.
     */
    public void updateDraw() {
        if (ShadowDefend.getFramesElapsed() < detonationFrame) {
            projectileImg.draw(pos.x, pos.y);
        } else if (detonated == false) {
            detonated = true;
            dealDamage();
        }
    }

    /**
     *
     * @param point
     * @return true if a point is within the effect radius of the bomb
     */
    private boolean withinEffectRadius(Point point) {
        // multiplying effectRadius by 2 to get the full side length of the rectangle
        Rectangle effectRectangle = new Rectangle(pos.x - EFFECT_RADIUS,
                pos.y - EFFECT_RADIUS, EFFECT_RADIUS * 2, EFFECT_RADIUS * 2);


        if (effectRectangle.intersects(point)) {
            return true;
        }
        else {
            return false;
        }
    }

    /**
     * @return a list of slicers that the bomb will damage
     */
    private List<Slicer> findSlicersAffected() {
        List<Slicer> slicersAffected = new ArrayList<Slicer>();
        for (Slicer slicer : WaveSpawner.getLivingSlicers()) {
            Point projectedSlicerPos = slicer.positionAfterFrames(0.0);
            if (withinEffectRadius(projectedSlicerPos)) {
                slicersAffected.add(slicer);
            }
        }
        return slicersAffected;
    }

    /**
     * deducts health from the slicers that are affected by the bomb
     */
    private void dealDamage() {
        if (WaveSpawner.getLivingSlicers() == null) {
            return;
        }

        List<Slicer> slicersAffected = findSlicersAffected();
        for (Slicer slicer : slicersAffected) {
            slicer.deductHealth(DAMAGE);
        }
    }
}
