package com.example.phillesphotoapp;

import android.graphics.Bitmap;

public class Image {
    public Bitmap image;
    public String description;

    public Image(Bitmap image, String description){
        this.image = image;
        this.description = description;
    }
}
