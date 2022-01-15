package com.hotel.services.booktel.owner;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseException;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.hotel.services.booktel.R;
import com.squareup.picasso.Picasso;

public class rooms extends Fragment {
    private Button btnAddroom;
    private String UserId;
    private DatabaseReference mReference;
    private RecyclerView roomsShowlist;
    private TextView tvRoom;
    private ProgressDialog dialog;

    public rooms() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate( R.layout.fragment_rooms, container, false );
        btnAddroom = (Button) v.findViewById( R.id.btnAddRoom );
        tvRoom = (TextView) v.findViewById( R.id.tvroom );
        UserId = FirebaseAuth.getInstance().getUid().toString();

        mReference = FirebaseDatabase.getInstance().getReference().child( "Rooms" );
        roomsShowlist = (RecyclerView) v.findViewById( R.id.roomsShow );
        roomsShowlist.setHasFixedSize( true );
        roomsShowlist.setLayoutManager( new LinearLayoutManager( getContext() ) );

        dialog = new ProgressDialog( getContext() );
        dialog.setTitle( "loading..." );
        dialog.show();
        FirebaseDatabase.getInstance().getReference().child( "Rooms" ).addValueEventListener( new ValueEventListener() {
            @Override

            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if (dataSnapshot.hasChild( UserId )) {
                    tvRoom.setVisibility( View.INVISIBLE );
                } else {

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        } );

        btnAddroom.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent it = new Intent( getContext(), AddRoom.class );
                startActivity( it );
            }
        } );

        return v;
    }

    @Override
    public void onStart() {
        super.onStart();
        FirebaseRecyclerAdapter<singleRoom, rooms.UsersViewHolder> adapter = new FirebaseRecyclerAdapter<singleRoom, rooms.UsersViewHolder>(
                singleRoom.class,
                R.layout.singleroom,
                rooms.UsersViewHolder.class,
                mReference
        ) {
            @SuppressLint("ResourceAsColor")
            @Override
            protected void populateViewHolder(final rooms.UsersViewHolder holder, final singleRoom room, final int i) {
                try {
                    if (room.getOwnerID().equals( UserId )) {
                        tvRoom.setVisibility( View.GONE );
                        final String[] imageLink = {null};
                        holder.tvRoomno.setText( room.getRoomNo() + "" );
                        holder.tvhotel.setText( room.getHotel().toString() );
                        holder.ratingBar.setRating( Float.parseFloat( room.getRating().toString() ) );
                        holder.tvratings.setText( room.getRating() + "(" + room.getCustomer() + ")" );

                        FirebaseDatabase.getInstance().getReference().child( "Images" ).child( "Rooms" ).child( getRef( i ).getKey().toString() ).addValueEventListener( new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                for (DataSnapshot datas : dataSnapshot.getChildren()) {
                                    holder.setImage( datas.child( "thumb_image" ).getValue().toString() );
                                    break;
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        } );
                        FirebaseDatabase.getInstance().getReference().child( "Rooms" ).child( getRef( i ).getKey().toString() ).addValueEventListener( new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                if (dataSnapshot.hasChild( "Reserved" )) {
                                    holder.tvCheckedSts.setText( "Reserved" );
                                    holder.tvCheckedSts.setBackgroundColor( R.color.colorAccent );

                                } else {
                                    holder.tvCheckedSts.setText( "Available" );
                                    holder.tvCheckedSts.setBackgroundColor( R.color.colorPrimary );
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        } );
                        if (room.getCheckedIn().equals( "false" )) {
                            holder.tvCheckedSts.setText( "Available" );
                            holder.tvCheckedSts.setBackgroundColor( R.color.colorPrimary );
                            holder.tvcheckedDate.setVisibility( View.GONE );
                            holder.tvcheckedat.setVisibility( View.GONE );
                            holder.tvcustomer.setText( "RS." + room.getRent().toString() + "/-" );
                        } else {
                            holder.tvCheckedSts.setText( "Checked In" );
                            holder.tvCheckedSts.setBackgroundColor( R.color.colorPrimary );
                            holder.tvcheckedDate.setVisibility( View.GONE );
                            holder.tvcustomer.setText( "RS." + room.getRent().toString() + "/-" );

                        }
                    } else {
                        holder.roomCard.setVisibility( View.GONE );
                    }
                    holder.mView.setOnClickListener( new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent( getContext(), RoomInfo.class );
                            intent.putExtra( "roomKey", getRef( i ).getKey().toString() );
                            startActivity( intent );
                        }
                    } );
                } catch (DatabaseException e) {
                    e.printStackTrace();
                }
            }


        };
        roomsShowlist.setAdapter( adapter );
        dialog.dismiss();

    }


    public static class UsersViewHolder extends RecyclerView.ViewHolder {


        View mView;
        TextView tvRoomno, tvcustomer, tvhotel, tvcheckedDate, tvcheckedat, tvratings, tvPernight, tvCheckedSts;
        RatingBar ratingBar;
        CardView roomCard;


        public UsersViewHolder(@NonNull View itemView) {
            super( itemView );
            mView = itemView;


            tvRoomno = (TextView) mView.findViewById( R.id.tvroomnumber );
            tvcustomer = (TextView) mView.findViewById( R.id.guestname );
            tvhotel = (TextView) mView.findViewById( R.id.tv_hotelname );
            tvCheckedSts = (TextView) mView.findViewById( R.id.tv_checkedStatus );
            tvcheckedDate = (TextView) mView.findViewById( R.id.checkedDate );
            tvcheckedat = (TextView) mView.findViewById( R.id.checkedDateSR );
            tvratings = (TextView) mView.findViewById( R.id.tv_ratingsRoom );
            tvPernight = (TextView) mView.findViewById( R.id.perNight );
            ratingBar = (RatingBar) mView.findViewById( R.id.ratingbarsingleroom );
            roomCard = (CardView) mView.findViewById( R.id.roomCard );


        }

        public void setImage(String link) {

            ImageView img = (ImageView) mView.findViewById( R.id.Thumbservice );
            Picasso.get().load( link ).placeholder( R.drawable.roomsample ).into( img );
        }


    }
}
