import bagel.DrawOptions;
import bagel.Image;
import bagel.util.Point;
import bagel.util.Rectangle;
import java.util.*;

/**
 * handles drawing of an active tower and choosing targets to hit. Also contains utility methods for when a something is
 * intersect with it.
 */
abstract public class ActiveTower {
    private static final double FRAMES_PER_MS = 0.06;

    private Point centrePos;
    private DrawOptions rotationState;
    private List<Projectile> projectiles;
    private String projectileImgFP;
    private int effectRadius;
    private int cooldown;
    private Image towerImg;
    private double coolingStart;
    private int damage;

    /**
     * constructor for child class
     * @param effectRadius
     * @param damage
     * @param cooldown
     * @param towerImgFP
     * @param projectileImgFP
     * @param centrePosition
     */
    public ActiveTower(int effectRadius, int damage, int cooldown, String towerImgFP, String projectileImgFP,
                       Point centrePosition) {
        this.effectRadius = effectRadius;
        this.damage = damage;
        this.cooldown = cooldown;
        this.centrePos = centrePosition;

        towerImg = new Image(towerImgFP);
        this.projectileImgFP = projectileImgFP;

        projectiles = new ArrayList<Projectile>();
        coolingStart = 0;
        rotationState = new DrawOptions();
    }

    /**
     * updates tower's drawing. triggers the function to look for a target once the cooldown period is finished
     */
    public void updateTowerDraw() {
        if (WaveSpawner.getLivingSlicers() != null &&
                (ShadowDefend.getFramesElapsed() >= coolingStart + cooldown * FRAMES_PER_MS)) {
            addProjectileTarget(WaveSpawner.getLivingSlicers());
            coolingStart = ShadowDefend.getFramesElapsed();
        }
        updateProjectileDraw();
        towerImg.draw(centrePos.x, centrePos.y, rotationState);
    }

    /**
     * updates all projectiles, removes them if they've hit their target. also updates tower's rotation based on the
     * projectile being drawn
     */
    private void updateProjectileDraw() {
        Iterator iter = projectiles.iterator();
        while (iter.hasNext()) {
            Projectile projectile = (Projectile) iter.next();
            projectile.updateDraw();
            rotationState = rotationState.setRotation(projectile.rotationAngle());
            if (projectile.isFinished()) {
                iter.remove();
            }
        }
    }

    /**
     *
     * @param point
     * @return true if a point is intersecting with the tower's bounding box
     */
    public boolean isIntersectingBox(Point point) {
        if (towerImg.getBoundingBoxAt(centrePos).intersects(point)) {
            return true;
        }
        else {
            return false;
        }
    }

    /**
     * adds a projectile to the list of projectiles the tower has shot. This projectile is honed for a particular slicer
     * verified to be in the effect radius.
     * @param slicers
     */
    private void addProjectileTarget(List<Slicer> slicers) {
        for (Slicer slicer : slicers) {
            Point projectedSlicerPos = slicer.positionAfterFrames(0.0);
            if (withinEffectRadius(projectedSlicerPos)) {
                Projectile newProjectile = new Projectile(slicer, centrePos, damage, projectileImgFP);
                projectiles.add(newProjectile);
                return;
            }
        }
    }

    /**
     *
     * @param point
     * @return true if a point is within the effect radius of the tower
     */
    private boolean withinEffectRadius(Point point) {
        // multiplying effectRadius by 2 to get the full side length of the rectangle
        Rectangle effectRectangle = new Rectangle(centrePos.x - effectRadius,
                centrePos.y - effectRadius, effectRadius * 2, effectRadius * 2);

        if (effectRectangle.intersects(point)) {
            return true;
        }
        else {
            return false;
        }
    }

}
