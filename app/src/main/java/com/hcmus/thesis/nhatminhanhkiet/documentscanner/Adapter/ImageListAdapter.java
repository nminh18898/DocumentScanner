package com.hcmus.thesis.nhatminhanhkiet.documentscanner.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.hcmus.thesis.nhatminhanhkiet.documentscanner.R;

import java.util.ArrayList;

public class ImageListAdapter extends RecyclerView.Adapter<ImageListAdapter.ImageListHolder> {
    ArrayList<String> imageList;
    Context context;

    public ImageListAdapter(Context context, ArrayList<String> imageList) {
        this.context = context;
        this.imageList = imageList;
    }


    @NonNull
    @Override
    public ImageListHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.image_list_item, parent, false);
        return new ImageListHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ImageListHolder holder, int position) {
                Glide.with(context).load(imageList.get(position))
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .skipMemoryCache(true)
                .into(holder.imageView);
    }



    @Override
    public int getItemCount() {
        return imageList.size();
    }

    public class ImageListHolder extends RecyclerView.ViewHolder{
        ImageView imageView;

        public ImageListHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.ivImage);
        }
    }
}
