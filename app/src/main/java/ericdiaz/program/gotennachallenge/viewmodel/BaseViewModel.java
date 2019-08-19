package ericdiaz.program.gotennachallenge.viewmodel;

import androidx.annotation.NonNull;

import com.mapbox.geojson.Point;

import ericdiaz.program.gotennachallenge.data.api.interfaces.OnNetworkResponseFailure;
import ericdiaz.program.gotennachallenge.data.api.interfaces.OnNetworkResponseSuccess;
import ericdiaz.program.gotennachallenge.model.Place;
import io.reactivex.Single;

/**
 * A base ViewModel Interface for communicating between repositories and views
 * <p>
 * Created: 8/14/19
 *
 * @author Eric Diaz
 */

public interface BaseViewModel {

    Single<Place[]> getPlacesData();

    void getDirectionsData(@NonNull final String accessToken,
                           @NonNull final Point origin,
                           @NonNull final Point destination,
                           @NonNull OnNetworkResponseSuccess responseSuccess,
                           @NonNull OnNetworkResponseFailure responseFailure);

}
