package com.example.android.moviesapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by Bo2o on 10/21/2016.
 */
public class ImageAdapter extends BaseAdapter {

    private Context mContext;
    private ArrayList<String> mPhoto;
    private LayoutInflater mInflater;

    public ImageAdapter(Context c, ArrayList<String> m) {
        mContext = c;
        mPhoto = m;
        mInflater = (LayoutInflater) c.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }
    public ArrayList<String> getPhotos() {
        return mPhoto;
    }

    @Override
    public int getCount() {
        return mPhoto.size();
    }

    @Override
    public Object getItem(int i) {
        return mPhoto.get(i);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        ImageView imageView;
        if(view == null) {
            imageView = (ImageView) mInflater.inflate(R.layout.image, null);
        } else {
            imageView = (ImageView) view;
        }
        imageView.setAdjustViewBounds(true);
        Picasso.with(mContext).load("http://image.tmdb.org/t/p/w185/"+mPhoto.get(i)).into(imageView);
        return imageView;
    }

}
