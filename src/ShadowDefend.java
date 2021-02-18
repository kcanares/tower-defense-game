import bagel.*;
import bagel.map.TiledMap;
import bagel.Window;
import bagel.util.Point;

import java.io.FileNotFoundException;
import java.util.*;

public class ShadowDefend extends AbstractGame {
    private final static String MAP1_FILEPATH = "res/levels/1.tmx";
    private final static String MAP2_FILEPATH = "res/levels/2.tmx";
    private final static String WAVE_INFO_FP = "res/levels/waves.txt";
    private final static int MAX_LEVELS = 1;

    private static TiledMap map;
    private WaveSpawner waveSpawner;

    public static double getFramesElapsed() {
        return framesElapsed;
    }

    private static double framesElapsed;
    private BuyPanel buyPanel;
    private StatusPanel statusPanel;
    private PointDrawer pointDrawer;
    private static int windowWidth;
    private static int windowHeight;

    private List<List<HashMap>> waveEventList;

    public static void addActiveTower(ActiveTower activeTower) {
        activeTowers.add(activeTower);
    }

    public static List<ActiveTower> getActiveTowers() {
        return activeTowers;
    }

    private static List<ActiveTower> activeTowers;

    public static void addPassiveTower(PassiveTower passiveTower) {
        passiveTowers.add(passiveTower);
    }

    private static List<PassiveTower> passiveTowers;

    public static int getWindowWidth() {
        return windowWidth;
    }

    public static int getWindowHeight() {
        return windowHeight;
    }

    public static List<Point> getPath() {
        return map.getAllPolylines().get(0);
    }
    private List<String> mapFPs;
    private int level;


    /**
     * Entry point for Bagel game
     *
     * Explore the capabilities of Bagel: https://people.eng.unimelb.edu.au/mcmurtrye/bagel-doc/
     */
    public static void main(String[] args) throws FileNotFoundException {
        // Create new instance of game and run it
        new ShadowDefend().run();
    }

    /**
     * Setup the game
     */
    public ShadowDefend() throws FileNotFoundException {
        this.mapFPs = new ArrayList<String>(Arrays.asList(MAP1_FILEPATH, MAP2_FILEPATH));
        level = 0;
        map = new TiledMap(mapFPs.get(level));
        this.windowHeight = map.getHeight();
        this.windowWidth = map.getWidth();
        framesElapsed = 0;

        buyPanel = new BuyPanel();
        pointDrawer = new PointDrawer();
        statusPanel = new StatusPanel(map.getHeight());
        activeTowers = new ArrayList<ActiveTower>();
        passiveTowers = new ArrayList<PassiveTower>();
        processTextFile();

    }

    /**
     * reads in the textfile for the level
     */
    private void processTextFile() throws FileNotFoundException {
        WaveEventInfoProcessor waveEventInfoProcessor = new WaveEventInfoProcessor(WAVE_INFO_FP);
        waveEventList = waveEventInfoProcessor.getWaveEventList();
    }

    /**
     * creates new instances of every object in the game to 'reset' the game. Basically starts a new game with a
     * different TiledMap.
     * @throws FileNotFoundException
     */
    private void lvlUp() throws FileNotFoundException {
        waveSpawner = null;
        statusPanel.setWaveNum(1);
        level++;
        map = new TiledMap(mapFPs.get(level));
        buyPanel = new BuyPanel();
        pointDrawer = new PointDrawer();
        statusPanel = new StatusPanel(map.getHeight());
        activeTowers = new ArrayList<ActiveTower>();
        passiveTowers = new ArrayList<PassiveTower>();
        processTextFile();
    }

    /**
     * Updates the game state approximately 60 times a second. Reads keyboard input: 'L' - increases slicer wave speed;
     * 'K' - decreases slicer wave speed; 'S' - spawns new slicer wave (given that none have spawned yet).
     * @param input The input instance which provides access to keyboard/mouse state information.
     */
    @Override
    protected void update(Input input) {
        map.draw(0, 0, 0, 0, windowWidth, windowHeight);

        if (statusPanel.getPlayerLives() <= 0) {
            Window.close();
        }

        // timescale controls
        if (input.wasPressed(Keys.L)) {
            statusPanel.increaseTimescale();
        }

        if (input.wasPressed(Keys.K)) {
            statusPanel.decreaseTimescale();
        }

        // update enemy waves
        if (input.wasPressed(Keys.S) && waveSpawner == null) {
            System.out.println("CHECK");
            waveSpawner = new WaveSpawner(map.getAllPolylines().get(0),
                                        waveEventList.get(statusPanel.getWaveNum() - 1), statusPanel.getWaveNum());
        }
        else if (waveSpawner == null) {
            statusPanel.setStatus("Awaiting Start");
        }
        else if (waveSpawner != null) {
            waveSpawner.executeWaveEvent(framesElapsed);
            statusPanel.setStatus("Wave In Progress");
            if (waveSpawner.isWaveFinished()) {
                // start next wave if there are still waves left
                statusPanel.overrideStatus("Awaiting Start");
                if (statusPanel.getWaveNum() < waveEventList.size()) {
                    if (input.wasPressed(Keys.S)) {
                        statusPanel.increaseWaveNum();
                        waveSpawner = new WaveSpawner(map.getAllPolylines().get(0),
                                waveEventList.get(statusPanel.getWaveNum() - 1), statusPanel.getWaveNum());
                    }
                }
                else if (level == MAX_LEVELS){
                    statusPanel.overrideStatus("Winner!");
                }
                else {
                    try {
                        lvlUp();
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                }
            }
        }

        // update towers
        for (ActiveTower tower : activeTowers) {
            if (waveSpawner != null) {
                tower.updateTowerDraw();
            }
            tower.updateTowerDraw();
        }

        // update passive towers
        Iterator<PassiveTower> iter = passiveTowers.iterator();
        while (iter.hasNext()) {
            PassiveTower passiveTower = iter.next();
            if (passiveTower.getFlightStatus() == "in flight") {
                passiveTower.updateDraw();
            } else if (passiveTower.getFlightStatus() == "finished") {
                iter.remove();
            }
        }

        // update panels
        pointDrawer.locateClick(input);
        buyPanel.updateDraw(input, map);
        statusPanel.updateDraw();

        framesElapsed = framesElapsed + statusPanel.getTimescale();
    }
}
