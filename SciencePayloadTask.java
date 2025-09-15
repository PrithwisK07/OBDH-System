
// SciencePayloadTask.java
import java.nio.charset.StandardCharsets;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ThreadLocalRandom;

public class SciencePayloadTask implements Runnable {
    private final BlockingQueue<TelemetryPacket> dataQueue;
    private int nominalSeqCount = 0;
    private int criticalSeqCount = 0;

    public SciencePayloadTask(BlockingQueue<TelemetryPacket> dataQueue) {
        this.dataQueue = dataQueue;
    }

    @Override
    public void run() {
        while (!Thread.currentThread().isInterrupted()) {
            try {
                Thread.sleep(10000); // Simulate 10 seconds

                TelemetryPacket packet;
                byte[] payload;
                if (ThreadLocalRandom.current().nextInt(10) == 0) {
                    String payloadStr = "CRITICAL_EVENT: High energy particle detected.";
                    payload = payloadStr.getBytes(StandardCharsets.UTF_8);
                    packet = new TelemetryPacket(APID.SCIENCE_CRITICAL, criticalSeqCount++, payload);
                } else {
                    int particleCount = ThreadLocalRandom.current().nextInt(5, 20);
                    String payloadStr = "NOMINAL_READING: Particle count=" + particleCount;
                    payload = payloadStr.getBytes(StandardCharsets.UTF_8);
                    packet = new TelemetryPacket(APID.SCIENCE_NOMINAL, nominalSeqCount++, payload);
                }

                System.out.println("SciencePayloadTask: Generated " + packet);
                dataQueue.put(packet);
                WatchDogManager.pet(Thread.currentThread());

            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                System.out.println("SciencePayloadTask interrupted.");
            }
        }
    }
}