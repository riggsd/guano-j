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

    /** Stateful list of all namespaces */
    private Set<String> namespaceNames = new HashSet<>();

    /** Stateful mapping of namespace to all of its fieldnames */
    private Map<String, Set<String>> namespaceFieldNames = new HashMap<>();

    /** Stateful mapping of namespace to field key->value mapping */
    private Map<String, Map<String, String>> namespaceFields = new HashMap<>();

    /**
     * Create an instance of a GUANO Reader and parse the underlying file.
     * @param filename
     * @throws IOException
     */
    public GuanoReader(String filename) throws IOException {
        this(new File(filename));
    }

    /**
     * Create an instance of a GUANO Reader and parse the underlying file.
     * @param file
     * @throws IOException
     */
    public GuanoReader(File file) throws IOException {
        this(new FileInputStream(file));
    }

    /**
     * Create an instance of a GUANO Reader and parse the underlying file.
     * @param fis
     * @throws IOException
     */
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
        // Populate our stateful data structures as we parse the underlying file

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

    /** Get a list of all namespaces present in the file */
    public Set<String> getNamespaces() {
        return namespaceNames;
    }

    /**
     * Get a list of all fieldnames present under the specified namespace
     * @param namespace a namespace, or empty string to ask for top-level fields
     * @return
     */
    public Set<String> getFieldnames(String namespace) {
        return namespaceFieldNames.get(namespace);
    }

    /**
     * Get all fields present under the specified namespace as a key->value mapping
     * @param namespace a namespace, or empty string to ask for top-level fields
     * @return
     */
    public Map<String, String> getFields(String namespace) {
        return namespaceFields.get(namespace);
    }

    /**
     * Get a single field's String value.
     * @param field a top-level field
     * @return
     */
    public String getString(GuanoField field) {
        return getString(field.toString());
    }

    /**
     * Get a single field's String value.
     * @param fieldname a top-level fieldname, or pipe-separated namespace and fieldname
     * @return
     */
    public String getString(String fieldname) {
        String ns = "";
        if (fieldname.contains("\\|")) {
            String[] toks = fieldname.split("\\|", 2);
            ns = toks[0];
            fieldname = toks[1];
        }
        return getString(ns, fieldname);
    }

    /**
     * Get a single field's String value.
     * @param namespace
     * @param fieldname
     * @return
     */
    public String getString(String namespace, String fieldname) {
        namespace = namespace.trim(); fieldname = fieldname.trim();
        return namespaceFields.get(namespace).get(fieldname);
    }

    /**
     * Get a single field's int value.
     * @param field a top-level field
     * @return
     */
    public Integer getInt(GuanoField field) {
        return getInt(field.toString());
    }

    /**
     * Get a single field's int value.
     * @param fieldname a top-level fieldname, or pipe-separated namespace and fieldname
     * @return
     */
    public Integer getInt(String fieldname) {
        return Integer.parseInt(getString(fieldname));
    }

    /**
     * Get a single field's int value.
     * @param namespace
     * @param fieldname
     * @return
     */
    public Integer getInt(String namespace, String fieldname) {
        return Integer.parseInt(getString(namespace, fieldname));
    }

    /**
     * Get a single field's float value.
     * @param field a top-level field
     * @return
     */
    public Float getFloat(GuanoField field) {
        return getFloat(field.toString());
    }

    /**
     * Get a single field's float value.
     * @param fieldname a top-level fieldname, or pipe-separated namespace and fieldname
     * @return
     */
    public Float getFloat(String fieldname) {
        return Float.parseFloat(getString(fieldname));
    }

    /**
     * Get a single field's float value.
     * @param namespace
     * @param fieldname
     * @return
     */
    public Float getFloat(String namespace, String fieldname) {
        return Float.parseFloat(getString(namespace, fieldname));
    }

    /**
     * Test application which simply prints metadata fields.
     * @param args
     */
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
