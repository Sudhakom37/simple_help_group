package com.luxand.shg.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.luxand.shg.R;
import com.luxand.shg.model.Image;
import com.luxand.shg.views.activities.MeetingImagesListActivity;

import java.util.ArrayList;


public class ImagesAdapter extends RecyclerView.Adapter<ImagesAdapter.ViewHolderImages> {
    Context mContext;
    ArrayList<Image> data;
    ArrayList<Image> selectedPos;

    public ImagesAdapter(Context mContext, ArrayList<Image> data) {
        this.mContext = mContext;
        this.data = data;
    }

    @NonNull
    @Override
    public ImagesAdapter.ViewHolderImages onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.adapter_images,parent,false);
        return new ImagesAdapter.ViewHolderImages(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ImagesAdapter.ViewHolderImages holder, int position) {
        Glide.with(mContext)
                .load(data.get(position).getUrl())
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .skipMemoryCache(true)
                .into(holder.iv_image);
        if(selectedPos != null && selectedPos.size()>position && selectedPos.get(position).getSelected()==1){
            holder.iv_selected.setVisibility(View.VISIBLE);
            MeetingImagesListActivity.itemSelect = true;
        }else{
            holder.iv_selected.setVisibility(View.GONE);
        }
    }

    public void setSelected(ArrayList<Image> position){
        this.selectedPos = position;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    class ViewHolderImages extends RecyclerView.ViewHolder {

        ImageView iv_image,iv_selected;
        LinearLayout ll_savings_adapter;

        public ViewHolderImages(@NonNull View itemView) {
            super(itemView);

            iv_image = itemView.findViewById(R.id.meeting_image);
            //iv_selected = itemView.findViewById(R.id.iv_selected);
            ll_savings_adapter = itemView.findViewById(R.id.ll_savings_adapter);
        }
    }
}
