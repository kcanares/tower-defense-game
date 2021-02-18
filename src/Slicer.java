import bagel.DrawOptions;
import bagel.Image;
import bagel.util.Point;
import bagel.util.Vector2;

import java.util.ArrayList;
import java.util.List;

/**
 * The Slicer class represents an individual slicer. Manages the slicer's movement in terms of moving from one starting
 * position to a target position in a given path using vector addition. Handles individual slicer's drawing on the user
 * interface as it draws movement and rotation.
 */
abstract public class Slicer {
    public void deductHealth(int healthDeduction) {
        health = health - healthDeduction;
    }

    private int health;
    private int penalty;
    private int reward;
    private Image slicerImage;

    public static double getSpeed() {
        return speed;
    }

    private static double speed;

    protected List<Point> path;
    protected int pathIndex;

    private Vector2 startPosition;
    private Vector2 targetPosition;

    protected Vector2 currentPosition;
    private Vector2 currentDirection;

    private DrawOptions slicerRotationState;

    public String getStatus() {
        return status;
    }

    private String status;

    /**
     * constructor
     * @param path
     * @param speed
     * @param slicerImgFp
     * @param health
     * @param reward
     * @param penalty
     */
    public Slicer(List<Point> path, double speed, String slicerImgFp, int health, int reward, int penalty) {
        slicerImage = new Image(slicerImgFp);

        this.path = path;
        pathIndex = 0;

        this.speed = speed;
        this.health = health;
        this.reward = reward;

        targetPosition = currentPosition = startPosition = path.get(pathIndex).asVector();
        currentDirection = new Vector2();
        slicerRotationState = new DrawOptions();
        this.penalty = penalty;
        status = "alive";
    }

    /**
     * Updates the drawing by getting a calculated unit direction vector multiplied by a magnitude of speed and adding
     * that to the current position - this is drawn to simulate movement. If enough vectors are added so it reaches the
     * target position, the direction and target are updated.
     * updates status after updating drawing position (to indicate whether it's finished the path)
     */
    public void updateDraw() {
        // changes slicer's direction if it's reached the target point in the path given that there's still a path left
        // to follow
        if (startPosition.sub(currentPosition).length() >= targetPosition.sub(startPosition).length()
                && pathIndex < path.size() - 1) {
            changeDirection();
            updateRotation();
        }

        // simulates movement along a polyline by adding a vector to the current position. The added vector is the unit
        // direction vector pointing to the target multiplied by speed
        currentPosition = currentPosition.add(currentDirection.mul(speed * StatusPanel.getTimescale()));

        slicerImage.draw(currentPosition.x, currentPosition.y, slicerRotationState);
        updateStatus();
    }

    /**
     * looks at all of the slicers parameters that affect its living status (position, health). Updates status and game
     * accordingly
     */
    private void updateStatus() {
        if (startPosition.sub(currentPosition).length() >= targetPosition.sub(startPosition).length()
                && pathIndex == path.size() - 1) {
            StatusPanel.deductPlayerLife(penalty);
            status = "successful";
        } else if (health <= 0) {
            BuyPanel.addPlayerMoney(reward);
            status = "dead";
        }
    }

    /**
     *
     * @return children that the slicer would spawn (if it spawns any). The path these children will take is the rest
     * of the dead slicer's path starting from where the slicer died.
     */
    public abstract List<Slicer> spawnChildren();

    /**
     * @param frames
     * @return the point a slicer arrives to after it moves for a given amount of frames
     */
    public Point positionAfterFrames(Double frames) {
        double currFrame = 0;
        int projectedPathIndex = this.pathIndex;

        Vector2 projectedTargetPosition = this.targetPosition;
        Vector2 projectedPosition = this.currentPosition;
        Vector2 projectedStart = projectedPosition;
        Vector2 projectedDirection = this.currentDirection;

        while (currFrame < frames) {
            projectedPosition = projectedPosition.add(projectedDirection.mul(speed * StatusPanel.getTimescale()));
            if (projectedStart.sub(projectedPosition).length() >= projectedTargetPosition.sub(projectedStart).length()
                    && projectedPathIndex < path.size() - 1) {
                projectedPathIndex++;
                projectedPosition = projectedStart = projectedTargetPosition;
                projectedTargetPosition = path.get(projectedPathIndex).asVector();
                projectedDirection = projectedTargetPosition.sub(projectedStart).normalised();
            }
            currFrame++;
        }
        return projectedPosition.asPoint();
    }


    /**
     *  The current and start position is updated to be the previous target position, the target moves on to the next
     *  point. The direction vector will be updated to be a unit vector pointing from the start position to the target.
     */
    private void changeDirection() {
        pathIndex++;

        currentPosition = startPosition = targetPosition;
        targetPosition = path.get(pathIndex).asVector();

        currentDirection = targetPosition.sub(startPosition).normalised();
    }

    /**
     * The rotation state updated to be the angle that the direction vector currently makes from the x-axis.
     */
    private void updateRotation() {
        slicerRotationState = slicerRotationState.setRotation(Math.atan2(currentDirection.y,currentDirection.x));
    }

    /**
     * @param point
     * @return true if a given point is in the slicer's bounding box
     */
    public boolean inBoundingBox(Point point) {
        return slicerImage.getBoundingBoxAt(currentPosition.asPoint()).intersects(point);
    }
}