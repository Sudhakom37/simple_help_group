package com.luxand.shg.adapters;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.luxand.shg.R;
import com.luxand.shg.model.DepartmentDashboardModel;

import java.util.List;

public class AdapterDepartmentDashboard extends RecyclerView.Adapter<AdapterDepartmentDashboard.ViewHolderDepartmentDashboard> {

    Context mContext;
    List<DepartmentDashboardModel.Data> data;

    public AdapterDepartmentDashboard(Context mContext, List<DepartmentDashboardModel.Data> data) {
        this.mContext = mContext;
        this.data = data;
    }

    @NonNull
    @Override
    public AdapterDepartmentDashboard.ViewHolderDepartmentDashboard onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        assert inflater != null;
        View view = inflater.inflate(R.layout.adapter_department_dashboard,parent,false);
        return new AdapterDepartmentDashboard.ViewHolderDepartmentDashboard(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AdapterDepartmentDashboard.ViewHolderDepartmentDashboard holder, int position) {
        holder.tv_title.setText(data.get(position).getName());
        holder.tv_value.setText(String.valueOf(data.get(position).getCount()));
        Glide.with(mContext).load(data.get(position).getIcon())
                .apply(RequestOptions.centerCropTransform())
                .into(holder.ivIcon);
        int[] colors = {Color.parseColor(""+data.get(position).getColor1()),Color.parseColor(""+data.get(position).getColor2())};

        //create a new gradient color
        GradientDrawable gd = new GradientDrawable(GradientDrawable.Orientation.LEFT_RIGHT, colors);

        gd.setCornerRadius(8f);
        //apply the button background to newly created drawable gradient
        holder.cardView_department.setBackground(gd);
        //holder.cardView_department.setCardBackgroundColor(Color.parseColor(data.get(position).getColor1()));
    }

    @Override
    public int getItemCount() {
        return data.size();
    }
    class ViewHolderDepartmentDashboard extends RecyclerView.ViewHolder {

        TextView tv_title,tv_value;
        ImageView ivIcon;
        CardView cardView_department;
        ViewHolderDepartmentDashboard(@NonNull View itemView) {
            super(itemView);

            tv_title = itemView.findViewById(R.id.tv_title);
            tv_value = itemView.findViewById(R.id.tv_value);
            ivIcon = itemView.findViewById(R.id.iv_icon);
            cardView_department = itemView.findViewById(R.id.cardview_department);
        }
    }
}
