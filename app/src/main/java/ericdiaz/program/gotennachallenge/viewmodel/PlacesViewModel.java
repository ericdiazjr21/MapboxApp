package ericdiaz.program.gotennachallenge.viewmodel;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;

import com.mapbox.api.directions.v5.models.DirectionsResponse;
import com.mapbox.geojson.Point;

import ericdiaz.program.gotennachallenge.api.OnNetworkResponseFailure;
import ericdiaz.program.gotennachallenge.api.OnNetworkResponseSuccess;
import ericdiaz.program.gotennachallenge.model.Place;
import ericdiaz.program.gotennachallenge.repository.BaseDatabaseRepository;
import ericdiaz.program.gotennachallenge.repository.BaseDirectionsRepository;
import ericdiaz.program.gotennachallenge.repository.BaseNetworkRepository;
import ericdiaz.program.gotennachallenge.repository.DirectionsRepository;
import ericdiaz.program.gotennachallenge.repository.PlacesDatabaseRepository;
import ericdiaz.program.gotennachallenge.repository.PlacesNetworkRepository;
import io.reactivex.Single;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A lifecycle-aware ViewModel for managing data
 * <p>
 * Created: 8/13/19
 *
 * @author Eric Diaz
 */

public final class PlacesViewModel extends AndroidViewModel implements BaseViewModel {

    //==============================================================================================
    // Class Properties
    //==============================================================================================
    private static final String TAG = "PlacesViewModel";
    private final BaseNetworkRepository placesNetworkRepository;
    private final BaseDirectionsRepository directionsRepository;
    private final BaseDatabaseRepository placesDatabaseRepository;
    private Disposable disposable;

    //==============================================================================================
    // Constructor
    //==============================================================================================

    public PlacesViewModel(@NonNull Application application) {
        super(application);
        placesNetworkRepository = new PlacesNetworkRepository();
        directionsRepository = new DirectionsRepository();
        placesDatabaseRepository = new PlacesDatabaseRepository(application);
    }

    //==============================================================================================
    // BaseViewModel Interface Methods
    //==============================================================================================

    @Override
    public Single<Place[]> getPlacesData() {
        if (placesDatabaseRepository.isEmpty()) {
            return placesNetworkRepository
              .getPlaces()
              .map(places -> {
                  storePlacesInDatabase(places);
                  return places;
              });
        } else {
            return Single.just(placesDatabaseRepository
              .getAllPlaces()).subscribeOn(Schedulers.io());
        }
    }

    @Override
    public void getDirectionsData(@NonNull final String accessToken,
                                  @NonNull final Point origin,
                                  @NonNull final Point destination,
                                  @NonNull final OnNetworkResponseSuccess responseSuccess,
                                  @NonNull final OnNetworkResponseFailure responseFailure) {
        disposable = directionsRepository
          .getDirections(accessToken, origin, destination)
          .subscribe(mapboxDirectionsService -> mapboxDirectionsService
            .getDirections(accessToken, origin, destination)
            .enqueueCall(new Callback<DirectionsResponse>() {
                @Override
                public void onResponse(Call<DirectionsResponse> call, Response<DirectionsResponse> response) {
                    responseSuccess.onSuccess(response);
                }

                @Override
                public void onFailure(Call<DirectionsResponse> call, Throwable t) {
                    responseFailure.onFailure(t);
                }
            }));
    }

    //==============================================================================================
    // Class Instance Methods
    //==============================================================================================

    private void storePlacesInDatabase(Place[] places) {
        for (Place place : places) {
            placesDatabaseRepository.insertPlace(place);
        }
    }

    //==============================================================================================
    // Super Class Methods
    //==============================================================================================

    @Override
    protected void onCleared() {
        super.onCleared();
        disposable.dispose();
    }
}
