
// DataManagerTask.java
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.concurrent.BlockingQueue;

public class DataManagerTask implements Runnable {
    private final BlockingQueue<TelemetryPacket> dataQueue;
    private final String fileSystemRoot;

    public DataManagerTask(BlockingQueue<TelemetryPacket> dataQueue, String fileSystemRoot) {
        this.dataQueue = dataQueue;
        this.fileSystemRoot = fileSystemRoot;
        initializeFileSystem();
    }

    private void initializeFileSystem() {
        System.out.println("Initializing simulated file system at: " + new File(fileSystemRoot).getAbsolutePath());
        for (APID apid : APID.values()) {
            new File(fileSystemRoot + apid.getStoragePath()).mkdirs();
        }
        // Also create a directory for files being sent
        new File(fileSystemRoot, "sending").mkdirs();
    }

    @Override
    public void run() {
        while (!Thread.currentThread().isInterrupted()) {
            try {
                TelemetryPacket packet = dataQueue.take();
                storePacket(packet);
                WatchDogManager.pet(Thread.currentThread());
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                System.out.println("DataManagerTask interrupted.");
            }
        }
    }

    private void storePacket(TelemetryPacket packet) {
        String dirPath = fileSystemRoot + packet.getApid().getStoragePath();
        String fileName = String.format("%d_%d.bin", packet.getApid().getId(), packet.getTimestamp());

        Path tempFile = Paths.get(dirPath, fileName + ".tmp");
        Path finalFile = Paths.get(dirPath, fileName);

        try (FileOutputStream fos = new FileOutputStream(tempFile.toFile())) {
            fos.write(packet.toByteArray());
        } catch (IOException e) {
            System.err.println("Failed to write to temporary packet file: " + e.getMessage());
            return; // Abort if write fails
        }

        try {
            // Atomically move the temporary file to its final destination
            Files.move(tempFile, finalFile, StandardCopyOption.ATOMIC_MOVE);
            System.out.printf("DataManagerTask: Stored packet to %s\n", finalFile);
        } catch (IOException e) {
            System.err.println("Failed to move packet file to final destination: " + e.getMessage());
        }
    }
}