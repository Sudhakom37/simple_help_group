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

import com.luxand.shg.R;
import com.luxand.shg.model.ListData;

import java.util.List;

public class DivisionMeetingAdapter extends RecyclerView.Adapter<DivisionMeetingAdapter.ViewHolderMeeting>{
    Context mContext;
    List<ListData> data;


    public DivisionMeetingAdapter(Context mContext, List<ListData> data) {
        this.mContext = mContext;
        this.data = data;

    }

    @NonNull
    @Override
    public DivisionMeetingAdapter.ViewHolderMeeting onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.adapter_division_meeting,parent,false);
        return new DivisionMeetingAdapter.ViewHolderMeeting(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DivisionMeetingAdapter.ViewHolderMeeting holder, int position) {


        holder.tvUserCount.setText(data.get(position).getStatus());
        holder.tv_meetingDate.setText(String.valueOf(data.get(position).getMeetingDate()));


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
