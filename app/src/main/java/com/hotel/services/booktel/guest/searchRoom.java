package com.hotel.services.booktel.guest;


import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseException;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.hotel.services.booktel.R;
import com.hotel.services.booktel.hotel;

/**
 * A simple {@link Fragment} subclass.
 */
public class searchRoom extends Fragment {

    private RecyclerView hotelsList;
    private DatabaseReference mReference;
    private ProgressDialog dialog;

    public searchRoom() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate( R.layout.fragment_search_room, container, false );
        dialog = new ProgressDialog( getContext() );
        dialog.setTitle( "Loading..." );
        dialog.show();
        hotelsList = v.findViewById( R.id.allhotels );
        hotelsList.setHasFixedSize( true );
        hotelsList.setLayoutManager( new LinearLayoutManager( getContext() ) );
        mReference = FirebaseDatabase.getInstance().getReference().child( "Hotels" );
        return v;
    }

    public void onStart() {
        super.onStart();
        FirebaseRecyclerAdapter<hotel, UsersViewHolder> adapter = new FirebaseRecyclerAdapter<hotel, UsersViewHolder>(
                hotel.class,
                R.layout.singlehotel,
                UsersViewHolder.class,
                mReference
        ) {
            @SuppressLint("ResourceAsColor")
            @Override
            protected void populateViewHolder(final UsersViewHolder holder, final hotel objHotel, final int i) {
                try {
                    final String[] ownerName = {null};
                    //       holder.tvRooms.setText( objHotel.gettRooms() + "" );
                    holder.tvhotelName.setText( objHotel.getHtlName() );

                    FirebaseDatabase.getInstance().getReference().child( "Owners" ).child( objHotel.getOwnerid() ).addValueEventListener( new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                            ownerName[0] = dataSnapshot.child( "name" ).getValue().toString();
                            holder.tvOwnerName.setText( ownerName[0] );
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    } );


                    holder.mView.setOnClickListener( new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent it = new Intent( getContext(), dummy.class );
                            it.putExtra( "hotelKey", getRef( i ).getKey() );
                            it.putExtra( "hotelName", objHotel.getHtlName() );
                            it.putExtra( "earnings", "0" );
                            it.putExtra( "rooms", "0" );
                            it.putExtra( "userView", "Guest" );
                            startActivity( it );
                        }
                    } );
                } catch (DatabaseException e) {
                    e.printStackTrace();
                }
            }


        };
        hotelsList.setAdapter( adapter );
        dialog.dismiss();


    }

    public static class UsersViewHolder extends RecyclerView.ViewHolder {
        View mView;
        TextView tvhotelName, tvOwnerName, tvRooms;
        CardView card;

        public UsersViewHolder(@NonNull View itemView) {
            super( itemView );
            mView = itemView;
            card = mView.findViewById( R.id.hotelCard );

            tvRooms = mView.findViewById( R.id.tvNofRooms );
            tvOwnerName = mView.findViewById( R.id.tvHotelOwnerName );
            tvhotelName = mView.findViewById( R.id.tvHotelName );

        }

    }


}
