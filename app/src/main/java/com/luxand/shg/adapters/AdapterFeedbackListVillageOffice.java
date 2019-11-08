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
import com.luxand.shg.model.FeedbackModelVO;

import java.util.List;

public class AdapterFeedbackListVillageOffice extends RecyclerView.Adapter<AdapterFeedbackListVillageOffice.ViewHolderFeedbackVo> {

        Context mContext;
        List<FeedbackModelVO.Data> data;

        public AdapterFeedbackListVillageOffice(Context mContext, List<FeedbackModelVO.Data> data) {
            this.mContext = mContext;
            this.data = data;
        }

        @NonNull
        @Override
        public AdapterFeedbackListVillageOffice.ViewHolderFeedbackVo onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            LayoutInflater inflater = (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View view = inflater.inflate(R.layout.adapter_feedback_po,parent,false);
            return new ViewHolderFeedbackVo(view);
        }

        @Override
        public void onBindViewHolder(@NonNull AdapterFeedbackListVillageOffice.ViewHolderFeedbackVo holder, int position) {
            holder.tvFeedbackCount.setText(String.valueOf(data.get(position).getFeedbackCount()));
            holder.tv_meetingDate.setText(String.valueOf(data.get(position).getMeetingDate()));
            holder.tvAudioCount.setText(String.valueOf(data.get(position).getAudio()));
            holder.tvVideoCount.setText(String.valueOf(data.get(position).getVideo()));
            Glide.with(mContext)
                    .load(data.get(position).getGroupphoto())
                    .apply(RequestOptions.centerCropTransform())
                    .into(holder.ivGroupImage);

        }

        @Override
        public int getItemCount() {
            return data.size();
        }
        class ViewHolderFeedbackVo extends RecyclerView.ViewHolder {

            TextView tvFeedbackCount,tv_meetingDate,tvAudioCount,tvVideoCount;
            ImageView ivGroupImage;
            LinearLayout llMeeting;
            ViewHolderFeedbackVo(@NonNull View itemView) {
                super(itemView);

                tvFeedbackCount = itemView.findViewById(R.id.tv_no_feedback);
                tv_meetingDate = itemView.findViewById(R.id.tv_meeting_date_value);
                tvAudioCount = itemView.findViewById(R.id.tv_audio_count);
                tvVideoCount = itemView.findViewById(R.id.tv_video_count);
                ivGroupImage = itemView.findViewById(R.id.iv_group_image);
                llMeeting = itemView.findViewById(R.id.ll_meeting);
            }
        }

    }

