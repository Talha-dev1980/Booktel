package com.hotel.services.booktel.owner;


import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.hotel.services.booktel.R;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * A simple {@link Fragment} subclass.
 */
public class about extends Fragment {


    private Spinner spHotels;
    private RatingBar ratingBar;
    private CircleImageView imgView;
    private TextView tvName;
    private DatabaseReference mUserdatabase;
    private String name, profile, UserId, email, phone;
    private Button btnOwner;
    private LinearLayout lyHotlinfo, ownerinfoLayout;

    public about() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v = inflater.inflate( R.layout.fragment_about, container, false );

        lyHotlinfo = (LinearLayout) v.findViewById( R.id.hotlinfoLayout );
        ownerinfoLayout = (LinearLayout) v.findViewById( R.id.ownerinfoLayout );
        ratingBar = (RatingBar) v.findViewById( R.id.ratingbarAbout );
        btnOwner = (Button) v.findViewById( R.id.btn_owner );
        UserId = FirebaseAuth.getInstance().getUid().toString();
        tvName = (TextView) v.findViewById( R.id.tv_ownername );
        imgView = (CircleImageView) v.findViewById( R.id.ownerImg );

        ratingBar.setRating( 4 );
        fetchUserData();

        ownerinfoLayout.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent it = new Intent( getContext(), info.class );
                it.putExtra( "Name", name );
                it.putExtra( "email", email );
                it.putExtra( "Profile", profile );
                it.putExtra( "phone", phone );
                it.putExtra( "userView", "Owner" );
                /*Intent it = new Intent( dummy.this, info.class );
                        it.putExtra( "Name", dataSnapshot.child( "name" ).getValue().toString() );
                        it.putExtra( "email",dataSnapshot.child( "email" ).getValue().toString() );
                        it.putExtra( "Profile", dataSnapshot.child( "Profile" ).getValue().toString()  );
                        it.putExtra( "phone", dataSnapshot.child( "phone" ).getValue().toString()  );
                        it.putExtra( "userView", "Guest" );*/
                startActivity( it );
            }
        } );
        lyHotlinfo.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent it = new Intent( getContext(), Hotel_info.class );
                startActivity( it );
            }
        } );


        return v;
    }


    public void fetchUserData() {
        FirebaseDatabase.getInstance().getReference().child( "Owners" ).child( UserId ).addValueEventListener( new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                email = dataSnapshot.child( "email" ).getValue().toString();
                profile = dataSnapshot.child( "Profile" ).getValue().toString();
                name = dataSnapshot.child( "name" ).getValue().toString();
                if (dataSnapshot.hasChild( "phone" )) {
                    phone = dataSnapshot.child( "phone" ).getValue().toString();
                }
                Picasso.get().load( profile + "" ).placeholder( R.drawable.ic_avator ).into( imgView );
                tvName.setText( name );
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        } );

    }
}
