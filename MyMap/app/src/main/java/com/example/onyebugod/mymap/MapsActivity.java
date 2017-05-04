package com.example.onyebugod.mymap;
import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.Api;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polygon;
import com.google.android.gms.maps.model.PolygonOptions;
import com.google.maps.android.SphericalUtil;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static android.R.attr.radius;
import static android.R.id.list;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener {

    public static final String MyPREFERENCES = "MyPrefs" ;
    SharedPreferences sharedpreferences;
    private static final String LOG_TAG = "";
    private GoogleMap mMap;
    GoogleApiClient mGoogleApiClient;
    Location mLastLocation;
    Marker mCurrLocationMarker;
    LocationRequest mLocationRequest;
    private Button button1;
    ArrayList<Location> locations = new ArrayList<Location>();
    ArrayList<Marker> markers = new ArrayList<Marker>();
    static final int polygon_size = 5;
    Polygon shape;
    public static final String Name = "name";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        button1 = (Button)findViewById(R.id.button1);

       button1.setOnClickListener(new View.OnClickListener() {
         @Override
        public void onClick(View v) {
            double area = calculateAreaOfGPSPolygonOnEarthInSquareMeters(locations);
            Toast.makeText(MapsActivity.this, Double.toString(area), Toast.LENGTH_LONG).show();
        }

       });


        if(googleServicesAvailable()){
            Toast.makeText(this, "Perfect!!", Toast.LENGTH_LONG).show();
        }
        if(checkLocationPermission()){
            Toast.makeText(this, "Location Confirmed", Toast.LENGTH_LONG).show();
        }


        //else {
          //  TextView changeText = (TextView) findViewById(R.id.button);
            //changeText.setText("End Polygon");
        //}
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
        //mMap.setOnMapClickListener(this);

        //goToLocation(50.9852566,10.8752515);
        //goToLocation(50.9852566,10.8752515);

        // Implement the drag and drop event of Icons on the map
        if(mMap !=null){
            mMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
                @Override
                public void onMapLongClick(LatLng latLng) {
                    MapsActivity.this.setMarker("local", latLng.latitude, latLng.longitude);

                }
            });
            mMap.setOnMarkerDragListener(new GoogleMap.OnMarkerDragListener() {
                @Override
                public void onMarkerDragStart(Marker marker) {
                    marker.remove();
                    marker = null;
                }

                @Override
                public void onMarkerDrag(Marker marker) {

                }

                // Gets the current info of the place to be displayed to the user
                @Override
                public void onMarkerDragEnd(Marker marker) {
                    Geocoder gc = new Geocoder(MapsActivity.this);
                    LatLng ll = marker.getPosition();
                    double lat =ll.latitude;
                    double lng = ll.longitude;
                    List<Address> list = null;
                    try {
                         list = gc.getFromLocation(lat, lng, 10);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    Address add = list.get(0);
                    marker.setTitle(add.getLocality());
                    marker.showInfoWindow();
                }
            });


        }
        if(Build.VERSION.SDK_INT >=Build.VERSION_CODES.M){
            if(checkSelfPermission(
                    Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {
                buildGoogleApiClient();
                mMap.setMyLocationEnabled(true);
            }
        }
        else {
            buildGoogleApiClient();
            mMap.setMyLocationEnabled(true);
        }

    }

    private void goToLocation(double lat, double lng) {
        // Add a marker in Sydney and move the camera
        //LatLng sydney = new LatLng(lat, lng);
        EditText text1 = (EditText)findViewById(R.id.editText);
        String location = text1.getText().toString();
        LatLng erfurt = new LatLng(lat, lng);
        //mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney").draggable(true));
        mMap.addMarker(new MarkerOptions().position(erfurt).title(location));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(erfurt, 15));
        mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);



    }

    private synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        mGoogleApiClient.connect();
    }


    @Override
    public void onLocationChanged(Location location) {
        mLastLocation = location;
        if (mCurrLocationMarker != null) {
            Toast.makeText(this, "Can't get current Location", Toast.LENGTH_SHORT).show();
            mCurrLocationMarker.remove();
        }

        //Place current location marker
        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(latLng);
        markerOptions.title("Current Position");
        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE));
        mCurrLocationMarker = mMap.addMarker(markerOptions);

        //move map camera
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));
        mMap.animateCamera(CameraUpdateFactory.zoomTo(11));

        //stop location updates
        if (mGoogleApiClient != null) {
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
        }

    }


    @Override
    public void onConnected(Bundle bundle) {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(1000);
        mLocationRequest.setFastestInterval(1000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
        }

    }

    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;
    public boolean checkLocationPermission(){
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            // Asking user if explanation is needed
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {

                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.

                //Prompt the user once explanation has been shown
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);


            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);
            }
            return false;
        } else {
            return true;
        }
    }

    // this method checks if the user has google play services
    public boolean googleServicesAvailable(){
        GoogleApiAvailability api = GoogleApiAvailability.getInstance();
        int isAvailable = api.isGooglePlayServicesAvailable(this);
        if(isAvailable == ConnectionResult.SUCCESS){
            return true;
        }
        else if (api.isUserResolvableError(isAvailable)){
            Dialog dialog = api.getErrorDialog(this, isAvailable, 0);
            dialog.show();
        }else {
            Toast.makeText(this, "can't connect to play services", Toast.LENGTH_LONG).show();
        }
        return false;
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }


    /* this gets the string from the user and converts it converts it into Lng and Lat
     using the goToLocationZoom() method to pin point the location on the map
     */
    public void locateMap(View view) throws IOException {
        EditText text = (EditText)findViewById(R.id.editText);
        String location = text.getText().toString();
        Geocoder gc = new Geocoder(this);
        List<Address> list = gc.getFromLocationName(location, 1 );
        sharedpreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);

        Address address = list.get(0);
        String locality = address.getLocality();
        Toast.makeText(this, "location found", Toast.LENGTH_LONG).show();
        double lat = address.getLatitude();
        double lng = address.getLongitude();
        goToLocation(lat, lng);

        // This set a marker to the map.
        setMarker(locality, lat, lng);
        //stores in 
        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.putString(Name, location);
        editor.commit();
        Toast.makeText(this,"Thanks",Toast.LENGTH_LONG).show();
    }



    private void setMarker(String locality, double lat, double lng) {

        if(markers.size() == polygon_size){
            removeEverything();
        }
        // changes the text of the button to the string "Search Location"
        //TextView changeText1 = (TextView) findViewById(R.id.button);
        //changeText1.setText("Search Location");
        EditText text1 = (EditText)findViewById(R.id.editText);
        String location = text1.getText().toString();
        // Adding a marker to the location
        MarkerOptions options = new MarkerOptions()
                                 .title(location)
                                 .draggable(true)
                                 .position(new LatLng(lat, lng))
                                 .snippet("You are here");

        markers.add(mMap.addMarker(options));


        if(markers.size() ==(polygon_size-1)){
            // change the text on the button to Start Polygon
            //TextView changeText = (TextView) findViewById(R.id.button);
            //changeText.setText("Start Polygon");
            Toast.makeText(this,"Enter the last marker using the 'Start Polygon' button",Toast.LENGTH_LONG).show();
        }

        if(markers.size() == polygon_size){
            drawPolygon();
            // change the text on the button to End Polygon
            TextView changeText = (TextView) findViewById(R.id.button);
            changeText.setText("End Polygon");
        }


    }

    private void drawPolygon() {
        PolygonOptions options = new PolygonOptions()
                .fillColor(0x330000FF)
                .strokeWidth(3)
                .strokeColor(Color.RED);

        for(int i=0; i<polygon_size; i++){
            options.add(markers.get(i).getPosition());
        }
        shape = mMap.addPolygon(options);

    }

    private void removeEverything(){
        for(Marker marker :markers){
            marker.remove();
        }
        markers.clear();
        shape.remove();
        shape=null;
    }

    private static final double EARTH_RADIUS = 6371000;// meters

    public static double calculateAreaOfGPSPolygonOnEarthInSquareMeters(ArrayList<Location> markers) {
        return calculateAreaOfGPSPolygonOnSphereInSquareMeters(markers, EARTH_RADIUS);
    }

    private static double calculateAreaOfGPSPolygonOnSphereInSquareMeters(List<Location> markers, final double earthRadius) {
        if (markers.size() < 3) {
            return 0;
        }
        final double diameter = radius * 2;
        final double circumference = diameter * Math.PI;
        final List<Double> listY = new ArrayList<Double>();
        final List<Double> listX = new ArrayList<Double>();
        final List<Double> listArea = new ArrayList<Double>();

        // calculate segment x and y in degrees for each point
        final double latitudeRef = markers.get(0).getLatitude();
        final double longitudeRef = markers.get(0).getLongitude();
        for (int i = 1; i < markers.size(); i++) {
            final double latitude = markers.get(i).getLatitude();
            final double longitude = markers.get(i).getLongitude();
            listY.add(calculateYSegment(latitudeRef, latitude, circumference));
            Log.d(LOG_TAG, String.format("Y %s: %s", listY.size() - 1, listY.get(listY.size() - 1)));
            listX.add(calculateXSegment(longitudeRef, longitude, latitude, circumference));
            Log.d(LOG_TAG, String.format("X %s: %s", listX.size() - 1, listX.get(listX.size() - 1)));
        }

        // calculate areas for each triangle segment
        for (int i = 1; i < listX.size(); i++) {
            final double x1 = listX.get(i - 1);
            final double y1 = listY.get(i - 1);
            final double x2 = listX.get(i);
            final double y2 = listY.get(i);
            listArea.add(calculateAreaInSquareMeters(x1, x2, y1, y2));
            Log.d(LOG_TAG, String.format("area %s: %s", listArea.size() - 1, listArea.get(listArea.size() - 1)));
        }


        // sum areas of all triangle segments
        double areasSum = 0;
        for (final Double area : listArea) {
            areasSum = areasSum + area;
        }
        // get abolute value of area, it can't be negative
        return  areasSum;// Math.sqrt(areasSum * areasSum);



    }

    private static Double calculateAreaInSquareMeters(final double x1, final double x2, final double y1, final double y2) {
        return (y1 * x2 - x1 * y2) / 2;
    }

    private static Double calculateXSegment(final double longitudeRef, final double longitude, final double latitude, final double circumference) {
        return (longitude - longitudeRef) * circumference * Math.cos(Math.toRadians(latitude)) / 360.0;
    }

    private static Double calculateYSegment(final double latitudeRef, final double latitude, final double circumference) {
        return (latitude - latitudeRef) * circumference / 360.0;
    }
    //@Override
            //public void onMapLongClick(LatLng latLng) {
    //Toast.makeText(MapsActivity.this,
    //"onMapLongClick:\n" + latLng.latitude + " : " + latLng.longitude,
            // Toast.LENGTH_LONG).show();

        //Add marker on LongClick position
    // MarkerOptions markerOptions =
            // new MarkerOptions().position(latLng).title(latLng.toString());
    // mMap.addMarker(markerOptions);
    // }

    // @Override
    // public void onMapClick(LatLng latLng) {
    //Toast.makeText(MapsActivity.this,
    //  "onMapClick:\n" + latLng.latitude + " : " + latLng.longitude,
    //Toast.LENGTH_LONG).show();

}
