package ericdiaz.program.gotennachallenge.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;

import ericdiaz.program.gotennachallenge.model.Place;
import ericdiaz.program.gotennachallenge.repository.BaseDatabaseRepository;
import ericdiaz.program.gotennachallenge.repository.BaseNetworkRepository;
import ericdiaz.program.gotennachallenge.repository.PlacesDatabaseRepository;
import ericdiaz.program.gotennachallenge.repository.PlacesNetworkRepository;
import io.reactivex.Single;
import io.reactivex.schedulers.Schedulers;

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

    public PlacesViewModel(@NonNull Application application) {
        super(application);
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
}
