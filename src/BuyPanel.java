import bagel.Font;
import bagel.Image;
import bagel.Input;
import bagel.MouseButtons;
import bagel.map.TiledMap;
import bagel.util.Point;
import bagel.util.Rectangle;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * represents panel at the top of the screen where players can buy items. Keeps track of player money and things it
 * can purchase with it.
 */
public class BuyPanel {
    private final static String PANEL_IMG_FP = "res/images/buypanel.png";
    private final static int INITIAL_MONEY = 500;
    
    private final static int PANEL_RENDER_COORDS_X = 0;
    private final static int PANEL_RENDER_COORDS_Y = 0;

    // money text constants
    private final static int MONEY_FONT_SIZE = 48;
    private final static Point MONEY_RENDER_COORD = new Point(842.80, 56.80);

    // instruction text constants
    private final static int INSTRUCTIONS_FONT_SIZE = 12;
    private final static Point TXT_RENDER_COORD = new Point(550, 20.80);
    private final static String FONT_FILEPATH = "res/fonts/DejaVuSans-Bold.ttf";
    private final static String INSTRUCTIONS =  "Key binds:\n\n" +
                                                "S - Start Wave\n" +
                                                "L - Increase Timescale\n" +
                                                "K - Decrease Timescale";

    private static int playerMoney;
    private String playerMoneyStr;
    private Font keybindText;
    private Font playerMoneyText;

    public static double getPanelHeight() {
        return panel.getHeight();
    }

    private static Image panel;
    private List<BuyItem> buyItems;

    public static void spendPlayerMoney(int money) {
        BuyPanel.playerMoney = playerMoney - money;
    }

    public static void addPlayerMoney(int money) {
        BuyPanel.playerMoney = playerMoney + money;
    }

    public static Rectangle getBoundingBox() {
        return panel.getBoundingBox();
    }

    /**
     * updates everything affected by a change in playerMoney. This includes: string representation of money, colour
     * of BuyItem prices.
     * @param playerMoney
     */
    public void formatMoneyDraw(int playerMoney) {
        formatMoneyAsString();
        for (BuyItem buyItem : buyItems) {
            buyItem.changeColour(playerMoney);
        }
    }

    /**
     * constructor
     */
    public BuyPanel() {
        panel = new Image(PANEL_IMG_FP);
        buyItems = new ArrayList<BuyItem>();
        Collections.addAll(buyItems, new BuyItemTank(), new BuyItemSuperTank(), new BuyItemAirplane());

        playerMoney = INITIAL_MONEY;
        playerMoneyStr = Integer.toString(playerMoney);
        formatMoneyDraw(playerMoney);

        keybindText = new Font(FONT_FILEPATH, INSTRUCTIONS_FONT_SIZE);
        playerMoneyText = new Font(FONT_FILEPATH, MONEY_FONT_SIZE);
    }

    /**
     * updates all drawings in the panel
     * @param input the player's input
     */
    public void updateDraw(Input input, TiledMap map) {
        formatMoneyDraw(playerMoney);

        panel.drawFromTopLeft(PANEL_RENDER_COORDS_X, PANEL_RENDER_COORDS_Y);
        keybindText.drawString(INSTRUCTIONS, TXT_RENDER_COORD.x, TXT_RENDER_COORD.y);
        playerMoneyText.drawString(playerMoneyStr, MONEY_RENDER_COORD.x, MONEY_RENDER_COORD.y);

        if (input.wasPressed(MouseButtons.LEFT) && StatusPanel.getStatus() != "Placing") {
            checkPurchaseClick(input.getMousePosition());
        }

        for (BuyItem buyItem : buyItems) {
            buyItem.updateDraw(input, map);
        }
    }

    /**
     * check if the user has clicked on any of the buyItem images
     * @param clickLocation
     */
    private void checkPurchaseClick(Point clickLocation) {
        for (BuyItem buyItem : buyItems) {
            buyItem.checkPointWithinDrawing(clickLocation, playerMoney);
        }
    }

    /**
     * Formats the player money from an int to a String in a form like: $nnn,nnn where n is an integer.
     */
    private void formatMoneyAsString() {
        StringBuilder strBuilder = new StringBuilder();
        playerMoneyStr = Integer.toString(playerMoney);
        int i;
        for (i = playerMoneyStr.length() - 1; i >= 0; i--) {
            // commas are placed after 3 digits when representing ints as money
            if (strBuilder.length() % 3 == 0 && strBuilder.length() != 0) {
                strBuilder.insert(0, ",");
            }
            strBuilder.insert(0, playerMoneyStr.charAt(i));
        }

        strBuilder.insert(0, "$");
        playerMoneyStr = strBuilder.toString();
    }
}
