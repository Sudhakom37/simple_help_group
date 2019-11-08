package com.luxand.shg.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.luxand.shg.R;
import com.luxand.shg.model.TrainingVideoModel;

import java.util.List;

public class AdapterTrainingVideos extends RecyclerView.Adapter<AdapterTrainingVideos.ViewHolderVideos> {
    Context mContext;
    List<TrainingVideoModel.Data> data;

    public AdapterTrainingVideos(Context mContext, List<TrainingVideoModel.Data> data) {
        this.mContext = mContext;
        this.data = data;
    }

    @NonNull
    @Override
    public AdapterTrainingVideos.ViewHolderVideos onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.adapter_training_videos,parent,false);
        return new AdapterTrainingVideos.ViewHolderVideos(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AdapterTrainingVideos.ViewHolderVideos holder, int position) {
        holder.tv_tip_no.setText( data.get(position).getName());
        holder.tv_tip_text.setText(data.get(position).getDetails());
        /*if(position%2 == 0){
            holder.ll_savings_adapter.setBackgroundColor(mContext.getColor(R.color.white));
        }else{
            holder.ll_savings_adapter.setBackgroundColor(mContext.getColor(R.color.color_light_grey));
        }*/
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    class ViewHolderVideos extends RecyclerView.ViewHolder {

        TextView tv_tip_no,tv_tip_text;
        LinearLayout ll_savings_adapter;

        public ViewHolderVideos(@NonNull View itemView) {
            super(itemView);

            tv_tip_no = itemView.findViewById(R.id.tv_tip_no);
            tv_tip_text = itemView.findViewById(R.id.tv_tip_text);
            ll_savings_adapter = itemView.findViewById(R.id.ll_savings_adapter);
        }
    }

}
