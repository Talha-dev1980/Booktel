package com.hotel.services.booktel.guest;


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
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

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
public class Profile extends Fragment {


    private RatingBar ratingBar;
    private String UserId, name, ratedby, profile, roomKey, duration, email;
    private TextView tvRating, tvName;
    private float rating;
    private Button btn_editguest;
    private CircleImageView imgView;
    private Button btnLogout;
    private RecyclerView reservedRomList;
    private DatabaseReference mReference;
    private ProgressDialog dialog;
    private TextView tvRoomno, tvhotel, tvratings, tvDur;
    private ImageView img;

    public Profile() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate( R.layout.fragment_profile, container, false );
        ratingBar = (RatingBar) v.findViewById( R.id.ratingbar );
        tvRating = (TextView) v.findViewById( R.id.tvRatingsGP );
        tvName = (TextView) v.findViewById( R.id.tv_guestName );
        imgView = (CircleImageView) v.findViewById( R.id.guestImageGV );
        UserId = FirebaseAuth.getInstance().getUid().toString();
        dialog = new ProgressDialog( getContext() );
        mReference = FirebaseDatabase.getInstance().getReference().child( "ReservedRooms" );
        img = (ImageView) v.findViewById( R.id.imgAllroom );

//        btnLogout=(Button) v.findViewById( R.id.btnLogoutGuest );
        btn_editguest = (Button) v.findViewById( R.id.btn_editguest );
        dialog.setTitle( "Loading..." );
        dialog.show();
        fetchUserData();

        tvRoomno = (TextView) v.findViewById( R.id.romNoallroom );
        tvhotel = (TextView) v.findViewById( R.id.hotelnamAllrom );
        tvratings = (TextView) v.findViewById( R.id.inuseratings );
        tvDur = (TextView) v.findViewById( R.id.allroomDuration );
        btn_editguest.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity( new Intent( getContext(), guestProfile.class ) );
            }
        } );
        return v;
    }

    private void fetchUserData() {
        FirebaseDatabase.getInstance().getReference().child( "Guests" ).child( UserId ).addValueEventListener( new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                name = dataSnapshot.child( "name" ).getValue().toString();
                email = dataSnapshot.child( "email" ).getValue().toString();
                ratedby = dataSnapshot.child( "ratedby" ).getValue().toString();
                profile = dataSnapshot.child( "Profile" ).getValue().toString();
                rating = Float.parseFloat( dataSnapshot.child( "rating" ).getValue().toString() );
                ratingBar.setRating( rating );
                tvRating.setText( rating + "(" + ratedby + ")" );
                tvName.setText( name );
                Picasso.get().load( profile + "" ).placeholder( R.drawable.ic_avator ).into( imgView );

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        } );

        mReference.child( UserId ).addValueEventListener( new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                roomKey = dataSnapshot.child( "roomKey" ).getValue().toString();
                FirebaseDatabase.getInstance().getReference().child( "Rooms" ).child( roomKey ).addValueEventListener( new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        tvhotel.setText( dataSnapshot.child( "hotel" ).getValue().toString() );
                        tvratings.setText( dataSnapshot.child( "rating" ).getValue().toString() + "(" + dataSnapshot.child( "customer" ).getValue().toString() + ")" );
                        tvRoomno.setText( "Room Number " + dataSnapshot.child( "roomNo" ).getValue().toString() );
                        FirebaseDatabase.getInstance().getReference().child( "Images" ).child( "Rooms" ).child( roomKey ).addValueEventListener( new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                for (DataSnapshot datas : dataSnapshot.getChildren()) {
                                    setImage( datas.child( "thumb_image" ).getValue().toString() );
                                    dialog.dismiss();
                                    break;
                                }


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
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        } );

    }

    private void setImage(String thumb_image) {
        Picasso.get().load( thumb_image ).placeholder( R.drawable.roomsample ).into( img );
    }

    /* @Override
     public void onStart() {
         super.onStart();
         FirebaseRecyclerAdapter<singleRoom, UsersViewHolder> adapter = new FirebaseRecyclerAdapter<singleRoom, UsersViewHolder>(
                 singleRoom.class,
                 R.layout.allrooms,
                 UsersViewHolder.class,
                 mReference
         ) {
             @SuppressLint("ResourceAsColor")
             @Override
             protected void populateViewHolder(final UsersViewHolder holder, final singleRoom room, final int i) {
                 try {
                     if (room.getGuestId().equals( UserId )) {
                         String roomkey = room.getRoomKey().toString();
                         holder.tvDur.setText( room.getDuration().toString() );
                         FirebaseDatabase.getInstance().getReference().child( "Rooms" ).child( roomkey ).addValueEventListener( new ValueEventListener() {
                             @Override
                             public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                 holder.tvhotel.setText( dataSnapshot.child( "hotel" ).getValue().toString() );
                                 holder.tvratings.setText( dataSnapshot.child( "rating" ).getValue().toString() + "(" + dataSnapshot.child( "customer" ).getValue().toString() + ")" );
                                 holder.tvRoomno.setText( dataSnapshot.child( "roomNo" ).getValue().toString() );
                             }

                             @Override
                             public void onCancelled(@NonNull DatabaseError databaseError) {
                             }
                         } );
                     } else {
                         holder.mView.setVisibility( View.GONE );
                     }
                 } catch (DatabaseException e) {
                     e.printStackTrace();
                 }
             }


         };
         reservedRomList.setAdapter( adapter );
         dialog.dismiss();


     }

    public static class UsersViewHolder extends RecyclerView.ViewHolder {
        View mView;
        TextView tvRoomno, tvhotel, tvratings, tvDur;
//allRoomsGV,romNoallroomGV,hotelnamAllromGV,inuseratingsGV,allroomDurationGV

        public UsersViewHolder(@NonNull View itemView) {
            super( itemView );
            mView = itemView;
            tvRoomno = (TextView) mView.findViewById( R.id.romNoallroom );
            tvhotel = (TextView) mView.findViewById( R.id.hotelnamAllrom );
            tvratings = (TextView) mView.findViewById( R.id.inuseratings );
            tvDur = (TextView) mView.findViewById( R.id.allroomDuration );
        }

        public void setImage(String link) {
            ImageView img = (ImageView) mView.findViewById( R.id.imgAllroom );
            Picasso.get().load( link ).placeholder( R.drawable.roomsample ).into( img );
        }


    }
*/
}
