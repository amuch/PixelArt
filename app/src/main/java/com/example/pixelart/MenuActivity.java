package com.example.pixelart;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MenuActivity extends Activity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        Button createBitmapButton = findViewById(R.id.createBitmapButton);
        createBitmapButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.createBitmapButton:
                Intent intent = new Intent(this, DrawingActivity.class);
                startActivity(intent);
                break;

            default:
                break;
        }
    }
}
