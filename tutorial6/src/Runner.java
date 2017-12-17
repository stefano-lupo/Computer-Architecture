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
    private final static String OUTPUT_FILE_NAME = "tv.txt";;


    public static void main(String... aArgs) throws IOException, IllegalArgumentException{

        // Read the file
        byte[] bytes = readSmallBinaryFile(FILE_NAME);
//        System.out.println("Small - size of file read in:" + bytes.length + "\n");

//        Inspect some of the traces
//        displayContents(bytes, 10, 3);
//        displayContents(bytes, 16, 3);
//        displayContents(bytes, 2, 3);



        // Or, using built in java buffer - create an int array from bytes
        int[] littleEndian = getAsIntArray(bytes, false);
//        int[] bigEndian = getAsIntArray(bytes, true);
//        System.out.println("Number of traces 32bit: " + littleEndian.length+ "\n");

        // Get subset of integers for examination
        int[] littleEndianSnippet = Arrays.copyOfRange(littleEndian, 0 , 2);
//        int[] bigEndianSnippet = Arrays.copyOfRange(bigEndian, 0 , 2);

        // Print out the contents
        for(int i=0; i<littleEndianSnippet.length; i++) {
//            // Print in little endian
//            System.out.println("Little Endian");
//            System.out.println(Integer.toString(littleEndianSnippet[i], 16));
//            System.out.println(Integer.toString(littleEndianSnippet[i]));
//            System.out.println(Integer.toString(littleEndianSnippet[i], 2) + "\n");

//            // Print in Big endian
//            System.out.println("Big Endian");
//            System.out.println(Integer.toString(bigEndianSnippet[i], 16));
//            System.out.println(Integer.toString(bigEndianSnippet[i]));
//            System.out.println(Integer.toString(bigEndianSnippet[i], 2) + "\n");
        }

        // Write a subset to test file for inspection
        writeSmallBinaryFile(bytes, OUTPUT_FILE_NAME);




        Cache iCache = new Cache(16, 1, 1024);
        Cache dCache = new Cache(16, 8, 256);

        Analyser analyser = new Analyser(iCache, dCache, littleEndian);
//        Analyser analyser = new Analyser(iCache, dCache, bigEndian);
        analyser.analyse();


    }

    // Generate array of 32 bit integers
    private static int[] getAsIntArray(byte[] bytes, boolean bigEndian) {
        IntBuffer intBuffer = ByteBuffer.wrap(bytes)
                .order(bigEndian ? ByteOrder.BIG_ENDIAN : ByteOrder.LITTLE_ENDIAN)
                .asIntBuffer();
        int[] array = new int[intBuffer.remaining()];
        intBuffer.get(array);
        return array;
    }


    private static void displayContents(byte[] bytes, int radix, int numToShow) {
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