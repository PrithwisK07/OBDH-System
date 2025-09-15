
// CommunicationsManager.java
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Arrays;
import java.util.Comparator;

public class CommunicationsManager {
    private final String fileSystemRoot;
    private final File sendingDir;

    public CommunicationsManager(String fileSystemRoot) {
        this.fileSystemRoot = fileSystemRoot;
        this.sendingDir = new File(fileSystemRoot, "sending");
    }

    public void simulateDownlinkPass() {
        System.out.println("\n--- STARTING DOWNLINK PASS ---");

        // Step 1: Move files to be sent into the 'sending' directory based on priority.
        prepareFilesForDownlink();

        // Step 2: Transmit all files from the 'sending' directory.
        transmitFromSendingDirectory();

        System.out.println("--- DOWNLINK PASS COMPLETE ---\n");
    }

    private void prepareFilesForDownlink() {
        // Define downlink priority
        APID[] priorityOrder = {
                APID.SCIENCE_CRITICAL,
                APID.DIAGNOSTIC_LOG,
                APID.HOUSEKEEPING,
                APID.SCIENCE_NOMINAL // Added nominal science to the downlink
        };

        System.out.println("--- Preparing files for downlink ---");
        for (APID apid : priorityOrder) {
            File sourceDir = new File(fileSystemRoot + apid.getStoragePath());

            // List only completed files (no .tmp suffix)
            File[] files = sourceDir.listFiles((dir, name) -> name.toLowerCase().endsWith(".bin"));

            if (files != null) {
                for (File file : files) {
                    try {
                        Path sourcePath = file.toPath();
                        Path destPath = new File(sendingDir, file.getName()).toPath();
                        Files.move(sourcePath, destPath, StandardCopyOption.ATOMIC_MOVE);
                        System.out.printf("Moved %s to sending directory.\n", file.getName());
                    } catch (IOException e) {
                        System.err.printf("Failed to move %s to sending directory: %s\n", file.getName(),
                                e.getMessage());
                    }
                }
            }
        }
    }

    private void transmitFromSendingDirectory() {
        File[] filesToSend = sendingDir.listFiles();
        if (filesToSend == null || filesToSend.length == 0) {
            System.out.println("--- No files in sending directory to transmit. ---");
            return;
        }

        System.out.printf("--- Transmitting %d files from staging area ---\n", filesToSend.length);
        // Sort files by name (timestamp) to send oldest first
        Arrays.sort(filesToSend, Comparator.comparing(File::getName));

        for (File file : filesToSend) {
            transmitFile(file);
        }
    }

    private void transmitFile(File file) {
        try {
            byte[] fileBytes = Files.readAllBytes(file.toPath());
            // Simulate wrapping in a protocol frame (e.g., AX.25) and sending
            System.out.printf("Transmitting %s: %d bytes\n", file.getName(), fileBytes.length);

            // Simulate receiving an ACK by deleting the file after transmission
            if (file.delete()) {
                // System.out.printf("ACK received. Deleted %s.\n", file.getName());
            } else {
                System.err.printf("Transmission successful, but failed to delete %s.\n", file.getName());
            }
        } catch (IOException e) {
            System.err.println("Failed to read file for transmission: " + file.getPath());
        }
    }
}