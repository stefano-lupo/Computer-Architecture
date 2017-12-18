import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

class Cache {

    private static final int DEFAULT_ADDRESS_SIZE = 25;
    private int addressSize = DEFAULT_ADDRESS_SIZE;


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

    /**
     * Create a Cache with a specified addressSize
     * @param l number of bytes in each cache line
     * @param k number of cache lines per set (directories)
     * @param n number of sets
     * @param addressSize size of the physical address (required for calculating # bits for offset)
     */
    Cache(int l, int k, int n, int addressSize) {
        this(l, k, n);
        this.addressSize = addressSize;
    }

    /**
     * Creates a cache with default (assignment) address size of 25 bits
     * @param l number of bytes in each cache line
     * @param k number of cache lines per set (directories)
     * @param n number of sets
     */
    Cache(int l, int k,  int n){
        this.l = l;
        this.k = k;
        this.n = n;
        System.out.println("Created " + l + ", " + k + ", " + n + " cache (" + l*k*n + "kb)");

        // Get number of bits for l
        Double math = (Math.log(l) / Math.log(2));
        int offsetBits = math.intValue();

        // Get number of bits for n
        math = Math.log(n) / Math.log(2);
        int setBits = math.intValue();
        System.out.println(offsetBits + ", " + setBits);

        // Offset mask is least significant l bits (after shifting)
        offsetMask = (1 << offsetBits) - 1;
        offsetShift = 0;
        System.out.println("Offset Mask: " + Integer.toBinaryString(offsetMask));

        // Set mask is least significant n bits (after shifting)
        setMask = (1 << setBits) - 1;
        setShift = offsetBits;
        System.out.println("Set Mask: " + Integer.toBinaryString(setMask));

        // Tag mask is least significant remainder of bits (after shifting)
        tagMask = (1 << (addressSize - offsetBits - setBits)) -1;
        tagShift = offsetBits + setBits;
        System.out.println("Tag Mask: " + Integer.toBinaryString(tagMask) + "\n");

        // Instantiate the hash maps for our cache
        sets = new HashMap<>(n, 1);
        for(int i=0; i<n; i++) {
            sets.put(i, new HashMap<>(k, 1));
        }
    }

    /**
     * Requests data from the cache
     * @param physicalAddress "addressSize" bit physical address
     * @param burstCount the number of adjacent memory address requested
     * @throws IllegalArgumentException if the physical address is invalid
     */
    void feedAddress(int physicalAddress, int burstCount) throws IllegalArgumentException{

        // Extract the info
        int setNumber = (physicalAddress >> setShift) & setMask;
        int offset = (physicalAddress >> offsetShift) & offsetMask;
        int tagNumber = (physicalAddress >> tagShift) & tagMask;

        // Ensure values are valid
        if(setNumber < 0 || setNumber > setMask || offset < 0 || offset > offsetMask || tagNumber < 0 || tagNumber > tagMask) {
            String error = "Invalid Cache Values: setNumber: " + setNumber+ ", tagNumber: " + tagNumber + ", offset: " + offset;
            throw new IllegalArgumentException(error);
        }

        // Display the values
        // System.out.println("\n" + Integer.toHexString(physicalAddress));
        // System.out.println("Set: " + setNumber + ", Offset: " + offset + ", Tag: " + tagNumber + ", Burstcount: " +burstCount);

        // Get the k tags in this set
        HashMap<Integer, TagData> set = sets.get(setNumber);

        // Check for the tag we are interested in
        TagData tagData = set.get(tagNumber);

        // Check for tag match
        if(tagData != null) {
            // System.out.println(Integer.toHexString(physicalAddress) + ": Hit found");
            hits += burstCount + 1;

            // Update this tags last access time
            tagData.lastAccess = ++timestamp;
        } else {
            // System.out.println(Integer.toHexString(physicalAddress) + ": Miss");
            misses++;

            // Think the memory address that were adjacent count as hits since they will technically be read from the cache?
            hits += burstCount;

            // Check if k directories are full
            if(set.values().size() < k) {
                // System.out.println("Compulsory miss - k directories not full, inserting..");
                set.put(tagNumber, new TagData(++timestamp));
            } else {
                int lruTag = getLRU(set);
                // System.out.println(Integer.toHexString(physicalAddress) + ": directory full, removing lru: " + lruTag);
                set.remove(lruTag);
                set.put(tagNumber, new TagData(++timestamp));
            }
        }
    }


    /**
     * Performs the least recently used algorithm
     * @param set the set from the cache that needs to have an eviction
     * @return the tag to be evicted
     */
    private int getLRU(HashMap<Integer, TagData> set) {

        // Find the tag with the smallest timestamp as its last access
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

    /**
     * Prints the results of the simulation
     */
    void printResults() {
        System.out.println("Total accesses: " + (misses + hits));
        System.out.println("Misses: " + misses);
        System.out.println("Hits: " + hits);
        System.out.println("Hit Rate: " + (float)hits * 100 / (hits + misses) + "%");
    }

    /**
     * Data structure which could be expanded to actually hold some data
     */
    class TagData {

        int lastAccess;

        TagData() {
            this.lastAccess = 0;
        }

        TagData(int lastAccess) {
            this.lastAccess = lastAccess;
        }
    }
}
