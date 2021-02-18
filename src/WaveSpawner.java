import bagel.util.Point;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

/**
 * The SlicerWave class represents a wave of slicers. Useful for managing wave timescale and spawning.
 */
public class WaveSpawner {
    private final static double FRAMES_PER_MS = 0.06;
    private final static int BASE_REWARD = 150;
    private final static int REWARD_MULTIPLIER = 100;

    public static List<Slicer> getLivingSlicers() {
        return livingSlicers;
    }

    private static List<Slicer> livingSlicers;
    private int slicersSpawnedInEvent;
    private double latestSpawnFrameNum;
    private List<Point> path;
    private double spawnFrameGap;
    private int waveEventIndex;
    private HashMap currentWaveEvent;
    private List<HashMap> waveInfo;
    private boolean eventStarted;
    private double startOfDelay;

    public boolean isWaveFinished() {
        return waveFinished;
    }

    private boolean waveFinished;
    private int waveNum;


    /**
     * Constructor.
     * @param path: a list of points forming a polyline for the wave to follow.
     * @param waveInfo list of hashmap containing all wave events
     */
    public WaveSpawner(List<Point> path, List<HashMap> waveInfo, int waveNum) {
        this.path = path;
        this.waveInfo = waveInfo;
        this.eventStarted = false;
        this.waveNum = waveNum;

        livingSlicers = new ArrayList<Slicer>();
        slicersSpawnedInEvent = 0;
        waveEventIndex = 0;
        waveFinished = false;
    }

    /**
     * adds new slicers to the wave and updates their rendered drawing. Returns early when all slicers have finished
     * traversing.
     * @param currentFrameNum is the game's current frame number
     */
    public void executeWaveEvent(double currentFrameNum) {
        // returns early if all slicers in the wave have finished traversing the path or they're all dead
        if (waveEventIndex == waveInfo.size() && livingSlicers.isEmpty()) {
            if (waveFinished != true) {
                BuyPanel.addPlayerMoney(BASE_REWARD + REWARD_MULTIPLIER * waveNum);
            }
            waveFinished = true;
            return;
        }

        // checks whether there's still events left and whether the event to be executed is a spawn or delay event
        if (waveEventIndex < waveInfo.size()) {
            currentWaveEvent = waveInfo.get(waveEventIndex);
            if (currentWaveEvent.get("event type").equals("spawn")) {
                spawnWaveEvent(currentFrameNum);
            }
            else if (currentWaveEvent.get("event type").equals("delay")) {
                executeDelay(currentFrameNum, spawnFrameGap);
            }
        }
        updateIndividualSlicers();
    }

    /**
     * spawns a wave
     * @return true if the wave event has finished spawning. False if there's still slicers left to spawn.
     */
    private void spawnWaveEvent(double currentFrameNum) {
        // setup info for a spawn event if it hasn't started yet
        if (!eventStarted) {
            spawnFrameGap = (double) waveInfo.get(waveEventIndex).get("spawn delay") * FRAMES_PER_MS;
            slicersSpawnedInEvent = 0;
            eventStarted = true;
        }

        // adds new slicer to the wave if there are no slicers in the wave yet or there is a sufficient time gap.
        if (livingSlicers.isEmpty()
                || (currentFrameNum - latestSpawnFrameNum >= spawnFrameGap
                && slicersSpawnedInEvent < (int) currentWaveEvent.get("spawn number"))) {

            slicersSpawnedInEvent++;
            latestSpawnFrameNum = currentFrameNum;
            spawnSlicer();
        }

        if (slicersSpawnedInEvent == (int) currentWaveEvent.get("spawn number")) {
            waveEventIndex++;
            eventStarted = false;
        }
    }

    /**
     * spawns a slicer and adds that to the living slicer list depending on what the wave event's enemy type is
     */
    private void spawnSlicer() {
        if (waveInfo.get(waveEventIndex).get("enemy type").equals("slicer")) {
            livingSlicers.add(new RegularSlicer(path));
        }
        else if (waveInfo.get(waveEventIndex).get("enemy type").equals("superslicer")) {
            livingSlicers.add(new SuperSlicer(path));
        }
        if (waveInfo.get(waveEventIndex).get("enemy type").equals("megaslicer")) {
            livingSlicers.add(new MegaSlicer(path));
        }
        else if (waveInfo.get(waveEventIndex).get("enemy type").equals("apexslicer")) {
            livingSlicers.add(new ApexSlicer(path));
        }
    }

    /**
     * increases the waveEventIndex when the difference between current frame num and start of delay has reached the
     * number of delay frames given i.e. delay has finished. Also switches the eventStarted to false once this happens
     * @param currentFrameNum
     * @param delayFrames
     */
    private void executeDelay(double currentFrameNum, double delayFrames) {
        // setup info for a delay event
        if (!eventStarted) {
            spawnFrameGap = (double) waveInfo.get(waveEventIndex).get("spawn delay") * FRAMES_PER_MS;
            startOfDelay = currentFrameNum;
            eventStarted = true;
        }

        if (delayFrames <= currentFrameNum - startOfDelay) {
            waveEventIndex++;
            eventStarted = false;
        }
    }

    /**
     * updates each individual slicer in the wave. Removes slicer from the list of slicers once it's finished traversing
     * the path.
     */
    private void updateIndividualSlicers() {
        List<Slicer> childrenToAdd = new ArrayList<Slicer>();

        // got the iterator code outline from this top stackoverflow answer:
        // 'https://stackoverflow.com/questions/18448671/how-to-avoid-concurrentmodificationexception-while-removing-
        // elements-from-arr'
        Iterator<Slicer> iter = livingSlicers.iterator();
        while (iter.hasNext()) {
            Slicer slicer = iter.next();
            slicer.updateDraw();
            // if a slicer has finished traversing the path, it's removed from the list of slicers in
            // the wave.
            if (slicer.getStatus() == "successful") {
                iter.remove();
            }
            if (slicer.getStatus() == "dead") {
                List<Slicer> children = slicer.spawnChildren();
                if (children != null) {
                    childrenToAdd.addAll(children);
                }
                iter.remove();
            }
        }
        livingSlicers.addAll(childrenToAdd);
    }
}