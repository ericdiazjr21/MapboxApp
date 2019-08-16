package ericdiaz.program.gotennachallenge;

import android.annotation.SuppressLint;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.mapbox.android.core.permissions.PermissionsListener;
import com.mapbox.android.core.permissions.PermissionsManager;
import com.mapbox.geojson.Feature;
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
import com.mapbox.mapboxsdk.style.layers.PropertyFactory;
import com.mapbox.mapboxsdk.style.layers.SymbolLayer;
import com.mapbox.mapboxsdk.style.sources.GeoJsonSource;

import java.util.List;

import ericdiaz.program.gotennachallenge.model.Place;
import ericdiaz.program.gotennachallenge.utils.MapBoxUtils;
import ericdiaz.program.gotennachallenge.viewmodel.BaseViewModel;
import ericdiaz.program.gotennachallenge.viewmodel.PlacesViewModel;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;

public class MainActivity extends AppCompatActivity implements PermissionsListener {

    private MapView mapView;
    private PermissionsManager permissionsManager;
    private BaseViewModel placesViewModel;
    private Disposable disposable;
    private MapboxMap mapboxMap;
    private LocationComponent locationComponent;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Mapbox.getInstance(this, MapBoxUtils.ACCESS_KEY);
        setContentView(R.layout.activity_main);
        initMapBoxView(savedInstanceState);
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
          mapboxMap.setStyle(Style.MAPBOX_STREETS,
            style -> {
                this.mapboxMap = mapboxMap;
                addIconToStyle(style);
                enableLocationComponent(style);
                setCameraPosition(mapboxMap);
                disposable = placesViewModel
                  .getPlacesData()
                  .observeOn(AndroidSchedulers.mainThread())
                  .subscribe(places -> {
                      for (Place place : places) {
                          Log.d("Main", "initMapBoxView: " + place.getLatitude() + " " + place.getLongitude());
                          String iD = String.valueOf(place.getId());

                          addGeoJsonSourceToStyle(style, iD, place.getLongitude(), place.getLatitude());

                          addSymbolLayerToStyle(style, iD);
                      }
                  }, throwable -> {
                  });


            }));
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
          PropertyFactory.iconImage(MapBoxUtils.ICON_NAME)
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

    private void requestLocationPermission() {
        if (!PermissionsManager.areLocationPermissionsGranted(this)) {
            permissionsManager = new PermissionsManager(this);
            permissionsManager.requestLocationPermissions(this);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        permissionsManager.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    public void onExplanationNeeded(List<String> permissionsToExplain) {
        Toast.makeText(this, R.string.user_location_permission_explanation, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onPermissionResult(boolean granted) {
        if (granted) {
            mapboxMap.getStyle(this::enableLocationComponent);
        } else {
            Toast.makeText(this, R.string.user_location_permission_not_granted, Toast.LENGTH_LONG).show();
            finish();
        }
    }
}
