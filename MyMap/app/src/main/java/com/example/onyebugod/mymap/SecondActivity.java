package com.example.onyebugod.mymap;

import android.content.Intent;
import android.view.View;
import android.widget.TextView;
import android.widget.Button;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;



public class SecondActivity extends AppCompatActivity {
    private TextView textView;
    private Button button;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);
        button = (Button)findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SecondActivity.this, MapsActivity.class );
                startActivity(intent);
            }
        });
    }
}

