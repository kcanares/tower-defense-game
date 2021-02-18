import bagel.util.Point;

/**
 * super tank child class of buy item
 */
public class BuyItemSuperTank extends BuyItem {
    private static final String ITEM_IMG_FP = "res/images/supertank.png";
    private static double DRAW_XPOS = 184;
    private static int PRICE = 600;

    public BuyItemSuperTank() {
        super(PRICE, ITEM_IMG_FP, DRAW_XPOS);
    }

    @Override
    protected void addTowerToGame(Point clickpoint) {
        ShadowDefend.addActiveTower(new SuperTank(clickpoint));
    }
}
