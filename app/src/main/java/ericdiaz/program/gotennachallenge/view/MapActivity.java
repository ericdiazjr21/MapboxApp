package ericdiaz.program.gotennachallenge.view;

import android.graphics.BitmapFactory;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.mapbox.geojson.Feature;
import com.mapbox.geojson.FeatureCollection;
import com.mapbox.geojson.Point;
import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.Style;

import java.util.ArrayList;
import java.util.List;

import ericdiaz.program.gotennachallenge.R;
import ericdiaz.program.gotennachallenge.model.Place;
import ericdiaz.program.gotennachallenge.utils.LocationUtils;
import ericdiaz.program.gotennachallenge.utils.MapUtils;
import ericdiaz.program.gotennachallenge.view.recyclerview.PlacesAdapter;
import ericdiaz.program.gotennachallenge.view.recyclerview.PlacesViewHolder;
import ericdiaz.program.gotennachallenge.viewmodel.BaseViewModel;
import ericdiaz.program.gotennachallenge.viewmodel.PlacesViewModel;
import io.reactivex.Completable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;
import timber.log.Timber;

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
    private BaseViewModel placesViewModel;
    private LocationUtils locationUtils;
    private MapUtils mapUtils;
    private CompositeDisposable compositeDisposable = new CompositeDisposable();

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
        mapUtils.tearDown();
        compositeDisposable.dispose();
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

        // Contact directions API when "GET DIRECTIONS" clicked
        placesViewModel
          .getDirectionsData(

            MapUtils.getAccessKey(),

            locationUtils.getUserLastKnownPoint(),

            locationUtils.getDestinationPoint(place.getLongitude(), place.getLatitude()),

            directionsResponse ->

              compositeDisposable.add(Completable.fromAction(() ->

                mapUtils.updateRouteMap(place.getId() - 1, directionsResponse))

                .subscribeOn(Schedulers.computation())

                .observeOn(AndroidSchedulers.mainThread())

                .subscribe(() -> mapUtils.drawNavigationRoute(place.getId() - 1))),

            throwable -> Timber.d("initMap: %s", throwable.toString()));
    }


    //==============================================================================================
    // Class Instance Methods
    //==============================================================================================

    private void initMap(@Nullable Bundle savedInstanceState) {
        mapView = findViewById(R.id.mapView);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(mapboxMap -> {

            //Initialize Style builder and MapUtils
            Style.Builder styleBuilder = new Style.Builder();

            mapUtils = new MapUtils(mapboxMap, styleBuilder);

            //Compose Style object through Builder
            mapUtils.addPersonLayerToStyle(locationUtils.getUserLastKnownPoint());

            mapUtils.addPinIconToStyle(
              BitmapFactory.decodeResource(getResources(), R.drawable.mapbox_marker_icon_default));

            mapUtils.addDirectionLinesToStyle();

            //Set Style from builder and return loadedStyle
            mapboxMap.setStyle(styleBuilder, loadedStyle -> {

                //Begin tracking users location
                locationUtils.enableLocationComponent(loadedStyle, mapboxMap);

                mapUtils.setLoadedStyle(loadedStyle);

                //Set camera animation
                mapUtils.setCameraPosition(locationUtils.getUserLastKnownLatLng());

                //Initialize network call to places API
                compositeDisposable.add(placesViewModel
                  .getPlacesData()
                  .observeOn(AndroidSchedulers.mainThread())
                  .subscribe(places -> {

                      initRecyclerView(places);

                      drawPlacesOnMap(places);
                  }));
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

    private void drawPlacesOnMap(Place[] places) {
        List<Feature> coordinatesList = new ArrayList<>();

        compositeDisposable.add(Completable.fromAction(() -> {
            for (Place place : places) {

                coordinatesList.add(

                  Feature.fromGeometry(

                    Point.fromLngLat(place.getLongitude(), place.getLatitude())));
            }
        }).subscribeOn(Schedulers.computation())

          .observeOn(AndroidSchedulers.mainThread())

          .subscribe(() -> {

              mapUtils.addLocationCoordinatesToStyle(FeatureCollection.fromFeatures(coordinatesList));

              mapUtils.addLocationPinLayerToStyle();
          }));
    }

}
