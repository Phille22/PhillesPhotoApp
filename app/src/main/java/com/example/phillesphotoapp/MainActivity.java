package com.example.phillesphotoapp;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Calendar;

public class MainActivity extends AppCompatActivity {
    private ArrayList<Image> arrayList;
    private RecyclerView mRecyclerView;
    private ImageAdapter mAdapter;
    static final int REQUEST_IMAGE_CAPTURE = 1;
    static final int REQUEST_IMAGE_GET = 2;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        arrayList = new ArrayList<>();
        recycleSetup();


        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                takeImage();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void recycleSetup(){
        mRecyclerView = findViewById(R.id.recyclerView);
        mAdapter = new ImageAdapter(this, arrayList);
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    private void takeImage(){
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if(intent.resolveActivity(getPackageManager()) != null){
            startActivityForResult(intent, REQUEST_IMAGE_CAPTURE);
        }
    }

    public void getImage(View view){
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        if(intent.resolveActivity(getPackageManager())!= null){
            startActivityForResult(intent, REQUEST_IMAGE_GET);
        }
    }

    public static Bitmap scaleDownBitmap(Bitmap bitmap, int newHeight, Context context){
        final float densityMultiplier = context.getResources().getDisplayMetrics().density;

        int h = (int)(newHeight * densityMultiplier);
        int w = (int)(h* bitmap.getWidth()/(double) bitmap.getHeight());
        bitmap=Bitmap.createScaledBitmap(bitmap, w, h, true);
        return bitmap;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        String currentTime = Calendar.getInstance().getTime().toString();
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK){
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            arrayList.add(new Image(imageBitmap, currentTime));
            recycleSetup();
            mRecyclerView.getAdapter().notifyItemInserted(arrayList.size());
        }
        if(requestCode == REQUEST_IMAGE_GET && resultCode == RESULT_OK){
            try{
                Uri image = data.getData();
                Bitmap thumb = MediaStore.Images.Media.getBitmap(this.getContentResolver(), image);
                thumb = scaleDownBitmap(thumb, 100, this);
                arrayList.add(new Image(thumb, currentTime));
                recycleSetup();
                mRecyclerView.getAdapter().notifyItemInserted(arrayList.size());
            }catch (IOException e){
                Log.e("Error", ""+ e);
            }
        }
    }
}
