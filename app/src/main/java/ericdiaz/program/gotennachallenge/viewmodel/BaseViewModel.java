package ericdiaz.program.gotennachallenge.viewmodel;

import ericdiaz.program.gotennachallenge.model.Place;
import io.reactivex.Single;

public interface BaseViewModel {

    Single<Place[]> getPlacesData();
}
