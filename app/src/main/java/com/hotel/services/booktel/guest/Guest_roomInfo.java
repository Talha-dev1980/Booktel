package com.hotel.services.booktel.guest;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseException;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.hotel.services.booktel.GuestSignup;
import com.hotel.services.booktel.R;
import com.hotel.services.booktel.owner.PicInfo;
import com.squareup.picasso.Picasso;

public class Guest_roomInfo extends AppCompatActivity {

    private Toolbar mtoolbar;
    private String roomKey;
    private String UserId, ownerName;
    private RatingBar ratingBar;
    private Button btnReserve;
    private TextView tv_roomno, tvhotelName, tvownerName, tvbeds, tvrent, tvrating, tvReservedate, tvduration;
    private ImageView imgNet;
    private RecyclerView imagesShow;
    private RelativeLayout reserveLyout;
    private DatabaseReference mReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_guest_room_info );
        mtoolbar = findViewById( R.id.toolbarGRoominfo );
        setSupportActionBar( mtoolbar );
        getSupportActionBar().setTitle( R.string.roominfo );
        getSupportActionBar().setDisplayHomeAsUpEnabled( true );

        Intent it = getIntent();
        roomKey = it.getStringExtra( "Key" );
        UserId = it.getStringExtra( "UserId" );
        ownerName = it.getStringExtra( "OwnerName" );


        ratingBar = (RatingBar) findViewById( R.id.ratingbarRoominfoGV );
        imagesShow = (RecyclerView) findViewById( R.id.imagesShowRominfo );
        imagesShow.setHasFixedSize( true );
        imagesShow.setLayoutManager( new LinearLayoutManager( this, LinearLayoutManager.HORIZONTAL, false ) );
        reserveLyout = (RelativeLayout) findViewById( R.id.tv_reservedatL );
        tvownerName = (TextView) findViewById( R.id.ownernameRominfoGV );
        tvhotelName = (TextView) findViewById( R.id.tvhotelnameRominfoGV );
        tv_roomno = (TextView) findViewById( R.id.roomNoinfoGV );
        tvbeds = (TextView) findViewById( R.id.nofbedsRominfoGV );
        imgNet = (ImageView) findViewById( R.id.netAvailRominfoGV );
        tvrent = (TextView) findViewById( R.id.edtRentrominfoGV );
        tvrating = (TextView) findViewById( R.id.ratingRominfoGV );
        tvduration = (TextView) findViewById( R.id.tv_stayDuration );
        tvReservedate = (TextView) findViewById( R.id.tv_reservedate );
        btnReserve = (Button) findViewById( R.id.btnreserverominfoGV );

        mReference = FirebaseDatabase.getInstance().getReference().child( "Images" ).child( "Rooms" ).child( roomKey );
        fetchData( roomKey, UserId );
        FirebaseDatabase.getInstance().getReference().child( "Rooms" ).child(roomKey ).addValueEventListener( new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChild( "Reserved" ) ) {
                  btnReserve.setVisibility( View.INVISIBLE );
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        } );
        btnReserve.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent it = new Intent( Guest_roomInfo.this, RoomReservation.class );
                it.putExtra( "Key", roomKey );
                it.putExtra( "UserId", UserId );
                it.putExtra( "OwnerName", ownerName );
                startActivity( it );
            }
        } );
    }

    private void fetchData(String roomKey, String userId) {

        FirebaseDatabase.getInstance().getReference().child( "Rooms" ).child( roomKey ).addValueEventListener( new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                tvhotelName.setText( dataSnapshot.child( "hotel" ).getValue().toString() );
                tvownerName.setText( ownerName );
                tv_roomno.setText( dataSnapshot.child( "roomNo" ).getValue().toString() );
                tvbeds.setText( dataSnapshot.child( "beds" ).getValue().toString() );
                tvrent.setText( dataSnapshot.child( "rent" ).getValue().toString() );
                tvrating.setText( dataSnapshot.child( "rating" ).getValue().toString() + "(" + dataSnapshot.child( "customer" ).getValue().toString() + ")" );
                ratingBar.setRating( Float.parseFloat( dataSnapshot.child( "rating" ).getValue().toString() ) );
                if (dataSnapshot.child( "net" ).getValue().toString().equals( "true" )) {
                    imgNet.setImageResource( R.drawable.ic_save );
                } else {
                    imgNet.setImageResource( R.drawable.ic_close );

                }
                if (dataSnapshot.child( "checkedIn" ).getValue().toString().equals( "no" )) {
                    reserveLyout.setVisibility( View.INVISIBLE );
                }
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
    protected void onStart() {
        super.onStart();
        FirebaseAuth mAuth= FirebaseAuth.getInstance();

        FirebaseUser currentUser = mAuth.getCurrentUser();
        //updateUI(currentUser);
        if (currentUser == null) {
            startActivity(new Intent( Guest_roomInfo.this, GuestSignup.class));
            finish();
        }
        FirebaseRecyclerAdapter<PicInfo, Guest_roomInfo.UsersViewHolder> adapter = new FirebaseRecyclerAdapter<PicInfo, Guest_roomInfo.UsersViewHolder>(
                PicInfo.class,
                R.layout.roomimg,
                Guest_roomInfo.UsersViewHolder.class,
                mReference
        ) {
            @Override
            protected void populateViewHolder(Guest_roomInfo.UsersViewHolder holder, final PicInfo pic, final int i) {
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
}
