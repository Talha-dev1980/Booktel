package com.hotel.services.booktel.owner;

import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseException;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.hotel.services.booktel.R;
import com.hotel.services.booktel.ownerSignUp;
import com.squareup.picasso.Picasso;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class RoomInfo extends AppCompatActivity {

    private Toolbar mtoolbar;
    private Button btnSave;
    private String roomKey, currDate, checkInDate;
    private EditText tv_rent, tvCheckindate;
    private TextView tv_hotel, tv_roomno, tv_beds, tv_bill, tv_guest, tv_duration, tvReservedate, tvcheckoutDate;
    private LinearLayout reservedatelayout, checkInlayout, checkOutlayout;
    private ImageButton btnCout;
    private Switch manualCheckedIN, manualCheckedOut;
    private CheckBox checkNet;
    private Calendar calenCheckIn, calenCheckOut;
    private DatePickerDialog.OnDateSetListener dateIn, dateOut;
    private DatabaseReference mReference;
    private RecyclerView imagesShow;
    //String roomno, String nobeds, boolean netFlag, String hotel, String rent
    private String roomNo, noBeds, hotel, rent, hotelId, ownerId, rating, customers, earning;
    private boolean netFlag;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_room_info );

        mtoolbar = findViewById( R.id.toolbarRoominfo );
        setSupportActionBar( mtoolbar );
        getSupportActionBar().setTitle( R.string.roominfo );
        getSupportActionBar().setDisplayHomeAsUpEnabled( true );
        imagesShow = (RecyclerView) findViewById( R.id.imgesShow );
        imagesShow.setHasFixedSize( true );
        imagesShow.setLayoutManager( new LinearLayoutManager( this, LinearLayoutManager.HORIZONTAL, false ) );
        Intent intent = getIntent();
        roomKey = intent.getStringExtra( "roomKey" );
        btnSave = (Button) findViewById( R.id.btn_roomInfoOwner );
        reservedatelayout = (LinearLayout) findViewById( R.id.reservedatelayout );
        checkInlayout = (LinearLayout) findViewById( R.id.checkedInlayout );
        checkOutlayout = (LinearLayout) findViewById( R.id.checkedOutlayout );
        tv_hotel = (TextView) findViewById( R.id.headingAR );
        tv_roomno = (TextView) findViewById( R.id.roomnumberRI );
        tv_rent = (EditText) findViewById( R.id.tv_rentOfrom );
        tv_bill = (TextView) findViewById( R.id.tv_totalrom );
        tv_duration = (TextView) findViewById( R.id.tv_totalEarnings );
        tv_beds = (TextView) findViewById( R.id.nofbeds );
        tvReservedate = (TextView) findViewById( R.id.tvreserveDate );
        tvCheckindate = (EditText) findViewById( R.id.tvCheckedin );
        tvcheckoutDate = (TextView) findViewById( R.id.checkOutDate );
        tv_guest = (TextView) findViewById( R.id.guestnamelayout );
        manualCheckedIN = (Switch) findViewById( R.id.manualCheckedIN );
        manualCheckedOut = (Switch) findViewById( R.id.manualCheckedOut );
        //btnCIn = (ImageButton) findViewById( R.id.btnCin );
        btnCout = (ImageButton) findViewById( R.id.btnCout );
        btnSave = (Button) findViewById( R.id.btn_roomInfoOwner );
        checkNet = (CheckBox) findViewById( R.id.cbNet );

        reservedatelayout.setVisibility( View.GONE );
        checkInlayout.setVisibility( View.GONE );
        checkOutlayout.setVisibility( View.GONE );
        tv_guest.setText( "Available" );


        mReference = FirebaseDatabase.getInstance().getReference().child( "Images" ).child( "Rooms" ).child( roomKey );
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

                tvcheckoutDate.setText( sdf.format( calenCheckOut.getTime() ) );
            }

        };
        updateCheckIn();
        updateCheckOut();


        fetchAllpreData( roomKey );
        btnSave.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        } );
        btnCout.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DatePickerDialog( RoomInfo.this, dateOut, calenCheckOut
                        .get( Calendar.YEAR ), calenCheckOut.get( Calendar.MONTH ),
                        calenCheckOut.get( Calendar.DAY_OF_MONTH ) ).show();
            }
        } );
      /*  btnCIn.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DatePickerDialog( RoomInfo.this, dateIn, calenCheckIn
                        .get( Calendar.YEAR ), calenCheckIn.get( Calendar.MONTH ),
                        calenCheckIn.get( Calendar.DAY_OF_MONTH ) ).show();
            }
        } );*/
        manualCheckedIN.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean checkInstatus = manualCheckedIN.isChecked();
                updateCheckInManual( checkInstatus );
            }
        } );
        manualCheckedOut.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateCheckOutManual( manualCheckedIN.isChecked() );
            }
        } );
        btnSave.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!tv_roomno.getText().equals( null )) {
                    if (!tv_beds.getText().equals( null )) {
                        if (!tv_rent.equals( null ) && noBeds.length() < 3) {


                            //editRoom(String roomno, String nobeds, boolean netFlag, String hotel, String rent, String hotelId, String ownerid, String rating, String customer)
                            editRoom( tv_roomno.getText().toString(), tv_beds.getText().toString(), checkNet.isChecked(), hotel, tv_rent.getText().toString(), hotelId, ownerId, rating, customers, earning );
                        } else {
                            tv_rent.setError( "Enter Valid Rent" );
                        }
                    } else {
                        tv_beds.setError( "Mention No Of Beds" );
                    }
                } else {
                    tv_roomno.setError( "Room No is Necessary" );
                }
            }
        } );
    }

    private void updateCheckInManual(boolean checkInstatus) {
        Map map = new HashMap();
        map.put( "checkedIn", checkInstatus + "" );
        FirebaseDatabase.getInstance().getReference().child( "Rooms" ).child( roomKey ).updateChildren( map ).addOnSuccessListener( new OnSuccessListener() {
            @Override
            public void onSuccess(Object o) {
                new AlertDialog.Builder( RoomInfo.this )
                        .setMessage( R.string.guestCheckin )
                        .setTitle( R.string.success )
                        .setPositiveButton( "OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        } )
                        .show();
            }
        } );
    }

    private void updateCheckOutManual(boolean checkOutstatus) {
        Map map = new HashMap();
        map.put( "checkedIn", checkOutstatus + "" );
        manualCheckedIN.setEnabled( false );
        if (checkOutstatus == true) {
            FirebaseDatabase.getInstance().getReference().child( "Rooms" ).child( roomKey ).child( "Reserved" ).removeValue().addOnSuccessListener( new OnSuccessListener() {
                @Override
                public void onSuccess(Object o) {
                    FirebaseDatabase.getInstance().getReference().child( "Rooms" ).child( roomKey ).child( "checkedIn" ).setValue( "false" );
                    new AlertDialog.Builder( RoomInfo.this )
                            .setMessage( "Guest Leave the room !" )
                            .setTitle( R.string.success )
                            .setPositiveButton( "OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                   // startActivity( new Intent( RoomInfo.this, RoomInfo.class ) );

                                }
                            } )
                            .show();
                }
            } );
        } else {
            new AlertDialog.Builder( RoomInfo.this )
                    .setMessage( "Guest alredy leaved !" )
                    .setTitle( "Reminder" )
                    .setPositiveButton( "OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            manualCheckedIN.setEnabled( false );
                            manualCheckedOut.setChecked( true );

                        }
                    } )
                    .show();
        }
    }

    private void editRoom(String roomno, String nobeds, boolean netFlag, String hotel, String rent, String hotelId, String ownerid, String rating, String customer, final String earning) {

        final DatabaseReference dbref = FirebaseDatabase.getInstance().getReference().child( "Rooms" );
        singleRoom objroom = new singleRoom( roomno, nobeds, netFlag + "", hotel, hotelId, rent, ownerid, rating, customer, "false" );

        dbref.child( roomKey ).setValue( objroom ).addOnCompleteListener( new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    dbref.child( roomKey ).child( "earning" ).setValue( earning );
                    new AlertDialog.Builder( RoomInfo.this )
                            .setMessage( R.string.successfully )
                            .setTitle( R.string.success )
                            .setPositiveButton( "OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                }
                            } )
                            .show();
                }
            }
        } );

    }

    private void fetchAllpreData(String roomKey) {
        checkInDate = "0";
        FirebaseDatabase.getInstance().getReference().child( "Rooms" ).child( roomKey ).addValueEventListener( new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull final DataSnapshot dataSnapshot) {
                roomNo = dataSnapshot.child( "roomNo" ).getValue().toString();
                noBeds = dataSnapshot.child( "beds" ).getValue().toString();
                rent = dataSnapshot.child( "rent" ).getValue().toString();
                hotel = dataSnapshot.child( "hotel" ).getValue().toString();
                rating = dataSnapshot.child( "rating" ).getValue().toString();
                customers = dataSnapshot.child( "customer" ).getValue().toString();
                netFlag = Boolean.parseBoolean( dataSnapshot.child( "net" ).getValue().toString() );
                hotelId = dataSnapshot.child( "hotelID" ).getValue().toString();
                ownerId = dataSnapshot.child( "ownerID" ).getValue().toString();
                if (dataSnapshot.hasChild( "earning" )) {
                    earning = dataSnapshot.child( "earning" ).getValue().toString();
                } else {
                    earning = "0";
                }
                tv_rent.setText( rent );
                tv_beds.setText( noBeds );
                tv_roomno.setText( roomNo );
                tv_hotel.setText( hotel );
                // String roomno, String nobeds, boolean netFlag, String hotel, String rent

                if (dataSnapshot.child( "net" ).getValue().toString().equals( "true" )) {
                    checkNet.setChecked( true );
                } else {
                    checkNet.setChecked( false );
                }

                if (dataSnapshot.hasChild( "Reserved" )) {
                    reservedatelayout.setVisibility( View.VISIBLE );
                    btnSave.setVisibility( View.GONE );

                    FirebaseDatabase.getInstance().getReference().child( "ReservedRooms" )
                            .child( dataSnapshot.child( "Reserved" ).child( "guestId" ).getValue().toString() ).addValueEventListener( new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot mydataSnapshot) {
                            tv_duration.setText( mydataSnapshot.child( "duration" ).getValue().toString() );
                            tv_guest.setText( mydataSnapshot.child( "guestName" ).getValue().toString() );
                            tv_bill.setText( mydataSnapshot.child( "totalBill" ).getValue().toString() );
                            tvReservedate.setText( mydataSnapshot.child( "reserveDate" ).getValue().toString() );
                            tvCheckindate.setText( mydataSnapshot.child( "checkIndate" ).getValue().toString() );
                            manualCheckedIN.setChecked( Boolean.parseBoolean( dataSnapshot.child( "checkedIn" ).getValue().toString() ) );
                            if (dataSnapshot.child( "checkedIn" ).getValue().toString().equals( "false" )) {
                                Toast.makeText( RoomInfo.this, "Not Check IN", Toast.LENGTH_SHORT ).show();
                                checkInlayout.setVisibility( View.VISIBLE );
                                manualCheckedIN.setVisibility( View.VISIBLE );
                            } else {
                                //btnCIn.setVisibility( View.GONE );
                                tvCheckindate.setVisibility( View.GONE );
                                checkInlayout.setVisibility( View.VISIBLE );
                                manualCheckedIN.setVisibility( View.VISIBLE );
                                checkOutlayout.setVisibility( View.VISIBLE );
                                manualCheckedOut.setVisibility( View.VISIBLE );
                            }
                            tvcheckoutDate.setText( mydataSnapshot.child( "checkOutDate" ).getValue().toString() );
                            checkDuration( mydataSnapshot.child( "checkIndate" ).getValue().toString(), mydataSnapshot.child( "checkOutDate" ).getValue().toString() );
                            checkOutlayout.setVisibility( View.VISIBLE );
                            manualCheckedOut.setVisibility( View.VISIBLE );
                            btnCout.setVisibility( View.GONE );

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                        }
                    } );
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        } );
    }

    private void updateCheckIn() {
        String myFormat = "dd/MM/yy"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat( myFormat, Locale.US );
        currDate = sdf.format( calenCheckIn.getTime() );
        // tvCheckindate.setText( sdf.format( calenCheckIn.getTime() ) );

    }

    private void updateCheckOut() {
        String myFormat = "dd/MM/yy"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat( myFormat, Locale.US );

        tvcheckoutDate.setText( sdf.format( calenCheckOut.getTime() ) );
        tvReservedate.setText( sdf.format( calenCheckOut.getTime() ) );

    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseAuth mAuth= FirebaseAuth.getInstance();

        FirebaseUser currentUser = mAuth.getCurrentUser();
        //updateUI(currentUser);
        if (currentUser == null) {
            startActivity(new Intent(RoomInfo.this, ownerSignUp.class));
            finish();
        }
        FirebaseRecyclerAdapter<PicInfo, UsersViewHolder> adapter = new FirebaseRecyclerAdapter<PicInfo, UsersViewHolder>(
                PicInfo.class,
                R.layout.roomimg,
                UsersViewHolder.class,
                mReference
        ) {
            @Override
            protected void populateViewHolder(UsersViewHolder holder, final PicInfo pic, final int i) {
                try {
                    holder.setPic( pic.getthumb_image().toString() );

                    final String user_id = getRef( i ).getKey();

                    //dialog.dismiss();
                } catch (DatabaseException e) {
                    e.printStackTrace();
                }
            }
        };
        imagesShow.setAdapter( adapter );
    }

    public static class UsersViewHolder extends RecyclerView.ViewHolder {

        // public     CircleImageView userImage;

        View mView;

        public UsersViewHolder(@NonNull View itemView) {
            super( itemView );
            mView = itemView;
            // userImage=(CircleImageView) mView.findViewById(R.id.single_userImage_users);


        }

        public void setPic(String link) {
            ImageView img = (ImageView) itemView.findViewById( R.id.serviceThumbroom );
            Picasso.get().load( link + "" ).placeholder( R.drawable.roomsample ).into( img );

        }

    }

    private void checkDuration(String checkInDate, String checkOutDate) {
        String myFormat = "dd/MM/yy"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat( myFormat, Locale.US );
        currDate = sdf.format( calenCheckIn.getTime() );

        SimpleDateFormat formatter = new SimpleDateFormat( "dd/MM/yy" );
        try {
            Date todayDate = formatter.parse( currDate );
            Date checkdate = formatter.parse( checkInDate );
            Date checkOdate = formatter.parse( checkOutDate );


            if (todayDate.getTime() >= checkdate.getTime()) {
                int days = (int) ((checkdate.getTime() - todayDate.getTime()) / (60 * 60 * 24 * 1000));
                tvCheckindate.setError( "Manually Check in If User is In" );
                Toast.makeText( RoomInfo.this, "Manually Check in If User is In", Toast.LENGTH_LONG ).show();

            }
            if (todayDate.getTime() >= checkOdate.getTime()) {
                int days = (int) ((checkOdate.getTime() - todayDate.getTime()) / (60 * 60 * 24 * 1000));
                tvcheckoutDate.setError( "Manually Check Out If User leave" );

            }

        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

}
