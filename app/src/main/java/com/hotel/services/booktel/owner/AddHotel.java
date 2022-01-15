package com.hotel.services.booktel.owner;

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
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.hotel.services.booktel.R;
import com.hotel.services.booktel.ownerSignUp;

import java.util.HashMap;

public class AddHotel extends AppCompatActivity {

    private Button btnaddHotel, btnGetLoc;
    private EditText edtLat, edtLang, edtHotelname, edtContact;
    private DatabaseReference dbref;
    private String UserId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_hotel);
        btnaddHotel = (Button) findViewById(R.id.btnRegisterhotel);

        btnGetLoc = (Button) findViewById(R.id.btngetLoc);
        edtLat = (EditText) findViewById(R.id.edthtlloclat);
        edtLang = (EditText) findViewById(R.id.edthtlloclang);
        edtHotelname = (EditText) findViewById(R.id.edthtlName);
        edtContact = (EditText) findViewById(R.id.edthtlnumber);

        UserId=FirebaseAuth.getInstance().getUid().toString();
        dbref = FirebaseDatabase.getInstance().getReference().child("Hotels");

        btnGetLoc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               /* Intent it = new Intent(getApplicationContext(), OwnerView.class);
                it.putExtra("regStatus", "yes");
                startActivity(it);*/
                getcurrentlocation();

            }
        });
        btnaddHotel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name, contact, lng, lat, pic;
                pic = "null";
                name = edtHotelname.getText().toString();
                contact = edtContact.getText().toString();
                lng = edtLang.getText().toString().trim();
                lat = edtLat.getText().toString().trim();
                if (!name.equals(null)) {
                    if (!(contact.length() < 11)) {
                        if (!lat.equals(null) && !lng.equals(null)) {
                            addHotel(name, contact, lng, lat, pic);

                        }

                    }
                }
            }
        });
    }

    private void addHotel(String name, String contact, String lng, String lat, String pic) {
        String ownerid = FirebaseAuth.getInstance().getUid().toString();
        HashMap<String, String> hotelData = new HashMap<>();
        hotelData.put("htlName", name);
        hotelData.put("htlContact", contact);
        hotelData.put("htllng", lng);
        hotelData.put("htllat", lat);
        hotelData.put("htlpic", pic);
        hotelData.put("ownerid", ownerid);
        String key = dbref.push().getKey().toString();
        dbref.child(key).setValue(hotelData).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {

                if (task.isSuccessful()) {
                    edtHotelname.setText("");
                    edtContact.setText("");
                    edtLang.setText("");
                    edtLat.setText("");
                    Toast.makeText(AddHotel.this, "Added SuccessFuly", Toast.LENGTH_LONG).show();

                } else {
                    Toast.makeText(AddHotel.this, "Failed To Add Hotel", Toast.LENGTH_LONG).show();
                }
            }
        });
        HashMap<String,String> htlDetails=new HashMap<>();
        htlDetails.put("htlName", name);
        FirebaseDatabase.getInstance().getReference().child("Owners").child(ownerid).child("Hotels").child(key).setValue(htlDetails);
        startActivity(new Intent(AddHotel.this,OwnerView.class));
    }


    @TargetApi(Build.VERSION_CODES.M)
    private void getcurrentlocation() {
        //initialize task Location
        /// if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        FusedLocationProviderClient fusedLocationProviderClient = null;

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(AddHotel.this);

        @SuppressLint("MissingPermission") Task<Location> locationTask = fusedLocationProviderClient.getLastLocation();
        locationTask.addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {

                if (location != null) {
                    edtLat.setText(location.getLatitude() + "");
                    edtLang.setText(location.getLongitude() + "");

                } else {
                    Toast.makeText(AddHotel.this, location.getLatitude() + "X : Y" + location.getLongitude() + "", Toast.LENGTH_SHORT).show();

                }
            }

        });
    }

    public boolean checkPerms() {
        if (ActivityCompat.checkSelfPermission(AddHotel.this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {

            return true;
        } else {
            //when permission denied
            ActivityCompat.requestPermissions(AddHotel.this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 44);
            return true;

        }
    }

    public boolean checkGPS() {
        LocationManager lm = (LocationManager) this.getSystemService(AddHotel.LOCATION_SERVICE);
        boolean gps_enabled = false;

        try {
            gps_enabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);

        } catch (Exception ex) {
            ex.printStackTrace();
        }

        if (!gps_enabled) {
            // notify user
            new AlertDialog.Builder(AddHotel.this)
                    .setMessage(R.string.gps_network_not_enabled)

                    .setPositiveButton(R.string.open_location_settings, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                            startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));

                        }
                    })
                    .setNegativeButton(R.string.Cancel, null)

                    .show();


        } else {
            Toast.makeText(AddHotel.this, "GPS Enabled", Toast.LENGTH_SHORT).show();
            return true;
        }
        return false;

    }

    public boolean isConnected() {
        boolean connected = false;
        try {
            ConnectivityManager cm = (ConnectivityManager) getApplicationContext().getSystemService(AddHotel.CONNECTIVITY_SERVICE);
            NetworkInfo nInfo = cm.getActiveNetworkInfo();
            connected = nInfo != null && nInfo.isAvailable() && nInfo.isConnected();
            return connected;
        } catch (Exception e) {
            Log.e("Connectivity Exception", e.getMessage());
        }
        return connected;
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseAuth mAuth= FirebaseAuth.getInstance();

        FirebaseUser currentUser = mAuth.getCurrentUser();
        //updateUI(currentUser);
        if (currentUser == null) {
            startActivity(new Intent(AddHotel.this, ownerSignUp.class));
            finish();
        }

        if (isConnected()) {
            if (checkGPS()) {
                if (checkPerms()) {


                } else
                    Toast.makeText(this, R.string.turnonperms, Toast.LENGTH_SHORT).show();
            } else Toast.makeText(this, R.string.turnongps, Toast.LENGTH_SHORT).show();
        } else new AlertDialog.Builder(AddHotel.this)
                .setMessage(R.string.no_internetS)
                .setTitle(R.string.no_internet)
                .setPositiveButton("OK", null)
                .setNegativeButton(R.string.Cancel, null)
                .show();

    }


}
