import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.IntBuffer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;

public class Runner {

    private final static String FILE_NAME = "gcc1.trace";
    private final static String OUTPUT_FILE_NAME = "subtrace.txt";


    public static void main(String... aArgs) throws IOException, IllegalArgumentException{

        // Start a timer
        final long startTime = System.currentTimeMillis();

        // Read the file
        byte[] bytes = readSmallBinaryFile(FILE_NAME);


        // Create an int array from bytes
        int[] traces = getAsIntArray(bytes, false);
        System.out.println("Number of 32bit ints: " + traces.length + ", number of traces = " + traces.length / 2 + "\n");

        // See how long it takes to read the file
        final long timeAfterRead = System.currentTimeMillis();

        // Get subset of integers for examination
        // int[] littleEndianSnippet = Arrays.copyOfRange(littleEndian, 250000 , 250100);

        // Print out the contents
        // displayIntArray(littleEndian);

        // Write a subset to test file for inspection
        // writeSmallBinaryFile(bytes, OUTPUT_FILE_NAME, 100);


        // Run the assignment simulation
        runAssignment(traces);

        // Run the tutorial
//        runTutorial();

        // Stop the timer
        final long finishTime = System.currentTimeMillis();

        System.out.println("\nFile Reading Time: " + (timeAfterRead - startTime) + "ms");
        System.out.println("Cache Simulation Time: " + (finishTime - timeAfterRead) + "ms");
        System.out.println("Total Elapsed Time: " + (finishTime - startTime) + "ms");

    }


    /*
     *********************************************************
     * Helper Methods
     ********************************************************/

    /**
     * Sets up and runs the simulation for the tutorials (like the online animation)
     */
    public static void runTutorial() {
        int[] tutorialData = {
                0x0000, 0x0004, 0x00c, 0x2200, 0x00d0, 0x00e0, 0x1130, 0x0028, 0x113c, 0x2204,
                0x0010, 0x0020, 0x0004, 0x0040, 0x2208, 0x0008, 0x00a0, 0x0004, 0x1104, 0x0028, 0x000c,
                0x0084, 0x000c, 0x3390, 0x00b0, 0x1100, 0x0028, 0x0064, 0x0070, 0x00d0, 0x0008, 0x3394
        };

        // Pick some parameters here and pick same on website
        Cache iCache = new Cache(32, 8, 4, 8);
        Analyser analyser = new Analyser(iCache, tutorialData);
        analyser.analyseTutorial();
    }

    /**
     * Sets up and runs the simulation for the assignment
     * @param traces they int array from the tracefile
     */
    private static void runAssignment(int[] traces) {
        // Create the caches
        Cache iCache = new Cache(16, 1, 1024);
        Cache dCache = new Cache(16, 8, 256);

        // Analyse the cache
        Analyser analyser = new Analyser(iCache, dCache, traces);
        analyser.analyseAssignment();
    }

    /**
     * Builds array of 32 bit words from the bytes read from the trace file
     * @param bytes the byte array to get the ints from
     * @param bigEndian true if data is in big endian
     * @return int array
     */
    private static int[] getAsIntArray(byte[] bytes, boolean bigEndian) {
        IntBuffer intBuffer = ByteBuffer.wrap(bytes)
                .order(bigEndian ? ByteOrder.BIG_ENDIAN : ByteOrder.LITTLE_ENDIAN)
                .asIntBuffer();
        int[] array = new int[intBuffer.remaining()];
        intBuffer.get(array);
        return array;
    }


    /**
     * Displays the byte arrays in a specific radix formatted nicely
     * @param bytes array to print
     * @param radix base to print in
     * @param numToShow number of elements from the array to print
     */
    private static void displayBytes(byte[] bytes, int radix, int numToShow) {
        System.out.println("Displaying in base " + radix);
        for(int i=0;i<(numToShow*8); i++){

            // Split the byte strings nicely
            if(i % 8 == 0) {
                System.out.println();
            } else if(i % 4 == 0) {
                System.out.print("  -  ");
            }

            // Create the String
            String str = Integer.toString(bytes[i], radix);

            // Pad binary strings with X's
            if(radix == 2){
                str = String.format("%8s", str).replace(' ', 'X');
            }

            System.out.print(str + " ");
        }
        System.out.println("\n");
    }


    /**
     * Prints the integer array in multiple bases
     * @param arr array to print
     */
    private static void displayIntArray(int[] arr) {
        System.out.println("Hex, Dec, Binary");
        for(int i : arr) {
            System.out.println(Integer.toString(i, 16));
            System.out.println(Integer.toString(i));
            System.out.println(Integer.toString(i, 2) + "\n");
        }
    }

    /**
     * Reads the bytes in from the tracefile
     * @param filename name of tracefile
     * @return byte array containing all bytes in that file (little endian)
     * @throws IOException if anything scary happens when reading the file
     */
    private static byte[] readSmallBinaryFile(String filename) throws IOException {
        Path path = Paths.get(filename);

        return Files.readAllBytes(path);
    }


    /**
     * Writes a subset of the bytes to a file for easier viewing
     * @param bytes the full bytes array
     * @param filename name of the output file
     * @throws IOException if something scary happens when writing the file
     */
    private static void writeSmallBinaryFile(byte[] bytes, String filename, int number) throws IOException {
        Path path = Paths.get(filename);
        byte[] snippet = Arrays.copyOfRange(bytes, 0 , number);
        Files.write(path, snippet); //creates, overwrites
    }
}  