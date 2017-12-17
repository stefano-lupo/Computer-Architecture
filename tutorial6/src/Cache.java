import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class Cache {

    public final int ADDRESS_SIZE = 25;

    private int l;
    private int k;
    private int n;

    private int setMask;
    private int offsetMask;
    private int tagMask;

    private int setShift;
    private int offsetShift;
    private int tagShift;

    private int hits = 0;
    private int misses = 0;
    private int timestamp = 0;

    private HashMap<Integer, HashMap<Integer, TagData>> sets;

    Cache(int l, int k,  int n){
         this.l = l;
        this.k = k;
        this.n = n;
        System.out.println("Created " + l + ", " + k + ", " + n + " sets (" + l*k*n + "kb)");

        Double math = (Math.log(l) / Math.log(2));
        int lBits = math.intValue();

        math = Math.log(n) / Math.log(2);
        int nBits = math.intValue();
        System.out.println(lBits + ", " + nBits);

        // Mask is least significant l bits (after shifting)
        offsetMask = (1 << lBits) - 1;
        offsetShift = 0;
        System.out.println("Offset Mask: " + Integer.toBinaryString(offsetMask));

        setMask = (1 << nBits) - 1;
        setShift = lBits;
        System.out.println("Set Mask: " + Integer.toBinaryString(setMask));

        tagMask = (1 << (ADDRESS_SIZE - lBits - nBits)) -1;
        tagShift = lBits + nBits;
        System.out.println("Tag Mask: " + Integer.toBinaryString(tagMask) + "\n");

        // Create the hash maps for our cache
        sets = new HashMap<>(n, 1);
        for(int i=0; i<n; i++) {
            sets.put(i, new HashMap<>(k, 1));
        }
    }

    /**
     * @param physicalAddress 25bit physical address ?
     * @param burstCount
     */
    void feedAddress(int physicalAddress, int burstCount) throws IllegalArgumentException{
        int setNumber = (physicalAddress >> setShift) & setMask;
        int offset = (physicalAddress >> offsetShift) & offsetMask;
        int tagNumber = (physicalAddress >> tagShift) & tagMask;

        // TODO Remove for prod
        if(setNumber < 0 || setNumber > setMask || offset < 0 || offset > offsetMask || tagNumber < 0 || tagNumber > tagMask) {
            String error = "Invalid Cache Values: setNumber: " + setNumber+ ", tagNumber: " + tagNumber + ", offset: " + offset;
            throw new IllegalArgumentException(error);
        }

//        System.out.println("Set: " + setNumber + ", Offset: " + offset + ", Tag: " + tagNumber);

        // Get the k tags in this set
        HashMap<Integer, TagData> set = sets.get(setNumber);

        // Check for the tag we are interested in
        TagData tagData = set.get(tagNumber);

        // Check for tag match
        if(tagData != null) {
            hits++;
            System.out.println("Hit found: " + hits);
            tagData.lastAccess = ++timestamp;
        } else {
            misses ++;
            System.out.println("Miss: " + misses);

            // Check if k directories are full
            if(set.values().size() < k) {
                System.out.println("K Directories not full, inserting..");
                set.put(tagNumber, new TagData(++timestamp));
            } else {
                int lruTag = getLRU(set);
                System.out.println("Directories full, removing lru: " + lruTag);
                set.remove(lruTag);
                set.put(tagNumber, new TagData(++timestamp));
            }

        }

    }

    int getLRU(HashMap<Integer, TagData> set) {
        int minAccess = Integer.MAX_VALUE;
        int lruTag = -1;
        for(Map.Entry<Integer, TagData> mapEntry : set.entrySet()) {
            int lastAccess = mapEntry.getValue().lastAccess;
            if(lastAccess < minAccess) {
                minAccess = lastAccess;
                lruTag = mapEntry.getKey();
            }
        }

        return lruTag;
    }


    void printResults() {
        System.out.println("Misses: " + misses);
        System.out.println("Hits: " + misses);
        System.out.println("Hit Rate: " + (float)hits / (hits + misses) );
    }


    class TagData {

        int lastAccess;
        int data;

        TagData() {
            this.lastAccess = 0;
        }

        TagData(int lastAccess) {
            this.lastAccess = lastAccess;
        }



    }
}
