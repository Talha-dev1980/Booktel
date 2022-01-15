package com.hotel.services.booktel.guest;

import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.hotel.services.booktel.GuestSignup;
import com.hotel.services.booktel.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class RoomReservation extends AppCompatActivity {

    private Toolbar mtoolbar;
    private TextView tv_hotelName, tv_ownerName, tvRoomno, tvBeds, tvtoday, tvCalDur, tvBill, tvcheckOut, tvcheckIn;
    private ImageView imgNet;
    private Button btnReserve;
    private ImageButton btnchekIn, btnCheckout;
    private String roomKey, UserId, ownerName, ownerId, Key;
    private int customers, earning, earningRoom, customerRoom;
    private boolean flag = false;
    private int rentPerNight;
    private Calendar calenCheckIn, calenCheckOut;
    private DatePickerDialog.OnDateSetListener dateIn, dateOut;
    private boolean durationCheck = true;
    private DatabaseReference myRef, mReference;
    String[] months = {"Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"};
    String month;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_room_reservation );
        mtoolbar = findViewById( R.id.toolbarRoomReserv );
        setSupportActionBar( mtoolbar );
        getSupportActionBar().setTitle( R.string.roominfo );
        getSupportActionBar().setDisplayHomeAsUpEnabled( true );

        tv_hotelName = (TextView) findViewById( R.id.tv_hotelNameRomResr );
        tv_ownerName = (TextView) findViewById( R.id.tv_ownernameRomReser );
        tvRoomno = (TextView) findViewById( R.id.tv_roomnumRomReser );
        tvBeds = (TextView) findViewById( R.id.tv_bedsRomreserv );
        imgNet = (ImageView) findViewById( R.id.img_romreserv );
        tvtoday = (TextView) findViewById( R.id.tvtodayRomreserv );
        tvCalDur = (TextView) findViewById( R.id.tv_calDurationromreserv );
        tvcheckIn = (TextView) findViewById( R.id.tvCheckedinRomreser );
        tvcheckOut = (TextView) findViewById( R.id.tvCheckedoutRomReser );
        tvBill = (TextView) findViewById( R.id.tv_totalBillRomReser );
        btnReserve = (Button) findViewById( R.id.btnReserveRomInfo );
        btnchekIn = (ImageButton) findViewById( R.id.btnCheckInDate );
        btnCheckout = (ImageButton) findViewById( R.id.btnCheckOutDate );

        myRef = FirebaseDatabase.getInstance().getReference();
        calenCheckIn = Calendar.getInstance();
        calenCheckOut = Calendar.getInstance();
        //  checkInDate();
        dateIn = new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                calenCheckIn.set( Calendar.YEAR, year );
                calenCheckIn.set( Calendar.MONTH, monthOfYear );
                calenCheckIn.set( Calendar.DAY_OF_MONTH, dayOfMonth );
                updateCheckIn();

                checkDuration();
            }

        };
        dateOut = new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                calenCheckOut.set( Calendar.YEAR, year );
                calenCheckOut.set( Calendar.MONTH, monthOfYear );
                calenCheckOut.set( Calendar.DAY_OF_MONTH, dayOfMonth );
                String myFormat = "dd/MM/yy"; //In which you need put here
                SimpleDateFormat sdf = new SimpleDateFormat( myFormat, Locale.US );

                tvcheckOut.setText( sdf.format( calenCheckOut.getTime() ) );
                checkDuration();
            }

        };
        updateCheckIn();
        updateCheckOut();


        Intent it = getIntent();
        roomKey = it.getStringExtra( "Key" );
        UserId = it.getStringExtra( "UserId" );
        ownerName = it.getStringExtra( "OwnerName" );
        tv_ownerName.setText( ownerName );

        fetchData( roomKey, UserId );

        //Toast.makeText( this, ownerId + "", Toast.LENGTH_LONG ).show();


        //Key=mReference.push().getKey().toString();


        btnchekIn.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DatePickerDialog( RoomReservation.this, dateIn, calenCheckIn
                        .get( Calendar.YEAR ), calenCheckIn.get( Calendar.MONTH ),
                        calenCheckIn.get( Calendar.DAY_OF_MONTH ) ).show();
            }
        } );
        btnCheckout.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DatePickerDialog( RoomReservation.this, dateOut, calenCheckOut
                        .get( Calendar.YEAR ), calenCheckOut.get( Calendar.MONTH ),
                        calenCheckOut.get( Calendar.DAY_OF_MONTH ) ).show();

            }
        } );
        btnReserve.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkDuration();
                final String reserveDate, checkindate, checkOutdate, totalBill, duration;
                reserveDate = tvtoday.getText().toString();
                checkindate = tvcheckIn.getText().toString();
                checkOutdate = tvcheckOut.getText().toString();
                totalBill = tvBill.getText().toString();
                duration = tvCalDur.getText().toString();
                myRef.child( "Guests" ).child( UserId ).addValueEventListener( new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        try {

                            String guestName = dataSnapshot.child( "name" ).getValue().toString();

                            UserId = FirebaseAuth.getInstance().getUid().toString();
                            reserveRoom( UserId, guestName, reserveDate, checkindate, checkOutdate, totalBill, duration, roomKey, ownerId );

                        } catch (Exception e) {
                            e.getMessage();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                } );


            }
        } );


    }

    private void reserveRoom(String userId, String guestName, String reserveDate, final String checkindate, String checkOutdate, final String totalBill, String duration, final String roomKey, String ownerId) {
        HashMap<String, String> roomDetail = new HashMap<>();
        roomDetail.put( "guestId", userId );
        roomDetail.put( "guestName", guestName );
        roomDetail.put( "duration", duration );
        roomDetail.put( "roomKey", roomKey );
        roomDetail.put( "totalBill", totalBill );

        myRef.child( "Rooms" ).child( roomKey ).child( "Reserved" ).setValue( roomDetail );
        roomDetail.put( "reserveDate", reserveDate );
        roomDetail.put( "checkIndate", checkindate );
        roomDetail.put( "checkOutDate", checkOutdate );
        roomDetail.put( "ownerId", ownerId );

        myRef.child( "ReservedRooms" ).child( userId ).setValue( roomDetail ).addOnCompleteListener( new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {

                    new AlertDialog.Builder( RoomReservation.this )
                            .setMessage( R.string.reservedSuccess )
                            .setTitle( R.string.success )
                            .setCancelable( false )
                            .setPositiveButton( "OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    month = months[calenCheckIn.getTime().getMonth()];

                                    addPayment( checkindate, roomKey, month, totalBill );
                                    startActivity( new Intent( RoomReservation.this, GuestsView.class ) );
                                }
                            } )
                            .show();
                }
            }
        } );

    }

    private void addPayment(String checkInDate, String roomId, final String thisMonth, final String totalBill) {
        String id = mReference.child( "All" ).push().getKey().toString();


        final HashMap<String, String> pointvalue = new HashMap<>();
        pointvalue.put( "xValue", calenCheckIn.get( Calendar.DAY_OF_MONTH ) + "" );
        pointvalue.put( "yValue", totalBill + "" );
        pointvalue.put( "month", thisMonth );
        pointvalue.put( "roomId", roomId );
        pointvalue.put( "dateRecieved", calenCheckIn.getTime().toString() );
        final Map earningValue = new HashMap();
        earningValue.put( "month", thisMonth );

        int totalEarning = earning + Integer.parseInt( totalBill );
        earningValue.put( "customer", customers + 1 + "" );
        earningValue.put( "earning", totalEarning + "" );

        mReference.child( "All" ).child( thisMonth ).updateChildren( earningValue );

        mReference.child( "GraphData" ).child( thisMonth ).child( id ).setValue( pointvalue );


    }

    private void checkDuration() {
        SimpleDateFormat formatter = new SimpleDateFormat( "dd/MM/yy" );
        try {
            Date dateIn = formatter.parse( tvcheckIn.getText().toString() );
            Date dateOut = formatter.parse( tvcheckOut.getText().toString() );


            if (dateOut.getTime() > dateIn.getTime()) {
                int days = (int) ((dateOut.getTime() - dateIn.getTime()) / (60 * 60 * 24 * 1000));
                tvCalDur.setTextColor( Color.BLACK );
                tvCalDur.setText( days + " days" );
                tvBill.setText( "" + (days * rentPerNight) );

            } else {
                // int days=dateOut.getDay()- dateIn.getDay();
                tvcheckOut.setError( "Date Can't be Handle" );
                tvCalDur.setText( R.string.negDuration );
                tvCalDur.setTextColor( Color.RED );
                durationCheck = false;
            }


        } catch (ParseException e) {
            e.printStackTrace();
        }
    }


    private void updateCheckIn() {
        String myFormat = "dd/MM/yy"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat( myFormat, Locale.US );

        tvcheckIn.setText( sdf.format( calenCheckIn.getTime() ) );

    }

    private void updateCheckOut() {
        String myFormat = "dd/MM/yy"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat( myFormat, Locale.US );

        tvcheckOut.setText( sdf.format( calenCheckOut.getTime() ) );
        tvtoday.setText( sdf.format( calenCheckOut.getTime() ) );

    }

    private void fetchData(String roomKey, String userId) {

        FirebaseDatabase.getInstance().getReference().child( "Rooms" ).child( roomKey ).addValueEventListener( new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                ownerId = dataSnapshot.child( "ownerID" ).getValue().toString();
                //Toast.makeText( RoomReservation.this, ownerId + "", Toast.LENGTH_LONG ).show();
                mReference = FirebaseDatabase.getInstance().getReference().child( "Earnings" ).child( ownerId );

                Key = mReference.push().getKey().toString();
                rentPerNight = Integer.parseInt( dataSnapshot.child( "rent" ).getValue().toString() );
                tv_hotelName.setText( dataSnapshot.child( "hotel" ).getValue().toString() );
                tvRoomno.setText( dataSnapshot.child( "roomNo" ).getValue().toString() );
                tvBeds.setText( dataSnapshot.child( "beds" ).getValue().toString() );
                tvBill.setText( dataSnapshot.child( "rent" ).getValue().toString() );
                if (dataSnapshot.child( "net" ).getValue().toString().equals( "true" )) {
                    imgNet.setImageResource( R.drawable.ic_save );
                } else {
                    imgNet.setImageResource( R.drawable.ic_close );
                }
                month = months[calenCheckIn.getTime().getMonth()];
                mReference.child( "All" ).child( month ).addValueEventListener( new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.hasChild( "customer" )) {
                            customers = Integer.parseInt( dataSnapshot.child( "customer" ).getValue().toString() );
                            earning = Integer.parseInt( dataSnapshot.child( "earning" ).getValue().toString() );
                           // Toast.makeText( RoomReservation.this, customers + "   " + earning, Toast.LENGTH_LONG ).show();
                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                } );

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        } );


    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseAuth mAuth= FirebaseAuth.getInstance();

        FirebaseUser currentUser = mAuth.getCurrentUser();
        //updateUI(currentUser);
        if (currentUser == null) {
            startActivity(new Intent( RoomReservation.this, GuestSignup.class));
            finish();
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
