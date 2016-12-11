package guano;


/**
 * Top-level GUANO metadata fields.
 *
 * Created by driggs on 12/11/16.
 */
public enum GuanoField {

    FILTER_HP ("Filter HP"),
    FILTER_LP ("Filter LP"),
    FIRMWARE_VERSION ("Firmware Version"),
    HARDWARE_VERSION ("Hardware Version"),
    HUMIDITY ("Humidity"),
    LENGTH   ("Length"),
    LOC_ACCURACY  ("Loc Accuracy"),
    LOC_ELEVATION ("Loc Elevation"),
    LOC_POSITION  ("Loc Position"),
    MAKE  ("Make"),
    MODEL ("Model"),
    NOTE  ("Note"),
    SAMPLERATE ("Samplerate"),
    SPECIES_AUTO_ID   ("Species Auto ID"),
    SPECIES_MANUAL_ID ("Species Manual ID"),
    TAGS ("Tags"),
    TE   ("TE"),
    TEMPERATURE_EXT ("Temperature Ext"),
    TEMPERATURE_INT ("Temperature Int"),
    TIMESTAMP ("Timestamp");


    private final String name;

    GuanoField(final String name) {
        this.name = name;
    }

    /**
     * @return the exact GUANO fieldname
     */
    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return name;
    }

}
