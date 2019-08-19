package ericdiaz.program.gotennachallenge.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.location.LocationManager;

import androidx.annotation.NonNull;

import com.mapbox.geojson.Point;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.location.LocationComponent;
import com.mapbox.mapboxsdk.location.LocationComponentActivationOptions;
import com.mapbox.mapboxsdk.location.modes.CameraMode;
import com.mapbox.mapboxsdk.location.modes.RenderMode;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.Style;

import java.util.Objects;

/**
 * A utility class for retrieving users location
 * <p>
 * Created 8/14/19
 *
 * @author Eric Diaz
 */

public final class LocationUtils {

    //==============================================================================================
    // Class Properties
    //==============================================================================================

    private Context context;
    private LocationComponent locationComponent;
    private LocationManager locationManager;

    //==============================================================================================
    // Constructor
    //==============================================================================================

    public LocationUtils(@NonNull Context context) {
        this.context = context;
        locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
    }

    //==============================================================================================
    // Class Instance Methods
    //==============================================================================================

    @SuppressLint("MissingPermission")
    public void enableLocationComponent(@NonNull Style loadedMapStyle,
                                        @NonNull MapboxMap mapboxMap) {
        locationComponent = mapboxMap.getLocationComponent();
        locationComponent.activateLocationComponent(
          LocationComponentActivationOptions.builder(context, loadedMapStyle).build());
        locationComponent.setLocationComponentEnabled(true);
        locationComponent.setCameraMode(CameraMode.TRACKING);
        locationComponent.setRenderMode(RenderMode.COMPASS);
    }

    public LatLng getUserLastKnownLatLng() {
        return new LatLng(
          Objects.requireNonNull(
            locationComponent.getLastKnownLocation()).getLatitude(),
          locationComponent.getLastKnownLocation().getLongitude());
    }

    @SuppressLint("MissingPermission")
    public Point getUserLastKnownPoint() {
        return Point.fromLngLat(
          locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER).getLongitude(),
          locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER).getLatitude());
    }

    public Point getDestinationPoint(double longitude, double latitude) {
        return Point.fromLngLat(longitude, latitude);
    }

    public void tearDown() {
        context = null;
    }

}
