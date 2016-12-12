package guano;

import java.io.DataOutputStream;
import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.OutputStream;


/**
 * Created by driggs on 12/11/16.
 */
public class WaveDataOutputStream extends DataOutputStream {

    /**
     * Creates a new data output stream to write data to the specified
     * underlying output stream. The counter <code>written</code> is
     * set to zero.
     *
     * @param out the underlying output stream, to be saved for later
     *            use.
     * @see FilterOutputStream#out
     */
    public WaveDataOutputStream(OutputStream out) {
        super(out);
    }

    /**
     * Increases the written counter by the specified value
     * until it reaches Integer.MAX_VALUE.
     */
    private void incCount(int value) {
        int temp = written + value;
        if (temp < 0) {
            temp = Integer.MAX_VALUE;
        }
        written = temp;
    }


    public void writeWavInt(int v) throws IOException {
        out.write((v >>>  0) & 0xFF);
        out.write((v >>>  8) & 0xFF);
        out.write((v >>> 16) & 0xFF);
        out.write((v >>> 24) & 0xFF);
        incCount(4);
    }

    public void writeWavShort(int v) throws IOException {
        out.write((v >>> 0) & 0xFF);
        out.write((v >>> 8) & 0xFF);
        incCount(2);
    }

    public void writeWavBytes(short[] audioData) throws IOException {
        for (short v : audioData) {
            out.write((v >>> 0) & 0xFF);
            out.write((v >>> 8) & 0xFF);
        }
        incCount(2 * audioData.length);
    }

}
