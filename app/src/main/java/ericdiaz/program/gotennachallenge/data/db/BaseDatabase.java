package ericdiaz.program.gotennachallenge.data.db;

import androidx.annotation.NonNull;

import ericdiaz.program.gotennachallenge.model.Place;

/**
 * A base database interface for reading and writing to local storage
 *
 * Created: 8/15/19
 *
 * @author Eric Diaz
 */

public interface BaseDatabase {

    boolean isEmpty();

    void insertPlace(@NonNull final Place place);

    Place[] getAllPlaces();

    void deletePlace(@NonNull final Place place);

}
