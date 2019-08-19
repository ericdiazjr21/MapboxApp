package ericdiaz.program.gotennachallenge.repository;

import ericdiaz.program.gotennachallenge.data.api.RetrofitServiceGenerator;
import ericdiaz.program.gotennachallenge.model.Place;
import ericdiaz.program.gotennachallenge.repository.interfaces.BaseNetworkRepository;
import io.reactivex.Single;

/**
 * A repository for conducting places data network calls
 * <p>
 * Created 8/13/19
 *
 * @author Eric Diaz
 */

public final class PlacesNetworkRepository implements BaseNetworkRepository {

    //==============================================================================================
    // BaseNetworkRepository Interface Methods Implementation
    //==============================================================================================

    @Override
    public Single<Place[]> getPlaces() {
        return RetrofitServiceGenerator.getPlacesService().getPlaces();
    }
}
