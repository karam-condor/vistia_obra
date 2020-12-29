package com.karam.visitaobra;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.GridView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class PhotoActivity extends AppCompatActivity implements TaskListener{

    public void setSelector(int selector) {
        this.selector = selector;
    }

    int selector = 1 ; //1 load photos data, 2 save foto, 3 delete foto
    String id_visita;
    GridView gridView;
    photoAdapter photoAdapter;
    HighResPhoto highResPhoto = new HighResPhoto(this,this);
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo);
        //set SH
        setSH();
        //load photos data from the database
        loadPhotosData();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == HighResPhoto.REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            if(highResPhoto.captureReturn){
                Bitmap img = highResPhoto.getHighResPhoto();
                if(img != null){
                    saveImage(img);
                }else{
                    Toast.makeText(this, "Aconteceu um erro", Toast.LENGTH_LONG).show();
                }
            }
        }else{
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    private void setPhotos(String response){
        ArrayList<Photo> photosArr = new ArrayList<>();
        List<HashMap<String,String>> responseArr = Methods.toList(response);
        if(responseArr.size()>0) {
            Photo photo;
            for (HashMap<String, String> item :
                    responseArr) {
                photo = new Photo(item.get("ID"),item.get("LINKSERVER"),item.get("DESCRICAO"),Methods.integerParser(item.get("SEQ")));
                photosArr.add(photo);
            }
            if(photosArr.size()>0) {
                setLayout(photosArr);
            }
        }else{
            setLayout(new ArrayList<>());
        }
    }

    private void loadPhotosData() {
        selector = 1;
        Methods.showLoadingDialog(this);
        HashMap<String, String> map = Methods.stringToHashMap("ID_VISITA",id_visita);
        String encodedParams = "";
        try {
            encodedParams = Methods.encode(map);
        } catch (Exception e) {
            e.printStackTrace();
        }
        SRVConnection connection = new SRVConnection(this,"response",getString(R.string.url_master) + getString(R.string.url_load_photos_data),encodedParams);
    }

    public void saveImage(Bitmap img){
        selector = 2;
        Methods.showLoadingDialog(this);
        HashMap<String, String> map = Methods.stringToHashMap("ID_VISITA%FOTOBASE64%DESCRICAO",id_visita,Methods.getBase64FromImage(img),"");
        String encodedParams = "";
        try {
            encodedParams = Methods.encode(map);
        } catch (Exception e) {
            e.printStackTrace();
        }
        SRVConnection connection = new SRVConnection(this,"response",getString(R.string.url_master) + getString(R.string.url_save_photo),encodedParams);
    }

    private void setSH() {
        id_visita = (String) Methods.getSharedPref(this,"string",getString(R.string.sh_id_visita));
    }

    private void setLayout(ArrayList<Photo> photos) {
        gridView = findViewById(R.id.photo_gridVw);
        photoAdapter = new photoAdapter(this,this,photos,id_visita,highResPhoto);
        gridView.setAdapter(photoAdapter);
    }


    @Override
    public void onTaskFinish(String response) {
        switch (selector){
            case 1:
                if(Methods.checkValidJson(response)){
                    setPhotos(response);
                }else{
                    Toast.makeText(this, "Aconteceu um erro para carregar os dados", Toast.LENGTH_LONG).show();
                    finish();
                }
                break;
            case 2:
                if(Methods.checkValidJson(response)){
                    loadPhotosData();
                }else{
                    Toast.makeText(this, "Aconteceu um erro para carregar os dados", Toast.LENGTH_LONG).show();
                }
                break;
            case 3:
                Log.i("TAG", "dd" + response);
                if(response.trim().equals("ok")){
                    loadPhotosData();
                }
                break;
        }
        Methods.closeLoadingDialog();
    }
}