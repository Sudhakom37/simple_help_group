package com.luxand.shg.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.luxand.shg.R;
import com.luxand.shg.model.HomeModel;

import java.util.List;

public class AdapterHome extends RecyclerView.Adapter<AdapterHome.ViewHolderHome> {

    Context mContext;
    List<HomeModel.Data> data;

    public AdapterHome(Context mContext, List<HomeModel.Data> data) {
        this.mContext = mContext;
        this.data = data;
    }

    @NonNull
    @Override
    public AdapterHome.ViewHolderHome onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.adapter_home,parent,false);
        return new ViewHolderHome(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AdapterHome.ViewHolderHome holder, int position) {
        holder.tvHome.setText(data.get(position).getName());
        Glide.with(mContext).load(data.get(position).getIcon())
                .apply(RequestOptions.centerCropTransform())
                .into(holder.ivHome);

    }

    @Override
    public int getItemCount() {
        return data.size();
    }
    class ViewHolderHome extends RecyclerView.ViewHolder {

        TextView tvHome;
        ImageView ivHome;
        public ViewHolderHome(@NonNull View itemView) {
            super(itemView);

            tvHome = itemView.findViewById(R.id.tv_home);
            ivHome = itemView.findViewById(R.id.iv_home);
        }
    }
}
