package ericdiaz.program.gotennachallenge.repository;

import androidx.annotation.NonNull;

import ericdiaz.program.gotennachallenge.model.Place;

/**
 * A base database repository for reading and writing to local storage
 * <p>
 * Created: 8/14/19
 *
 * @author Eric Diaz
 */

public interface BaseDatabaseRepository {

    boolean isEmpty();

    void insertPlace(@NonNull final Place place);

    Place[] getAllPlaces();

    void deletePlace(@NonNull final Place place);
}
