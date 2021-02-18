import bagel.Image;
import bagel.util.Point;
import bagel.util.Vector2;

/**
 * represents projectile that tanks shoot. Handles visual rendering and dealing damage.
 */
public class Projectile {
    private Image projectileImg;
    private Slicer slicerTarget;
    private Point projectilePos;
    private Point tankPos;
    private final int PROJECTILE_SPEED = 10;
    private Vector2 direction;
    private int damage;

    public boolean isFinished() {
        return projectileFinished;
    }

    private boolean projectileFinished;

    /**
     * constructor
     * @param slicer
     * @param tankPos
     * @param damage
     * @param imgFP
     */
    public Projectile(Slicer slicer, Point tankPos, int damage, String imgFP) {
        this.slicerTarget = slicer;
        this.damage = damage;
        this.tankPos = this.projectilePos = tankPos;
        projectileImg = new Image(imgFP);
        direction = new Vector2();
        updateProjectilePos();
        projectileFinished = false;
    }

    /**
     * updates visual representation in the game. Stops drawing when it's reached its target, thus deals damage and
     * sets projectileFinished as true
     */
    public void updateDraw() {
        updateProjectilePos();
        if (reachedTarget() && projectileFinished == false) {
            dealDamage();
            projectileFinished = true;
        }
        else {
            projectileImg.draw(projectilePos.x, projectilePos.y);
        }
    }

    /**
     * updates projectile position according to where the targeted slicer will be after a given number of frames
     */
    private void updateProjectilePos() {
        Point targetPos = slicerTarget.positionAfterFrames(PROJECTILE_SPEED/slicerTarget.getSpeed());
        direction = targetPos.asVector().sub(tankPos.asVector()).normalised();
        projectilePos = projectilePos.asVector().add(
                direction.mul(PROJECTILE_SPEED * StatusPanel.getTimescale())).asPoint();
    }

    /**
     * @return true if the projectile's position has reached the bounding box of the targeted slicer
     */
    public boolean reachedTarget() {
        return slicerTarget.inBoundingBox(projectilePos);
    }

    /**
     * @return active tower's rotation angle in relation to the projectile that it fired
     */
    public double rotationAngle() {
        // rotate to the right (90 degrees) to compensate for changing vector addition
        return Math.atan2(direction.y, direction.x) + Math.toRadians(90);
    }

    /**
     * deducts health from the slicer that it's targeting
     */
    public void dealDamage() {
        slicerTarget.deductHealth(damage);
    }
}
