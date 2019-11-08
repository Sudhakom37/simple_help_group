package com.luxand.shg.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.luxand.shg.R;
import com.luxand.shg.model.DashBoard;
import com.luxand.shg.model.Users;
import com.luxand.shg.util.CircleTransform;
import com.squareup.picasso.Picasso;

import java.util.List;


public class AdapterDashBoardFeedBack extends RecyclerView.Adapter<AdapterDashBoardFeedBack.ViewHolderDashBoardFeedBack> {
    private static final String TAG = "AdapterDashBoardFeedBac";
    Context mContext;
    List<Users> data;
    public AdapterDashBoardFeedBack(Context mContext, List<Users> data) {
        this.mContext = mContext;
        this.data = data;
    }

    @NonNull
    @Override
    public AdapterDashBoardFeedBack.ViewHolderDashBoardFeedBack onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.adapter_dashboard_member,parent,false);
        return new AdapterDashBoardFeedBack.ViewHolderDashBoardFeedBack(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AdapterDashBoardFeedBack.ViewHolderDashBoardFeedBack holder, int position) {

        if(data != null) {
            Users users = data.get(position);
            Log.d(TAG, "onBindViewHolder: "+users.getImage());
            holder.tvMemberName.setText(users.getName());
            holder.tvMobileNo.setText(users.getMobileno());
            holder.tvAmount.setText(String.valueOf(users.getAmount()));

        }
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    class ViewHolderDashBoardFeedBack extends RecyclerView.ViewHolder {

        TextView tvMemberName,tvMobileNo,tvAmount;
        public ViewHolderDashBoardFeedBack(@NonNull View itemView) {
            super(itemView);

            tvMemberName = itemView.findViewById(R.id.tv_member_name);
            tvMobileNo = itemView.findViewById(R.id.tv_mobile_number);
            tvAmount = itemView.findViewById(R.id.tv_amount);

        }
    }

}
