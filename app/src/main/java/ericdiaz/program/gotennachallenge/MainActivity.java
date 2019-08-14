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

import ericdiaz.program.gotennachallenge.utils.MapBoxUtils;
import ericdiaz.program.gotennachallenge.viewmodel.BaseViewModel;
import ericdiaz.program.gotennachallenge.viewmodel.PlacesViewModel;

public class MainActivity extends AppCompatActivity {

    private MapView mapView;
    private BaseViewModel placesViewModel;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Mapbox.getInstance(this, MapBoxUtils.ACCESS_KEY);
        setContentView(R.layout.activity_main);
        initMapBoxView(savedInstanceState);
        placesViewModel = new ViewModelProvider.NewInstanceFactory().create(PlacesViewModel.class);
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
                addIconToStyle(style, "tempName");
                addLatLngPositionToStyle(style, "tempSourceId", 40.73581, -73.99155);
                addSymbolLayerToStyle(style, "layer-id", "source-id", "marker-icon-id");
            }));
    }

    private void addSymbolLayerToStyle(Style style, String layerId, String sourceId, String markerIconId) {
        SymbolLayer symbolLayer = new SymbolLayer(layerId, sourceId);
        symbolLayer.withProperties(
          PropertyFactory.iconImage(markerIconId)
        );
        style.addLayer(symbolLayer);
    }

    private void addLatLngPositionToStyle(Style style, String sourceId, double latitude, double longitude) {
        GeoJsonSource geoJsonSource = new GeoJsonSource(sourceId, Feature.fromGeometry(
          Point.fromLngLat(longitude, latitude)));
        style.addSource(geoJsonSource);
    }

    private void addIconToStyle(Style style, String name) {
        style.addImage(name,
          BitmapFactory.decodeResource(
            MainActivity.this.getResources(), R.drawable.mapbox_marker_icon_default));
    }
}
