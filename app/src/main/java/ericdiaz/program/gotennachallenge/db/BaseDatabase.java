package ericdiaz.program.gotennachallenge.db;

import androidx.annotation.NonNull;

import java.util.List;

import ericdiaz.program.gotennachallenge.model.Place;

public interface BaseDatabase {

    boolean isDatabaseEmpty();

    void insertPlace(@NonNull final Place place);

    List<Place> getAllPlaces();

    void deletePlace(@NonNull final Place place);

}
