package com.hotel.services.booktel.owner;

import android.Manifest;
import android.app.ProgressDialog;
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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.hotel.services.booktel.R;
import com.hotel.services.booktel.ownerSignUp;

public class OwnerView extends AppCompatActivity {

    String user_loggedin;
    private FirebaseAuth mAuth;
    private rooms objroom;
    private Analytics objanalytics;
    private about objabout;
    private AddRoom objaddroom;
    private ProgressDialog dialog;
    private EarningPlot objEarningplot;
    private RoomInfo objRoominfo;
    private String name, email, profile, phone;
    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.nav_room:
                    setFragment(objroom, name, profile, email);
                    return true;
                case R.id.nav_sales:
                    setFragment(objanalytics, name, profile, email);
                    return true;
                case R.id.nav_about:
                    setFragment(objabout, name, profile, email);
                    return true;
            }
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_owner_view);
        BottomNavigationView navView = findViewById(R.id.nav_view);
        navView.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        objabout = new about();
        objroom = new rooms();
        objanalytics = new Analytics();
        objRoominfo = new RoomInfo();
        objaddroom = new AddRoom();
        objEarningplot = new EarningPlot();
        isConnected();
        checkGPS();
        checkPerms();

        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        //updateUI(currentUser);
        if (currentUser == null) {
            sendToStart();
        } else {
            user_loggedin = currentUser.getUid().toString();
            loadOwnerData();
            seeforHotels();
        }
        dialog = new ProgressDialog(OwnerView.this);
        dialog.setTitle("Loading Your Data !");
        dialog.show();


        setFragment(objroom, name, profile, email);
        navView.setSelectedItemId(R.id.nav_room);

    }

    private void showDaloge() {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Alert");
        builder.setMessage("You Don't have any Hotel");
        builder.setCancelable(false);
        // add a button
        builder.setPositiveButton("Add Hotel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                startActivity(new Intent(getApplicationContext(), AddHotel.class));
            }
        });
        // create and show the alert dialog
        AlertDialog dialog = builder.create();
        dialog.show();

    }

    private void setFragment(Fragment fragment, String user, String pic, String email) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        Bundle bundle = new Bundle();
        bundle.putString("Name", user);
        bundle.putString("email", email);
        bundle.putString("Profile", pic);

        fragment.setArguments(bundle);
        transaction.replace(R.id.mianFrame, fragment);
        // transaction.addToBackStack(null);
        transaction.commit();
        dialog.dismiss();
    }

    @Override
    public void onStart() {
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        //updateUI(currentUser);
        if (currentUser == null) {
            sendToStart();
        } else {
            user_loggedin = currentUser.getUid().toString();
            loadOwnerData();
            seeforHotels();
        }
        super.onStart();

    }

    private void sendToStart() {
        startActivity(new Intent(OwnerView.this, ownerSignUp.class));
        finish();
    }

    private void seeforHotels() {

        DatabaseReference dbref = FirebaseDatabase.getInstance().getReference().child("Owners").child(user_loggedin);
        dbref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if (!dataSnapshot.hasChild("Hotels")) {
                    showDaloge();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

                showDaloge();

            }
        });

    }

    private void loadOwnerData() {
        DatabaseReference db = FirebaseDatabase.getInstance().getReference().child("Owners");
       // Toast.makeText( this,user_loggedin+"",Toast.LENGTH_LONG ).show();
        db.child(user_loggedin).addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                name = dataSnapshot.child("name").getValue().toString();
                email = dataSnapshot.child("email").getValue().toString();
                profile = dataSnapshot.child("Profile").getValue().toString();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

                showDaloge("Your Session is out login again !");
            }
        });
    }





    public void btnlogout(View view) {
        FirebaseAuth.getInstance().signOut();
        sendToStart();
    }

    private void showDaloge(String mesaage) {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Alert");
        builder.setMessage(mesaage);
        builder.setCancelable(false);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(getApplicationContext(), ownerSignUp.class));
            }
        });
        // create and show the alert dialog
        AlertDialog dialog = builder.create();
        dialog.show();

    }

    public void btnback(View view) {
        onBackPressed();
    }

    public boolean checkPerms() {
        if (ActivityCompat.checkSelfPermission( OwnerView.this, Manifest.permission.ACCESS_FINE_LOCATION )
                == PackageManager.PERMISSION_GRANTED) {

            return true;
        } else {
            //when permission denied
            ActivityCompat.requestPermissions( OwnerView.this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 44 );
            return true;

        }
    }

    public boolean checkGPS() {
        LocationManager lm = (LocationManager) this.getSystemService( OwnerView.LOCATION_SERVICE );
        boolean gps_enabled = false;

        try {
            gps_enabled = lm.isProviderEnabled( LocationManager.GPS_PROVIDER );

        } catch (Exception ex) {
            ex.printStackTrace();
        }

        if (!gps_enabled) {
            // notify user
            new AlertDialog.Builder( OwnerView.this )
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
            Toast.makeText( OwnerView.this, "GPS Enabled", Toast.LENGTH_SHORT ).show();
            return true;
        }
        return false;

    }

    public boolean isConnected() {
        boolean connected = false;
        try {
            ConnectivityManager cm = (ConnectivityManager) getApplicationContext().getSystemService( OwnerView.CONNECTIVITY_SERVICE );
            NetworkInfo nInfo = cm.getActiveNetworkInfo();
            connected = nInfo != null && nInfo.isAvailable() && nInfo.isConnected();
            return connected;
        } catch (Exception e) {
            Log.e( "Connectivity Exception", e.getMessage() );
        }
        return connected;
    }
}
