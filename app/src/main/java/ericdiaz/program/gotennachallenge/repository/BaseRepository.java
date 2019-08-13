package ericdiaz.program.gotennachallenge.repository;

import ericdiaz.program.gotennachallenge.model.Place;
import io.reactivex.Single;

public interface BaseRepository {

    Single<Place[]> getPlace();
}
