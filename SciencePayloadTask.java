import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ThreadLocalRandom;

public class SciencePayloadTask implements Runnable {
    private final BlockingQueue<TelemetryPacket> dataQueue;
    private final int SLEEP_TIME = 10000; // 10 seconds.

    private int nominalSeqCount = 0;
    private int criticalSeqCount = 0;

    public SciencePayloadTask(BlockingQueue<TelemetryPacket> dataQueue) {
        this.dataQueue = dataQueue;
    }

    private TelemetryPacket generateSciencePayloadData() {
        TelemetryPacket packet;

        if (ThreadLocalRandom.current().nextInt(10) == 0) { // 10% chance
            String payload = "CRITICAL_EVENT: High energy particle detected.";
            packet = new TelemetryPacket(APID.SCIENCE_CRITICAL, criticalSeqCount++, payload);
            System.out.println("SciencePayloadTask: Generated CRITICAL packet - SEQ " + (criticalSeqCount - 1));
        } else {
            int particleCount = ThreadLocalRandom.current().nextInt(5, 20);
            String payload = "NOMINAL_READING: Particle count=" + particleCount;
            packet = new TelemetryPacket(APID.SCIENCE_NOMINAL, nominalSeqCount++, payload);
            System.out.println("SciencePayloadTask: Generated NOMINAL packet - SEQ " + (nominalSeqCount - 1));
        }

        return packet;
    }

    @Override
    public void run() {
        while (!Thread.currentThread().isInterrupted()) {
            try {
                Thread.sleep(SLEEP_TIME);

                TelemetryPacket packet = generateSciencePayloadData();
                dataQueue.put(packet);

                WatchDogManager.pet(Thread.currentThread());

            } catch (Exception e) {
                Thread.currentThread().interrupt();
                System.out.println("SciencePayloadTask interrupted!");
            }
        }
    }
}
