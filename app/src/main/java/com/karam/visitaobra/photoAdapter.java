package com.karam.visitaobra;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import androidx.cardview.widget.CardView;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;

public class photoAdapter extends BaseAdapter {
    Context context;
    Activity activity;
    ArrayList<Photo> items;
    String visita_id;
    HighResPhoto highResPhoto;

    public photoAdapter(Context context,Activity activity, ArrayList<Photo> items,String visita_id,HighResPhoto highResPhoto) {
        this.context = context;
        this.items = items;
        this.activity = activity;
        this.visita_id = visita_id;
        this.highResPhoto = highResPhoto;
    }

    @Override
    public int getCount() {
        return 15;
    }

    @Override
    public Object getItem(int position) {
        return items.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if(convertView == null){
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.img_layout,parent,false);
            RelativeLayout itemLayout = convertView.findViewById(R.id.img_master_layout);
            int[] dims = Methods.getScreenSize(activity);
            itemLayout.setLayoutParams(new RelativeLayout.LayoutParams(dims[0]/3,dims[0]/3));
            ImageView imgVw = convertView.findViewById(R.id.photo_img);
            ImageView imgVwIcon = convertView.findViewById(R.id.photo_camera);
            CardView cardView = convertView.findViewById(R.id.photo_card);
            if(position >= items.size()){
                convertView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        highResPhoto.captureImage();
                    }
                });
            }

            if(position < items.size()){
                Picasso.get().load(items.get(position).getLink() + "fotos/" + items.get(position).getId() + ".jpg").into(imgVw, new Callback() {
                    @Override
                    public void onSuccess() {
                        cardView.setVisibility(View.VISIBLE);
                    }

                    @Override
                    public void onError(Exception e) {
                        imgVwIcon.setImageResource(R.drawable.ic_baseline_error_24);
                    }
                });

                //open the photo when onClick
                convertView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                           Intent galaryIntent = new Intent(activity,PhotoGallaryActivity.class);
                           Bundle bundle = new Bundle();
                           bundle.putParcelableArrayList("photo_array",items);
                           bundle.putInt("position",position);
                           galaryIntent.putExtras(bundle);
                           activity.startActivity(galaryIntent);
                    }
                });

                //delete the photo when on LongClick
                convertView.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        AlertDialog diag = new AlertDialog.Builder(context)
                                .setTitle("Apagar foto")
                                .setMessage("Deseja apagar esta foto")
                                .setNeutralButton("Apagar", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        ((PhotoActivity)activity).setSelector(3);
                                        Methods.showLoadingDialog(activity);
                                        HashMap<String, String> map = Methods.stringToHashMap("ID",items.get(position).getId());
                                        String encodedParams = "";
                                        try {
                                            encodedParams = Methods.encode(map);
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                        SRVConnection connection = new SRVConnection(activity,"response",activity.getString(R.string.url_master) + activity.getString(R.string.url_delete_photo),encodedParams);
                                    }
                                })
                                .create();
                        diag.show();
                        return true;
                    }
                });
            }

        }
        return convertView;
    }
}