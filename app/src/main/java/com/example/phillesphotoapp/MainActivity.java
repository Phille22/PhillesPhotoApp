package com.example.phillesphotoapp;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
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

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Calendar;

public class MainActivity extends AppCompatActivity {
    private ArrayList<Image> arrayList;
    private RecyclerView mRecyclerView;
    private ImageAdapter mAdapter;
    static final int REQUEST_IMAGE_CAPTURE = 1;
    static final int REQUEST_IMAGE_GET = 2;
    Helpers helpers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        arrayList = new ArrayList<>();
        loadData();
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
        arrayList.clear();
        recycleSetup();
        mRecyclerView.getAdapter().notifyDataSetChanged();
        saveData();
        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onPause() {
        super.onPause();
        saveData();
    }

    public void recycleSetup() {
        mRecyclerView = findViewById(R.id.recyclerView);
        mAdapter = new ImageAdapter(this, arrayList);
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    private void takeImage() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(intent, REQUEST_IMAGE_CAPTURE);
        }
    }

    public void getImage(View view) {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(intent, REQUEST_IMAGE_GET);
        }
    }

    public void saveData() {
        String filename = "SaveData.json";
        FileOutputStream outputStream;
        try {
            outputStream = openFileOutput(filename, Context.MODE_PRIVATE);
            Writer writer = new OutputStreamWriter(outputStream);
            Gson gson = new Gson();
            gson.toJson(arrayList, writer);
            writer.close();
            Log.d("Data sparat:", "" + arrayList);
        } catch (Exception e) {
            Log.e("Can´t save data", "", e);
        }
    }

    private void loadData() {
        String filename = "SaveData.json";
        FileInputStream inputStream;
        try {
            inputStream = openFileInput(filename);
            Reader reader = new BufferedReader(new InputStreamReader(inputStream));
            Gson gson = new Gson();
            Type collectionType = new TypeToken<ArrayList<Image>>() {
            }.getType();
            arrayList = gson.fromJson(reader, collectionType);
            reader.close();
            Log.d("Data laddat:", "" + arrayList);
        } catch (Exception e) {
            Log.e("Can´t load data", "", e);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        String currentTime = Calendar.getInstance().getTime().toString();
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            //Konvertera bitmap till String
            String bitMapString = helpers.getStringFromBitmap(imageBitmap);
            arrayList.add(new Image(bitMapString, currentTime));
        }
        if (requestCode == REQUEST_IMAGE_GET && resultCode == RESULT_OK) {
            try {
                Uri image = data.getData();
                Bitmap thumb = MediaStore.Images.Media.getBitmap(this.getContentResolver(), image);
                //Skala ned bitmap
                thumb = helpers.scaleDownBitmap(thumb, 100, this);
                //Konvertera bitmap till string
                String bitMapString = helpers.getStringFromBitmap(thumb);
                arrayList.add(new Image(bitMapString, currentTime));
            } catch (IOException e) {
                Log.e("Error", "" + e);
            }
        }
        recycleSetup();
        mRecyclerView.getAdapter().notifyItemInserted(arrayList.size());
        saveData();
    }
}
