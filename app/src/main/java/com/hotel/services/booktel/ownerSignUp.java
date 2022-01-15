package com.hotel.services.booktel;

import android.content.Intent;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.hotel.services.booktel.owner.OwnerView;

import java.util.HashMap;
import java.util.Map;

public class ownerSignUp extends AppCompatActivity {

    private Button btnLoc, btnownerRegis, btnlogin;
    private LocationManager mLocationManager;
    private FirebaseAuth mAuth;
    private EditText edtName, edtEmail, edtpass;
    private String email, password, currentUserid;
    private DatabaseReference ownersDatabaseRef;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_owner_sign_up );
        btnLoc = (Button) findViewById( R.id.btngetLoc );
        btnownerRegis = (Button) findViewById( R.id.btnRegisterAuth );
        btnlogin = (Button) findViewById( R.id.btnLoginAuth );
        edtName = (EditText) findViewById( R.id.edtownerName );
        edtEmail = (EditText) findViewById( R.id.edtownermail );
        edtpass = (EditText) findViewById( R.id.edtownerpass );
        mAuth = FirebaseAuth.getInstance();
        email = edtEmail.getText().toString();
        password = edtpass.getText().toString();
        ownersDatabaseRef = FirebaseDatabase.getInstance().getReference( "Owners" );
        btnownerRegis.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (edtName.getText().length() > 0) {

                    if (edtEmail.getText().length() > 0) {
                        if (edtpass.getText().length() > 0) {
                            signupOwner( edtEmail.getText().toString(), edtpass.getText().toString(), edtName.getText().toString() );
                        } else {
                            edtpass.setError( "Invalid Password" );
                        }

                    } else {
                        edtEmail.setError( "Invalid Email" );
                    }
                } else {
                    edtName.setError( "Should enter a name" );
                }
            }
        } );
        btnlogin.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent it = new Intent( getApplicationContext(), login.class );
                it.putExtra( "loginView", "Owners" );
                startActivity( it );
            }
        } );


    }

    public void signupOwner(final String email, String password, final String name) {
        mAuth.createUserWithEmailAndPassword( email, password )
                .addOnCompleteListener( this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText( getApplicationContext(), "Success", Toast.LENGTH_SHORT ).show();
                            currentUserid = mAuth.getCurrentUser().getUid();
                            enter_recordTodb( currentUserid, name, email );
                            Intent it = new Intent( getApplicationContext(), OwnerView.class );
                            it.putExtra( "regStatus", "no" );
                            startActivity( it );


                        } else {
                            // If sign in fails, display a message to the user.
                            Toast.makeText( getApplicationContext(), "Fail because " + task.getException(), Toast.LENGTH_SHORT ).show();

                        }


                    }
                } );
    }

    private boolean enter_recordTodb(String currentUserid, String name, String email) {
        Map<String, Object> map = new HashMap<>();
        map.put( "name", name );
        map.put( "email", email );
        map.put( "Profile", "null" );
        //  map.put( "Phone", "03435065046" );

        return ownersDatabaseRef.child( currentUserid ).setValue( map ).isSuccessful();
    }
}
