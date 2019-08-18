package ericdiaz.program.gotennachallenge.utils;

import androidx.annotation.NonNull;

/**
 * A utility class for the MapBox API
 * <p>
 * Created 8/13/19
 *
 * @author Eric Diaz
 */

public final class MapBoxUtils {

    public static final String ACCESS_KEY = "pk.eyJ1IjoiZXJpY2RpYXoiLCJhIjoiY2p6OThqZzJiMDRxaDNkcGo0Y3E4Z3M3ZyJ9.X9WoMdw49Am8iQSUdVWL4w";
    public static final String ICON_NAME = "pin-marker-icon";
    public static final String PERSON_ICON_ID = "person_icon_id";
    public static final String PERSON_SOURCE_ID = "person_source_id";
    public static final String PERSON_LAYER_ID = "person_layer_id";
    public static final String DASHED_DIRECTIONS_LINE_LAYER_ID = "dashed_directions_line_layer_id";
    public static final String DASHED_DIRECTIONS_LINE_LAYER_SOURCE_ID = "dashed_directions_line_layer_source_id";

    public static String getLayerId(@NonNull final String iD) {
        return "layer-id" + ": " + iD;
    }

    public static String getSourceId(@NonNull final String iD) {
        return "source-id" + ": " + iD;
    }
}
