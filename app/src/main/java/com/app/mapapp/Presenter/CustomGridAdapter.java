package com.app.mapapp.Presenter;

import android.app.Activity;
import android.content.Context;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.app.mapapp.Models.ImagesList;
import com.app.mapapp.R;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by user on 11/19/2017.
 */

public class CustomGridAdapter extends RecyclerView.Adapter<CustomGridAdapter.ViewHolder> {

    private Context context;
    private List<String> pugs;

    public CustomGridAdapter(List<String> pugs, Context context) {
        this.pugs = pugs;
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.inflate_smallimage, parent, false);

        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        //Glide.with(context).load(pugs.get(i)).error(R.mipmap.ic_launcher).into(holder.img_small);
        String url = pugs.get(position);
        if(url.contains("gif")) {
            Glide.with(context).load(pugs.get(position)).asGif()
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .error(R.mipmap.ic_launcher)
                    .into(holder.img_small);
        }else{
            Glide.with(context).load(pugs.get(position))
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .error(R.mipmap.ic_launcher)
                    .into(holder.img_small);
        }
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView img_small;

        public ViewHolder(View view) {
            super(view);
            img_small = (ImageView) view.findViewById(R.id.img_small);
        }
    }

    @Override
    public int getItemCount() {
        return pugs.size();
    }

}
