package com.hotel.services.booktel.guest;


import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
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
import com.hotel.services.booktel.owner.singleRoom;
import com.squareup.picasso.Picasso;


public class AvbRooms extends Fragment {


    private DatabaseReference mReference;
    private String UserId;
    private RecyclerView roomsShowlist;
    private String ownerName;

    private ProgressDialog dialog;

    public AvbRooms() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate( R.layout.fragment_avb_rooms, container, false );
        UserId = FirebaseAuth.getInstance().getUid().toString();

        dialog = new ProgressDialog( getContext() );
        dialog.setTitle( "Loading..." );
        dialog.show();
        mReference = FirebaseDatabase.getInstance().getReference().child( "Rooms" );
        roomsShowlist = (RecyclerView) v.findViewById( R.id.roomsViewGV );
        roomsShowlist.setHasFixedSize( true );
        roomsShowlist.setLayoutManager( new LinearLayoutManager( getContext() ) );


        return v;

    }

    public void onStart() {
        dialog.show();
        super.onStart();
        FirebaseRecyclerAdapter<singleRoom, AvbRooms.UsersViewHolder> adapter = new FirebaseRecyclerAdapter<singleRoom, AvbRooms.UsersViewHolder>(
                singleRoom.class,
                R.layout.guest_singleroom,
                AvbRooms.UsersViewHolder.class,
                mReference
        ) {
            @SuppressLint("ResourceAsColor")
            @Override
            protected void populateViewHolder(final AvbRooms.UsersViewHolder holder, final singleRoom room, final int i) {
                try {
                    final String[] imageLink = {null};
                    holder.tvRoomno.setText( room.getRoomNo() + "" );
                    holder.tvhotel.setText( room.getHotel().toString() );
                    holder.ratingBar.setRating( Float.parseFloat( room.getRating().toString() ) );
                    holder.tvratings.setText( room.getRating() + "(" + room.getCustomer() + ")" );
                    holder.tvPernight.setText( "RS." + room.getRent().toString() + "/-" );
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
                    FirebaseDatabase.getInstance().getReference().child( "Owners" ).child( room.getOwnerID().toString() ).addValueEventListener( new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull final DataSnapshot dataSnapshot) {
                            ownerName = dataSnapshot.child( "name" ).getValue().toString();
                            holder.tvOwnerName.setText( ownerName );
                            holder.mView.setOnClickListener( new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Intent it = new Intent( getContext(), Guest_roomInfo.class );
                                    it.putExtra( "Key", getRef( i ).getKey().toString() );
                                    it.putExtra( "UserId", UserId );
                                    it.putExtra( "OwnerName", dataSnapshot.child( "name" ).getValue().toString() );
                                    startActivity( it );
                                }
                            } );
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
        TextView tvRoomno, tvOwnerName, tvhotel, tvPernight, tvratings, tvCheckedSts;
        RatingBar ratingBar;

        public UsersViewHolder(@NonNull View itemView) {
            super( itemView );
            mView = itemView;
            tvRoomno = (TextView) mView.findViewById( R.id.tvroomnumberGV );
            tvOwnerName = (TextView) mView.findViewById( R.id.ownerNameGV );
            tvhotel = (TextView) mView.findViewById( R.id.hotelnamGV );
            tvCheckedSts = (TextView) mView.findViewById( R.id.tv_checkedStatusGV );
            tvPernight = (TextView) mView.findViewById( R.id.pernightGV );
            tvratings = (TextView) mView.findViewById( R.id.tv_ratingsRoomGV );
            ratingBar = (RatingBar) mView.findViewById( R.id.ratingbarGsingleroom );
        }

        public void setImage(String link) {
            ImageView img = (ImageView) mView.findViewById( R.id.serviceThumbGV );
            Picasso.get().load( link ).placeholder( R.drawable.roomsample ).into( img );
        }


    }
}
