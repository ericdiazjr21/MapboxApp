package ericdiaz.program.gotennachallenge.api;

import androidx.annotation.NonNull;

import com.mapbox.api.directions.v5.models.DirectionsResponse;

import retrofit2.Response;

/**
 * A Interface for communicating network successful response
 * <p>
 * Created: 8/16/19
 *
 * @author Eric Diaz
 */

public interface OnNetworkResponseSuccess {
    void onSuccess(@NonNull final Response<DirectionsResponse> directionsResponse);
}
