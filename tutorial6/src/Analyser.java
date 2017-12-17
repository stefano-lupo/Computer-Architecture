import java.util.Arrays;

class Analyser {

    private final static boolean DEBUG = false;

    private final static int ADDRESS_MASK = 0x007FFFFF;
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
        System.out.println("Traces Length: " + this.traces.length);
    }

    void analyse() throws IllegalArgumentException {

        int skips = 0;
        int dReads = 0;
        int dWrites = 0;
        int iReads = 0;
        int[] types = new int[8];


        int tracesToAnalyse = DEBUG ?  2 * 1 : traces.length;

        System.out.println("Analysing traces: " + tracesToAnalyse / 2);

        float actualNumTraces = tracesToAnalyse / 2;

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

            // Get bits 24 - 2 of the address
            int address = word & ADDRESS_MASK;

            // Shift bits to add two LS zeros
            address <<= 2;


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

//        Print distributions of Operations
        System.out.println("\n\nData Reads: " + dReads / actualNumTraces);
        System.out.println("Data Writes: " + dWrites / actualNumTraces);
        System.out.println("Instruction Reads: " + iReads / actualNumTraces);
        System.out.println("Skips: " + skips / actualNumTraces);
        System.out.println(Arrays.toString(types));

        System.out.println("Analysed " + (iReads + dReads + dWrites) + " traces, skipped " + skips);

        System.out.println("\nInstruction Cache");
        iCache.printResults();

        System.out.println("\nData Cache");
        dCache.printResults();
    }

    /**
     * Adds missing bits to LSB of the address
     * Can't just shift left by two places
     */
//    private int addMissingBitsToStart(int address) {
////        System.out.println("Initial Address: " + Integer.toBinaryString(address));
//
//        int addressUpper = address & 0xFFFFFF00;
////        System.out.println("Address Upper: " + Integer.toBinaryString(addressUpper));
//
//        int addressLower = address & 0x000000FF;
////        System.out.println("Address Lower: " + Integer.toBinaryString(addressLower));
//
//        addressLower <<= 2;
////        System.out.println("Address Lower Shifted: " + Integer.toBinaryString(addressLower));
//
//        address = addressUpper + addressLower;
//        return address;
////        System.out.println("Final Address: " + Integer.toBinaryString(address));
//
//    }
}
