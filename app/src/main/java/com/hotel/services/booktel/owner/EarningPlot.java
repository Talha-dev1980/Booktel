package com.hotel.services.booktel.owner;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
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
import com.hotel.services.booktel.R;
import com.hotel.services.booktel.ownerSignUp;
import com.jjoe64.graphview.DefaultLabelFormatter;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;
import com.squareup.picasso.Picasso;

import java.util.Calendar;

public class EarningPlot extends AppCompatActivity {
    private Toolbar mtoolbar;
    // line chart
    private LineGraphSeries series;
    private GraphView lineChart;
    private DatabaseReference mReference;
    private String UserId;
    private TextView tv_month;
    private RecyclerView roomsEarnings;
    String[] months = {"Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"};
    String month;
    private String ownerID;
    private Calendar calenDate;


    @RequiresApi(api = Build.VERSION_CODES.O)
    @SuppressLint("ResourceAsColor")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_earning_plot );
        mtoolbar = findViewById( R.id.toolbarEarningplot );
        setSupportActionBar( mtoolbar );
        getSupportActionBar().setTitle( R.string.anlytics );
        getSupportActionBar().setDisplayHomeAsUpEnabled( true );
        calenDate = Calendar.getInstance();
        ownerID = FirebaseAuth.getInstance().getUid().toString();
        month = months[calenDate.getTime().getMonth()];

        lineChart = (GraphView) findViewById( R.id.lineChart );
        roomsEarnings = (RecyclerView) findViewById( R.id.statsList );
        roomsEarnings.setHasFixedSize( true );
        roomsEarnings.setLayoutManager( new LinearLayoutManager( this ) );
        tv_month = (TextView) findViewById( R.id.tv_MonthName );
        series = new LineGraphSeries();
        lineChart.addSeries( series );
        UserId = FirebaseAuth.getInstance().getCurrentUser().getUid().toString();
        mReference = FirebaseDatabase.getInstance().getReference().child( "Rooms" );
        lineChart.setTitleTextSize( 16f );
        lineChart.getGridLabelRenderer().setLabelFormatter( new DefaultLabelFormatter() {
            @Override
            public String formatLabel(double value, boolean isValueX) {
                if (isValueX) {
                    return value + "";
                } else {
                    return super.formatLabel( value, isValueX );
                }
            }
        } );
        lineChart.setTitleColor( R.color.colorWhite );
        lineChart.setCursorMode( true );
        lineChart.setClickable( true );


    }


    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public void onStart() {
        FirebaseAuth mAuth= FirebaseAuth.getInstance();

        FirebaseUser currentUser = mAuth.getCurrentUser();
        //updateUI(currentUser);
        if (currentUser == null) {
            startActivity(new Intent(EarningPlot.this, ownerSignUp.class));
            finish();
        }
        Calendar calenDate = Calendar.getInstance();

        Toast.makeText( this, month + "", Toast.LENGTH_LONG ).show();
        setGraph();
        FirebaseRecyclerAdapter<singleRoom, UsersViewHolder> adapter = new FirebaseRecyclerAdapter<singleRoom, UsersViewHolder>(
                singleRoom.class,
                R.layout.monthlystats,
                UsersViewHolder.class,
                mReference
        ) {
            @Override
            protected void populateViewHolder(final UsersViewHolder holder, final singleRoom room, final int i) {

                if (room.getOwnerID().equals( ownerID )) {
                    holder.tv_RoomNo.setText( "Room : " + room.getRoomNo() );
                    holder.tv_customers.setText( room.getCustomer() + " Customers" );
                    holder.tv_earnings.setText( room.getEarning() + "/-" );
                    if (room.getCustomer() == null) {
                        holder.mView.setVisibility( View.GONE );
                    }
                    holder.mView.setOnClickListener( new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            new AlertDialog.Builder( EarningPlot.this )


                                    .setMessage(
                                            "Customers: " + room.getCustomer() + "\n" +
                                                    "Earnings: " + room.getEarning() + "/-" )
                                    .setTitle( "Room : " + room.getRoomNo() )
                                    .setCancelable( true )
                                    .setPositiveButton( "OK", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {

                                        }
                                    } )
                                    .show();
                        }


                    } );
                } else {
                    holder.card.setVisibility( View.GONE );
                }


            }
        };
        roomsEarnings.setAdapter( adapter );
        super.onStart();
    }


    private void setGraph() {
        final String keyMonth = months[calenDate.getTime().getMonth()];

        FirebaseDatabase.getInstance().getReference().child( "Earnings" ).child( ownerID )
                .child( "GraphData" ).child( keyMonth ).addValueEventListener( new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                DataPoint[] dp = new DataPoint[(int) dataSnapshot.getChildrenCount()];
                int[] arrX = new int[(int) dataSnapshot.getChildrenCount()];
                int[] arrY = new int[(int) dataSnapshot.getChildrenCount()];
                int index = 0;
                int tempX=0,tempY=0;
               // Toast.makeText( EarningPlot.this, ownerID + " " + dataSnapshot.getChildrenCount(), Toast.LENGTH_LONG ).show();
                // Toast.makeText( EarningPlot.this, Integer.parseInt( dataSnapshot.child( "yValue" ).getValue().toString() ) , Toast.LENGTH_LONG ).show();

                for (DataSnapshot data : dataSnapshot.getChildren()) {
                    //if (data.hasChild( "xValue" )) {
                    arrX[index] = Integer.parseInt( data.child( "xValue" ).getValue().toString() );
                    arrY[index] = Integer.parseInt( data.child( "yValue" ).getValue().toString() );

                    index++;

                    //}
                }
                tv_month.setText( keyMonth );
                for (int i = 0; i < arrX.length; i++) {
                    for (int j = i + 1; j < arrX.length; j++) {
                        if (arrX[i] > arrX[j]) {
                            tempX = arrX[i];
                            tempY=arrY[i];
                            arrX[i] = arrX[j];
                            arrY[i]=arrY[j];
                            arrX[j] = tempX;
                            arrY[j]=tempY;
                        }
                    }
                }
                for(int i=0;i<arrX.length;i++){
                dp[i] = new DataPoint( arrX[i] ,arrY[i]);}

                series.resetData( dp );
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        } );
    }


    public static class UsersViewHolder extends RecyclerView.ViewHolder {

        TextView tv_RoomNo, tv_customers, tv_earnings;
        View mView;
        CardView card;

        public UsersViewHolder(@NonNull View itemView) {
            super( itemView );
            mView = itemView;
            tv_RoomNo = (TextView) mView.findViewById( R.id.tvmonthName );
            tv_customers = (TextView) mView.findViewById( R.id.tv_customers );
            tv_earnings = (TextView) mView.findViewById( R.id.tv_earnings );
            card = (CardView) mView.findViewById( R.id.monthcard );


        }

        public void setPic(String link) {
            ImageView img = (ImageView) itemView.findViewById( R.id.serviceThumbroom );
            Picasso.get().load( link + "" ).placeholder( R.drawable.roomsample ).into( img );

        }

    }
}



