package guano;

import java.io.*;
import java.util.*;


/**
 * This is the main GUANO metadata reading implementation.
 *
 * Created by driggs on 12/10/16.
 */
public class GuanoReader {

    public static final String GUANO_CHUNK_ID = "guan";

    private Set<String> namespaceNames = new HashSet<>();
    private Map<String, Set<String>> namespaceFieldNames = new HashMap<>();
    private Map<String, Map<String, String>> namespaceFields = new HashMap<>();

    public GuanoReader(String filename) throws IOException {
        this(new File(filename));
    }

    public GuanoReader(File file) throws IOException {
        this(new FileInputStream(file));
    }

    public GuanoReader(FileInputStream fis) throws IOException {
        try {
            WaveReader reader = new WaveReader(fis);
            if (reader.hasChunk(GUANO_CHUNK_ID)) {
                String data = new String(reader.getChunk(GUANO_CHUNK_ID));
                BufferedReader br = new BufferedReader(new StringReader(data));
                String line;
                while ((line = br.readLine()) != null) {
                    if (line.trim().isEmpty()) continue;

                    String[] toks = line.split(":", 2);
                    String ns = "";
                    String field = toks[0];
                    String val = toks[1];
                    if (field.contains("|")) {
                        toks = field.split("\\|", 2);
                        ns = toks[0];
                        field = toks[1];
                    }
                    insert(ns, field, val);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void insert(String ns, String field, String val) {
        ns = ns.trim(); field = field.trim(); val = val.trim();
        //System.out.println(String.format("%s\t%s\t%s", ns, field, val));

        namespaceNames.add(ns);

        if (!namespaceFieldNames.containsKey(ns)) {
            namespaceFieldNames.put(ns, new HashSet<String>());
        }
        namespaceFieldNames.get(ns).add(field);

        if (!namespaceFields.containsKey(ns)) {
            namespaceFields.put(ns, new HashMap<String, String>());
        }
        namespaceFields.get(ns).put(field, val);
    }

    public Set<String> getNamespaces() {
        return namespaceNames;
    }

    public Set<String> getFieldnames(String namespace) {
        return namespaceFieldNames.get(namespace);
    }

    public Map<String, String> getFields(String namespace) {
        return namespaceFields.get(namespace);
    }

    public String getString(String fieldname) {
        String ns = "";
        if (fieldname.contains("\\|")) {
            String[] toks = fieldname.split("\\|", 2);
            ns = toks[0];
            fieldname = toks[1];
        }
        return getString(ns, fieldname);
    }

    public String getString(String namespace, String fieldname) {
        namespace = namespace.trim(); fieldname = fieldname.trim();
        return namespaceFields.get(namespace).get(fieldname);
    }

    public Integer getInt(String fieldname) {
        return Integer.parseInt(getString(fieldname));
    }

    public Integer getInt(String namespace, String fieldname) {
        return Integer.parseInt(getString(namespace, fieldname));
    }

    public Float getFloat(String fieldname) {
        return Float.parseFloat(getString(fieldname));
    }

    public Float getFloat(String namespace, String fieldname) {
        return Float.parseFloat(getString(namespace, fieldname));
    }

    public static void main(String[] args) {
        if (args.length < 1) {
            System.err.println("usage: java guano.GuanoReader WAVFILE");
            System.exit(2);
        }

        try {
            GuanoReader reader = new GuanoReader(args[0]);
            for (String ns : reader.getNamespaces()) {
                System.out.println(ns);
                Map<String, String> fields = reader.getFields(ns);
                for (Map.Entry<String, String> field : fields.entrySet()) {
                    System.out.println(String.format("\t%s:\t%s", field.getKey(), field.getValue()));
                }
                System.out.println();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
