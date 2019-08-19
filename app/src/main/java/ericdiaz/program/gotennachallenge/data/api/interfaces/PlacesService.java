package ericdiaz.program.gotennachallenge.data.api.interfaces;

import ericdiaz.program.gotennachallenge.model.Place;
import io.reactivex.Single;
import retrofit2.http.GET;

/**
 * A Retrofit service Interface for connection to API
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
