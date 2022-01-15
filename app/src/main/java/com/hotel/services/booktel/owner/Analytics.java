package com.hotel.services.booktel.owner;


import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.hotel.services.booktel.R;

import java.util.Calendar;

/**
 * A simple {@link Fragment} subclass.
 */
public class Analytics extends Fragment {


    private CardView earningPlotcard;
    private int reservedRooms = 0, allRooms = 0, checkedInRooms = 0;
    private String ownerId;
    String[] months = {"Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"};
    String month;
    private Calendar calen;
    private TextView tvEarning, tv_reservedRooms,tv_checkedIncounter;

    public Analytics() {
        // Required empty public constructor

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate( R.layout.fragment_analytics, container, false );
        ownerId = FirebaseAuth.getInstance().getUid().toString();
        earningPlotcard = (CardView) v.findViewById( R.id.earningPlotcard );
        tvEarning = (TextView) v.findViewById( R.id.earningTv );
        tv_reservedRooms = (TextView) v.findViewById( R.id.reservedRoomsCount );
        tv_checkedIncounter = (TextView) v.findViewById( R.id.checkedRoomsCount );
        calen = Calendar.getInstance();

        new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                calen.set( Calendar.YEAR, year );
                calen.set( Calendar.MONTH, monthOfYear );
                calen.set( Calendar.DAY_OF_MONTH, dayOfMonth );
            }

        };
        month = months[calen.getTime().getMonth()];

        earningPlotcard.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent it = new Intent( getContext(), EarningPlot.class );
                startActivity( it );
            }
        } );
        fetchEarning( ownerId, month );
        fetchReserved();
        fetchChecked();
        return v;
    }

    private void fetchEarning(final String id, final String mon) {

        FirebaseDatabase.getInstance().getReference().child( "Earnings" ).addValueEventListener( new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChild( id )) {
                    tvEarning.setText( dataSnapshot.child( id ).child( "All" ).child( mon ).child( "earning" ).getValue().toString() );
                } else tvEarning.setText( "0" );
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        } );

    }

    private void fetchReserved() {
        reservedRooms = 0;
        allRooms = 0;
        FirebaseDatabase.getInstance().getReference().child( "Rooms" ).addValueEventListener( new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot data : dataSnapshot.getChildren()) {
                    if (data.child( "ownerID" ).getValue().equals( ownerId )) {
                        if (data.hasChild( "Reserved" )) {
                            reservedRooms = reservedRooms + 1;
                        }

                    }
                    allRooms++;
                }
                tv_reservedRooms.setText( reservedRooms + " of " + allRooms );

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        } );
    }

    private void fetchChecked() {
        reservedRooms = 0;
        checkedInRooms = 0;
        FirebaseDatabase.getInstance().getReference().child( "Rooms" ).addValueEventListener( new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot data : dataSnapshot.getChildren()) {
                    if (data.child( "ownerID" ).getValue().equals( ownerId )) {
                        if (data.hasChild( "Reserved" )) {
                            if (data.child( "checkedIn" ).getValue().equals( "true" )) {
                                checkedInRooms++;
                            }
                            reservedRooms = reservedRooms + 1;
                        }

                    }
                }
                tv_checkedIncounter.setText( checkedInRooms + " of " + reservedRooms );

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        } );
    }

}
