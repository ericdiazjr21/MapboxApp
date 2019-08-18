package ericdiaz.program.gotennachallenge.repository;

import androidx.annotation.NonNull;

import com.mapbox.geojson.Point;

import ericdiaz.program.gotennachallenge.api.MapboxDirectionsService;
import io.reactivex.Single;
import io.reactivex.schedulers.Schedulers;

public class DirectionsRepository implements BaseDirectionsRepository {

    @Override
    public Single<MapboxDirectionsService> getDirections(@NonNull final String accessToken,
                                                         @NonNull final Point origin,
                                                         @NonNull final Point destination) {
        return Single.just(new MapboxDirectionsService()).subscribeOn(Schedulers.io());
    }
}
