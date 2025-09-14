import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.Comparator;

public class CommunicationsManager {
    private final String fileSystemRoot;

    public CommunicationsManager(String fileSystemRoot) {
        this.fileSystemRoot = fileSystemRoot;
    }

    public void simulateDownlinkPass() {
        System.out.println("\n--- STARTING DOWNLINK PASS ---");

        String[] priorityPaths = {
                APID.SCIENCE_CRITICAL.getStoragePath(),
                APID.DIAGNOSTIC_LOG.getStoragePath(),
                APID.HOUSEKEEPING.getStoragePath()
        };

        for (String path : priorityPaths) {
            File dir = new File(fileSystemRoot + path);
            File[] files = dir.listFiles();
            if (files != null && files.length > 0) {
                System.out.printf("--- Transmitting from %s (%d files) ---\n", path, files.length);
                Arrays.sort(files, Comparator.comparing(File::getName));
                for (File file : files) {
                    transmitFile(file);
                }
            }
        }
        System.out.println("--- DOWNLINK PASS COMPLETE ---\n");
    }

    private void transmitFile(File file) {
        try {
            String content = new String(Files.readAllBytes(file.toPath()));
            String ax25Frame = String.format(" %s", content.trim());
            System.out.println("Transmitting: " + ax25Frame);
            file.delete();
        } catch (IOException e) {
            System.err.println("Failed to read or delete file for transmission: " + file.getPath());
        }
    }
}