package ericdiaz.program.gotennachallenge.repository;

import ericdiaz.program.gotennachallenge.api.RetrofitServiceGenerator;
import ericdiaz.program.gotennachallenge.model.Place;
import io.reactivex.Single;

/**
 * A repository for conducting network calls
 * <p>
 * Created 8/13/19
 *
 * @author Eric Diaz
 */

public final class PlacesNetworkRepository implements BaseNetworkRepository {

    @Override
    public Single<Place[]> getPlace() {
        return RetrofitServiceGenerator.getPlacesService().getPlaces();
    }
}
