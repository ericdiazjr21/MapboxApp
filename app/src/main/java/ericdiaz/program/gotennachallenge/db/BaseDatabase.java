package ericdiaz.program.gotennachallenge.db;

import androidx.annotation.NonNull;

import ericdiaz.program.gotennachallenge.model.Place;

public interface BaseDatabase {

    boolean isEmpty();

    void insertPlace(@NonNull final Place place);

    Place[] getAllPlaces();

    void deletePlace(@NonNull final Place place);

}
