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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image);
        imageView = findViewById(R.id.imageViewPhoto);
        TextViewdescription = findViewById(R.id.textViewViewDescription);
        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        Bitmap imageBitmap = (Bitmap) extras.get("Picture");
        String description = (String) extras.get("Description");
        imageView.setImageBitmap(imageBitmap);
        TextViewdescription.setText(description);

    }
}
