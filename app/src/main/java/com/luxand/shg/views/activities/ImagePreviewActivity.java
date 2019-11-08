package com.luxand.shg.views.activities;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.bumptech.glide.Glide;
import com.luxand.shg.R;
import com.luxand.shg.helper.PrefKeys;

import java.util.ArrayList;


public class ImagePreviewActivity extends BaseActivity {
    private static final String TAG = "EventImagePreviewActivi";
    ImageView backButton,ivMenuOpen;
    ArrayList<String> images;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        setContentView(R.layout.activity_events_preview);
        TextView title =  findViewById(R.id.tv_title);
        //TextView tv_gallery =  findViewById(R.id.tv_gallery);
        ivMenuOpen = findViewById(R.id.iv_menu_open);
        backButton = findViewById(R.id.iv_menu_back);
        ivMenuOpen.setVisibility(View.GONE);
        backButton.setVisibility(View.VISIBLE);

        ViewPager viewPager = findViewById(R.id.event_image_view_pager);
        int position = getIntent().getIntExtra("position",0);
        images = getIntent().getStringArrayListExtra(PrefKeys.IMAGE_PATHS);
        if(images!=null)
        Log.d(TAG, "onCreate: images"+images.get(0));
        title.setText("Group Images ");
        //tv_gallery.setText("Camera Gallery( "+images.size()+" Images )");
        backButton.setOnClickListener(v -> finish());
        PagerAdapter adapter = new PagerAdapter() {

            @NonNull
            public Object instantiateItem(@NonNull ViewGroup container, int position) {
                LayoutInflater mLayoutInflater  =(LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
                View itemView = mLayoutInflater.inflate(R.layout.pager_item, container, false);

                ImageView imageView =  itemView.findViewById(R.id.imageView);
                    Glide.with(ImagePreviewActivity.this).
                            load(images.get(position))
                            .into(imageView);

                container.addView(itemView);

                return itemView;
            }

            @Override
            public int getCount() {
                return images != null ? images.size() : 0;
            }

            @Override
            public boolean isViewFromObject(@NonNull View view, @NonNull Object o) {

                return view == o;
            }

            public void destroyItem(ViewGroup container, int position,@NonNull Object object) {
                container.removeView((LinearLayout)object);
            }
        };
        viewPager.setAdapter(adapter);
        adapter.notifyDataSetChanged();
        viewPager.setCurrentItem(position,true);

    }
}
