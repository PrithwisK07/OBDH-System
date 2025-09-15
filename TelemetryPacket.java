
// TelemetryPacket.java
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.zip.CRC32;

public class TelemetryPacket {
    // --- Header ---
    private final APID apid;
    private final int sequenceCount;
    private final long timestamp;

    // --- Data Field ---
    private final byte[] payload;

    public TelemetryPacket(APID apid, int sequenceCount, byte[] payload) {
        this.apid = apid;
        this.sequenceCount = sequenceCount;
        this.payload = payload;
        this.timestamp = System.currentTimeMillis() / 1000L; // UNIX timestamp
    }

    public APID getApid() {
        return apid;
    }

    public long getTimestamp() {
        return timestamp;
    }

    /**
     * Serializes the entire packet into a byte array with a CRC32 checksum.
     * Format: [APID (4)]:[SEQ (4)]:[TIMESTAMP (8)]:[PAYLOAD_LEN (4)]:[PAYLOAD
     * (N)]:[CRC32 (4)]
     * 
     * @return The full binary representation of the packet.
     */
    public byte[] toByteArray() {
        // Allocate buffer for header + payload length + payload
        int bufferSize = 4 + 4 + 8 + 4 + payload.length;
        ByteBuffer contentBuffer = ByteBuffer.allocate(bufferSize);
        contentBuffer.order(ByteOrder.BIG_ENDIAN); // Network byte order

        // Write packet data
        contentBuffer.putInt(apid.getId());
        contentBuffer.putInt(sequenceCount);
        contentBuffer.putLong(timestamp);
        contentBuffer.putInt(payload.length);
        contentBuffer.put(payload);

        // Calculate CRC32 on the content
        CRC32 crc = new CRC32();
        crc.update(contentBuffer.array());
        int crcValue = (int) crc.getValue();

        // Create final buffer including the CRC
        ByteBuffer finalBuffer = ByteBuffer.allocate(bufferSize + 4);
        finalBuffer.put(contentBuffer.array());
        finalBuffer.putInt(crcValue);

        return finalBuffer.array();
    }

    // A simple representation for logging purposes.
    @Override
    public String toString() {
        return String.format(
                "APID: %d | SEQ: %d | TSTAMP: %d | PAYLOAD_SIZE: %d B",
                apid.getId(), sequenceCount, timestamp, payload.length);
    }
}