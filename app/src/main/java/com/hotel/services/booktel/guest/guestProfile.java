package com.hotel.services.booktel.guest;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.hotel.services.booktel.GuestSignup;
import com.hotel.services.booktel.R;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;
import id.zelory.compressor.Compressor;

public class guestProfile extends AppCompatActivity {

    private Button btnChangeImg, btnSaveinfo;
    private EditText edtName, edtemail, edtphone;
    private CircleImageView imgView;
    private String name, profile, email, phone;
    private Toolbar mtoolbar;
    private String imageurl, UserID;
    private StorageReference mStorageRef;
    private ProgressDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_guest_profile );
        mtoolbar = findViewById( R.id.toolbarGuestpro );
        setSupportActionBar( mtoolbar );
        getSupportActionBar().setTitle( R.string.guest );
        getSupportActionBar().setDisplayHomeAsUpEnabled( true );

        btnChangeImg = (Button) findViewById( R.id.btnChangImgGP );
        btnSaveinfo = (Button) findViewById( R.id.btnSaveinfoGP );
        edtName = (EditText) findViewById( R.id.edtNameGP );
        edtemail = (EditText) findViewById( R.id.ownerEmailGP );
        edtphone = (EditText) findViewById( R.id.edtPhoneGP );
        imgView = (CircleImageView) findViewById( R.id.imgGP );

        dialog = new ProgressDialog( this );
        edtemail.setEnabled( false );
        btnChangeImg.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CropImage.activity()
                        .setGuidelines( CropImageView.Guidelines.ON )
                        .setAspectRatio( 1, 1 )
                        .start( guestProfile.this );
            }
        } );
        UserID = FirebaseAuth.getInstance().getCurrentUser().getUid().toString();
        mStorageRef = FirebaseStorage.getInstance().getReference();
        fetchUserData( UserID );
        btnSaveinfo.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.setTitle( "Saving..." );
                dialog.show();
                if (edtName.getText().toString().length() > 1) {
                    if (edtphone.getText().toString().length() > 10) {
                        saveChangs( edtName.getText().toString(), edtphone.getText().toString() );
                    } else edtphone.setError( "Invalid Phone Number" );
                } else edtName.setError( "Name can't replace" );
            }
        } );

    }

    private void saveChangs(String name, String phone) {
        Map map = new HashMap<>();
        map.put( "name", name );
        map.put( "phone", phone );
        FirebaseDatabase.getInstance().getReference().child( "Guests" ).child( UserID ).updateChildren( map ).addOnCompleteListener( new OnCompleteListener() {

            @Override
            public void onComplete(@NonNull Task task) {
                dialog.dismiss();
                new AlertDialog.Builder( guestProfile.this )
                        .setMessage( R.string.chengessaved )
                        .setTitle( R.string.success )
                        .setPositiveButton( "OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                            }
                        } )
                        .show();

            }
        } );
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult( requestCode, resultCode, data );
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult( data );
            if (resultCode == RESULT_OK) {

                Uri resultUri = result.getUri();
                // Toast.makeText( info.this, resultUri + "", Toast.LENGTH_SHORT ).show();
                dialog.setTitle( "Uploading Image..." );
                dialog.show();
                saveimage( resultUri );

                //  Toast.makeText(CompanyProfile.this,""+resultUri,Toast.LENGTH_LONG).show();
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();

            }
        }
    }

    private void saveimage(Uri resultUri) {

        final File thumb_filepath = new File( resultUri.getPath() );

        final Bitmap thumb_bmp = new Compressor( guestProfile.this ).setMaxHeight( 200 ).setMaxWidth( 200 ).setQuality( 25 )
                .compressToBitmap( thumb_filepath );
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        thumb_bmp.compress( Bitmap.CompressFormat.JPEG, 100, outputStream );
        final byte[] thumb_byte = outputStream.toByteArray();
        StorageReference filepath = mStorageRef.child( "profile_images" ).child( UserID + ".jpg" );
        final StorageReference thumb_path = mStorageRef.child( "profile_images" ).child( "thumbs" ).child( UserID + ".jpg" );


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

                        map.put( "thumb_image", imageurl );
                        map.put( "Profile", thumburl );

                        FirebaseDatabase.getInstance().getReference()
                                .child( "Guests" ).child( UserID ).updateChildren( map ).addOnCompleteListener( new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {

                                    Toast.makeText( guestProfile.this, "Uploaded", Toast.LENGTH_SHORT ).show();
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

    private void fetchUserData(String userid) {
        FirebaseDatabase.getInstance().getReference().child( "Guests" ).child( userid ).addValueEventListener( new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                edtName.setText( dataSnapshot.child( "name" ).getValue().toString() );
                edtemail.setText( dataSnapshot.child( "email" ).getValue().toString() );
                if (dataSnapshot.hasChild( "phone" )) {
                    edtphone.setText( dataSnapshot.child( "phone" ).getValue().toString() );
                }
                profile = dataSnapshot.child( "Profile" ).getValue().toString();
                Picasso.get().load( profile + "" ).placeholder( R.drawable.ic_avator ).into( imgView );

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        } );
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
            startActivity(new Intent( guestProfile.this, GuestSignup.class));
            finish();
        }
    }
}
