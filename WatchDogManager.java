import java.util.concurrent.ConcurrentHashMap;
import java.util.Map;
import java.util.List;

public class WatchDogManager implements Runnable {
    private static final Map<Thread, Long> taskCheckIns = new ConcurrentHashMap<>();
    private final List<Thread> monitoredTasks;
    private final long timeoutMillis;

    public WatchDogManager(List<Thread> monitoredTasks, long timeoutMillis) {
        this.monitoredTasks = monitoredTasks;
        this.timeoutMillis = timeoutMillis;
        for (Thread task : monitoredTasks) {
            taskCheckIns.put(task, System.currentTimeMillis());
        }
    }

    public static void pet(Thread task) {
        taskCheckIns.put(task, System.currentTimeMillis());
    }

    @Override
    public void run() {
        while (!Thread.currentThread().isInterrupted()) {
            try {
                Thread.sleep(timeoutMillis / 2);
                long now = System.currentTimeMillis();

                for (Thread task : monitoredTasks) {
                    if (task.isAlive()) {
                        long lastPet = taskCheckIns.getOrDefault(task, 0L);
                        if (now - lastPet > timeoutMillis) {
                            System.err.printf(
                                    "WATCHDOG TIMEOUT: Task '%s' has not checked in for %d ms. Simulating system reset!\n",
                                    task.getName(), now - lastPet);
                            System.exit(1);
                        }
                    }
                }
            } catch (Exception e) {
                Thread.currentThread().interrupt();
                System.out.println("WatchDogManager interrupted!");
            }
        }
    }
}
