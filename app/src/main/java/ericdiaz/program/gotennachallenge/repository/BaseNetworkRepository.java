package ericdiaz.program.gotennachallenge.repository;

import ericdiaz.program.gotennachallenge.model.Place;
import io.reactivex.Single;

public interface BaseNetworkRepository {

    Single<Place[]> getPlace();
}
