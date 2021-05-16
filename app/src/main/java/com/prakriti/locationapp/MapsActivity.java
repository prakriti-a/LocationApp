package com.prakriti.locationapp;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {
// better tested on real device, as other process would've already asked for last location
// getLastLocation() gets location based on requests by other apps
    // on emulator run google maps first, then this app

    private GoogleMap mMap;
    private static final int LOCATION_REQ_CODE = 13;
    private FusedLocationProviderClient fusedLocationProviderClient;

    // to get updated user location
//    private LocationRequest locationRequest; // tested on emulator by changing set location in extended controls

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        prepareLocationServices();
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        // check if permission for user's location is given, add a marker & move camera to the current location
        getUsersCurrentLocation();
    }

    private void userLocationPermissionAccess() {
        ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_REQ_CODE);
        // we need a callback for the result from the user
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == LOCATION_REQ_CODE) {
            if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getUsersCurrentLocation();
            }
            else {
                Toast.makeText(this, "User Denied Location Access", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void getUsersCurrentLocation() {
        // determine if we have permission
        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // not granted, ask again
            userLocationPermissionAccess();
        }
        else { // granted

/*            mMap.clear(); // clear map of previous markers before accessing updated loc to avoid multiple markers

            // initialize location request obj once permission is granted
            if(locationRequest == null) {
                locationRequest = LocationRequest.create();
                // check if null obj is not assigned
                if(locationRequest != null) {
                    locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY); // priority of accuracy of location
                        // more accuracy uses more power
                    locationRequest.setInterval(5000); // normally -> ms
                    locationRequest.setFastestInterval(1000); // fastest val

                    // callback for result
                    LocationCallback locationCallback = new LocationCallback() {
                        @Override
                        public void onLocationResult(LocationResult locationResult) {
                            // called whenever a result is returned from requesting location
                            super.onLocationResult(locationResult); // -> empty fn
                            getUsersCurrentLocation();
                                // call the method itself to centre user on map on current loc again
                        }
                    };
                    // let loc client know
                fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, null);
                // pass loc request, loc callback & looper
                }
            }
 */
            // use inbuilt android method to get current location
            mMap.setMyLocationEnabled(true); // enables/disables my-location layer on map -> creates centralising button


            // FusedLocationProviderClient is the service used -> Google Play services API
            // add dependencies in gradle file from Project Structure window

            // add listener to be notified of result
            fusedLocationProviderClient.getLastLocation().addOnCompleteListener(new OnCompleteListener<Location>() {
                @Override
                public void onComplete(@NonNull Task<Location> task) {
                    Location location = task.getResult(); // returns a Location
                    // check for success of getting location
                    if(location != null) {
                        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
                        // now add marker & position camera
                   //     mMap.addMarker(new MarkerOptions().position(latLng).title("Current Location"));
                        // marker not required once setMyLoc() is enabled

                        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, 16.0f); // default val
                        mMap.moveCamera(cameraUpdate); // move cam to current loc
                    }
                    else {
                        Toast.makeText(MapsActivity.this, "Something went wrong\nPlease try again", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

    private void prepareLocationServices() {
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
    }
}