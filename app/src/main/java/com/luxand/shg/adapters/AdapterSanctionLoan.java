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
import com.luxand.shg.model.SanctionLoanModel;

import java.util.List;

public class AdapterSanctionLoan extends RecyclerView.Adapter<AdapterSanctionLoan.ViewHolderSanctionLoans> {

    Context mContext;
    List<SanctionLoanModel.Result> data;
    int from;

    public AdapterSanctionLoan(Context mContext, List<SanctionLoanModel.Result> data,int from) {
        this.mContext = mContext;
        this.data = data;
        this.from = from;
    }

    @NonNull
    @Override
    public AdapterSanctionLoan.ViewHolderSanctionLoans onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.adapter_sanction_loans,parent,false);
        return new AdapterSanctionLoan.ViewHolderSanctionLoans(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AdapterSanctionLoan.ViewHolderSanctionLoans holder, int position) {

            holder.iv_date.setVisibility(View.VISIBLE);
            holder.tv_loan_id.setText(String.valueOf(data.get(position).getGroupName()));
            holder.tv_loan_date.setText(data.get(position).getCreatedDate());
            holder.tv_loan_amount.setText(data.get(position).getLoanAmount());

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

    class ViewHolderSanctionLoans extends RecyclerView.ViewHolder {

        TextView tv_loan_id,tv_loan_date,tv_loan_amount;
        LinearLayout ll_savings_adapter;
        ImageView iv_date;
        public ViewHolderSanctionLoans(@NonNull View itemView) {
            super(itemView);

            tv_loan_id = itemView.findViewById(R.id.tv_loan_id);
            tv_loan_date = itemView.findViewById(R.id.tv_loan_date);
            iv_date = itemView.findViewById(R.id.iv_date);
            tv_loan_amount = itemView.findViewById(R.id.tv_loan_amount);
            ll_savings_adapter = itemView.findViewById(R.id.ll_savings_adapter);
        }
    }
}
