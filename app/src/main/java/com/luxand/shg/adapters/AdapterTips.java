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
import com.luxand.shg.model.TipsModel;

import java.util.List;
import java.util.Locale;

public class AdapterTips extends RecyclerView.Adapter<AdapterTips.ViewHolderTips> {

    Context mContext;
    List<TipsModel.Data> data;

    public AdapterTips(Context mContext, List<TipsModel.Data> data) {
        this.mContext = mContext;
        this.data = data;
    }

    @NonNull
    @Override
    public AdapterTips.ViewHolderTips onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.adapter_tips,parent,false);
        return new AdapterTips.ViewHolderTips(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AdapterTips.ViewHolderTips holder, int position) {
        holder.tv_tip_no.setText(String.format(Locale.ENGLISH,"Tip %d", data.get(position).getBusinessExpansionId()));
        holder.tv_tip_text.setText(data.get(position).getTipName());
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

    class ViewHolderTips extends RecyclerView.ViewHolder {

        TextView tv_tip_no,tv_tip_text;
        LinearLayout ll_savings_adapter;

        public ViewHolderTips(@NonNull View itemView) {
            super(itemView);

            tv_tip_no = itemView.findViewById(R.id.tv_tip_no);
            tv_tip_text = itemView.findViewById(R.id.tv_tip_text);
            ll_savings_adapter = itemView.findViewById(R.id.ll_savings_adapter);
        }
    }
}
