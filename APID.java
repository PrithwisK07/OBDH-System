public enum APID { // <- Application Process ID
    SCIENCE_CRITICAL(100, "/science/"),
    SCIENCE_NOMINAL(101, "/science/"),
    HOUSEKEEPING(200, "/housekeeping/"),
    DIAGNOSTIC_LOG(0, "/diag/");

    private final int id;
    private final String storagePath;

    APID(int id, String storagePath) {
        this.id = id;
        this.storagePath = storagePath;
    }

    public int getId() {
        return id;
    }

    public String getStoragePath() {
        return storagePath;
    }
}