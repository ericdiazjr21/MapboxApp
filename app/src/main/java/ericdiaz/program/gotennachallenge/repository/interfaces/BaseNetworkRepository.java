package ericdiaz.program.gotennachallenge.repository.interfaces;

import ericdiaz.program.gotennachallenge.model.Place;
import io.reactivex.Single;

/**
 * A base repository Interface for retrieving places data from network.
 * <p>
 * Created: 8/14/19
 *
 * @author Eric Diaz
 */

public interface BaseNetworkRepository {

    Single<Place[]> getPlaces();
}
