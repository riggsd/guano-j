package guano;

import java.io.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;


/**
 * Created by driggs on 12/10/16.
 */
public class WaveReader {

    Collection<String> chunkNames = new ArrayList<>();
    Map<String, byte[]> chunks = new HashMap<>();

    public WaveReader(String filename) throws IOException {
        this(new File(filename));
    }

    public WaveReader(File file) throws IOException {
        this(new FileInputStream(file));
    }

    public WaveReader(FileInputStream fis) throws IOException {
        try (WaveDataInputStream dis = new WaveDataInputStream(fis)) {

            // RIFF
            String id = dis.readString(4);
            int size = dis.readWavInt();
            //System.out.println(String.format("%s %d (%.1fmb)", id, size, (float)size/1024/1024));
            if (!id.equals("RIFF")) throw new IOException("RIFF chunk identifier not found");

            // WAVE
            id = dis.readString(4);
            //System.out.println(id);
            if (!id.equals("WAVE")) throw new IOException("WAVE RIFF type identifier not found");

            // individual subchunks...
            WaveDataInputStream.Chunk chunk;
            while (true) {
                try {
                    chunk = dis.readChunk();
                } catch (EOFException e) {
                    break;
                }
                //System.out.println(chunk);
                chunkNames.add(chunk.id);
                chunks.put(chunk.id, chunk.data);
            }
        }
    }

    public Collection<String> getChunkNames() {
        return chunkNames;
    }

    public boolean hasChunk(String name) {
        return chunkNames.contains(name);
    }

    public byte[] getChunk(String name) {
        return chunks.get(name);
    }

    public Map<String, byte[]> getChunks() {
        return chunks;
    }

    public static void main(String[] args) {
        if (args.length < 1) {
            System.err.println("usage: java guano.WaveReader WAVFILE");
            System.exit(2);
        }

        try {
            WaveReader reader = new WaveReader(args[0]);
            System.out.println(reader.getChunkNames());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
