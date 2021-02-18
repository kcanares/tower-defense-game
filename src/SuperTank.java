import bagel.util.Point;

/**
 * child class of active tower
 */
public class SuperTank extends ActiveTower {
    private static final int EFFECT_RADIUS = 100;
    private static final int DAMAGE = 3 * Tank.DAMAGE;
    private static final int COOLDOWN = 1000;
    private static final String TOWER_IMG_FILEPATH = "res/images/supertank.png";
    private static final String PROJECTILE_IMG_FILEPATH = "res/images/supertank_projectile.png";

    /**
     * constructor
     * @param centrePosition
     */
    public SuperTank(Point centrePosition) {
        super(EFFECT_RADIUS, DAMAGE, COOLDOWN, TOWER_IMG_FILEPATH, PROJECTILE_IMG_FILEPATH, centrePosition);
    }
}

