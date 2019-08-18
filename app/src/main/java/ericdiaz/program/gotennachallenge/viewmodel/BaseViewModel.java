package ericdiaz.program.gotennachallenge.viewmodel;

import androidx.annotation.NonNull;

import com.mapbox.geojson.Point;

import ericdiaz.program.gotennachallenge.api.OnNetworkResponseFailure;
import ericdiaz.program.gotennachallenge.api.OnNetworkResponseSuccess;
import ericdiaz.program.gotennachallenge.model.Place;
import io.reactivex.Single;

public interface BaseViewModel {

    Single<Place[]> getPlacesData();

    void getDirectionsData(@NonNull final String accessToken,
                           @NonNull final Point origin,
                           @NonNull final Point destination,
                           @NonNull OnNetworkResponseSuccess responseSuccess,
                           @NonNull OnNetworkResponseFailure responseFailure);

}
