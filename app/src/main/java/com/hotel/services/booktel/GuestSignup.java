package com.hotel.services.booktel;

import android.content.Intent;
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
import com.hotel.services.booktel.guest.GuestsView;
import com.hotel.services.booktel.owner.OwnerView;

import java.util.HashMap;
import java.util.Map;

public class GuestSignup extends AppCompatActivity {

    private Button btnRegister, btnLogin;
    private FirebaseAuth mAuth;
    private EditText edtEmail, edtpass, edtName;
    private String name, email, password;
    private String currentUserid;
    private DatabaseReference guestDbRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_guest_signup );
        edtEmail = (EditText) findViewById( R.id.edtguestmail );
        edtpass = (EditText) findViewById( R.id.edtguestpass );
        edtName = (EditText) findViewById( R.id.edtGuestName );
        btnRegister = (Button) findViewById( R.id.btnRegisterguestAuth );
        btnLogin = (Button) findViewById( R.id.btnLoginguestAuth );
        mAuth = FirebaseAuth.getInstance();
        guestDbRef = FirebaseDatabase.getInstance().getReference( "Guests" );

        btnRegister.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                name = edtName.getText().toString();
                email = edtEmail.getText().toString();
                password = edtpass.getText().toString();
                if (edtName.getText().length() > 0) {

                    if (edtEmail.getText().length() > 0) {
                        if (edtpass.getText().length() > 0) {
                            signupGuest( email, password, name );
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
        btnLogin.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent it = new Intent( getApplicationContext(), login.class );
                it.putExtra( "loginView", "Guests" );
                startActivity( it );
            }
        } );
    }

    public void signupGuest(final String email, String password, final String name) {
        mAuth.createUserWithEmailAndPassword( email, password )
                .addOnCompleteListener( this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText( getApplicationContext(), "Success", Toast.LENGTH_SHORT ).show();
                            currentUserid = mAuth.getCurrentUser().getUid();
                            enter_recordTodb( currentUserid, name, email );
                            Intent it = new Intent( getApplicationContext(), GuestsView.class );
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
        map.put( "rating","5.0" );
        map.put( "ratedby","0" );

        //  map.put( "Phone", "03435065046" );

        return guestDbRef.child( currentUserid ).setValue( map ).isSuccessful();
    }
}
