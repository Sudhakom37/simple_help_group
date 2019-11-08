package com.luxand.shg.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.luxand.shg.R;
import com.luxand.shg.model.GroupLoan;
import com.luxand.shg.model.HomeModel;
import com.luxand.shg.model.Results;

import java.util.List;

public class AdapterGroupLoans extends RecyclerView.Adapter<AdapterGroupLoans.ViewHolderGroupLoans> {

        Context mContext;
        List<GroupLoan.Results> data;
        int from;
        int state;

        public AdapterGroupLoans(Context mContext, List<GroupLoan.Results> data,int from) {
            this.mContext = mContext;
            this.data = data;
            this.from = from;
        }

        @NonNull
        @Override
        public AdapterGroupLoans.ViewHolderGroupLoans onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            LayoutInflater inflater = (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View view = inflater.inflate(R.layout.adapter_group_loansavings,parent,false);
            return new AdapterGroupLoans.ViewHolderGroupLoans(view);
        }

        @Override
        public void onBindViewHolder(@NonNull AdapterGroupLoans.ViewHolderGroupLoans holder, int position) {
            if(from==1){
                holder.iv_date.setVisibility(View.VISIBLE);
                holder.tv_distribution.setVisibility(View.VISIBLE);
                if(state == 1){
                    holder.tv_distribution.setText(data.get(position).getRepayment());
                    holder.tv_loan_amount.setText(String.valueOf(data.get(position).getDistribution()));
                }else{
                    holder.tv_distribution.setText(data.get(position).getDistribution());
                    holder.tv_loan_amount.setText(data.get(position).getAmount());
                }

                holder.tv_loan_id.setText(String.valueOf(data.get(position).getLoanId()));
                holder.tv_loan_date.setText(data.get(position).getDate());

            }else{
                holder.iv_date.setVisibility(View.GONE);
                //holder.tv_distribution.setVisibility(View.GONE);
                holder.tv_loan_id.setText(data.get(position).getLoanId());
                holder.tv_loan_date.setText(data.get(position).getAmount());
                holder.tv_loan_amount.setText(data.get(position).getDistribution());
            }

            if(position%2 == 0){
                holder.ll_savings_adapter.setBackgroundColor(mContext.getColor(R.color.white));
            }else{
                holder.ll_savings_adapter.setBackgroundColor(mContext.getColor(R.color.rv_list_bg));
            }
        }
        public void setState(int state){
            this.state = state;
            notifyDataSetChanged();
        }

        @Override
        public int getItemCount() {
            return data.size();
        }

        class ViewHolderGroupLoans extends RecyclerView.ViewHolder {

            TextView tv_loan_id,tv_loan_date,tv_loan_amount,tv_distribution;
            LinearLayout ll_savings_adapter;
            ImageView iv_date;
            ViewHolderGroupLoans(@NonNull View itemView) {
                super(itemView);

                tv_loan_id = itemView.findViewById(R.id.tv_loan_id);
                tv_loan_date = itemView.findViewById(R.id.tv_loan_date);
                iv_date = itemView.findViewById(R.id.iv_date);
                tv_loan_amount = itemView.findViewById(R.id.tv_loan_amount);
                tv_distribution = itemView.findViewById(R.id.tv_distribution);
                ll_savings_adapter = itemView.findViewById(R.id.ll_savings_adapter);
            }
        }

}
