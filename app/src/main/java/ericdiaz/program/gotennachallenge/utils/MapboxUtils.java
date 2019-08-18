package ericdiaz.program.gotennachallenge.utils;

import android.content.res.Resources;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.Drawable;

import androidx.annotation.NonNull;

import com.mapbox.api.directions.v5.models.DirectionsRoute;
import com.mapbox.core.constants.Constants;
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

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import ericdiaz.program.gotennachallenge.R;

/**
 * A utility class for the MapBox API
 * <p>
 * Created 8/13/19
 *
 * @author Eric Diaz
 */

public final class MapboxUtils {

    public static final String ACCESS_KEY = "pk.eyJ1IjoiZXJpY2RpYXoiLCJhIjoiY2p6OThqZzJiMDRxaDNkcGo0Y3E4Z3M3ZyJ9.X9WoMdw49Am8iQSUdVWL4w";
    private static final String PIN_ICON = "pin-marker-icon";
    private static final String PERSON_ICON_ID = "person_icon_id";
    private static final String PERSON_SOURCE_ID = "person_source_id";
    private static final String PERSON_LAYER_ID = "person_layer_id";
    private static final String DASHED_DIRECTIONS_LINE_LAYER_ID = "dashed_directions_line_layer_id";
    private static final String DASHED_DIRECTIONS_LINE_LAYER_SOURCE_ID = "dashed_directions_line_layer_source_id";

    private static FeatureCollection dashedLineDirectionsFeatureCollection;


    private static String getLayerId(@NonNull final int iD) {
        return "layer-id" + ": " + iD;
    }

    private static String getSourceId(@NonNull final int iD) {
        return "source-id" + ": " + iD;
    }

    @NotNull
    public static Style.Builder addDirectionLinesToStyle(Style.Builder styleBuilder) {
        return styleBuilder.withSource(new GeoJsonSource(
          MapboxUtils.DASHED_DIRECTIONS_LINE_LAYER_SOURCE_ID))
          .withLayerBelow(
            new LineLayer(MapboxUtils.DASHED_DIRECTIONS_LINE_LAYER_ID, MapboxUtils.DASHED_DIRECTIONS_LINE_LAYER_SOURCE_ID)
              .withProperties(
                PropertyFactory.lineWidth(5f),
                PropertyFactory.lineJoin(Property.LINE_JOIN_ROUND),
                PropertyFactory.lineColor(Color.parseColor("#2096F3"))
              ), MapboxUtils.PERSON_LAYER_ID);
    }


    @NotNull
    public static Style.Builder addPersonIconToStyle(Style.Builder styleBuilder, Drawable drawable, Point lastKnownPoint) {
        return styleBuilder.fromUri(Style.MAPBOX_STREETS)

          .withImage(MapboxUtils.PERSON_ICON_ID, Objects.requireNonNull(drawable))

          .withSource(new GeoJsonSource(MapboxUtils.PERSON_SOURCE_ID,
            Feature.fromGeometry(lastKnownPoint)))

          .withLayer(new SymbolLayer(MapboxUtils.PERSON_LAYER_ID, MapboxUtils.PERSON_SOURCE_ID).withProperties(
            PropertyFactory.iconImage(MapboxUtils.PERSON_ICON_ID),
            PropertyFactory.iconSize(2f),
            PropertyFactory.iconAllowOverlap(true),
            PropertyFactory.iconIgnorePlacement(true)
          ));
    }

    public static Style.Builder addPinIconToStyle(Style.Builder styleBuilder, Resources resources) {
        return styleBuilder.withImage(MapboxUtils.PIN_ICON,
          BitmapFactory.decodeResource(resources, R.drawable.mapbox_marker_icon_default));
    }

    public static void addLocationPointToStyle(Style style, int iD, double longitude, double latitude) {
        GeoJsonSource geoJsonSource = new GeoJsonSource(MapboxUtils.getSourceId(iD), Feature.fromGeometry(
          Point.fromLngLat(longitude, latitude)));
        style.addSource(geoJsonSource);
    }

    public static void addLocationPinLayerToStyle(Style style, int iD) {

        SymbolLayer symbolLayer = new SymbolLayer(MapboxUtils.getLayerId(iD), MapboxUtils.getSourceId(iD));
        symbolLayer.withProperties(
          PropertyFactory.iconImage(MapboxUtils.PIN_ICON)
        );
        style.addLayer(symbolLayer);
    }

    public static void drawNavigationPolylineRoute(final DirectionsRoute route, MapboxMap mapboxMap) {
        if (mapboxMap != null) {
            mapboxMap.getStyle(style -> {
                List<Feature> directionsRouteFeatureList = new ArrayList<>();
                LineString lineString = LineString.fromPolyline(Objects.requireNonNull(route.geometry()), Constants.PRECISION_6);
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

    public static void setCameraPosition(MapboxMap mapboxMap, LatLng lastKnownPosition) {
        CameraPosition position = new CameraPosition.Builder()
          .target(lastKnownPosition)
          .zoom(15)
          .bearing(180)
          .tilt(30)
          .build();
        mapboxMap.animateCamera(CameraUpdateFactory
          .newCameraPosition(position), 2000);
    }
}
