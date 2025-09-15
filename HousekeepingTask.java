
// HousekeepingTask.java
import java.nio.charset.StandardCharsets;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ThreadLocalRandom;

public class HousekeepingTask implements Runnable {
    private final BlockingQueue<TelemetryPacket> dataQueue;
    private int sequenceCount = 0;

    public HousekeepingTask(BlockingQueue<TelemetryPacket> dataQueue) {
        this.dataQueue = dataQueue;
    }

    @Override
    public void run() {
        while (!Thread.currentThread().isInterrupted()) {
            try {
                Thread.sleep(60000); // Simulate 60 seconds

                double voltage = 3.3 + (ThreadLocalRandom.current().nextDouble() * 0.2);
                double temp = 25.0 + (ThreadLocalRandom.current().nextDouble() * 5.0);
                String payloadStr = String.format("voltage=%.2fV, temp=%.1fC", voltage, temp);
                byte[] payload = payloadStr.getBytes(StandardCharsets.UTF_8);

                TelemetryPacket packet = new TelemetryPacket(APID.HOUSEKEEPING, sequenceCount++, payload);
                dataQueue.put(packet);

                System.out.println("HousekeepingTask: Generated packet " + packet);
                WatchDogManager.pet(Thread.currentThread());

            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                System.out.println("HousekeepingTask interrupted.");
            }
        }
    }
}