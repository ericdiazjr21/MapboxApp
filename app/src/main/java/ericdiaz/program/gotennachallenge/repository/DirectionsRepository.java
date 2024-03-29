package ericdiaz.program.gotennachallenge.repository;

import androidx.annotation.NonNull;

import com.mapbox.geojson.Point;

import ericdiaz.program.gotennachallenge.data.api.MapboxDirectionsService;
import ericdiaz.program.gotennachallenge.repository.interfaces.BaseDirectionsRepository;
import io.reactivex.Single;
import io.reactivex.schedulers.Schedulers;

/**
 * A repository for retrieving directions data from network.
 * <p>
 * Created: 8/14/19
 *
 * @author Eric Diaz
 */

public class DirectionsRepository implements BaseDirectionsRepository {

    //==============================================================================================
    // BaseDirectionRepository Interface Methods Implementation
    //==============================================================================================

    @Override
    public Single<MapboxDirectionsService> getDirections(@NonNull final String accessToken,
                                                         @NonNull final Point origin,
                                                         @NonNull final Point destination) {
        return Single.just(MapboxDirectionsService.getDirectionsService()).subscribeOn(Schedulers.io());
    }
}
