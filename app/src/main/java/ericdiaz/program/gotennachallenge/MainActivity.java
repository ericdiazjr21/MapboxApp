package ericdiaz.program.gotennachallenge;

import android.graphics.BitmapFactory;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.mapbox.geojson.Feature;
import com.mapbox.geojson.Point;
import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.Style;
import com.mapbox.mapboxsdk.style.layers.PropertyFactory;
import com.mapbox.mapboxsdk.style.layers.SymbolLayer;
import com.mapbox.mapboxsdk.style.sources.GeoJsonSource;

import ericdiaz.program.gotennachallenge.model.Place;
import ericdiaz.program.gotennachallenge.utils.MapBoxUtils;
import ericdiaz.program.gotennachallenge.viewmodel.BaseViewModel;
import ericdiaz.program.gotennachallenge.viewmodel.PlacesViewModel;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;

public class MainActivity extends AppCompatActivity {
    private MapView mapView;
    private BaseViewModel placesViewModel;
    private Disposable disposable;

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
                addIconToStyle(style);

                disposable = placesViewModel
                  .getPlacesData()
                  .observeOn(AndroidSchedulers.mainThread())
                  .subscribe(places -> {
                      for (Place place : places) {
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
}
