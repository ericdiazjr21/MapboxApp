package ericdiaz.program.gotennachallenge.view;

import android.annotation.SuppressLint;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.mapbox.android.core.permissions.PermissionsManager;
import com.mapbox.api.directions.v5.DirectionsCriteria;
import com.mapbox.api.directions.v5.MapboxDirections;
import com.mapbox.api.directions.v5.models.DirectionsResponse;
import com.mapbox.api.directions.v5.models.DirectionsRoute;
import com.mapbox.geojson.Feature;
import com.mapbox.geojson.FeatureCollection;
import com.mapbox.geojson.LineString;
import com.mapbox.geojson.Point;
import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.camera.CameraPosition;
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.location.LocationComponent;
import com.mapbox.mapboxsdk.location.LocationComponentActivationOptions;
import com.mapbox.mapboxsdk.location.modes.CameraMode;
import com.mapbox.mapboxsdk.location.modes.RenderMode;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.Style;
import com.mapbox.mapboxsdk.style.layers.LineLayer;
import com.mapbox.mapboxsdk.style.layers.SymbolLayer;
import com.mapbox.mapboxsdk.style.sources.GeoJsonSource;

import java.util.ArrayList;
import java.util.List;

import ericdiaz.program.gotennachallenge.R;
import ericdiaz.program.gotennachallenge.model.Place;
import ericdiaz.program.gotennachallenge.utils.MapBoxUtils;
import ericdiaz.program.gotennachallenge.view.recyclerview.PlacesAdapter;
import ericdiaz.program.gotennachallenge.view.recyclerview.PlacesViewHolder;
import ericdiaz.program.gotennachallenge.viewmodel.BaseViewModel;
import ericdiaz.program.gotennachallenge.viewmodel.PlacesViewModel;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.mapbox.core.constants.Constants.PRECISION_6;
import static com.mapbox.mapboxsdk.style.layers.Property.LINE_JOIN_ROUND;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconAllowOverlap;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconIgnorePlacement;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconImage;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconSize;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.lineColor;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.lineJoin;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.lineWidth;


public class MainActivity extends AppCompatActivity implements PlacesViewHolder.OnItemViewClickedListener {

    private static final String TAG = "MainActivity";
    private MapView mapView;
    private BaseViewModel placesViewModel;
    private Disposable disposable;
    private MapboxMap mapboxMap;
    private LocationComponent locationComponent;
    private RecyclerView placesRecyclerView;
    private LocationManager locationManager;
    private final List<DirectionsRoute> directionsRouteList = new ArrayList<>();
    private FeatureCollection dashedLineDirectionsFeatureCollection;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Mapbox.getInstance(this, MapBoxUtils.ACCESS_KEY);
        setContentView(R.layout.activity_main);
        initMapBoxView(savedInstanceState);
        placesViewModel = new ViewModelProvider.AndroidViewModelFactory(getApplication()).create(PlacesViewModel.class);
        placesRecyclerView = findViewById(R.id.place_recycler_view);
        placesRecyclerView.setLayoutManager(new LinearLayoutManager(this, RecyclerView.HORIZONTAL, false));
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
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
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }

    private void initMapBoxView(@Nullable Bundle savedInstanceState) {
        mapView = findViewById(R.id.mapView);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(mapboxMap ->
        {
            this.mapboxMap = mapboxMap;
            mapboxMap.setStyle(new Style.Builder().fromUri(Style.MAPBOX_STREETS)
                .withImage(MapBoxUtils.PERSON_ICON_ID, getDrawable(R.drawable.ic_person))
                .withSource(new GeoJsonSource(MapBoxUtils.PERSON_SOURCE_ID,
                  Feature.fromGeometry(getPoint())))
                .withLayer(new SymbolLayer(MapBoxUtils.PERSON_LAYER_ID, MapBoxUtils.PERSON_SOURCE_ID).withProperties(
                  iconImage(MapBoxUtils.PERSON_ICON_ID),
                  iconSize(2f),
                  iconAllowOverlap(true),
                  iconIgnorePlacement(true)
                )).withSource(new GeoJsonSource(
                  MapBoxUtils.DASHED_DIRECTIONS_LINE_LAYER_SOURCE_ID))
                .withLayerBelow(
                  new LineLayer(MapBoxUtils.DASHED_DIRECTIONS_LINE_LAYER_ID, MapBoxUtils.DASHED_DIRECTIONS_LINE_LAYER_SOURCE_ID)
                    .withProperties(
                      lineWidth(5f),
                      lineJoin(LINE_JOIN_ROUND),
                      lineColor(Color.parseColor("#2096F3"))
                    ), MapBoxUtils.PERSON_LAYER_ID),
              style -> {
                  addIconToStyle(style);
                  enableLocationComponent(mapboxMap.getStyle());
                  setCameraPosition(mapboxMap);
                  disposable = placesViewModel
                    .getPlacesData()
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(places -> {
                        PlacesAdapter adapter = new PlacesAdapter();
                        adapter.setData(places);
                        placesRecyclerView.setAdapter(adapter);
                        for (Place place : places) {
                            String iD = String.valueOf(place.getId());
                            addGeoJsonSourceToStyle(style, iD, place.getLongitude(), place.getLatitude());
                            addSymbolLayerToStyle(style, iD);
                            getRoute(Point.fromLngLat(place.getLongitude(), place.getLatitude()));
                        }
                    }, throwable -> {
                    });


              });
        });
    }

    @SuppressLint("MissingPermission")
    private Point getPoint() {
        return Point.fromLngLat(
          locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER).getLongitude(),
          locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER).getLatitude());
    }

    private void getRoute(Point fromLngLat) {
        MapboxDirections client = MapboxDirections.builder()
          .origin(getPoint())
          .destination(fromLngLat)
          .overview(DirectionsCriteria.OVERVIEW_FULL)
          .profile(DirectionsCriteria.PROFILE_DRIVING)
          .accessToken(MapBoxUtils.ACCESS_KEY)
          .build();
        client.enqueueCall(new Callback<DirectionsResponse>() {
            @Override
            public void onResponse(Call<DirectionsResponse> call, Response<DirectionsResponse> response) {
                if (response.body() == null) {
                    Log.d(TAG, "No routes found, make sure you set the right user and access token.");
                    return;
                } else if (response.body().routes().size() < 1) {
                    Log.d(TAG, "No routes found");
                    return;
                }
                directionsRouteList.add(response.body().routes().get(0));
            }

            @Override
            public void onFailure(Call<DirectionsResponse> call, Throwable throwable) {
                Log.d(TAG, "Error: " + throwable.getMessage());
                if (!throwable.getMessage().equals("Coordinate is invalid: 0,0")) {
                    Log.d(TAG, "onFailure: " + throwable.toString());
                }
            }
        });
    }
