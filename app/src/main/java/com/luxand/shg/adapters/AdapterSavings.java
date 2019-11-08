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
import com.luxand.shg.model.Results;
import com.luxand.shg.model.UserSavings;

import java.util.List;

public class AdapterSavings  extends RecyclerView.Adapter<AdapterSavings.ViewHolderSavings> {

    Context mContext;
    List<UserSavings.Results> data;
    int from;

    public AdapterSavings(Context mContext, List<UserSavings.Results> data,int from ) {
        this.mContext = mContext;
        this.data = data;
        this.from = from;
    }

    @NonNull
    @Override
    public AdapterSavings.ViewHolderSavings onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.adapter_savings,parent,false);
        return new AdapterSavings.ViewHolderSavings(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AdapterSavings.ViewHolderSavings holder, int position) {


        if(from==1){
            holder.tv_member_name.setText(data.get(position).getName());
        }else{
            holder.tv_member_name.setText(data.get(position).getDate());
            holder.tv_member_name.setTextColor(mContext.getColor(R.color.colorGrey));
        }
        holder.tv_savings_amount.setText(String.valueOf(data.get(position).getAmount()));
        if(position%2 == 0){
            holder.ll_savings_adapter.setBackgroundColor(mContext.getColor(R.color.white));
        }else{
            holder.ll_savings_adapter.setBackgroundColor(mContext.getColor(R.color.rv_list_bg));
        }
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    class ViewHolderSavings extends RecyclerView.ViewHolder {

        TextView tv_member_name,tv_savings_amount;
        LinearLayout ll_savings_adapter;
        public ViewHolderSavings(@NonNull View itemView) {
            super(itemView);

            tv_member_name = itemView.findViewById(R.id.tv_member_name);
            tv_savings_amount = itemView.findViewById(R.id.tv_savings_amount);
            ll_savings_adapter = itemView.findViewById(R.id.ll_savings_adapter);
        }
    }
}
