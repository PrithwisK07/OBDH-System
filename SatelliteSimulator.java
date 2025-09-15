
// SatelliteSimulator.java
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public class SatelliteSimulator {
    // Corrected main method signature
    public static void main(String[] args) throws InterruptedException {
        System.out.println("--- SATELLITE OBDH SIMULATOR STARTING ---");

        final String FILE_SYSTEM_ROOT = "./obdh_filesystem";
        BlockingQueue<TelemetryPacket> dataQueue = new ArrayBlockingQueue<>(50);

        // Create tasks
        Runnable housekeeping = new HousekeepingTask(dataQueue);
        Runnable science = new SciencePayloadTask(dataQueue);
        Runnable dataManager = new DataManagerTask(dataQueue, FILE_SYSTEM_ROOT);

        // Create threads
        Thread housekeepingThread = new Thread(housekeeping, "HousekeepingThread");
        Thread scienceThread = new Thread(science, "ScienceThread");
        Thread dataManagerThread = new Thread(dataManager, "DataManagerThread");

        // List of tasks for the watchdog to monitor
        List<Thread> monitoredTasks = new ArrayList<>();
        monitoredTasks.add(housekeepingThread);
        monitoredTasks.add(scienceThread);
        monitoredTasks.add(dataManagerThread);

        // Create and start the watchdog
        Runnable watchdog = new WatchDogManager(monitoredTasks, 90000); // 90-second timeout
        Thread watchdogThread = new Thread(watchdog, "WatchdogThread");
        watchdogThread.setDaemon(true);
        watchdogThread.start();

        // Start the main tasks
        housekeepingThread.start();
        scienceThread.start();
        dataManagerThread.start();

        // Simulate running for 5 minutes, with a downlink pass every 2 minutes
        CommunicationsManager commsManager = new CommunicationsManager(FILE_SYSTEM_ROOT);
        for (int i = 0; i < 3; i++) {
            Thread.sleep(120000); // Wait 2 minutes
            commsManager.simulateDownlinkPass();
        }

        // Shutdown sequence
        System.out.println("--- SIMULATION COMPLETE, SHUTTING DOWN ---");
        housekeepingThread.interrupt();
        scienceThread.interrupt();
        dataManagerThread.interrupt();
    }
}