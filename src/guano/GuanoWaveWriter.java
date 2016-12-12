package guano;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.OffsetDateTime;
import java.util.*;


/**
 * Simple class which writes 16-bit mono .WAV files with GUANO metadata.
 *
 * Created by driggs on 12/11/16.
 */
public class GuanoWaveWriter {

    private final FileOutputStream fos;
    private short[] audioData = null;
    private int sampleRate;  // Hz
    private short nChannels = 1;
    private short sampleWidth = 2;  // bytes

    /** Stateful mapping of namespace to field key->value mapping */
    private Map<String, Map<String, String>> namespaceFields = new HashMap<>();

    public GuanoWaveWriter(String filename) throws IOException {
        this(new File(filename));
    }

    public GuanoWaveWriter(File file) throws IOException {
        this(new FileOutputStream(file));
    }

    public GuanoWaveWriter(FileOutputStream fos) throws IOException {
        this.fos = fos;
        namespaceFields.put("", new HashMap<String, String>());
    }

    public void setString(String fieldname, String value) {
        setString("", fieldname, value);
    }

    public void setString(GuanoField field, String value) {
        setString("", field.toString(), value);
    }

    public void setString(String namespace, String fieldname, String value) {
        if (!namespaceFields.containsKey(namespace)) {
            namespaceFields.put(namespace, new HashMap<String, String>());
        }
        namespaceFields.get(namespace).put(fieldname, value);
    }

    public void setInt(String fieldname, int value) {
        setString(fieldname, Integer.toString(value));
    }

    public void setInt(GuanoField field, int value) {
        setString(field, Integer.toString(value));
    }

    public void setInt(String namespace, String fieldname, int value) {
        setString(namespace, fieldname, Integer.toString(value));
    }

    public void setFloat(String fieldname, float value) {
        setString(fieldname, Float.toString(value));
    }

    public void setFloat(GuanoField field, float value) {
        setString(field, Float.toString(value));
    }

    public void setFloat(String namespace, String fieldname, float value) {
        setString(namespace, fieldname, Float.toString(value));
    }

    public void setAudioData(int sampleRate, short[] audioData) {
        this.sampleRate = sampleRate;
        this.audioData = audioData;
    }

    public boolean validate() {
        return validate(true);
    }

    public boolean validate(boolean exceptionOnFail) {
        boolean valid = true;
        List<GuanoField> requiredFields = Arrays.asList(GuanoField.TIMESTAMP);
        Map<String, String> topLevelFields = namespaceFields.get("");
        for (GuanoField field : requiredFields) {
            if (!topLevelFields.containsKey(field.toString())) {
                valid = false;
                if (exceptionOnFail) throw new IllegalArgumentException("Missing required metadata field "+field.toString());
            }
        }
        return valid;
    }

    private String renderMetadata() {
        StringBuilder sb = new StringBuilder();
        sb.append("GUANO|Version: 1.0\n");
        for (String ns : namespaceFields.keySet()) {
            for (Map.Entry<String, String> field : namespaceFields.get(ns).entrySet()) {
                if (ns.isEmpty()) {
                    sb.append(String.format("%s: %s\n", field.getKey(), field.getValue()));
                } else {
                    sb.append(String.format("%s|%s: %s\n", ns, field.getKey(), field.getValue()));
                }
            }
        }
        return sb.toString();
    }

    public void write() throws IOException {
        String metadata = renderMetadata();
        byte[] metadataBytes = metadata.getBytes("UTF-8");

        int audioDataSize = audioData.length * sampleWidth * nChannels;

        int riffSize = 4 + (8 + 16) + (8 + audioDataSize) + (8 + metadataBytes.length + (metadataBytes.length % 2));

        try (WaveDataOutputStream out = new WaveDataOutputStream(fos)) {

            // HEADER
            out.writeBytes("RIFF");
            out.writeWavInt(riffSize);
            out.writeBytes("WAVE");

            // FORMAT CHUNK
            out.writeBytes("fmt ");
            out.writeWavInt(16);  // fmt_ subchunk size
            out.writeWavShort(1);  // PCM audio format
            out.writeWavShort(nChannels);
            out.writeWavInt(sampleRate);
            out.writeWavInt(sampleRate * nChannels * sampleWidth);  // byte rate
            out.writeWavShort(nChannels * sampleWidth);  // block alignment
            out.writeWavShort(sampleWidth * 8);  // samplewidth in bits

            // DATA CHUNK
            out.writeBytes("data");
            out.writeWavInt(audioDataSize);
            out.writeWavBytes(audioData);

            // GUANO METADATA CHUNK
            out.writeBytes("guan");
            out.writeWavInt(metadataBytes.length + (metadataBytes.length % 2));  // pad for 16-bit alignment
            out.write(metadataBytes);
            if ((metadataBytes.length % 2) == 1) {
                out.writeChar('\n');  // pad for 16-bit alignment
            }

            // FINALIZE
            out.flush();
            out.close();
        }
    }

    public static void main(String[] args) {
        try {
            GuanoWaveWriter writer = new GuanoWaveWriter("guano_out.wav");

            int sampleRate = 250_000;
            int lengthSeconds = 3;
            short[] audioData = new short[sampleRate * lengthSeconds];  // pretend this is real audio data
            writer.setAudioData(sampleRate, audioData);

            writer.setString(GuanoField.TIMESTAMP, OffsetDateTime.now().toString());
            writer.setFloat(GuanoField.LENGTH, lengthSeconds);
            writer.setString(GuanoField.MAKE, "Myotisoft");
            writer.setString(GuanoField.SPECIES_MANUAL_ID, "NOISE");
            writer.setString(GuanoField.NOTE, "This is a fake recording from our GUANO Writer test.\\nA new Line!\\nAnd another.");
            writer.setInt("MSFT", "Fnord", 42);

            writer.validate();
            writer.write();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
