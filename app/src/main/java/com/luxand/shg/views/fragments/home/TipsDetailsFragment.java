package com.luxand.shg.views.fragments.home;

import android.os.Bundle;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.luxand.shg.R;
import com.luxand.shg.views.activities.MainActivity;

public class TipsDetailsFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_tip_details, container, false);
        String description = getArguments().getString("tip_description");
        ((MainActivity)getActivity()).tv_title.setText("Tips");
        ((MainActivity)getActivity()).iv_menu_open.setVisibility(View.GONE);
        ((MainActivity)getActivity()).iv_menu_back.setVisibility(View.VISIBLE);
        TextView tv_tips_details = view.findViewById(R.id.tv_tips_details);
        tv_tips_details.setText(Html.fromHtml(description));
        return view;
    }
}
