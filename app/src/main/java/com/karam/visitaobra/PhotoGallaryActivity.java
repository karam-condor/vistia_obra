package com.karam.visitaobra;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ViewFlipper;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class PhotoGallaryActivity extends AppCompatActivity {
    ArrayList<Photo> photoArr;
    int position;
    ViewPager vwPager;
    photoPagerAdapter photoPagerAdapter;
    TextView descriptionTxtVw;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_gallary);
        //set Photo array
        setPhotoArray();
        //define layout controls
        setLayout();
        //show img at specific position
        setViewPager();
    }

    private void setLayout() {
        vwPager = findViewById(R.id.photo_gallary_vwPager);
        descriptionTxtVw = findViewById(R.id.photo_gallary_description_txtVw);
    }

    private void setPhotoArray() {
        Bundle bundle = getIntent().getExtras();
        photoArr = bundle.getParcelableArrayList("photo_array");
        position = bundle.getInt("position",0);
    }

    private void setViewPager() {
        photoPagerAdapter = new photoPagerAdapter(this,this,photoArr);
        vwPager.setAdapter(photoPagerAdapter);
        vwPager.setCurrentItem(position);
    }

    public void setDescriptionText(String txt){
        descriptionTxtVw.setText(txt);
    }
}