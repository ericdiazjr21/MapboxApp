package ericdiaz.program.gotennachallenge.repository;

import androidx.annotation.NonNull;

import java.util.List;

import ericdiaz.program.gotennachallenge.model.Place;

public interface BaseDatabaseRepository {

    boolean isDatabaseEmpty();

    void insertPlace(@NonNull final Place place);

    List<Place> getAllPlaces();

    void deletePlace(@NonNull final Place place);
}
