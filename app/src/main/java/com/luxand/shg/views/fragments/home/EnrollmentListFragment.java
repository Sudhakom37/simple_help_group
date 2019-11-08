package com.luxand.shg.views.fragments.home;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import com.luxand.shg.R;
import com.luxand.shg.views.activities.MainActivity;
import com.luxand.shg.adapters.AdapterEnrollmentList;
import com.luxand.shg.views.enrollment.Student_Face_Enroll;
import com.luxand.shg.helper.GridDividerDecoration;
import com.luxand.shg.helper.MySharedPreference;
import com.luxand.shg.helper.PrefKeys;
import com.luxand.shg.helper.RecyclerTouchListener;
import com.luxand.shg.model.GroupMember;
import com.luxand.shg.viewmodel.GroupMembersListViewModel;

import java.util.List;
import java.util.Objects;


public class EnrollmentListFragment extends Fragment {

        private static final String TAG = "EnrollmentListFragment";
        RecyclerView rv_members_list;
        ImageView iv_add_member,iv_search;
        Button button_submit;
        GroupMembersListViewModel viewModel;
        TextView tv_group_members_list;
        public List<GroupMember.Data> memberList;
        MySharedPreference preference;
        public EnrollmentListFragment(){

        }
        @Nullable
        @Override
        public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
            View view = inflater.inflate(R.layout.fragment_enrollment_list, container, false);
            rv_members_list = view.findViewById(R.id.rv_members_list);
            button_submit = view.findViewById(R.id.button_submit);
            tv_group_members_list = view.findViewById(R.id.tv_group_members_list);
            preference = new MySharedPreference(getActivity());
            viewModel = ViewModelProviders.of(getActivity()).get(GroupMembersListViewModel.class);
            ((MainActivity)getActivity()).tv_title.setText("Enrollment");
            viewModel.getGroupMemberList("4");

            getGroupList(viewModel);

            iv_add_member.setOnClickListener(view1 -> ((MainActivity)getActivity()).setHomeFragment(new EnrollmentFragment(),null));
            rv_members_list.setLayoutManager(new GridLayoutManager(getActivity(),3));
            rv_members_list.addItemDecoration(new GridDividerDecoration(getActivity()));

            rv_members_list.addOnItemTouchListener(new RecyclerTouchListener(getActivity(), rv_members_list, new RecyclerTouchListener.ClickListener() {
                @Override
                public void onClick(View view, int position) {
                    preference.setPref(PrefKeys.UUID,String.valueOf(memberList.get(position).getUuid()));
                    preference.setPref(PrefKeys.GROUP_ID,String.valueOf(memberList.get(position).getGroupId()));
                    Intent intent = new Intent(getActivity(), Student_Face_Enroll.class);
                    startActivity(intent);
                }

                @Override
                public void onLongClick(View view, int position) {

                }
            }));
            return view;
        }
    private void getGroupList(GroupMembersListViewModel viewModel){
        viewModel.getData().observe(Objects.requireNonNull(getActivity()), new Observer<GroupMember>() {
            @Override
            public void onChanged(GroupMember groupMember) {

                if(groupMember!=null && groupMember.getData() != null) {
                    Log.d(TAG, "onChanged: "+groupMember.getData());


                    memberList = groupMember.getData();
                    tv_group_members_list.setText("Group Members  ("+groupMember.getData().size()+" Members)");
                    AdapterEnrollmentList adapterEnrollmentList = new AdapterEnrollmentList(getActivity(),groupMember.getData());
                    rv_members_list.setAdapter(adapterEnrollmentList);

                    adapterEnrollmentList.notifyDataSetChanged();
                }
            }
        });
    }
}


