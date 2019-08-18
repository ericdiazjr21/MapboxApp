package ericdiaz.program.gotennachallenge.view;

import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.mapbox.api.directions.v5.models.DirectionsResponse;
import com.mapbox.api.directions.v5.models.DirectionsRoute;
import com.mapbox.geojson.Feature;
import com.mapbox.geojson.FeatureCollection;
import com.mapbox.geojson.LineString;
import com.mapbox.geojson.Point;
import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.camera.CameraPosition;
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.Style;
import com.mapbox.mapboxsdk.style.layers.LineLayer;
import com.mapbox.mapboxsdk.style.layers.SymbolLayer;
import com.mapbox.mapboxsdk.style.sources.GeoJsonSource;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import ericdiaz.program.gotennachallenge.R;
import ericdiaz.program.gotennachallenge.api.MapboxDirectionsService;
import ericdiaz.program.gotennachallenge.model.Place;
import ericdiaz.program.gotennachallenge.utils.LocationUtils;
import ericdiaz.program.gotennachallenge.utils.MapboxUtils;
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
    private MapboxMap mapboxMap;
    private RecyclerView placesRecyclerView;
    private Disposable disposable;
    private FeatureCollection dashedLineDirectionsFeatureCollection;
    private BaseViewModel placesViewModel;
    private final List<DirectionsRoute> directionsRouteList = new ArrayList<>();
    private LocationUtils locationUtils;

    private void initRecyclerView() {
        placesRecyclerView = findViewById(R.id.place_recycler_view);
        placesRecyclerView.setLayoutManager(new LinearLayoutManager(this, RecyclerView.HORIZONTAL, false));
    }

    private void initMapBoxView(@Nullable Bundle savedInstanceState) {
        mapView = findViewById(R.id.mapView);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(mapboxMap ->
        {
            this.mapboxMap = mapboxMap;
            mapboxMap.setStyle(new Style.Builder().fromUri(Style.MAPBOX_STREETS)
                .withImage(MapboxUtils.PERSON_ICON_ID, Objects.requireNonNull(getDrawable(R.drawable.ic_person)))
                .withSource(new GeoJsonSource(MapboxUtils.PERSON_SOURCE_ID,
                  Feature.fromGeometry(locationUtils.getLastKnownPoint())))
                .withLayer(new SymbolLayer(MapboxUtils.PERSON_LAYER_ID, MapboxUtils.PERSON_SOURCE_ID).withProperties(
                  iconImage(MapboxUtils.PERSON_ICON_ID),
                  iconSize(2f),
                  iconAllowOverlap(true),
                  iconIgnorePlacement(true)
                )).withSource(new GeoJsonSource(
                  MapboxUtils.DASHED_DIRECTIONS_LINE_LAYER_SOURCE_ID))
                .withLayerBelow(
                  new LineLayer(MapboxUtils.DASHED_DIRECTIONS_LINE_LAYER_ID, MapboxUtils.DASHED_DIRECTIONS_LINE_LAYER_SOURCE_ID)
                    .withProperties(
                      lineWidth(5f),
                      lineJoin(LINE_JOIN_ROUND),
                      lineColor(Color.parseColor("#2096F3"))
                    ), MapboxUtils.PERSON_LAYER_ID),
              style -> {
                  addIconToStyle(style);
                  locationUtils.enableLocationComponent(Objects.requireNonNull(mapboxMap.getStyle()), mapboxMap);
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

    private void getRoute(Point destination) {
        new MapboxDirectionsService()
          .getClient(MapboxUtils.ACCESS_KEY, locationUtils.getLastKnownPoint(), destination)
          .enqueueCall(new Callback<DirectionsResponse>() {
              @Override
              public void onResponse(Call<DirectionsResponse> call, Response<DirectionsResponse> response) {
                  if (response.body() != null) {
                      directionsRouteList.add(response.body().routes().get(0));
                  } else if (response.body().routes().size() < 1) {
                      Log.d(TAG, "No routes found");
                  }
              }

              @Override
              public void onFailure(Call<DirectionsResponse> call, Throwable throwable) {
                  Log.d(TAG, "onFailure: " + throwable.toString());
              }
          });
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Mapbox.getInstance(this, MapboxUtils.ACCESS_KEY);
        setContentView(R.layout.activity_main);
        initMapBoxView(savedInstanceState);
        initRecyclerView();

        locationUtils = new LocationUtils(this);
        placesViewModel = new ViewModelProvider.AndroidViewModelFactory(getApplication()).create(PlacesViewModel.class);
    }

    private void addIconToStyle(Style style) {
        style.addImage(MapboxUtils.ICON_NAME,
          BitmapFactory.decodeResource(
            MainActivity.this.getResources(), R.drawable.mapbox_marker_icon_default));
    }

    private void addGeoJsonSourceToStyle(Style style, String iD, double longitude, double latitude) {
        GeoJsonSource geoJsonSource = new GeoJsonSource(MapboxUtils.getSourceId(iD), Feature.fromGeometry(
          Point.fromLngLat(longitude, latitude)));
        style.addSource(geoJsonSource);
    }

    private void drawNavigationPolylineRoute(final DirectionsRoute route) {
        if (mapboxMap != null) {
            mapboxMap.getStyle(style -> {
                List<Feature> directionsRouteFeatureList = new ArrayList<>();
                LineString lineString = LineString.fromPolyline(Objects.requireNonNull(route.geometry()), PRECISION_6);
                List<Point> lineStringCoordinates = lineString.coordinates();
                for (int i = 0; i < lineStringCoordinates.size(); i++) {
                    directionsRouteFeatureList.add(Feature.fromGeometry(
                      LineString.fromLngLats(lineStringCoordinates)));
                }
                dashedLineDirectionsFeatureCollection =
                  FeatureCollection.fromFeatures(directionsRouteFeatureList);
                GeoJsonSource source = style.getSourceAs(MapboxUtils.DASHED_DIRECTIONS_LINE_LAYER_SOURCE_ID);
                if (source != null) {
                    source.setGeoJson(dashedLineDirectionsFeatureCollection);
                }
            });
        }
    }

    private void addSymbolLayerToStyle(Style style, String iD) {
        SymbolLayer symbolLayer = new SymbolLayer(MapboxUtils.getLayerId(iD), MapboxUtils.getSourceId(iD));
        symbolLayer.withProperties(
          iconImage(MapboxUtils.ICON_NAME)
        );
        style.addLayer(symbolLayer);
    }

    private void setCameraPosition(MapboxMap mapboxMap) {
        CameraPosition position = new CameraPosition.Builder()
          .target(locationUtils.getLastKnownLatLng())
          .zoom(15)
          .bearing(180)
          .tilt(30)
          .build();
        mapboxMap.animateCamera(CameraUpdateFactory
          .newCameraPosition(position), 5000);
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

    @Override
    public void onItemViewClicked(int position) {
        drawNavigationPolylineRoute(directionsRouteList.get(position));
    }
}
