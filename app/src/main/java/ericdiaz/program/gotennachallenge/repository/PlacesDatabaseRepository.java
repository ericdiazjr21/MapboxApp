package ericdiaz.program.gotennachallenge.repository;

import android.content.Context;

import androidx.annotation.NonNull;

import ericdiaz.program.gotennachallenge.db.BaseDatabase;
import ericdiaz.program.gotennachallenge.db.PlacesDatabase;
import ericdiaz.program.gotennachallenge.model.Place;

/**
 * A repository for reading and writing to local storage
 *
 * Created: 8/14/19
 *
 * @author Eric Diaz
 */

public final class PlacesDatabaseRepository implements BaseDatabaseRepository {

    //==============================================================================================
    // Class Properties
    //==============================================================================================

    private final BaseDatabase placesDatabase;

    //==============================================================================================
    // Constructor
    //==============================================================================================

    public PlacesDatabaseRepository(@NonNull final Context context) {
        placesDatabase = PlacesDatabase.getSingleDatabaseInstance(context);
    }

    //==============================================================================================
    // BaseDatabaseRepository Interface Methods Implementation
    //==============================================================================================

    @Override
    public boolean isEmpty() {
        return placesDatabase.isEmpty();
    }

    @Override
    public void insertPlace(@NonNull final Place place) {
        placesDatabase.insertPlace(place);
    }

    @Override
    public Place[] getAllPlaces() {
        return placesDatabase.getAllPlaces();
    }

    @Override
    public void deletePlace(@NonNull final Place place) {
        placesDatabase.deletePlace(place);
    }
}
