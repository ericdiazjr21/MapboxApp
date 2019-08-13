package ericdiaz.program.gotennachallenge.api;

import ericdiaz.program.gotennachallenge.model.Places;
import retrofit2.Call;
import retrofit2.http.GET;

public interface PlacesService {

    String PATH = "development/scripts/get_map_pins.php”";

    @GET(PATH)
    Call<Places> getPlaces();
}
