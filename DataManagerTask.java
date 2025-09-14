import java.util.concurrent.BlockingQueue;
import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.io.IOException;

public class DataManagerTask implements Runnable {
    private final BlockingQueue<TelemetryPacket> dataQueue;
    private final String fileSystemRoot;

    public DataManagerTask(BlockingQueue<TelemetryPacket> dataQueue, String fileSystemRoot) {
        this.dataQueue = dataQueue;
        this.fileSystemRoot = fileSystemRoot;
        initializeFileSystem();
    }

    private void initializeFileSystem() {
        System.out.println("Initializing file system at: " + new File(fileSystemRoot).getAbsolutePath());

        for (APID apid : APID.values()) {
            File dir = new File(fileSystemRoot + apid.getStoragePath());
            if (!dir.exists()) {
                dir.mkdirs();
            }
        }
    }

    @Override
    public void run() {
        while (!Thread.currentThread().isInterrupted()) {
            try {
                TelemetryPacket packet = dataQueue.take();
                storagePacket(packet);

                WatchDogManager.pet(Thread.currentThread());
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }

    private void storagePacket(TelemetryPacket packet) {
        String dirPath = fileSystemRoot + packet.getAPID().getStoragePath();
        String fileName = String.format("%d_%d.txt", packet.getAPID().getId(), packet.getTimestamp());

        File file = new File(dirPath, fileName);

        try (PrintWriter out = new PrintWriter(new FileWriter(file))) {
            out.println(packet.toString());
            System.out.printf("DataManagerTask: Stored packet to %s\n", file.getPath());
        } catch (IOException exception) {
            System.err.println("Failed to write file: " + exception.getMessage());
        }
    }
}
