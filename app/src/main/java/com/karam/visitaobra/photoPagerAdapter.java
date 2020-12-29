package com.karam.visitaobra;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class photoPagerAdapter extends PagerAdapter {
    Context context;
    Activity activity;
    ArrayList<Photo> items;

    public photoPagerAdapter(Context context,Activity activity, ArrayList<Photo> items) {
        this.context = context;
        this.activity = activity;
        this.items = items;
    }

    @Override
    public int getCount() {
        return items.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == object;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        ImageView imgVw = new ImageView(context);
        Picasso.get().load(items.get(position).getLink() + "fotos/" + items.get(position).getId() + ".jpg")
                .rotate(90)
                .into(imgVw, new Callback() {
            @Override
            public void onSuccess() {

            }

            @Override
            public void onError(Exception e) {
                imgVw.setImageResource(R.drawable.error_img);
            }
        });
        ((PhotoGallaryActivity)activity).setDescriptionText(items.get(position).description);
        container.addView(imgVw);
        return imgVw;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((View)object);
    }
}
