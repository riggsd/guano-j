guano-j
-------

Java reference implementation of the [GUANO metadata specification](https://github.com/riggsd/guano-py/blob/master/doc/guano_specification.md).

Don't use this code yet!


Example Usage
=============

Reading metadata from an existing file:

```java
import guano.GuanoReader

try {

    // create an instance of a GUANO Reader
    GuanoReader reader = new GuanoReader("test.wav");

    // iterate over all the namespaces present
    for (String ns : reader.getNamespaces()) {
        System.out.println(ns);

        // iterate over all the fields under a specified namespace
        Map<String, String> fields = reader.getFields(ns);
        for (Map.Entry<String, String> field : fields.entrySet()) {
            System.out.println(String.format("\t%s:\t%s", field.getKey(), field.getValue()));
        }

    // get the value for a top-level field
    reader.getString("Species Auto ID");

    // get the value (cast to appropriate data type) of a field underneath a namespace
    reader.getFloat("GUANO", "Version");

    // another way to reference a namespaced field
    reader.getInt("SB|Avg Duration");

    // all defined top-level fields are also available as Enums
    reader.getString(GuanoField.SPECIES_MANUAL_ID);
    for (GuanoField field : GuanoField.values()) {
        reader.getString(field);
    }

catch (IOException e) {
    e.printStackTrace();
}
```

Writing to a new .WAV file:

```java
try {

    // create a GUANO Writer instance
    GuanoWaveWriter writer = new GuanoWaveWriter("guano_out.wav");

    // here we create some fake audio data
    int sampleRate = 250_000;
    int lengthSeconds = 3;
    short[] audioData = new short[sampleRate * lengthSeconds];  // pretend this is real audio data
    writer.setAudioData(sampleRate, audioData);

    // populate the metadata fields
    writer.setString(GuanoField.TIMESTAMP, OffsetDateTime.now().toString());  // `Timestamp` is required!
    writer.setFloat(GuanoField.LENGTH, lengthSeconds);
    writer.setString(GuanoField.MAKE, "Myotisoft");
    writer.setString(GuanoField.SPECIES_MANUAL_ID, "NOISE");
    writer.setString(GuanoField.NOTE, "This is a fake recording from our GUANO Writer test.\\nA new Line!\\nAnd another.");
    writer.setInt("MSFT", "Fnord", 42);

    // validate it and dump it to a file
    writer.validate();
    writer.write();

} catch (IOException e) {
    e.printStackTrace();
}
```

Installation
============

This project is source compatible with Java 7+.

    $> git clone https://github.com/riggsd/guano-j.git
    $> cd guano-j
    $> ant
    $> java -jar dist/guano*.jar guano.GuanoReader test.wav  # or include the .jar in your own project

But seriously, don't use this code yet!
