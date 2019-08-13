package ericdiaz.program.gotennachallenge.model;

import androidx.annotation.NonNull;

public final class Place {

    private final int id;
    private final String name;
    private final double latitude;
    private final double longitude;
    private final String description;


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
