import java.util.Arrays;

class Analyser {

    private final static boolean DEBUG = false;

    private final static int ADDRESS_MASK = 0x01FFFFFC;
    private final static int CYCLE_MASK = 0xE0000000;
    private final static int BURST_MASK = 0x18000000;

    private final static int CYCLE_SHIFT = 29;
    private final static int BURST_SHIFT = 27;

    private Cache iCache, dCache;
    private int[] traces;

    /**
     * Assumes traces is [word00, word01, word10, word11 ...etc]
     * Assumes words are in big endian format
     * Assumes wordsX0 have not yet been shifted
     */
    Analyser(Cache iCache, Cache dCache, int[] traces) {
        this.iCache = iCache;
        this.dCache = dCache;
        this.traces = traces;
    }

    void analyse() throws IllegalArgumentException {

        int skips = 0;
        int dReads = 0;
        int dWrites = 0;
        int iReads = 0;

        int[] types = new int[8];

        float actualNumTraces = traces.length / 2;

        int tracesToAnalyse = DEBUG ?  2 * 80 : traces.length;

        for(int i=0;i<tracesToAnalyse; i+=2) {

            int word = traces[i];
//            word <<= 2;   // Dont know if we need to do this

//            Print the trace
//            String binary = Integer.toString(word, 2);
//            System.out.println(binary.length() + ": " + binary);

            // Unsigned bit shift
            // TODO: No longer shifting so masks are wrong
            int cycleType = (word & CYCLE_MASK) >>> CYCLE_SHIFT;

            //TODO: Burst Count + 1 in his code??
            int burstCount = (word & BURST_MASK) >>> BURST_SHIFT;
            int address = word & ADDRESS_MASK;

            // Ensure trace was valid
            // TODO: Turn this off on prod
            if(burstCount > 3 || burstCount < 0 || cycleType > 7 || cycleType < 0) {
                String error = "Invalid Trace: cycle type: " + cycleType + ", burstCount: " + burstCount;
                throw new IllegalArgumentException(error);
            }

            types[cycleType]++;

            if(cycleType == 4) {
                // Instruction Read
                iReads++;
                iCache.feedAddress(address, burstCount);
            } else if(cycleType == 6) {
                dReads ++;
                dCache.feedAddress(address, burstCount);
            } else if(cycleType == 7) {
                dCache.feedAddress(address, burstCount);
                dWrites ++;
            } else {
                skips++;
            }
        }

        System.out.println("\n\nData Reads: " + dReads / (tracesToAnalyse/2));
        System.out.println("Data Writes: " + dWrites / (tracesToAnalyse/2));
        System.out.println("Instruction Reads: " + iReads / (tracesToAnalyse/2));
        System.out.println("Skips: " + skips / (tracesToAnalyse/2));
        System.out.println(Arrays.toString(types));

        System.out.println("\nInstruction Cache");
        iCache.printResults();

        System.out.println("\nData Cache");
        dCache.printResults();
    }


}
