import bagel.*;
import bagel.map.TiledMap;
import bagel.util.Colour;
import bagel.util.Point;
import org.lwjgl.system.CallbackI;

/**
 * abstract class of an item that can be purchased in the buy panel. Handles rendering of these items
 */
abstract public class BuyItem {
    private static final String FONT_FP = "res/fonts/DejaVuSans-Bold.ttf";
    private static final int FONT_SIZE = 24;
    private static final int TXT_OFFSET = 25;
    private static final int IMG_OFFSET_Y = 10;

    private Font priceText;
    private DrawOptions textColour;
    private Image itemImg;
    private int price;
    private double drawXpos;
    private double drawYpos;
    private String status;

    /**
     * constructors
     * @param price
     * @param itemImgFP
     * @param drawXpos
     */
    public BuyItem(int price, String itemImgFP, double drawXpos) {
        this.price = price;
        this.itemImg = new Image(itemImgFP);
        this.drawXpos = drawXpos;
        this.drawYpos = BuyPanel.getPanelHeight() / 2 - IMG_OFFSET_Y; // draw image 10px above panel's center

        priceText = new Font(FONT_FP, FONT_SIZE);
        textColour = new DrawOptions();
        status = "Not Placing";
    }

    /**
     * updates text and image drawings.
     * @param input
     * @param map
     */
    public void updateDraw(Input input, TiledMap map) {
        itemImg.draw(drawXpos, drawYpos);
        String priceStr = "$" + Integer.toString(price);
        priceText.drawString(priceStr, drawXpos - TXT_OFFSET, itemImg.getHeight() + TXT_OFFSET, textColour);

        if (status == "Placing") {
            drawImageAtMouse(input, map);
        }
    }

    /**
     * renders copy of the panel at the user’s cursor.
     *
     * A tower cannot be placed on a coordinate if the center of the
     * tower to place intersects with a panel, the bounding box of another tower, or with a blocked tile. If this occurs
     * the image won't be rendered
     * @param input
     * @param map
     */
    public void drawImageAtMouse(Input input, TiledMap map) {
        if (!(map.hasProperty((int) input.getMousePosition().x, (int) input.getMousePosition().y, "blocked")
        || BuyPanel.getBoundingBox().intersects(input.getMousePosition())
        || StatusPanel.getBoundingBox().intersects(input.getMousePosition()))) {

            // ensures it's not intersecting any towers
            for (ActiveTower tower : ShadowDefend.getActiveTowers()) {
                if (tower.isIntersectingBox(input.getMousePosition())) {
                    return;
                }
            }

            itemImg.draw(input.getMousePosition().x, input.getMousePosition().y);

            if (input.wasPressed(MouseButtons.LEFT)) {
                addTowerToGame(new Point(input.getMousePosition().x, input.getMousePosition().y));
                status = "Not Placing";
                StatusPanel.overrideStatus(""); // override status to go back to a lower status
            }
        }
    }

    protected abstract void addTowerToGame(Point clickpoint);

    /**
     * changes colour option to red if the player has less money than the price of the item. The colour is green
     * otherwise
     * @param playerMoney
     */
    public void changeColour(int playerMoney) {
        if (playerMoney < price) {
            textColour.setBlendColour(Colour.RED);
        } else {
            textColour.setBlendColour(Colour.GREEN);
        }
    }

    /**
     * checks if a point (presumably where the user has clicked) is within the image's bounding box). If it is and the
     * player has enough money to purchase the item, then the buyItem's status is now marked as 'placing' and player
     * money is spent according to how much the item costs.
     * @param clickpoint
     */
    public void checkPointWithinDrawing(Point clickpoint, int playerMoney) {
        if (itemImg.getBoundingBoxAt(new Point(drawXpos, drawYpos)).intersects(clickpoint) && playerMoney >= price) {
            status = "Placing";
            StatusPanel.setStatus(status);
            BuyPanel.spendPlayerMoney(price);
        }
    }
}
