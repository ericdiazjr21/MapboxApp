package ericdiaz.program.gotennachallenge.api;

import androidx.annotation.NonNull;

import com.mapbox.api.directions.v5.DirectionsCriteria;
import com.mapbox.api.directions.v5.MapboxDirections;
import com.mapbox.api.directions.v5.models.DirectionsResponse;
import com.mapbox.geojson.Point;

import retrofit2.Response;

/**
 * A Mapbox network class for accessing directions service API
 * <p>
 * Created: 8/17/19
 *
 * @author Eric Diaz
 */

public class MapboxDirectionsService {

    public MapboxDirections getDirections(@NonNull final String accessToken,
                                          @NonNull final Point origin,
                                          @NonNull final Point destination) {
        return MapboxDirections.builder()
          .origin(origin)
          .destination(destination)
          .overview(DirectionsCriteria.OVERVIEW_FULL)
          .profile(DirectionsCriteria.PROFILE_WALKING)
          .accessToken(accessToken)
          .build();
    }
}
