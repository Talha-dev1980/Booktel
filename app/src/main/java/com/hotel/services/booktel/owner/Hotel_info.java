package com.hotel.services.booktel.owner;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.hotel.services.booktel.R;
import com.hotel.services.booktel.guest.dummy;
import com.hotel.services.booktel.ownerSignUp;

import java.util.ArrayList;

public class Hotel_info extends AppCompatActivity implements AdapterView.OnItemSelectedListener, OnMapReadyCallback {

    private TextView tvHotelname, tv_Hotelearnings, tv_roomsInhotel;
    private Spinner spHotels;
    private Toolbar mtoolbar;
    private Button btnAddHotel;
    private String UserID, hotelkey, htlname;
    private int rooms = 0, earning = 0;
    private GoogleMap mMap;
    private Button btnMore;
    private double currLat = 0, currlong = 0;
    private SupportMapFragment mapFragment;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_hotel_info );

        mtoolbar = findViewById( R.id.toolbarHotelinfo );
        setSupportActionBar( mtoolbar );
        getSupportActionBar().setTitle( R.string.menu_hotel );
        getSupportActionBar().setDisplayHomeAsUpEnabled( true );

        tvHotelname = (TextView) findViewById( R.id.htlname );
        tv_Hotelearnings = (TextView) findViewById( R.id.tv_Hotelearnings );
        tv_roomsInhotel = (TextView) findViewById( R.id.tv_roomsInhotel );
        btnAddHotel = (Button) findViewById( R.id.btn_AddHotel );
        btnMore = (Button) findViewById( R.id.btnMore );

        UserID = FirebaseAuth.getInstance().getUid().toString();
        fetchData();
        spHotels = (Spinner) findViewById( R.id.hotelsMenu );
        mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById( R.id.hotelLoc );


        mapFragment.getMapAsync( this );


        btnAddHotel.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity( new Intent( Hotel_info.this, AddHotel.class ) );
            }
        } );
        btnMore.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent it = new Intent( Hotel_info.this, dummy.class );
                it.putExtra( "hotelKey", hotelkey );
                it.putExtra( "hotelName", htlname );
                it.putExtra( "earnings", tv_Hotelearnings.getText() );
                it.putExtra( "rooms", tv_roomsInhotel.getText() );
                it.putExtra( "userView", "Owner" );
                startActivity( it );
            }
        } );

    }

    private void fetchData() {
        //create list to store all data
        final ArrayList<String> list = new ArrayList<String>();
        DatabaseReference myReference = FirebaseDatabase.getInstance().getReference().child( "Hotels" );
        // Read from the database
        myReference.addValueEventListener( new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                if (!dataSnapshot.getChildren().toString().equals( null )) {
                    for (DataSnapshot datas : dataSnapshot.getChildren()) {
                        if (datas.child( "ownerid" ).getValue().toString().equals( UserID )) {

                            String hotelName = datas.child( "htlName" ).getValue().toString();
                            list.add( hotelName );
                        }
                    }

                } else {
                    tvHotelname.setText( "Not fetching Data" );
                }
                ArrayAdapter<String> adapter = new ArrayAdapter<String>( Hotel_info.this, android.R.layout.simple_spinner_item, list );
                adapter.setDropDownViewResource( android.R.layout.simple_dropdown_item_1line );
                spHotels.setAdapter( adapter );
                spHotels.setOnItemSelectedListener( Hotel_info.this );


            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
            }
        } );
    }

    private void fetchRooms(final String hotelId) {
        rooms = 0;
        earning = 0;
        FirebaseDatabase.getInstance().getReference().child( "Rooms" ).addValueEventListener( new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    if (snapshot.child( "hotelID" ).getValue().toString().equals( hotelId )) {
                        rooms++;
                        if (snapshot.hasChild( "earning" )) {
                            earning = earning + Integer.parseInt( snapshot.child( "earning" ).getValue().toString() );
                        }
                    }
                }
                tv_roomsInhotel.setText( rooms + "" );
                tv_Hotelearnings.setText( earning + "" );

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        } );

    }


    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }


    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        tvHotelname.setText( parent.getItemAtPosition( position ) + "" );
        fetchHotel( parent.getItemAtPosition( position ) + "" );
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
    }

    private void fetchHotel(final String hotelName) {
        FirebaseDatabase.getInstance().getReference().child( "Hotels" ).addValueEventListener( new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    if (snapshot.child( "ownerid" ).getValue().equals( UserID ) && snapshot.child( "htlName" ).getValue().equals( hotelName )) {
                        double x = Double.parseDouble( snapshot.child( "htllat" ).getValue().toString() );
                        double y = Double.parseDouble( snapshot.child( "htllng" ).getValue().toString() );
                        MarkerOptions marker = new MarkerOptions();
                        marker.position( new LatLng( x, y ) );
                        marker.icon( BitmapDescriptorFactory.defaultMarker( BitmapDescriptorFactory.HUE_BLUE ) );
                        marker.title( snapshot.child( "htlName" ).getValue().toString() );
                        mMap.animateCamera( CameraUpdateFactory.newLatLngZoom(
                                new LatLng( x, y ), 14
                        ) );
                        mMap.addMarker( marker );
                        fetchRooms( snapshot.getRef().getKey().toString() );
                        hotelkey = snapshot.getKey().toString();
                        htlname = snapshot.child( "htlName" ).getValue().toString();

                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        } );
    }

    @Override
    protected void onStart() {

        super.onStart();
        FirebaseAuth mAuth = FirebaseAuth.getInstance();

        FirebaseUser currentUser = mAuth.getCurrentUser();
        //updateUI(currentUser);
        if (currentUser == null) {
            startActivity( new Intent( Hotel_info.this, ownerSignUp.class ) );
            finish();
        }
    }
}
