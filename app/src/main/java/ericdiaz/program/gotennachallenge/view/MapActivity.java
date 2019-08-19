package ericdiaz.program.gotennachallenge.view;

import android.graphics.BitmapFactory;
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
import com.mapbox.mapboxsdk.maps.Style;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import ericdiaz.program.gotennachallenge.R;
import ericdiaz.program.gotennachallenge.model.Place;
import ericdiaz.program.gotennachallenge.utils.LocationUtils;
import ericdiaz.program.gotennachallenge.utils.MapUtils;
import ericdiaz.program.gotennachallenge.view.recyclerview.PlacesAdapter;
import ericdiaz.program.gotennachallenge.view.recyclerview.PlacesViewHolder;
import ericdiaz.program.gotennachallenge.viewmodel.BaseViewModel;
import ericdiaz.program.gotennachallenge.viewmodel.PlacesViewModel;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;

/**
 * Map Activity where UI is implemented
 * <p>
 * Created 8/14/19
 *
 * @author Eric Diaz
 */

public class MapActivity extends AppCompatActivity implements PlacesViewHolder.OnItemViewClickedListener {

    //==============================================================================================
    // Class Properties
    //==============================================================================================

    private static final String TAG = "MapActivity";
    private MapView mapView;
    private Disposable disposable;
    private BaseViewModel placesViewModel;
    private LocationUtils locationUtils;
    private MapUtils mapUtils;
    private final Map<Integer, DirectionsRoute> directionsRouteHashMap = new HashMap<>();

    //==============================================================================================
    // Lifecycle Methods
    //==============================================================================================

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Mapbox.getInstance(this, MapUtils.getAccessKey());
        setContentView(R.layout.activity_main);

        //Initialize ViewModel and Utility
        locationUtils = new LocationUtils(this);
        placesViewModel = new ViewModelProvider.AndroidViewModelFactory(getApplication()).create(PlacesViewModel.class);

        initMap(savedInstanceState);
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
        disposable.dispose();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }

    //==============================================================================================
    // PlacesViewHolder Interface Methods
    //==============================================================================================

    @Override
    public void onItemViewClicked(Place place) {
        placesViewModel
          .getDirectionsData(

            MapUtils.getAccessKey(),

            locationUtils.getUserLastKnownPoint(),

            locationUtils.getDestinationPoint(place.getLongitude(), place.getLatitude()),

            directionsResponse -> {

                if (directionsRouteHashMap.containsKey(place.getId() - 1)) {

                    if (directionsResponse.body() != null) {

                        directionsRouteHashMap

                          .replace(place.getId() - 1,

                            directionsRouteHashMap.get(place.getId() - 1),

                            directionsResponse.body().routes().get(0));
                    }

                } else {

                    directionsRouteHashMap.put(place.getId() - 1, directionsResponse.body().routes().get(0));
                }

                mapUtils.drawNavigationRoute(Objects.requireNonNull(directionsRouteHashMap.get(place.getId() - 1)));
            },
            throwable -> Log.d(TAG, "initMap: " + throwable.toString()));
    }

    //==============================================================================================
    // Class Instance Methods
    //==============================================================================================

    private void initMap(@Nullable Bundle savedInstanceState) {
        mapView = findViewById(R.id.mapView);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(mapboxMap -> {

            Style.Builder styleBuilder = new Style.Builder();

            mapUtils = new MapUtils(mapboxMap, styleBuilder);

            mapUtils.addPersonLayerToStyle(locationUtils.getUserLastKnownPoint());

            mapUtils.addPinIconToStyle(
              BitmapFactory.decodeResource(getResources(), R.drawable.mapbox_marker_icon_default));

            mapUtils.addDirectionLinesToStyle();

            mapboxMap.setStyle(styleBuilder, loadedStyle -> {

                locationUtils.enableLocationComponent(loadedStyle, mapboxMap);

                mapUtils.setLoadedStyle(loadedStyle);

                mapUtils.setCameraPosition(locationUtils.getUserLastKnownLatLng());

                disposable = placesViewModel
                  .getPlacesData()
                  .observeOn(AndroidSchedulers.mainThread())
                  .subscribe(places -> {

                      initRecyclerView(places);

                      for (Place place : places) {
                          mapUtils.addLocationCoordinatesToStyle(
                            place.getId(),
                            place.getLongitude(),
                            place.getLatitude());

                          mapUtils.addLocationPinLayerToStyle(place.getId());
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
