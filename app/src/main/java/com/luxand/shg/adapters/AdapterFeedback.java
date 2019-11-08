package com.luxand.shg.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.luxand.shg.R;
import com.luxand.shg.model.Feedback;
import com.luxand.shg.model.FeedbackModelVO;

import java.util.List;


public class AdapterFeedback extends RecyclerView.Adapter {

    Context mContext;
    List<FeedbackModelVO.Data> data;
    private static final int HEADER =1;
    private static final int FOOTER =2;


    public AdapterFeedback(Context mContext,List<FeedbackModelVO.Data> data) {

        this.mContext = mContext;
        this.data = data;

    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        if(viewType == HEADER) {
            View viewHeader = inflater.inflate(R.layout.adapter_feedback_header, parent, false);
            return new ViewHolderFeedbackHeader(viewHeader);
        }else{
            View viewFooter = inflater.inflate(R.layout.adapter_feedback_footer,parent,false);

            return new ViewHolderFeedbackFooter(viewFooter);
        }

    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        /*holder.tvHome.setText(text[position]);
        Glide.with(mContext).load(images[position])
                .apply(RequestOptions.centerCropTransform())
                .into(holder.ivIcon);*/

    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    @Override
    public int getItemViewType(int position) {

        /*if(data.get(position).getFeedbackType().equalsIgnoreCase("audio")){
            return HEADER;
        }else{
            return FOOTER;
        }*/
        return 0;
    }

    class ViewHolderFeedbackHeader extends RecyclerView.ViewHolder {

        TextView tvHome;
        ImageView ivHome;
        public ViewHolderFeedbackHeader(@NonNull View itemView) {
            super(itemView);

            tvHome = itemView.findViewById(R.id.tv_home);
            ivHome = itemView.findViewById(R.id.iv_home);
        }
    }

    class ViewHolderFeedbackFooter extends RecyclerView.ViewHolder {

        TextView tvHome;
        ImageView ivHome;
        public ViewHolderFeedbackFooter(@NonNull View itemView) {
            super(itemView);

            tvHome = itemView.findViewById(R.id.tv_home);
            ivHome = itemView.findViewById(R.id.iv_home);
        }
    }
}
