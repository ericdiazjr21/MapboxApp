package ericdiaz.program.gotennachallenge.repository;

import androidx.annotation.NonNull;

import com.mapbox.geojson.Point;

import ericdiaz.program.gotennachallenge.api.MapboxDirectionsService;
import io.reactivex.Single;

public interface BaseDirectionsRepository {

    Single<MapboxDirectionsService> getDirections(@NonNull final String accessToken,
                                                  @NonNull final Point origin,
                                                  @NonNull final Point destination);
}
