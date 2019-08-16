package ericdiaz.program.gotennachallenge.repository;

import androidx.annotation.NonNull;

import ericdiaz.program.gotennachallenge.model.Place;

public interface BaseDatabaseRepository {

    boolean isEmpty();

    void insertPlace(@NonNull final Place place);

    Place[] getAllPlaces();

    void deletePlace(@NonNull final Place place);
}
