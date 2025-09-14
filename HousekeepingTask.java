
// HousekeepingTask.java
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ThreadLocalRandom;

public class HousekeepingTask implements Runnable {
    private final BlockingQueue<TelemetryPacket> dataQueue;
    private int sequenceCount = 0;

    // This is the constructor the error message says is missing.
    public HousekeepingTask(BlockingQueue<TelemetryPacket> dataQueue) {
        this.dataQueue = dataQueue;
    }

    @Override
    public void run() {
        while (!Thread.currentThread().isInterrupted()) {
            try {
                // Simulate taking 60 seconds to run (for testing, you can shorten this)
                Thread.sleep(60000);

                // Simulate collecting data
                double voltage = 3.3 + (ThreadLocalRandom.current().nextDouble() * 0.2);
                double temp = 25.0 + (ThreadLocalRandom.current().nextDouble() * 5.0);
                String payload = String.format("voltage=%.2fV, temp=%.1fC", voltage, temp);

                TelemetryPacket packet = new TelemetryPacket(APID.HOUSEKEEPING, sequenceCount++, payload);
                dataQueue.put(packet);

                System.out.println("HousekeepingTask: Generated packet SEQ " + (sequenceCount - 1));
                WatchDogManager.pet(Thread.currentThread()); // Pet the watchdog

            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                System.out.println("HousekeepingTask interrupted.");
            }
        }
    }
}