;
    private void drawNavigationPolylineRoute(final DirectionsRoute route) {
        if (mapboxMap != null) {
            mapboxMap.getStyle(style -> {
                List<Feature> directionsRouteFeatureList = new ArrayList<>();
                LineString lineString = LineString.fromPolyline(route.geometry(), PRECISION_6);
                List<Point> lineStringCoordinates = lineString.coordinates();
                for (int i = 0; i < lineStringCoordinates.size(); i++) {
                    directionsRouteFeatureList.add(Feature.fromGeometry(
                      LineString.fromLngLats(lineStringCoordinates)));
                }
                dashedLineDirectionsFeatureCollection =
                  FeatureCollection.fromFeatures(directionsRouteFeatureList);
                GeoJsonSource source = style.getSourceAs(MapBoxUtils.DASHED_DIRECTIONS_LINE_LAYER_SOURCE_ID);
                if (source != null) {
                    source.setGeoJson(dashedLineDirectionsFeatureCollection);
                }
            });
        }
    }

    private void addIconToStyle(Style style) {
        style.addImage(MapBoxUtils.ICON_NAME,
          BitmapFactory.decodeResource(
            MainActivity.this.getResources(), R.drawable.mapbox_marker_icon_default));
    }

    private void addGeoJsonSourceToStyle(Style style, String iD, double longitude, double latitude) {
        GeoJsonSource geoJsonSource = new GeoJsonSource(MapBoxUtils.getSourceId(iD), Feature.fromGeometry(
          Point.fromLngLat(longitude, latitude)));
        style.addSource(geoJsonSource);
    }

    private void addSymbolLayerToStyle(Style style, String iD) {
        SymbolLayer symbolLayer = new SymbolLayer(MapBoxUtils.getLayerId(iD), MapBoxUtils.getSourceId(iD));
        symbolLayer.withProperties(
          iconImage(MapBoxUtils.ICON_NAME)
        );
        style.addLayer(symbolLayer);
    }

    private void setCameraPosition(MapboxMap mapboxMap) {
        CameraPosition position = new CameraPosition.Builder()
          .target(new LatLng(
            locationComponent.getLastKnownLocation().getLatitude(),
            locationComponent.getLastKnownLocation().getLongitude()))
          .zoom(15)
          .bearing(180)
          .tilt(30)
          .build();

        mapboxMap.animateCamera(CameraUpdateFactory
          .newCameraPosition(position), 5000);
    }

    @SuppressLint("MissingPermission")
    private void enableLocationComponent(@NonNull Style loadedMapStyle) {
        if (PermissionsManager.areLocationPermissionsGranted(this)) {
            locationComponent = mapboxMap.getLocationComponent();
            locationComponent.activateLocationComponent(
              LocationComponentActivationOptions.builder(this, loadedMapStyle).build());
            locationComponent.setLocationComponentEnabled(true);
            locationComponent.setCameraMode(CameraMode.TRACKING);
            locationComponent.setRenderMode(RenderMode.COMPASS);
        }
    }

    @Override
    public void onItemViewClicked(int position) {
        drawNavigationPolylineRoute(directionsRouteList.get(position));
    }
}
