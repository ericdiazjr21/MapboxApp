package ericdiaz.program.gotennachallenge.viewmodel;

import androidx.lifecycle.ViewModel;

import ericdiaz.program.gotennachallenge.model.Place;
import ericdiaz.program.gotennachallenge.repository.BaseNetworkRepository;
import ericdiaz.program.gotennachallenge.repository.PlacesNetworkRepository;
import io.reactivex.Single;

/**
 * A ViewModel that is lifecycle-aware for maneging places data
 *
 * Created: 8/13/19
 *
 * @author Eric Diaz
 */

public class PlacesViewModel extends ViewModel implements BaseViewModel {

    private BaseNetworkRepository placesRepository;

    public PlacesViewModel() {
        placesRepository = new PlacesNetworkRepository();
    }

    @Override
    public Single<Place[]> getPlacesData() {
        return placesRepository.getPlace();
    }
}
