package guano;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;


/**
 * This DataInputStream has special helper methods for parsing the RIFF/WAVE
 * packed file structure, as well as extracting little-endian values from a
 * WAVE file.
 *
 * Created by driggs on 12/10/16.
 */
public class WaveDataInputStream extends DataInputStream {

    /**
     * Representation of a RIFF chunk.
     */
    public static class Chunk {
        public String id;
        public int size;
        public byte[] data;

        public Chunk(String id, int size, byte[] data) {
            this.id = id;
            this.size = size;
            this.data = data;
        }

        public String toString() {
            return String.format("%s[%d]", this.id, this.size);
        }

    }

    /**
     * Creates a DataInputStream that uses the specified
     * underlying InputStream.
     *
     * @param in the specified input stream
     */
    public WaveDataInputStream(InputStream in) {
        super(in);
    }

    /**
     * Read the next RIFF chunk from our current offset.
     * @return
     * @throws IOException
     */
    public Chunk readChunk() throws IOException {
        String id = this.readString(4);
        int size = this.readWavInt();
        byte[] data = new byte[size];
        this.readFully(data);
        return new Chunk(id, size, data);
    }

    /**
     * Read an ASCII string of arbitrary size.
     * @param size
     * @return
     * @throws IOException
     */
    public String readString(int size) throws IOException {
        byte[] bytes = new byte[size];
        this.readFully(bytes);
        return new String(bytes);
    }

    /**
     * Read a four-byte little-endian int.
     * @return
     * @throws IOException
     */
    public int readWavInt() throws IOException {
        byte[] bytes = new byte[4];
        this.readFully(bytes);

        long val = (
            (bytes[0] & 0xFF) | ((bytes[1] & 0xFF) << 8) | ((bytes[2] & 0xFF) << 16) | ((bytes[3] & 0xFF) << 24)
        );

        return (int) val;
    }

    /**
     * Read a two-byte little-indian short.
     * @return
     * @throws IOException
     */
    public short readWavShort() throws IOException {
        byte[] bytes = new byte[2];
        this.readFully(bytes);

        int val = (bytes[0] & 0xFF) | ((bytes[1] & 0xFF) << 8);

        return (short) val;
    }

}
