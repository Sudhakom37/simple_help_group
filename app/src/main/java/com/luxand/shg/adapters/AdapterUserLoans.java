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
import com.luxand.shg.model.GroupUserLoan;

import java.util.List;

public class AdapterUserLoans extends RecyclerView.Adapter<AdapterUserLoans.ViewHolderGroupLoans> {

        Context mContext;
        List<GroupUserLoan.Results> data;



        public AdapterUserLoans(Context mContext, List<GroupUserLoan.Results> data) {
            this.mContext = mContext;
            this.data = data;

        }

        @NonNull
        @Override
        public AdapterUserLoans.ViewHolderGroupLoans onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            LayoutInflater inflater = (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View view = inflater.inflate(R.layout.adapter_group_user_loans,parent,false);
            return new AdapterUserLoans.ViewHolderGroupLoans(view);
        }

        @Override
        public void onBindViewHolder(@NonNull AdapterUserLoans.ViewHolderGroupLoans holder, int position) {
                holder.tv_name.setText(String.valueOf(data.get(position).getName()));
                holder.tv_loan_amount.setText(data.get(position).getAmount());
                //holder.tv_distribution.setText(data.get(position).getTaken());
                holder.tv_repayment.setText(data.get(position).getUsed_amount());

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

        class ViewHolderGroupLoans extends RecyclerView.ViewHolder {

            TextView tv_name,tv_loan_amount,tv_distribution,tv_repayment;
            LinearLayout ll_savings_adapter;
            ViewHolderGroupLoans(@NonNull View itemView) {
                super(itemView);

                tv_name = itemView.findViewById(R.id.tv_name);
                tv_loan_amount = itemView.findViewById(R.id.tv_loan_amount);
                tv_distribution = itemView.findViewById(R.id.tv_distribution);
                tv_repayment = itemView.findViewById(R.id.tv_repayment);
                ll_savings_adapter = itemView.findViewById(R.id.ll_savings_adapter);
            }
        }


}
