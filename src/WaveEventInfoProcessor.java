import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

/**
 * processes wave txt file so that the information can be accessed in a list of hashmaps.
 */
public class WaveEventInfoProcessor {
    private static String waveFP;
    private static final int SPAWN_EVENT_ARR_LEN = 5;
    private static final int DELAY_EVENT_ARR_LEN = 3;

    public static List<List<HashMap>> getWaveEventList() {
        return waveEventList;
    }

    private static List<List<HashMap>> waveEventList;

    public WaveEventInfoProcessor(String waveFP) throws FileNotFoundException {
        this.waveFP = waveFP;
        populateEventList();
    }

    /**
     * Each list in the list of lists represents a wave with the hashmaps representing events in that wave.
     * the keys include "wave num", "event type", "spawn delay", "spawn number", "enemy type"
     * @throws FileNotFoundException
     */
    private void populateEventList() throws FileNotFoundException {
        Scanner scanner = new Scanner(new File(waveFP));
        waveEventList = new ArrayList<>();

        int eventListIndex = 0;
        waveEventList.add(new ArrayList<>());
        while (scanner.hasNext()){
            String[] eventInfo = scanner.next().split(",");
            
            // if there's a new wave, add new list
            if (Integer.valueOf(eventInfo[0]) - 1 > eventListIndex) {
                eventListIndex++;
                waveEventList.add(new ArrayList<>());
            }

            if (eventInfo.length == DELAY_EVENT_ARR_LEN) {
                waveEventList.get(eventListIndex).add(hashDelayEvent(eventInfo));
            }
            else if (eventInfo.length == SPAWN_EVENT_ARR_LEN) {
                waveEventList.get(eventListIndex).add(hashSpawnEvent(eventInfo));
            }
        }
        scanner.close();
    }

    /**
     * represents a list of strings into a hashmap of information in a delay event
     * @param eventInfo
     * @return hashmap representing information in a delay event
     */
    private HashMap hashDelayEvent(String[] eventInfo) {
        HashMap eventHashed = new HashMap();

        eventHashed.put("wave num", Integer.valueOf(eventInfo[0]));
        eventHashed.put("event type", eventInfo[1]);
        eventHashed.put("spawn delay", Double.valueOf(eventInfo[2]));

        return eventHashed;
    }

    /**
     * represents a list of strings into a hashmap of information in a spawn event
     * @param eventInfo
     * @return hashmap representing information in a spawn event
     */
    private HashMap hashSpawnEvent(String[] eventInfo) {
        HashMap eventHashed = new HashMap<>();

        eventHashed.put("wave num", Integer.valueOf(eventInfo[0]));
        eventHashed.put("event type", eventInfo[1]);
        eventHashed.put("spawn number", Integer.valueOf(eventInfo[2]));
        eventHashed.put("enemy type", eventInfo[3]);
        eventHashed.put("spawn delay", Double.valueOf(eventInfo[4]));

        return eventHashed;
    }
}
