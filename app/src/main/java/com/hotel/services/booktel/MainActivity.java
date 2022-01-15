package com.hotel.services.booktel;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.hotel.services.booktel.guest.GuestsView;
import com.hotel.services.booktel.owner.OwnerView;

public class MainActivity extends AppCompatActivity {

    private static final String MY_PREFS_NAME = "UserView";
    SharedPreferences.Editor editor;
    Boolean firstTime = false;
    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_main );
        sharedPreferences = getSharedPreferences( "MyWelcome", MODE_PRIVATE );
        firstTime = sharedPreferences.getBoolean( "firstTime", true );
        editor = getSharedPreferences( MY_PREFS_NAME, MODE_PRIVATE ).edit();
        if (!firstTime) {

           // check();
        }
    }

    public void btnOwner(View view) {
        Intent it = new Intent( getApplicationContext(), OwnerView.class );
        editor.putString( "user", "Owner" );
        editor.apply();
        SharedPreferences.Editor editor = sharedPreferences.edit();
        firstTime = false;
        editor.putBoolean( "firstTime", firstTime );
        editor.apply();
        startActivity( it );
    }

    public void btnGuest(View view) {
        Intent it = new Intent( getApplicationContext(), GuestsView.class );
        editor.putString( "user", "Guest" );
        editor.apply();
        SharedPreferences.Editor editor = sharedPreferences.edit();
        firstTime = false;
        editor.putBoolean( "firstTime", firstTime );
        editor.apply();
        it.putExtra( "loginView", "Guests" );
        startActivity( it );
    }

    public void check() {
        SharedPreferences prefs = getSharedPreferences( MY_PREFS_NAME, MODE_PRIVATE );
        String userView = prefs.getString( "user", "nothig" );
        if (userView.equals( "Guest" )) {
            Intent it = new Intent( getApplicationContext(), GuestsView.class );
            it.putExtra( "loginView", "Guests" );
            startActivity( it );
        } else if (userView.equals( "Owner" )) {
            Intent it = new Intent( getApplicationContext(), OwnerView.class );
            startActivity( it );
        } else {

        }
    }
}
