package com.luxand.shg.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.luxand.shg.R;
import com.luxand.shg.model.ListData;
import com.luxand.shg.model.Meeting;

import java.util.List;

public class AdapterMeeting extends RecyclerView.Adapter<AdapterMeeting.ViewHolderMeeting> {

    Context mContext;
    List<ListData> data;
    int from;

    public AdapterMeeting(Context mContext, List<ListData> data , int from) {
        this.mContext = mContext;
        this.data = data;
        this.from = from;
    }

    @NonNull
    @Override
    public ViewHolderMeeting onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.adapter_meeting,parent,false);
        return new ViewHolderMeeting(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolderMeeting holder, int position) {

        if(from == 1) {
            holder.iv_meeting_date.setVisibility(View.VISIBLE);
            holder.ivUser.setVisibility(View.GONE);
            holder.tvUserCount.setText(data.get(position).getStatus());
            holder.tv_meetingDate.setText(String.valueOf(data.get(position).getMeetingDate()));
        }
        else if(from == 2) {
            holder.iv_meeting_date.setVisibility(View.GONE);
            holder.ivUser.setVisibility(View.VISIBLE);
            holder.tvUserCount.setText(String.valueOf(data.get(position).getUsers()));
            holder.tv_meetingDate.setText(String.valueOf(data.get(position).getGroupName()));

        }else if(from == 3) {
            holder.iv_meeting_date.setVisibility(View.VISIBLE);
            holder.ivUser.setVisibility(View.VISIBLE);
            holder.tvUserCount.setText(String.valueOf(data.get(position).getUsers()));
            holder.tv_meetingDate.setText(String.valueOf(data.get(position).getMeetingDate()));

        }

        if(position%2 == 0){
            holder.llMeeting.setBackgroundColor(mContext.getResources().getColor(R.color.rv_list_bg));

        }else{
            holder.llMeeting.setBackgroundColor(mContext.getResources().getColor(R.color.white));
        }

    }

    @Override
    public int getItemCount() {
        return data.size();
    }
    class ViewHolderMeeting extends RecyclerView.ViewHolder {

        TextView tvUserCount,tv_meetingDate;
        LinearLayout llMeeting;
        ImageView iv_meeting_date,ivUser;
        public ViewHolderMeeting(@NonNull View itemView) {
            super(itemView);

            tvUserCount = itemView.findViewById(R.id.tv_user_count);
            tv_meetingDate = itemView.findViewById(R.id.tv_meeting_date);
            iv_meeting_date = itemView.findViewById(R.id.iv_meeting_date);
            llMeeting = itemView.findViewById(R.id.ll_meeting);
            ivUser = itemView.findViewById(R.id.iv_user);
        }
    }
}
