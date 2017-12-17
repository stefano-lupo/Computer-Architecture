import com.sun.org.apache.xpath.internal.SourceTree;

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

        System.out.println("Total Bytes: " + bytes.length);

        // Create an int array from bytes
        int[] littleEndian = getAsIntArray(bytes, false);
        System.out.println("Number of 32bit ints: " + littleEndian.length + ", number of traces = " + littleEndian.length / 2 + "\n");

        // See how long it takes to read the file
        final long timeAfterRead = System.currentTimeMillis();

        // Get subset of integers for examination
//      int[] littleEndianSnippet = Arrays.copyOfRange(littleEndian, 250000 , 250100);

        // Print out the contents
//      displayIntArray(littleEndian);

        // Write a subset to test file for inspection
//      writeSmallBinaryFile(bytes, OUTPUT_FILE_NAME);



        // Create the caches
        Cache iCache = new Cache(16, 1, 1024);
        Cache dCache = new Cache(16, 8, 256);

        // Analyse the cache
        Analyser analyser = new Analyser(iCache, dCache, littleEndian);
        analyser.analyse();

        final long finishTime = System.currentTimeMillis();

        System.out.println("\nFile Reading Time: " + (timeAfterRead - startTime) + "ms");
        System.out.println("Cache Simulation Time: " + (finishTime - timeAfterRead) + "ms");
        System.out.println("Total Elapsed Time :" + (finishTime - startTime) + "ms");

    }


    /********************************************************
     * Helper Methods
     ********************************************************/


    // Generate array of 32 bit integers
    private static int[] getAsIntArray(byte[] bytes, boolean bigEndian) {
        IntBuffer intBuffer = ByteBuffer.wrap(bytes)
                .order(bigEndian ? ByteOrder.BIG_ENDIAN : ByteOrder.LITTLE_ENDIAN)
                .asIntBuffer();
        int[] array = new int[intBuffer.remaining()];
        intBuffer.get(array);
        return array;
    }


    private static void displayBytes(byte[] bytes, int radix, int numToShow) {
        System.out.println("Displaying in base " + radix);
        for(int i=0;i<(numToShow*8); i++){
            if(i % 8 == 0) {
                System.out.println();
            } else if(i % 4 == 0) {
                System.out.print("  -  ");
            }

            String str = Integer.toString(bytes[i], radix);
            if(radix == 2){
                str = String.format("%8s", str).replace(' ', 'X');
            }

            System.out.print(str + " ");
        }

        System.out.println("\n");
    }

    private static void displayIntArray(int[] arr) {
        System.out.println("Hex, Dec, Binary");
        for(int i=0; i<arr.length; i++) {
            System.out.println(Integer.toString(arr[i], 16));
            System.out.println(Integer.toString(arr[i]));
            System.out.println(Integer.toString(arr[i], 2) + "\n");
        }
    }

    private static byte[] readSmallBinaryFile(String aFileName) throws IOException {
        Path path = Paths.get(aFileName);
        return Files.readAllBytes(path);
    }

    private static void writeSmallBinaryFile(byte[] aBytes, String aFileName) throws IOException {
        Path path = Paths.get(aFileName);
        byte[] snippet = Arrays.copyOfRange(aBytes, 0 , 800);
        Files.write(path, snippet); //creates, overwrites
    }
}  