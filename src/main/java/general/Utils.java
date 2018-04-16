package general;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.lang.management.ManagementFactory;
import java.util.*;
import java.util.concurrent.locks.ReentrantLock;


public class Utils {
    private Utils() {
    }

    /**
     * Helper method to get the current process ID
     *
     * @return process id
     */
    public static int getProcessId() {
        final String jvmName = ManagementFactory.getRuntimeMXBean().getName();
        final int index = jvmName.indexOf('@');

        if (index < 1) {
            return 0;
        }

        try {
            return Integer.parseInt(jvmName.substring(0, index));
        } catch (NumberFormatException e) {
            return 0;
        }
    }
    /**
     * Gets the contents of the specified file.
     * @param id the file ID
     * @return the array of integers, representing the contents of the file to transmit
     */
    public static Integer[] getFileContents(int id) {
        File fileToTransmit = new File(String.format("rdtcInput%d.png", id));
        try (FileInputStream fileStream = new FileInputStream(fileToTransmit)) {
            Integer[] fileContents = new Integer[(int) fileToTransmit.length()];

            for (int i = 0; i < fileContents.length; i++) {
                int nextByte = fileStream.read();
                if (nextByte == -1) {
                    throw new Exception("File size is smaller than reported");
                }

                fileContents[i] = nextByte;
            }
            return fileContents;
        } catch (Exception e) {
            System.err.println(e.getMessage());
            System.err.println(e.getStackTrace());
            return null;
        }
    }


}

