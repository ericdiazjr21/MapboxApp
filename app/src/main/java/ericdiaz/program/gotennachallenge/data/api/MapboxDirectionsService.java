package ericdiaz.program.gotennachallenge.data.api;

import androidx.annotation.NonNull;

import com.mapbox.api.directions.v5.DirectionsCriteria;
import com.mapbox.api.directions.v5.MapboxDirections;
import com.mapbox.geojson.Point;

/**
 * A Mapbox network class for retrieving directions data from API
 * <p>
 * Created: 8/17/19
 *
 * @author Eric Diaz
 */

public class MapboxDirectionsService {

    //==============================================================================================
    // Class Properties
    //==============================================================================================

    private static MapboxDirectionsService singleServiceInstance;

    //==============================================================================================
    // Constructor
    //==============================================================================================

    private MapboxDirectionsService() {
    }

    //==============================================================================================
    // Class Instance Methods
    //==============================================================================================

    public static MapboxDirectionsService getDirectionsService() {
        if (singleServiceInstance == null) {
            singleServiceInstance = new MapboxDirectionsService();
        }
        return singleServiceInstance;
    }

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
