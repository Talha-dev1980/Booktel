package com.hotel.services.booktel;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.hotel.services.booktel.guest.GuestsView;
import com.hotel.services.booktel.owner.OwnerView;

public class login extends AppCompatActivity {

    private TextView tvLoginView;
    private Button btnRegister, btnLogin;
    private EditText edtEmail, edtPass;
    private String loginView;
    private DatabaseReference dbRef;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        tvLoginView = (TextView) findViewById(R.id.tvLoginView);
        btnRegister = (Button) findViewById(R.id.btnregisterView);
        btnLogin = (Button) findViewById(R.id.btnLogin);
        edtEmail = (EditText) findViewById(R.id.edtemail);
        edtPass = (EditText) findViewById(R.id.edtPass);


        loginView = getIntent().getStringExtra("loginView");
        dbRef = FirebaseDatabase.getInstance().getReference().child(loginView);
        mAuth = FirebaseAuth.getInstance();
        if (loginView.equals("Guests")) {
            tvLoginView.setText("Guest Login");
        } else {
            tvLoginView.setText("Owner Login");
        }
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (loginView.equals("Guests")) {
                    startActivity(new Intent(getApplicationContext(), GuestSignup.class));
                } else {
                    startActivity(new Intent(getApplicationContext(), ownerSignUp.class));
                }
            }
        });
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                loginUser(edtEmail.getText().toString(), edtPass.getText().toString());
            }
        });

    }

    private void loginUser(String email, String password) {
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            FirebaseUser user = mAuth.getCurrentUser();
                            if (loginView.equals("Guests")) {
                                Intent it = new Intent(getApplicationContext(), GuestsView.class);
                                it.putExtra("regStatus", "no");
                                startActivity(it);
                            } else {
                                Intent it = new Intent(getApplicationContext(), OwnerView.class);
                                it.putExtra("regStatus", "no");
                                startActivity(it);
                            }

                        } else {
                            // If sign in fails, display a message to the user.
                            Toast.makeText(getApplicationContext(), "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }

                        // ...
                    }
                });
    }


}
