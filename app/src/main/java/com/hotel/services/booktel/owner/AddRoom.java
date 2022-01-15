package com.hotel.services.booktel.owner;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseException;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.hotel.services.booktel.R;
import com.hotel.services.booktel.ownerSignUp;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import id.zelory.compressor.Compressor;

public class AddRoom extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    private Toolbar mtoolbar;
    private TextView textView;
    private String hotel;
    private DatabaseReference mReference;
    private RecyclerView roomImgLst;
    private Button btnAddimg, btnAddroom;
    private ProgressDialog dialog;
    private String imageurl, UserID;
    private StorageReference mStorageRef;
    private EditText edtroomNo, edtNobeds, edtRent;
    private CheckBox cbNet;
    private Spinner spHotels;
    private PicInfo obj;
    private int index;
    private ArrayList<PicInfo> idlist;
    private boolean NetFlag = false;
    private String roomid;
    private int imgCount = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_add_room );
        mtoolbar = findViewById( R.id.toolbaraddRoom );
        setSupportActionBar( mtoolbar );
        getSupportActionBar().setTitle( R.string.addroom );
        getSupportActionBar().setDisplayHomeAsUpEnabled( true );
        // components casting
        btnAddimg = (Button) findViewById( R.id.btn_addimg );
        btnAddroom = (Button) findViewById( R.id.btnAddRoomAR );
        edtroomNo = (EditText) findViewById( R.id.edtRoomno );
        edtNobeds = (EditText) findViewById( R.id.edtRoombeds );
        edtRent = (EditText) findViewById( R.id.rentperNight );
        spHotels = (Spinner) findViewById( R.id.htlsSpinnerAR );
        cbNet = (CheckBox) findViewById( R.id.checkboxNet );
        idlist = new ArrayList<PicInfo>();


        mReference = FirebaseDatabase.getInstance().getReference().child( "Images" ).child( "Rooms" );
        mStorageRef = FirebaseStorage.getInstance().getReference().child( "RoomImgs" );
        roomid = mReference.push().getKey().toString();
        UserID = FirebaseAuth.getInstance().getUid().toString();
        mReference = mReference.child( roomid );
        roomImgLst = (RecyclerView) findViewById( R.id.roomImgsLst );
        roomImgLst.setHasFixedSize( true );
        roomImgLst.setLayoutManager( new LinearLayoutManager( this, LinearLayoutManager.HORIZONTAL, true ) );
        // components events
        cbNet.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean checked = ((CheckBox) v).isChecked();
                // Check which checkbox was clicked
                if (checked) {
                    NetFlag = true;
                    Toast.makeText( AddRoom.this, "Interent Available", Toast.LENGTH_SHORT ).show();
                } else {
                    // if not checked
                    NetFlag = false;
                    Toast.makeText( AddRoom.this, "Interent Not Available", Toast.LENGTH_SHORT ).show();
                }
            }
        } );
        btnAddimg.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CropImage.activity()
                        .setGuidelines( CropImageView.Guidelines.ON )
                        .setAspectRatio( 16, 9 )

                        .start( AddRoom.this );

            }
        } );
        // get data from database to show in spinner


        fetchData();

        //Adding new Room
        btnAddroom.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String roomno, nobeds, hotel, rent;
                roomno = edtroomNo.getText().toString().trim();
                nobeds = edtNobeds.getText().toString().trim();
                hotel = spHotels.getSelectedItem().toString();
                index = spHotels.getSelectedItemPosition();
                rent = edtRent.getText().toString().trim();
                if (edtroomNo.getText().length()>1 ) {
                    if (Integer.parseInt( edtNobeds.getText().toString() )>1) {
                        if (edtRent.getText().length()>3) {

                            if (imgCount > 0) {
                                NewRoom( roomno, nobeds, NetFlag, hotel, rent );
                            } else {
                                btnAddimg.setError( "Atleast one image needed" );
                            }
                        } else {
                            edtRent.setError( "Enter Valid Rent" );
                        }
                    } else {
                        edtNobeds.setError( "Mention No Of Beds" );
                    }
                } else {
                    edtroomNo.setError( "Room No is Necessary" );
                }
            }
        } );
    }


    private void NewRoom(String roomno, String nobeds, boolean netFlag, String hotel, String rent) {

        DatabaseReference dbref = FirebaseDatabase.getInstance().getReference().child( "Rooms" );
        singleRoom objroom = new singleRoom( roomno, nobeds, netFlag + "", hotel, idlist.get( index ).getthumb_image() + "", rent, UserID, "5.0", "0", "false" );

        dbref.child( roomid ).setValue( objroom ).addOnCompleteListener( new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    new AlertDialog.Builder( AddRoom.this )
                            .setMessage( R.string.successfully )
                            .setTitle( R.string.success )
                            .setPositiveButton( "OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    startActivity( new Intent( AddRoom.this, OwnerView.class ) );
                                }
                            } )
                            .show();
                }
            }
        } );

    }

    private void fetchData() {
        //create list to store all data

        final ArrayList<String> list = new ArrayList<String>();
        obj = new PicInfo();
        final DatabaseReference myReference = FirebaseDatabase.getInstance().getReference().child( "Hotels" );
        // Read from the database
        myReference.addValueEventListener( new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.

                for (DataSnapshot datas : dataSnapshot.getChildren()) {
                    if (datas.child( "ownerid" ).getValue().toString().equals( UserID )) {
                        String hotelName = datas.child( "htlName" ).getValue().toString();

                        list.add( hotelName );
                        obj.setImage( hotelName );
                        obj.setthumb_image( datas.getKey().toString() );
                        idlist.add( obj );
                    }
                }
                ArrayAdapter<String> adapter = new ArrayAdapter<String>( AddRoom.this, android.R.layout.simple_spinner_item, list );
                adapter.setDropDownViewResource( android.R.layout.simple_dropdown_item_1line );
                spHotels.setAdapter( adapter );
                spHotels.setOnItemSelectedListener( AddRoom.this );


            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
            }
        } );
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult( requestCode, resultCode, data );
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult( data );
            if (resultCode == RESULT_OK) {
                dialog = new ProgressDialog( AddRoom.this );
                dialog.setTitle( "Updating..." );
                dialog.setMessage( "Saving image" );
                dialog.setCanceledOnTouchOutside( false );
                dialog.show();
                Uri resultUri = result.getUri();
                saveimage( resultUri );

                //  Toast.makeText(CompanyProfile.this,""+resultUri,Toast.LENGTH_LONG).show();
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
                dialog.dismiss();
            }
        } else if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
            Toast.makeText( AddRoom.this, "Error While Processing ", Toast.LENGTH_SHORT ).show();
        } else {
            Toast.makeText( AddRoom.this, "Error While Processing Image", Toast.LENGTH_SHORT ).show();

        }
    }

    private void saveimage(Uri resultUri) {

        final File thumb_filepath = new File( resultUri.getPath() );

        final Bitmap thumb_bmp = new Compressor( AddRoom.this ).setMaxHeight( 200 ).setMaxWidth( 200 ).setQuality( 25 )
                .compressToBitmap( thumb_filepath );
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        thumb_bmp.compress( Bitmap.CompressFormat.JPEG, 100, outputStream );
        final byte[] thumb_byte = outputStream.toByteArray();
        final String imgId = FirebaseDatabase.getInstance().getReference().push().getKey().toString();
        StorageReference filepath = mStorageRef.child( "normal" ).child( imgId + ".jpg" );
        final StorageReference thumb_path = mStorageRef.child( "normal" ).child( "thumbs" ).child( imgId + ".jpg" );


        filepath.putFile( resultUri ).addOnSuccessListener( new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                Task<Uri> uri = taskSnapshot.getStorage().getDownloadUrl();
                while (!uri.isComplete()) ;
                imageurl = uri.getResult().toString();
                UploadTask uploadTask = thumb_path.putBytes( thumb_byte );
                uploadTask.addOnSuccessListener( new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot thumbTask) {
                        Task<Uri> uri = thumbTask.getStorage().getDownloadUrl();
                        while (!uri.isComplete()) ;
                        String thumburl = uri.getResult().toString();
                        Map map = new HashMap<>();

                        map.put( "image", imageurl );
                        map.put( "thumb_image", thumburl );

                        mReference.child( imgId ).updateChildren( map ).addOnCompleteListener( new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {

                                    Toast.makeText( AddRoom.this, "Uploaded", Toast.LENGTH_SHORT ).show();
                                    imgCount++;
                                    dialog.dismiss();
                                } else {
                                    dialog.setMessage( "Operation Failed" );
                                    dialog.dismiss();
                                }
                            }
                        } );
                    }
                } );

            }
        } );


    }


    public void onBackPress(View view) {
        onBackPressed();
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
            startActivity(new Intent(AddRoom.this, ownerSignUp.class));
            finish();
        }


        FirebaseRecyclerAdapter<PicInfo, UsersViewHolder> adapter = new FirebaseRecyclerAdapter<PicInfo, UsersViewHolder>(
                PicInfo.class,
                R.layout.roomimg,
                UsersViewHolder.class,
                mReference
        ) {
            @Override
            protected void populateViewHolder(UsersViewHolder holder, final PicInfo pic, final int i) {
                try {
                    holder.setPic( pic.getthumb_image().toString() );

                    final String user_id = getRef( i ).getKey();

                    //dialog.dismiss();
                } catch (DatabaseException e) {
                    e.printStackTrace();
                }
            }
        };
        roomImgLst.setAdapter( adapter );


    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        String selectedHotel = parent.getItemAtPosition( position ).toString();
        Toast.makeText( this, "" + selectedHotel + "", Toast.LENGTH_SHORT ).show();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

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
