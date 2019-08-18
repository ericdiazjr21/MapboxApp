package ericdiaz.program.gotennachallenge.view;

import android.os.Bundle;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.mapbox.api.directions.v5.models.DirectionsRoute;
import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.Style;

import java.util.ArrayList;
import java.util.List;

import ericdiaz.program.gotennachallenge.R;
import ericdiaz.program.gotennachallenge.model.Place;
import ericdiaz.program.gotennachallenge.utils.LocationUtils;
import ericdiaz.program.gotennachallenge.utils.MapboxUtils;
import ericdiaz.program.gotennachallenge.view.recyclerview.PlacesAdapter;
import ericdiaz.program.gotennachallenge.view.recyclerview.PlacesViewHolder;
import ericdiaz.program.gotennachallenge.viewmodel.BaseViewModel;
import ericdiaz.program.gotennachallenge.viewmodel.PlacesViewModel;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;


public class MainActivity extends AppCompatActivity implements PlacesViewHolder.OnItemViewClickedListener {

    private static final String TAG = "MainActivity";
    private MapView mapView;
    private MapboxMap mapboxMap;
    private Disposable disposable;
    private BaseViewModel placesViewModel;
    private LocationUtils locationUtils;
    private final List<DirectionsRoute> directionsRouteList = new ArrayList<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Mapbox.getInstance(this, MapboxUtils.ACCESS_KEY);
        setContentView(R.layout.activity_main);
        initMapBoxView(savedInstanceState);
        locationUtils = new LocationUtils(this);
        placesViewModel = new ViewModelProvider.AndroidViewModelFactory(getApplication()).create(PlacesViewModel.class);
    }

    @Override
    public void onStart() {
        super.onStart();
        mapView.onStart();
    }

    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();
        disposable.dispose();
    }

    @Override
    public void onStop() {
        super.onStop();
        mapView.onStop();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
        locationUtils.tearDown();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }

    @Override
    public void onItemViewClicked(int position) {
        MapboxUtils.drawNavigationPolylineRoute(directionsRouteList.get(position), mapboxMap);
    }

    private void initMapBoxView(@Nullable Bundle savedInstanceState) {
        mapView = findViewById(R.id.mapView);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(mapboxMap -> {
            this.mapboxMap = mapboxMap;

            Style.Builder styleBuilder = new Style.Builder();

            MapboxUtils.addPersonIconToStyle(styleBuilder, getDrawable(R.drawable.ic_person), locationUtils.getUserLastKnownPoint());
            MapboxUtils.addPinIconToStyle(styleBuilder, MainActivity.this.getResources());
            MapboxUtils.addDirectionLinesToStyle(styleBuilder);

            mapboxMap.setStyle(styleBuilder, loadedStyle -> {
                locationUtils.enableLocationComponent(loadedStyle, mapboxMap);

                MapboxUtils.setCameraPosition(mapboxMap, locationUtils.getUserLastKnownLatLng());

                disposable = placesViewModel
                  .getPlacesData()
                  .observeOn(AndroidSchedulers.mainThread())
                  .subscribe(places -> {

                      initRecyclerView(places);

                      for (Place place : places) {
                          MapboxUtils.addLocationPointToStyle(loadedStyle, place.getId(), place.getLongitude(), place.getLatitude());
                          MapboxUtils.addLocationPinLayerToStyle(loadedStyle, place.getId());

                          placesViewModel.getDirectionsData(
                            MapboxUtils.ACCESS_KEY,
                            locationUtils.getUserLastKnownPoint(),
                            locationUtils.getDestinationPoint(place.getLongitude(), place.getLatitude()),
                            directionsResponse -> directionsRouteList.add(directionsResponse.body().routes().get(0)),
                            throwable -> Log.d(TAG, "initMapBoxView: " + throwable.toString()));
                      }
                  });
            });
        });
    }


    private void initRecyclerView(Place[] places) {
        RecyclerView placesRecyclerView = findViewById(R.id.place_recycler_view);
        PlacesAdapter adapter = new PlacesAdapter();
        adapter.setData(places);
        placesRecyclerView.setAdapter(adapter);
        placesRecyclerView.setLayoutManager(new LinearLayoutManager(this, RecyclerView.HORIZONTAL, false));
    }


}
