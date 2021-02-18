import bagel.Input;
import bagel.MouseButtons;
import bagel.util.Point;

/**
 * for debugging purposes. Prints out where you've clicked on the screen. Feel free to ignore
 */
public class PointDrawer {
    private Point latestPos;
    Input latestInput;
    public PointDrawer() {
        Point latestPos = new Point();
    }

    public void locateClick(Input input) {
        latestInput = input;
        if (latestInput.wasPressed(MouseButtons.LEFT)) {
            latestPos = latestInput.getMousePosition();
            System.out.print("clicked on: ");
            System.out.println(latestPos);
        }
    }
}
