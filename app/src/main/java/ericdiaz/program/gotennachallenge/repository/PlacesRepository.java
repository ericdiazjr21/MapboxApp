package ericdiaz.program.gotennachallenge.repository;

import ericdiaz.program.gotennachallenge.api.RetrofitServiceGenerator;
import ericdiaz.program.gotennachallenge.model.Place;
import io.reactivex.Single;

public class PlacesRepository implements BaseRepository{

    @Override
    public Single<Place[]> getPlace() {
        return RetrofitServiceGenerator.getPlacesService().getPlaces();
    }
}
