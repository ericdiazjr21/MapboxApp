package ericdiaz.program.gotennachallenge.utils;

import android.graphics.Bitmap;
import android.graphics.Color;

import androidx.annotation.NonNull;

import com.mapbox.api.directions.v5.models.DirectionsResponse;
import com.mapbox.api.directions.v5.models.DirectionsRoute;
import com.mapbox.geojson.Feature;
import com.mapbox.geojson.FeatureCollection;
import com.mapbox.geojson.LineString;
import com.mapbox.geojson.Point;
import com.mapbox.mapboxsdk.camera.CameraPosition;
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.Style;
import com.mapbox.mapboxsdk.style.layers.LineLayer;
import com.mapbox.mapboxsdk.style.layers.Property;
import com.mapbox.mapboxsdk.style.layers.PropertyFactory;
import com.mapbox.mapboxsdk.style.layers.SymbolLayer;
import com.mapbox.mapboxsdk.style.sources.GeoJsonSource;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Response;

import static com.mapbox.core.constants.Constants.PRECISION_6;

/**
 * A utility class for the MapBox API
 * <p>
 * Created 8/13/19
 *
 * @author Eric Diaz
 */

public final class MapUtils {

    //==============================================================================================
    // Class Properties
    //==============================================================================================

    private final MapboxMap mapboxMap;
    private final Style.Builder styleBuilder;
    private final Map<Integer, DirectionsRoute> directionsRouteHashMap = new HashMap<>();
    private Style loadedStyle;

    //==============================================================================================
    // Constructor
    //==============================================================================================

    public MapUtils(@NonNull MapboxMap mapboxMap,
                    @NonNull Style.Builder styleBuilder) {
        this.mapboxMap = mapboxMap;
        this.styleBuilder = styleBuilder;
    }

    //==============================================================================================
    // Class Static Methods
    //==============================================================================================

    public static String getAccessKey() {
        return Constants.ACCESS_KEY;
    }

    //==============================================================================================
    // Class Instance Methods
    //==============================================================================================

    private String getLayerId(final int iD) {
        return "layer-id" + ": " + iD;
    }

    private String getSourceId(final int iD) {
        return "source-id" + ": " + iD;
    }

    public void setLoadedStyle(@NonNull final Style loadedStyle) {
        this.loadedStyle = loadedStyle;
    }

    public void addPersonLayerToStyle(@NonNull final Point lastKnownPoint) {
        styleBuilder.fromUri(Style.MAPBOX_STREETS)

          .withSource(new GeoJsonSource(Constants.PERSON_SOURCE_ID, Feature.fromGeometry(lastKnownPoint)))

          .withLayer(new SymbolLayer(Constants.PERSON_LAYER_ID, Constants.PERSON_SOURCE_ID));
    }

    public void addPinIconToStyle(@NonNull final Bitmap pinImage) {
        styleBuilder.withImage(Constants.PIN_ICON, pinImage);
    }

    public void addDirectionLinesToStyle() {
        styleBuilder

          .withSource(new GeoJsonSource(Constants.DASHED_DIRECTIONS_LINE_LAYER_SOURCE_ID))

          .withLayerBelow(

            new LineLayer(
              Constants.DASHED_DIRECTIONS_LINE_LAYER_ID,
              Constants.DASHED_DIRECTIONS_LINE_LAYER_SOURCE_ID)
              .withProperties(
                PropertyFactory.lineWidth(5f),
                PropertyFactory.lineJoin(Property.LINE_JOIN_ROUND),
                PropertyFactory.lineColor(Color.parseColor("#2096F3"))),

            Constants.PERSON_LAYER_ID);
    }

    public void addLocationCoordinatesToStyle(final int iD,
                                              final double longitude,
                                              final double latitude) {
        loadedStyle.addSource(

          new GeoJsonSource(getSourceId(iD), Feature.fromGeometry(Point.fromLngLat(longitude, latitude))));
    }

    public void addLocationPinLayerToStyle(int iD) {
        final SymbolLayer symbolLayer = new SymbolLayer(getLayerId(iD), getSourceId(iD));

        symbolLayer.withProperties(PropertyFactory.iconImage(Constants.PIN_ICON));

        loadedStyle.addLayer(symbolLayer);
    }

    public void setCameraPosition(@NonNull final LatLng lastKnownPosition) {
        final CameraPosition position = new CameraPosition.Builder()
          .target(lastKnownPosition)
          .zoom(15)
          .bearing(180)
          .tilt(30)
          .build();

        mapboxMap.animateCamera(CameraUpdateFactory.newCameraPosition(position), 2000);
    }

    public void updateRouteMap(final int position,
                               @NonNull Response<DirectionsResponse> directionsResponse) {
        if (directionsRouteHashMap.containsKey(position)) {

            if (directionsResponse.body() != null) {

                directionsRouteHashMap

                  .replace(position,

                    directionsRouteHashMap.get(position),

                    directionsResponse.body().routes().get(0));
            }

        } else {

            directionsRouteHashMap.put(position, directionsResponse.body().routes().get(0));
        }
    }

    public void drawNavigationRoute(int position) {

        final List<Feature> directionsRouteFeatureList = new ArrayList<>();

        final LineString lineString = LineString

          .fromPolyline(directionsRouteHashMap.get(position).geometry(), PRECISION_6);

        final List<Point> lineStringCoordinates = lineString.coordinates();

        for (int i = 0; i < lineStringCoordinates.size(); i++) {
            directionsRouteFeatureList.add(Feature.fromGeometry(LineString.fromLngLats(lineStringCoordinates)));
        }

        GeoJsonSource source = loadedStyle.getSourceAs(Constants.DASHED_DIRECTIONS_LINE_LAYER_SOURCE_ID);

        if (source != null) {
            source.setGeoJson(FeatureCollection.fromFeatures(directionsRouteFeatureList));
        }
    }

    //==============================================================================================
    // MapUtils Constants Class
    //==============================================================================================

    private final class Constants {

        private static final String ACCESS_KEY = "pk.eyJ1IjoiZXJpY2RpYXoiLCJhIjoiY2p6OThqZzJiMDRxaDNkcGo0Y3E4Z3M3ZyJ9.X9WoMdw49Am8iQSUdVWL4w";

        private static final String PIN_ICON = "pin-marker-icon";

        private static final String PERSON_SOURCE_ID = "person_source_id";

        private static final String PERSON_LAYER_ID = "person_layer_id";

        private static final String DASHED_DIRECTIONS_LINE_LAYER_ID = "dashed_directions_line_layer_id";

        private static final String DASHED_DIRECTIONS_LINE_LAYER_SOURCE_ID = "dashed_directions_line_layer_source_id";

    }
}
