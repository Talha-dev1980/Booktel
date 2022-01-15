package com.hotel.services.booktel.guest;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.hotel.services.booktel.GuestSignup;
import com.hotel.services.booktel.R;
import com.hotel.services.booktel.owner.EarningPlot;
import com.hotel.services.booktel.owner.singleRoom;
import com.hotel.services.booktel.ownerSignUp;
import com.squareup.picasso.Picasso;

public class seeHotelGV extends AppCompatActivity {

    private DatabaseReference dbRef;
    private Toolbar mtoolbar;
    private RecyclerView roomsList;
    private ProgressDialog dialog;
    private DatabaseReference mReference;
    private String hotelKey, hotelName;
    private TextView tv_hotelName, tv_totalroms, tv_totalEarnings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_see_hotel_gv );
        mtoolbar = findViewById( R.id.toolbarHotelinfoGV );
        setSupportActionBar( mtoolbar );
        getSupportActionBar().setTitle( R.string.menu_hotel );
        getSupportActionBar().setDisplayHomeAsUpEnabled( true );

        dialog = new ProgressDialog( this );
        dialog.setTitle( "Loading..." );
        dialog.show();
        Intent intent = getIntent();
        hotelKey = intent.getStringExtra( "hotelKey" );
        hotelName = intent.getStringExtra( "hotelName" );

        tv_hotelName = (TextView) findViewById( R.id.tv_hotelOwner );
        tv_totalEarnings = (TextView) findViewById( R.id.tv_totalEarnings );
        tv_totalroms = (TextView) findViewById( R.id.tv_totalroms );
        mReference = FirebaseDatabase.getInstance().getReference().child( "Rooms" );

        roomsList = (RecyclerView) findViewById( R.id.hotelroomscHotel );
        roomsList.setHasFixedSize( true );
        roomsList.setLayoutManager( new LinearLayoutManager( this ) );
        fetchUserData();
        tv_totalEarnings.setText( hotelKey + "" );


    }

    private void fetchUserData() {
        dialog.show();

        tv_hotelName.setText( hotelName );
        FirebaseDatabase.getInstance().getReference().child( "Hotels" ).child( hotelKey ).addValueEventListener( new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChild( "tRooms" )) {
                    if (dataSnapshot.hasChild( "tEarning" )) {


                        tv_totalEarnings.setText( dataSnapshot.child( "tEarning" ).getValue().toString() );
                    }
                    tv_totalroms.setText( dataSnapshot.child( "tRooms" ).getValue().toString() );

                } else {

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

        dialog.show();
        FirebaseAuth mAuth= FirebaseAuth.getInstance();

        FirebaseUser currentUser = mAuth.getCurrentUser();
        //updateUI(currentUser);
        if (currentUser == null) {
            startActivity(new Intent( seeHotelGV.this, GuestSignup.class));
            finish();
        }
        FirebaseRecyclerAdapter<singleRoom, seeHotelGV.UsersViewHolder> adapter = new FirebaseRecyclerAdapter<singleRoom, seeHotelGV.UsersViewHolder>(
                singleRoom.class,
                R.layout.allrooms,
                seeHotelGV.UsersViewHolder.class,
                mReference
        ) {
            @Override
            protected void populateViewHolder(seeHotelGV.UsersViewHolder holder, singleRoom room, int i) {
                //if (room.getHotelID().equals( hotelKey )) {
                holder.tvRoomno.setText( room.getRoomNo().toString() );

                //}
            }
        };
        roomsList.setAdapter( adapter );
        dialog.dismiss();
    }

    public static class UsersViewHolder extends RecyclerView.ViewHolder {
        View mView;
        TextView tvRoomno, tvhotel, tvratings, tvDur;


        public UsersViewHolder(@NonNull View itemView) {
            super( itemView );
            mView = itemView;

            tvRoomno = (TextView) mView.findViewById( R.id.romNoallroomGV );
            tvhotel = (TextView) mView.findViewById( R.id.hotelnamAllromGV );
            tvratings = (TextView) mView.findViewById( R.id.inuseratingsGV );
            tvDur = (TextView) mView.findViewById( R.id.allroomDurationGV );

        }

        public void setImage(String link) {

            ImageView img = (ImageView) mView.findViewById( R.id.imgAllroomgv );
            Picasso.get().load( link ).placeholder( R.drawable.roomsample ).into( img );
        }


    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
