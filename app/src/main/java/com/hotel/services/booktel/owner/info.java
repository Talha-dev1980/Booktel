package com.hotel.services.booktel.owner;

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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
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
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;
import id.zelory.compressor.Compressor;

public class info extends AppCompatActivity {

    private Button btnChangeImg, btnSaveinfo;
    private EditText edtName, edtemail, edtphone;
    private DatabaseReference ownerDbref;
    private CircleImageView imgView;
    private String name, profile, email, phone, userView;
    private Toolbar mtoolbar;
    private String imageurl, UserID;
    private StorageReference mStorageRef;
    private ProgressDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_info );
        mtoolbar = findViewById( R.id.toolbarinfo );
        setSupportActionBar( mtoolbar );
        getSupportActionBar().setTitle( R.string.ownerinfo );
        getSupportActionBar().setDisplayHomeAsUpEnabled( true );

        btnChangeImg = (Button) findViewById( R.id.btnChangImg );
        btnSaveinfo = (Button) findViewById( R.id.btnSaveinfo );
        edtName = (EditText) findViewById( R.id.edtNameOI );
        edtemail = (EditText) findViewById( R.id.ownerEmailOI );
        edtphone = (EditText) findViewById( R.id.edtPhoneOI );
        imgView = (CircleImageView) findViewById( R.id.imgOI );
        Intent intent = getIntent();
        if (intent != null) {
            name = intent.getStringExtra( "Name" );
            email = intent.getStringExtra( "email" );
            profile = intent.getStringExtra( "Profile" );
            phone = intent.getStringExtra( "phone" );
            userView = intent.getStringExtra( "userView" );

            edtName.setText( name );
            edtemail.setText( email );
            edtemail.setEnabled( false );
            edtphone.setText( phone );
            if (userView.equals( "Guest" )) {
                edtemail.setEnabled( false );
                edtphone.setEnabled( false );
                edtName.setEnabled( false );
                btnSaveinfo.setVisibility( View.GONE );
                btnChangeImg.setVisibility( View.GONE );
            }

        }

        dialog = new ProgressDialog( this );
        btnChangeImg.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CropImage.activity()
                        .setGuidelines( CropImageView.Guidelines.ON )
                        .setAspectRatio( 1, 1 )
                        .start( info.this );
            }
        } );
        UserID = FirebaseAuth.getInstance().getCurrentUser().getUid().toString();
        mStorageRef = FirebaseStorage.getInstance().getReference();


        Picasso.get().load( profile + "" ).placeholder( R.drawable.ic_avator ).into( imgView );
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
        FirebaseDatabase.getInstance().getReference().child( "Owners" ).child( UserID ).updateChildren( map ).addOnCompleteListener( new OnCompleteListener() {

            @Override
            public void onComplete(@NonNull Task task) {
                dialog.dismiss();
                new AlertDialog.Builder( info.this )
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
    protected void onStart() {
        FirebaseAuth mAuth= FirebaseAuth.getInstance();

        FirebaseUser currentUser = mAuth.getCurrentUser();
        //updateUI(currentUser);
        if (currentUser == null) {
            startActivity(new Intent(info.this, ownerSignUp.class));
            finish();
        }
        super.onStart();
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

        final Bitmap thumb_bmp = new Compressor( info.this ).setMaxHeight( 200 ).setMaxWidth( 200 ).setQuality( 25 )
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
                                .child( "Owners" ).child( UserID ).updateChildren( map ).addOnCompleteListener( new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {

                                    Toast.makeText( info.this, "Uploaded", Toast.LENGTH_SHORT ).show();
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

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

}
