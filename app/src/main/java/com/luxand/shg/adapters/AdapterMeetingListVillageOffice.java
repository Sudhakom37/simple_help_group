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
import com.bumptech.glide.request.RequestOptions;
import com.luxand.shg.R;
import com.luxand.shg.model.Meeting;
import com.luxand.shg.model.MeetingPO;

import java.util.List;
import java.util.Locale;

public class AdapterMeetingListVillageOffice extends RecyclerView.Adapter<AdapterMeetingListVillageOffice.ViewHolderMeetingVO> {

        Context mContext;
        List<MeetingPO.Data> data;

        public AdapterMeetingListVillageOffice(Context mContext, List<MeetingPO.Data> data) {
            this.mContext = mContext;
            this.data = data;
        }

        @NonNull
        @Override
        public AdapterMeetingListVillageOffice.ViewHolderMeetingVO onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            LayoutInflater inflater = (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View view = inflater.inflate(R.layout.adapter_meeting_po,parent,false);
            return new ViewHolderMeetingVO(view);
        }

        @Override
        public void onBindViewHolder(@NonNull AdapterMeetingListVillageOffice.ViewHolderMeetingVO holder, int position) {
            holder.tvUserCount.setText(String.format(Locale.ENGLISH,"No Of Attendees %d", data.get(position).getAttendedUsers()));
            holder.tv_meetingDate.setText(String.valueOf(data.get(position).getMeetingDate()));

            Glide.with(mContext)
                    .load(data.get(position).getGroupphoto())
                    .apply(RequestOptions.centerCropTransform())
                    .into(holder.ivGroupImage);

        }

        @Override
        public int getItemCount() {
            return data.size();
        }
        class ViewHolderMeetingVO extends RecyclerView.ViewHolder {

            TextView tvUserCount,tv_meetingDate;
            ImageView ivGroupImage;
            LinearLayout llMeeting;
            ViewHolderMeetingVO(@NonNull View itemView) {
                super(itemView);

                tvUserCount = itemView.findViewById(R.id.tv_no_attendees);
                tv_meetingDate = itemView.findViewById(R.id.tv_meeting_date_value);
                ivGroupImage = itemView.findViewById(R.id.iv_group_image);
                llMeeting = itemView.findViewById(R.id.ll_meeting);
            }
        }

}
