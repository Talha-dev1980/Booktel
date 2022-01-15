package com.hotel.services.booktel;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Button;

public class GetLocation extends AppCompatActivity {

    private Button btn_getLoc;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_get_location);
        btn_getLoc=(Button) findViewById(R.id.btngetLoc);

    }
}
