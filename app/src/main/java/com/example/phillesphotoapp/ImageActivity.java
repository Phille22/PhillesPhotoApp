package com.example.phillesphotoapp;

import android.content.Intent;
import android.graphics.Bitmap;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

public class ImageActivity extends AppCompatActivity {
    ImageView imageView;
    TextView TextViewdescription;
    Helpers helper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image);
        imageView = findViewById(R.id.imageViewPhoto);
        TextViewdescription = findViewById(R.id.textViewViewDescription);
        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        String imageBitmapString = (String) extras.get("Picture");
        //Konvertera string till bitmap
        Bitmap imageBitmap = helper.getBitmapFromString(imageBitmapString);
        String description = (String) extras.get("Description");
        imageView.setImageBitmap(imageBitmap);
        TextViewdescription.setText(description);
    }
}
