package ericdiaz.program.gotennachallenge.model;

import androidx.annotation.NonNull;

/**
 * A model class for places data
 * <p>
 * Created: 8/14/19
 *
 * @author Eric Diaz
 */

public final class Place {

    //==============================================================================================
    // Class Properties
    //==============================================================================================

    private final int id;
    private final String name;
    private final double latitude;
    private final double longitude;
    private final String description;

    //==============================================================================================
    // Constructor
    //==============================================================================================

    public Place(final int id,
                 @NonNull final String name,
                 final double latitude,
                 final double longitude,
                 @NonNull final String description) {
        this.id = id;
        this.name = name;
        this.latitude = latitude;
        this.longitude = longitude;
        this.description = description;
    }

    //==============================================================================================
    // Class Instance Methods
    //==============================================================================================

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public String getDescription() {
        return description;
    }
}
