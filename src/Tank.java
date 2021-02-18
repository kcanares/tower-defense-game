import bagel.util.Point;

/**
 * child class of active tower
 */
public class Tank extends ActiveTower {
    private static final int EFFECT_RADIUS = 100;
    public static final int DAMAGE = 1;
    private static final int COOLDOWN = 1000;
    private static final String TOWER_IMG_FILEPATH = "res/images/tank.png";
    private static final String PROJECTILE_IMG_FILEPATH = "res/images/tank_projectile.png";

    public Tank(Point centrePosition) {
        super(EFFECT_RADIUS, DAMAGE, COOLDOWN, TOWER_IMG_FILEPATH, PROJECTILE_IMG_FILEPATH, centrePosition);
    }
}

