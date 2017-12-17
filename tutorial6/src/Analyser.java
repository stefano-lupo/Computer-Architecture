import java.util.Arrays;

class Analyser {

    private final static boolean DEBUG = false;
    private final static int MAX_TRACES_TO_PROCESS = 100;

    private final static int ADDRESS_MASK = 0x007FFFFF;
    private final static int CYCLE_MASK = 0xE0000000;
    private final static int BURST_MASK = 0x18000000;

    private final static int CYCLE_SHIFT = 29;
    private final static int BURST_SHIFT = 27;

    private Cache iCache, dCache;
    private int[] traces;

    /**
     * Creates an analyser for simulations with two caches
     * Assumes traces is [word00, word01, word10, word11 ...etc]
     * Assumes words are in big endian format
     * Assumes wordsX0 lowest (address) byte have not yet been shifted
     * @param iCache the instruction cache to be used
     * @param dCache the data cache to be used
     * @param traces int array of the traces
     */
    Analyser(Cache iCache, Cache dCache, int[] traces) {
        this.iCache = iCache;
        this.dCache = dCache;
        this.traces = traces;
        System.out.println("Traces Length: " + this.traces.length);
    }

    /**
     * Creates an analyser for simulations with one cache
     * @param cache the cache to be used for the simulation
     * @param traces the traces to be used for the simulation
     */
    Analyser(Cache cache, int[] traces) {
        this.iCache = cache;
        this.traces = traces;
    }

    /**
     * Runs the basic (single cache) tutorial simulations
     */
    void analyseTutorial() {
        for(int trace : traces) {
            iCache.feedAddress(trace, 0);
        }

        System.out.println("\nInstruction Cache");
        iCache.printResults();
    }


    /**
     * Runs the more complex assignment (multi cache) simulations
     * @throws IllegalArgumentException if something scary happens
     */
    void analyseAssignment() throws IllegalArgumentException {

        // Keep track of the different cache access to ensure everything seems appropriate
        int skips = 0;
        int dataReads = 0;
        int dataWrites = 0;
        int instructionReads = 0;

        // Keeps track of the frequencies of the different cache accesses
        int[] types = new int[8];

        // Process some or all of the traces
        int tracesToAnalyse = DEBUG ?  2 * MAX_TRACES_TO_PROCESS : traces.length;
        System.out.println("Analysing traces: " + tracesToAnalyse / 2);

        // We only actually process half that number of traces since there is two words per trace
        float actualNumTraces = tracesToAnalyse / 2;

        // Iterate over all of the traces
        for(int i=0;i<tracesToAnalyse; i+=2) {

            int word = traces[i];

            // Print the trace
            // String binary = Integer.toString(word, 2);
            // System.out.println(binary.length() + ": " + binary);

            // Extract the relvant bits (three angles is unsigned bit shift)
            int cycleType = (word & CYCLE_MASK) >>> CYCLE_SHIFT;
            int burstCount = (word & BURST_MASK) >>> BURST_SHIFT;

            // Get bits 24 - 2 of the address
            int address = word & ADDRESS_MASK;

            // Shift bits to add the missing two LS zeros
            address <<= 2;


            // Ensure trace was valid
            if(burstCount > 3 || burstCount < 0 || cycleType > 7 || cycleType < 0) {
                String error = "Invalid Trace: cycle type: " + cycleType + ", burstCount: " + burstCount;
                throw new IllegalArgumentException(error);
            }

            // Increment number of these cycles that have occurred
            types[cycleType]++;


            // Feed the address to the appropriate cache
            // 4: instruction read, 6: data read, 7: data write
            if(cycleType == 4) {
                instructionReads++;
                iCache.feedAddress(address, burstCount);
            } else if(cycleType == 6) {
                dataReads ++;
                dCache.feedAddress(address, burstCount);
            } else if(cycleType == 7) {
                dCache.feedAddress(address, burstCount);
                dataWrites ++;
            } else {
                skips++;
            }
        }

        // Print the distributions of cycle types and other info
        System.out.println("\n\nData Reads: " + dataReads / actualNumTraces);
        System.out.println("Data Writes: " + dataWrites / actualNumTraces);
        System.out.println("Instruction Reads: " + instructionReads / actualNumTraces);
        System.out.println("Skips: " + skips / actualNumTraces);
        System.out.println(Arrays.toString(types));
        System.out.println("Analysed " + (instructionReads + dataReads + dataWrites) + " traces, skipped " + skips);

        System.out.println("\nInstruction Cache");
        iCache.printResults();

        System.out.println("\nData Cache");
        dCache.printResults();
    }

    /**
     * Adds missing bits to LSB of the address
     * This is NOT used as I _think_ its the same as the shifting done instead
     */
    private int addMissingBitsToStart(int address) {
        // System.out.println("Initial Address: " + Integer.toBinaryString(address));

        int addressUpper = address & 0xFFFFFF00;
        // System.out.println("Address Upper: " + Integer.toBinaryString(addressUpper));

        int addressLower = address & 0x000000FF;
        // System.out.println("Address Lower: " + Integer.toBinaryString(addressLower));

        addressLower <<= 2;
        // System.out.println("Address Lower Shifted: " + Integer.toBinaryString(addressLower));

        address = addressUpper + addressLower;
        // System.out.println("Final Address: " + Integer.toBinaryString(address));

        return address;
    }
}
