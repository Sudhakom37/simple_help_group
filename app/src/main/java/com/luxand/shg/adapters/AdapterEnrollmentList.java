package com.luxand.shg.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.luxand.shg.R;
import com.luxand.shg.model.GroupMember;

import java.util.List;


public class AdapterEnrollmentList extends RecyclerView.Adapter<AdapterEnrollmentList.ViewHolderEnrollment> {

    private Context mContext;
    private List<GroupMember.Data> data;

    public AdapterEnrollmentList(Context mContext, List<GroupMember.Data> data) {
        this.mContext = mContext;
        this.data = data;
    }

    @NonNull
    @Override
    public ViewHolderEnrollment onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.adapter_enrollment,parent,false);
        return new ViewHolderEnrollment(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolderEnrollment holder, int position) {
        holder.tv_member_name.setText(data.get(position).getName());
        Glide.with(mContext).load(data.get(position).getImage())
                .apply(RequestOptions.circleCropTransform())
                .into(holder.iv_member_image);
            if(data.get(position).getIsEnrolled()!=null && data.get(position).getIsEnrolled().equalsIgnoreCase("1")){
                holder.ivCheckSelect.setVisibility(View.VISIBLE);
                holder.iv_check.setVisibility(View.GONE);

            }else{
                holder.ivCheckSelect.setVisibility(View.GONE);
                holder.iv_check.setVisibility(View.VISIBLE);
            }


    }

    @Override
    public int getItemCount() {
        return data.size();
    }
    class ViewHolderEnrollment extends RecyclerView.ViewHolder {

        TextView tv_member_name;
        ImageView iv_member_image,iv_check,ivCheckSelect;
        public ViewHolderEnrollment(@NonNull View itemView) {
            super(itemView);

            tv_member_name = itemView.findViewById(R.id.tv_member_name);
            iv_member_image = itemView.findViewById(R.id.iv_member_image);
            iv_check = itemView.findViewById(R.id.iv_check);
            ivCheckSelect = itemView.findViewById(R.id.iv_check_select);
        }
    }
}
