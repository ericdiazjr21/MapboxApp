package ericdiaz.program.gotennachallenge.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;

import com.mapbox.api.directions.v5.models.DirectionsResponse;
import com.mapbox.geojson.Point;

import ericdiaz.program.gotennachallenge.api.MapboxDirectionsService;
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
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A ViewModel that is lifecycle-aware for maneging places data
 * <p>
 * Created: 8/13/19
 *
 * @author Eric Diaz
 */

public class PlacesViewModel extends AndroidViewModel implements BaseViewModel {

    private BaseNetworkRepository placesNetworkRepository;
    private BaseDatabaseRepository placesDatabaseRepository;
    private BaseDirectionsRepository directionsRepository;
    private Disposable disposable;

    public PlacesViewModel(@NonNull Application application) {
        super(application);
        directionsRepository = new DirectionsRepository();
        placesNetworkRepository = new PlacesNetworkRepository();
        placesDatabaseRepository = new PlacesDatabaseRepository(application);
    }

    @Override
    public Single<Place[]> getPlacesData() {
        if (placesDatabaseRepository.isEmpty()) {
            return placesNetworkRepository.getPlace()
              .map(places -> {
                  for (Place place : places) {
                      placesDatabaseRepository.insertPlace(place);
                  }
                  return places;
              });
        } else {
            return Single.just(placesDatabaseRepository
              .getAllPlaces());
        }
    }

    public void getDirectionsData(@NonNull String accessToken,
                                  @NonNull Point origin,
                                  @NonNull Point destination,
                                  @NonNull OnNetworkResponseSuccess responseSuccess,
                                  @NonNull OnNetworkResponseFailure responseFailure) {
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

    @Override
    protected void onCleared() {
        super.onCleared();
        disposable.dispose();
    }
}
