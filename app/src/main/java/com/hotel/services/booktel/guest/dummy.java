package com.hotel.services.booktel.guest;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.hotel.services.booktel.R;
import com.hotel.services.booktel.owner.RoomInfo;
import com.hotel.services.booktel.owner.info;
import com.hotel.services.booktel.owner.singleRoom;
import com.squareup.picasso.Picasso;

public class dummy extends AppCompatActivity {

    private RecyclerView recyclerView;
    private DatabaseReference mReference;
    private Toolbar mtoolbar;
    private ProgressDialog dialog;
    private int rooms = 0, earning = 0;
    private String hotelKey, hotelName, ownerName, userView, ownerId;
    private TextView tv_hotelName, tv_totalroms, tv_totalEarnings, tv_norooms;
    private Button btn_owner;
    private LinearLayout earningsLayoutdummy;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_dummy );//toolbarHotelRooms
        mtoolbar = findViewById( R.id.toolbarHotelRooms );
        setSupportActionBar( mtoolbar );
        getSupportActionBar().setTitle( R.string.menu_hotel );
        getSupportActionBar().setDisplayHomeAsUpEnabled( true );
        dialog = new ProgressDialog( this );
        dialog.setTitle( "Loading..." );
        dialog.show();
        Intent intent = getIntent();
        hotelKey = intent.getStringExtra( "hotelKey" );
        hotelName = intent.getStringExtra( "hotelName" );
        userView = intent.getStringExtra( "userView" );

        recyclerView = (RecyclerView) findViewById( R.id.AllRooomsShowList );
        earningsLayoutdummy = (LinearLayout) findViewById( R.id.earningsLayoutdummy );
        recyclerView.setHasFixedSize( true );
        recyclerView.setLayoutManager( new LinearLayoutManager( this ) );
        tv_hotelName = (TextView) findViewById( R.id.tv_hotelOwner );
        tv_totalEarnings = (TextView) findViewById( R.id.tv_totalEarnings );
        tv_totalroms = (TextView) findViewById( R.id.tv_totalroms );
        tv_norooms = (TextView) findViewById( R.id.norroms );
        btn_owner = (Button) findViewById( R.id.btn_owner );
        if (userView.equals( "Owner" )) {
            btn_owner.setVisibility( View.GONE );
            tv_totalEarnings.setText( intent.getStringExtra( "earnings" )+"/-" );
            tv_totalroms.setText( intent.getStringExtra( "rooms" ) );
        }else{
            earningsLayoutdummy.setVisibility( View.GONE );
        }
        mReference = FirebaseDatabase.getInstance().getReference().child( "Rooms" );
        tv_hotelName.setText( hotelName + "" );
        btn_owner.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ownerProfile( hotelKey );
            }
        } );
    }

    private void ownerProfile(String hotelKey) {
        FirebaseDatabase.getInstance().getReference().child( "Hotels" ).child( hotelKey ).addValueEventListener( new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                ownerId = dataSnapshot.child( "ownerid" ).getValue().toString();
                FirebaseDatabase.getInstance().getReference().child( "Owners" ).child( ownerId ).addValueEventListener( new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        Intent it = new Intent( dummy.this, info.class );
                        it.putExtra( "Name", dataSnapshot.child( "name" ).getValue().toString() );
                        it.putExtra( "email", dataSnapshot.child( "email" ).getValue().toString() );
                        it.putExtra( "Profile", dataSnapshot.child( "Profile" ).getValue().toString() );
                        if (dataSnapshot.hasChild( "phone" )){
                        it.putExtra( "phone", dataSnapshot.child( "phone" ).getValue().toString() );}
                        else{
                            it.putExtra( "phone","");
                        }
                        it.putExtra( "userView", "Guest" );
                        startActivity( it );
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
        earning = 0;
        rooms = 0;
        FirebaseRecyclerAdapter<singleRoom, dummy.UsersViewHolder> adapter = new FirebaseRecyclerAdapter<singleRoom, dummy.UsersViewHolder>(
                singleRoom.class,
                R.layout.allrooms,
                dummy.UsersViewHolder.class,
                mReference

        ) {
            @Override
            protected void populateViewHolder(final UsersViewHolder usersViewHolder, final singleRoom room, final int i) {
                if (room.getHotelID().equals( hotelKey )) {
                    usersViewHolder.tv_room.setText( room.getRoomNo() + "" );
                    usersViewHolder.tvhotel.setText( room.getHotel() + "" );
                    usersViewHolder.tv_rating.setText( room.getRent() );
                    FirebaseDatabase.getInstance().getReference().child( "Images" ).child( "Rooms" ).child( getRef( i ).getKey().toString() ).addValueEventListener( new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            for (DataSnapshot datas : dataSnapshot.getChildren()) {
                                usersViewHolder.setImage( datas.child( "thumb_image" ).getValue().toString() );
                                break;
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                        }
                    } );
                    if (!userView.equals( "Owner" )) {

                        usersViewHolder.tv_dur.setVisibility( View.GONE );
                    }
                    FirebaseDatabase.getInstance().getReference().child( "Owners" ).child( room.getOwnerID().toString() ).addValueEventListener( new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            ownerName = dataSnapshot.child( "name" ).getValue().toString();
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    } );
                    usersViewHolder.mView.setOnClickListener( new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {


                            if (userView.equals( "Owner" )){
                                Intent it = new Intent( dummy.this, RoomInfo.class );
                                it.putExtra( "roomKey", getRef( i ).getKey().toString() );
                                it.putExtra( "UserId", room.getOwnerID() );
                                it.putExtra( "OwnerName", ownerName );
                                startActivity( it );
                            }else {
                                Intent it = new Intent( dummy.this, Guest_roomInfo.class );
                                it.putExtra( "Key", getRef( i ).getKey().toString() );
                                it.putExtra( "UserId", room.getOwnerID() );
                                it.putExtra( "OwnerName", ownerName );
                                startActivity( it );}
                        }
                    } );

                } else {
                    usersViewHolder.cardView.setVisibility( View.GONE );

                }
            }

        };
        recyclerView.setAdapter( adapter );

        dialog.dismiss();

    }

    public static class UsersViewHolder extends RecyclerView.ViewHolder {

        View mView;
        public TextView tv_room, tvhotel, tv_dur, tv_rating;
        public CardView cardView;
        public ImageView imageView;

        public UsersViewHolder(@NonNull View itemView) {
            super( itemView );
            mView = itemView;
            tv_room = (TextView) mView.findViewById( R.id.romNoallroomGV );
            tvhotel = (TextView) mView.findViewById( R.id.hotelnamAllromGV );
            tv_dur = (TextView) mView.findViewById( R.id.allroomDurationGV );
            tv_rating = (TextView) mView.findViewById( R.id.inuseratingsGV );
            cardView = (CardView) mView.findViewById( R.id.allRoomsGV );
            imageView = (ImageView) mView.findViewById( R.id.imgAllroomgv );

        }
        public void setImage(String imgUrl){
            Picasso.get().load( imgUrl ).placeholder( R.drawable.roomsample ).into( imageView );
        }
    }


    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
