import bagel.DrawOptions;
import bagel.Font;
import bagel.Image;
import bagel.util.Colour;
import bagel.util.Point;
import bagel.util.Rectangle;

public class StatusPanel {
    private static final String PANEL_IMG_FP = "res/images/statuspanel.png";
    private static final String FONT_FP = "res/fonts/DejaVuSans-Bold.ttf";
    private static final int FONT_SIZE = 18;
    private static final int WAVE_TXT_XPOS = 5;
    private static final int TIMESCALE_TXT_XPOS = 200;
    private static final int STATUS_TXT_XPOS = TIMESCALE_TXT_XPOS + 200;
    private static final int LIVES_TXT_XPOS = STATUS_TXT_XPOS + 500;
    private static final int STARTING_LIVES = 25;
    private static final int STARTING_TIMESCALE = 1;

    private static Point panelPos;
    private double txtYpos;
    public int getPlayerLives() {
        return playerLives;
    }

    public static void deductPlayerLife(int penalty) {
        StatusPanel.playerLives = playerLives - penalty;
    }

    private static int playerLives;
    private static Image panel;

    private Font waveText;
    private Font timescaleText;
    private Font statusText;
    private Font livesText;
    private DrawOptions timescaleTextColour;

    public static int getWaveNum() {
        return waveNum;
    }

    public void setWaveNum(int waveNum) {
        this.waveNum = waveNum;
    }

    public void increaseWaveNum() {
        waveNum++;
    }

    private static int waveNum;

    public static double getTimescale() {
        return timescale;
    }

    public void increaseTimescale() {
        timescaleTextColour.setBlendColour(Colour.GREEN);
        timescale++;
    }

    private static double timescale;

    public static String getStatus() {
        return status;
    }

    public static Rectangle getBoundingBox() {
        return panel.getBoundingBoxAt(new Point(panelPos.x + panel.getWidth()/2,
                                                panelPos.y + panel.getHeight()/2));
    }

    /**
     * Sets status according to precedence in the. (Winner! > Placing > Wave In Progress > Awaiting Start)
     * @param status
     */
    public static void setStatus(String status) {
        if (status == "Winner!") {
            StatusPanel.status = status;
        }
        else if (status == "Placing" && StatusPanel.status != "Winner!") {
            StatusPanel.status = status;
        }
        else if (status == "Wave In Progress" && !(StatusPanel.status == "Placing" || StatusPanel.status == "Winner!")){
            StatusPanel.status = status;
        }
        else if (status == "Awaiting Start"
                && !(StatusPanel.status == "Placing"
                || StatusPanel.status == "Winner!" || StatusPanel.status == "Wave In Progress")) {
            StatusPanel.status = status;
        }
    }

    /**
     * updates status regardless of what the order the status is meant to be in
     * @param status
     */
    public static void overrideStatus(String status) {
        StatusPanel.status = status;
    }

    private static String status;

    /**
     * decreases timescale by 1. Note that the timescale cannot go beneath 1.
     */
    public void decreaseTimescale() {
        if (timescale > STARTING_TIMESCALE) {
            timescale--;
        }

        if (timescale == STARTING_TIMESCALE) {
            timescaleTextColour.setBlendColour(Colour.WHITE);
        }
    }

    /**
     * constructor
     * @param windowHeight
     */
    public StatusPanel(int windowHeight) {
        panel = new Image(PANEL_IMG_FP);
        panelPos = new Point(0, windowHeight - panel.getHeight());
        txtYpos = panelPos.y + 20;  // 20 is an arbitrary offset for the position and chosen by trial and error

        waveText = timescaleText = statusText = livesText = new Font(FONT_FP, FONT_SIZE);

        timescaleTextColour = new DrawOptions();


        timescale = STARTING_TIMESCALE;
        waveNum = 1;
        playerLives = STARTING_LIVES;
    }

    /**
     * updates all the drawings in the panel
     */
    public void updateDraw() {
        panel.drawFromTopLeft(panelPos.x, panelPos.y);
        waveText.drawString("Wave: " + waveNum, WAVE_TXT_XPOS, txtYpos);
        timescaleText.drawString("Time Scale: " +  timescale, TIMESCALE_TXT_XPOS, txtYpos, timescaleTextColour);
        statusText.drawString("Status: " + status, STATUS_TXT_XPOS, txtYpos);
        livesText.drawString("Lives: " + playerLives, LIVES_TXT_XPOS, txtYpos);
    }
}
