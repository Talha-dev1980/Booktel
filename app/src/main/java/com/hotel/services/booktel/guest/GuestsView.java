package com.hotel.services.booktel.guest;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.hotel.services.booktel.GuestSignup;
import com.hotel.services.booktel.R;

public class GuestsView extends AppCompatActivity {
    private AvbRooms objrooms;
    private Profile objprofile;
    private String user_loggedin = "Dummy Guest";
    private FirebaseAuth mAuth;
    private searchRoom objRoomsMap;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.nav_roomss:
                    setFragment( objrooms, user_loggedin );
                    return true;
                case R.id.nav_search:
                    setFragment( objRoomsMap, user_loggedin );
                    return true;
                case R.id.nav_profile:
                    setFragment( objprofile, user_loggedin );
                    return true;
            }
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_guests_view );
        BottomNavigationView navView = findViewById( R.id.nav_view );
        objprofile = new Profile();
        objrooms = new AvbRooms();
        objRoomsMap = new searchRoom();
        isConnected();
        checkGPS();
        checkPerms();
        //  objMap = new mapFrag();

        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        //updateUI(currentUser);
        if (currentUser == null) {
            sendToStart();
        } else {
            user_loggedin = currentUser.getUid().toString();

        }
        navView.setOnNavigationItemSelectedListener( mOnNavigationItemSelectedListener );
        setFragment( objrooms, user_loggedin );
        navView.setSelectedItemId( R.id.nav_roomss );
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        //updateUI(currentUser);
        if (currentUser == null) {
            sendToStart();
        } else {
            user_loggedin = currentUser.getUid().toString();

        }
    }

    private void sendToStart() {
        startActivity( new Intent( this, GuestSignup.class ) );
        finish();
    }

    private void setFragment(Fragment fragment, String user) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        Bundle bundle = new Bundle();
        bundle.putString( "User_loggedIn", user );

        fragment.setArguments( bundle );
        transaction.replace( R.id.mianFrame2, fragment );
        transaction.commit();
    }


    public void btnLogoutGuest(View view) {
        FirebaseAuth.getInstance().signOut();
        sendToStart();
    }

    public void btnSearchOnmap(View view) {
        startActivity( new Intent( GuestsView.this, searchRoomsMap.class ) );
    }

    public void btnGuestroominfo(View view) {
        Toast.makeText( getApplicationContext(), "Currently You are not Using any Room!", Toast.LENGTH_LONG ).show();
    }

    public boolean checkPerms() {
        if (ActivityCompat.checkSelfPermission( GuestsView.this, Manifest.permission.ACCESS_FINE_LOCATION )
                == PackageManager.PERMISSION_GRANTED) {

            return true;
        } else {
            //when permission denied
            ActivityCompat.requestPermissions( GuestsView.this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 44 );
            return true;

        }
    }

    public boolean checkGPS() {
        LocationManager lm = (LocationManager) this.getSystemService( GuestsView.LOCATION_SERVICE );
        boolean gps_enabled = false;

        try {
            gps_enabled = lm.isProviderEnabled( LocationManager.GPS_PROVIDER );

        } catch (Exception ex) {
            ex.printStackTrace();
        }

        if (!gps_enabled) {
            // notify user
            new AlertDialog.Builder( GuestsView.this )
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
            Toast.makeText( GuestsView.this, "GPS Enabled", Toast.LENGTH_SHORT ).show();
            return true;
        }
        return false;

    }

    public boolean isConnected() {
        boolean connected = false;
        try {
            ConnectivityManager cm = (ConnectivityManager) getApplicationContext().getSystemService( GuestsView.CONNECTIVITY_SERVICE );
            NetworkInfo nInfo = cm.getActiveNetworkInfo();
            connected = nInfo != null && nInfo.isAvailable() && nInfo.isConnected();
            return connected;
        } catch (Exception e) {
            Log.e( "Connectivity Exception", e.getMessage() );
        }
        return connected;
    }

}
