package com.luxand.shg.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.luxand.shg.R;

import java.util.List;


public class AdapterDashBoardImages extends RecyclerView.Adapter<AdapterDashBoardImages.ViewHolderDashBoardImages> {

    Context mContext;
    List<String> images;
    public AdapterDashBoardImages(Context mContext,List<String> images) {
        this.mContext = mContext;
        this.images = images;
    }

    @NonNull
    @Override
    public AdapterDashBoardImages.ViewHolderDashBoardImages onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.adapter_dashboard_images,parent,false);
        return new ViewHolderDashBoardImages(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AdapterDashBoardImages.ViewHolderDashBoardImages holder, int position) {
        if(images != null) {
            Glide.with(mContext)
                    .load(images.get(position))
                    .apply(RequestOptions.centerCropTransform())
                    .into(holder.iv_dashboard_list);
        }

    }

    @Override
    public int getItemCount() {
        if(images != null){
            return images.size();
        }else {
            return 0;
        }
    }

    class ViewHolderDashBoardImages extends RecyclerView.ViewHolder {

        ImageView iv_dashboard_list;
        public ViewHolderDashBoardImages(@NonNull View itemView) {
            super(itemView);
            iv_dashboard_list = itemView.findViewById(R.id.iv_dashboard_list);
        }
    }

}
