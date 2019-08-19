package ericdiaz.program.gotennachallenge.repository.interfaces;

import androidx.annotation.NonNull;

import com.mapbox.geojson.Point;

import ericdiaz.program.gotennachallenge.data.api.MapboxDirectionsService;
import io.reactivex.Single;

/**
 * A base repository Interface for retrieving directions data from network
 * <p>
 * Created: 8/14/19
 *
 * @author Eric Diaz
 */

public interface BaseDirectionsRepository {

    Single<MapboxDirectionsService> getDirections(@NonNull final String accessToken,
                                                  @NonNull final Point origin,
                                                  @NonNull final Point destination);
}
