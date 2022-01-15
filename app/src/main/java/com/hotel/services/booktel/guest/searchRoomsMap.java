package com.hotel.services.booktel.guest;

import android.Manifest;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.hotel.services.booktel.GuestSignup;
import com.hotel.services.booktel.R;

public class searchRoomsMap extends FragmentActivity implements OnMapReadyCallback {
    private GoogleMap mMap;


    private double currLat = 0, currlong = 0;
    private SupportMapFragment mapFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_search_rooms_map );
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById( R.id.map );


        mapFragment.getMapAsync( this );


    }

    private void fetchHotels(final GoogleMap googleMap) {

        FirebaseDatabase.getInstance().getReference().child( "Hotels" ).addValueEventListener( new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                int i = 0;
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {

                    // Toast.makeText( searchRoomsMap.this, locs[i][0] + "X : Y" + locs[i][1] + "", Toast.LENGTH_LONG ).show();
                    try {
                        double x = Double.parseDouble( snapshot.child( "htllat" ).getValue().toString() );
                        double y = Double.parseDouble( snapshot.child( "htllng" ).getValue().toString() );
                        MarkerOptions marker = new MarkerOptions();
                        marker.position( new LatLng( x, y ) );
                        marker.icon( BitmapDescriptorFactory.defaultMarker( BitmapDescriptorFactory.HUE_BLUE ) );
                        marker.title( snapshot.child( "htlName" ).getValue().toString() );

                        googleMap.addMarker( marker );

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    i++;

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        } );
    }


    public boolean checkPerms() {
        if (ActivityCompat.checkSelfPermission( searchRoomsMap.this, Manifest.permission.ACCESS_FINE_LOCATION )
                == PackageManager.PERMISSION_GRANTED) {

            return true;
        } else {
            //when permission denied
            ActivityCompat.requestPermissions( searchRoomsMap.this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 44 );
            return true;

        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    public boolean checkGPS() {
        LocationManager lm = (LocationManager) this.getSystemService( searchRoomsMap.LOCATION_SERVICE );
        boolean gps_enabled = false;

        try {
            gps_enabled = lm.isProviderEnabled( LocationManager.GPS_PROVIDER );

        } catch (Exception ex) {
            ex.printStackTrace();
        }

        if (!gps_enabled) {
            // notify user
            new AlertDialog.Builder( searchRoomsMap.this )
                    .setMessage( R.string.gps_network_not_enabled )

                    .setPositiveButton( R.string.open_location_settings, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                            startActivity( new Intent( Settings.ACTION_LOCATION_SOURCE_SETTINGS ) );

                        }
                    } )
                    .setNegativeButton( R.string.Cancel, null )

                    .show();


        } else {
            Toast.makeText( searchRoomsMap.this, "GPS Enabled", Toast.LENGTH_SHORT ).show();
            return true;
        }
        return false;

    }

    public boolean isConnected() {
        boolean connected = false;
        try {
            ConnectivityManager cm = (ConnectivityManager) getApplicationContext().getSystemService( searchRoomsMap.CONNECTIVITY_SERVICE );
            NetworkInfo nInfo = cm.getActiveNetworkInfo();
            connected = nInfo != null && nInfo.isAvailable() && nInfo.isConnected();
            return connected;
        } catch (Exception e) {
            Log.e( "Connectivity Exception", e.getMessage() );
        }
        return connected;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        fetchHotels( googleMap );
        //  Toast.makeText( searchRoomsMap.this, locs[0][0] + "X : Y" + locs[0][1] + "", Toast.LENGTH_LONG ).show();

        getcurrentlocation();


        googleMap.setOnMarkerClickListener( new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                LatLng latLng = marker.getPosition();
                String placeName = marker.getTitle();
                Toast.makeText( searchRoomsMap.this, marker.getTitle() + "X : Y" + latLng.longitude + "", Toast.LENGTH_SHORT ).show();
                Intent it = new Intent(searchRoomsMap.this, dummy.class);
                it.putExtra("Lat", String.valueOf(latLng.latitude));
                it.putExtra("lng", String.valueOf(latLng.longitude));
                it.putExtra("name", placeName);
               // startActivity(it);
                return false;
            }
        } );

    }


    @TargetApi(Build.VERSION_CODES.M)
    private void getcurrentlocation() {
        //initialize task Location
        /// if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        FusedLocationProviderClient fusedLocationProviderClient = null;

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient( searchRoomsMap.this );

        @SuppressLint("MissingPermission") Task<Location> locationTask = fusedLocationProviderClient.getLastLocation();
        locationTask.addOnSuccessListener( new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {

                if (location != null) {

                    currLat = location.getLatitude();
                    currlong = location.getLongitude();
                    //Toast.makeText( searchRoomsMap.this, currLat + "X : Y" + currlong + "", Toast.LENGTH_SHORT ).show();
                    mapFragment.getMapAsync( new OnMapReadyCallback() {
                        @Override
                        public void onMapReady(GoogleMap googleMap) {
                            //when map is ready
                            mMap = googleMap;
                            //Zoom to current location
                            mMap.animateCamera( CameraUpdateFactory.newLatLngZoom(
                                    new LatLng( currLat, currlong ), 13
                            ) );
                            mMap.addMarker( new MarkerOptions().position( new LatLng( currLat, currlong ) ).title( "Your Location" )
                                    .icon( BitmapDescriptorFactory.defaultMarker( BitmapDescriptorFactory.HUE_GREEN ) ) );
                            mMap.getUiSettings().setMyLocationButtonEnabled( true );
                            mMap.addCircle( new CircleOptions()
                                    .center( new LatLng( currLat, currlong ) )
                                    .radius( 5000 )
                                    .strokeWidth( 0f )
                                    .fillColor( 0x3E1BD85A ) );


                            return;

                        }

                    } );
                } else {
                    Toast.makeText( searchRoomsMap.this, currLat + "X : Y" + currlong + "", Toast.LENGTH_SHORT ).show();

                }
            }

        } );
    }
    // }


    @Override
    protected void onStart() {
        super.onStart();
        FirebaseAuth mAuth= FirebaseAuth.getInstance();

        FirebaseUser currentUser = mAuth.getCurrentUser();
        //updateUI(currentUser);
        if (currentUser == null) {
            startActivity(new Intent( searchRoomsMap.this, GuestSignup.class));
            finish();
        }
        if (isConnected()) {
            if (checkGPS()) {
                if (checkPerms()) {

                } else
                    Toast.makeText( this, R.string.turnonperms, Toast.LENGTH_SHORT ).show();
            } else Toast.makeText( this, R.string.turnongps, Toast.LENGTH_SHORT ).show();
        } else new AlertDialog.Builder( searchRoomsMap.this )
                .setMessage( R.string.no_internetS )
                .setTitle( R.string.no_internet )
                .setPositiveButton( "OK", null )
                .setNegativeButton( R.string.Cancel, null )

                .show();

    }


    @Override
    protected void onRestart() {
        super.onRestart();
        if (isConnected()) {
            if (checkGPS()) {
                if (checkPerms()) {
                    if (mMap == null) {
                        //  onMapReady( mMap );
                    } else mMap.clear();

                } else
                    Toast.makeText( this, R.string.turnonperms, Toast.LENGTH_SHORT ).show();
            } else Toast.makeText( this, R.string.turnongps, Toast.LENGTH_SHORT ).show();
        } else new AlertDialog.Builder( searchRoomsMap.this )
                .setMessage( R.string.no_internetS )
                .setTitle( R.string.no_internet )
                .setPositiveButton( "OK", null )
                .setNegativeButton( R.string.Cancel, null )
                .show();

    }


}

