package ericdiaz.program.gotennachallenge.api;

import ericdiaz.program.gotennachallenge.model.Place;
import io.reactivex.Single;
import retrofit2.http.GET;

/**
 * Interface for service connecting to api
 * <p>
 * Created 8/13/2019
 *
 * @author Eric Diaz
 */

public interface PlacesService {

    String PATH = "development/scripts/get_map_pins.php";

    @GET(PATH)
    Single<Place[]> getPlaces();
}
