package ericdiaz.program.gotennachallenge.api;

import androidx.annotation.NonNull;

import com.mapbox.api.directions.v5.models.DirectionsResponse;

import retrofit2.Response;

public interface OnNetworkResponseSuccess {
    void onSuccess(@NonNull final Response<DirectionsResponse> directionsResponse);
}
