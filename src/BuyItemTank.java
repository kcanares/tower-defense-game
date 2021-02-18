import bagel.util.Point;

/**
 * tank child class of buy item
 */
public class BuyItemTank extends BuyItem {
    private static final String ITEM_IMG_FP = "res/images/tank.png";
    private static double DRAW_XPOS = 64;
    private static int PRICE = 250;

    public BuyItemTank() {
        super(PRICE, ITEM_IMG_FP, DRAW_XPOS);
    }

    @Override
    protected void addTowerToGame(Point clickpoint) {
        ShadowDefend.addActiveTower(new Tank(clickpoint));
    }
}
