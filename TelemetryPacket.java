import java.time.Instant;

public class TelemetryPacket {
    private final APID apid;
    private final int sequenceCount;
    private final long timestamp;

    private final String payload;

    public TelemetryPacket(APID apid, int sequenceCount, String payload) {
        this.apid = apid;
        this.sequenceCount = sequenceCount;
        this.timestamp = Instant.now().getEpochSecond();
        this.payload = payload;
    }

    public APID getAPID() {
        return apid;
    }

    public long getTimestamp() {
        return timestamp;
    }

    @Override
    public String toString() {
        return String.format("APID: %d | SEQ: %d | TSTAMP: %d | PAYLOAD: %s",
                apid.getId(), sequenceCount, timestamp, payload);
    }
}
