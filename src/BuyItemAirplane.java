import bagel.util.Point;

/**
 * airplane child class of buy item
 */
public class BuyItemAirplane extends BuyItem {
    private static final String ITEM_IMG_FP = "res/images/airsupport.png";
    private static double DRAW_XPOS = 304;
    private static int PRICE = 250;

    public BuyItemAirplane() {
        super(PRICE, ITEM_IMG_FP, DRAW_XPOS);
    }

    @Override
    protected void addTowerToGame(Point clickpoint) {
        ShadowDefend.addPassiveTower(new Airplane(clickpoint));
    }
}