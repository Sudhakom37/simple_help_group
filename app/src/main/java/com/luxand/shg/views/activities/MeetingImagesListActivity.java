package com.luxand.shg.views.activities;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.JsonObject;
import com.luxand.shg.R;
import com.luxand.shg.adapters.ImagesAdapter;
import com.luxand.shg.api.RetrofitInstance;
import com.luxand.shg.helper.MySharedPreference;
import com.luxand.shg.helper.PrefKeys;
import com.luxand.shg.helper.RecyclerTouchListener;
import com.luxand.shg.model.Image;
import com.luxand.shg.model.MeetingPhotosModel;
import com.luxand.shg.util.ProgressBarDialog;


import java.io.File;
import java.util.ArrayList;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;


public class MeetingImagesListActivity extends BaseActivity {

    private static final String TAG = "SavingsFragment";

    private RecyclerView rvHome;
    MySharedPreference preference ;
    TextView tv_total_savings_value;
    ArrayList<String> images;
    public static boolean isSelected,itemSelect;
    ImageView iv_back_photo,ivMenuOpen;
    ArrayList<Image> imageData = new ArrayList<>();
    ArrayList<Image> selectedImages = new ArrayList<>();
    Button btSubmit;
    int i;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_meeting_images);
        tv_total_savings_value = findViewById(R.id.tv_total_savings_value);
        rvHome = findViewById(R.id.rv_savings_list);
        btSubmit = findViewById(R.id.bt_submit);
        ivMenuOpen = findViewById(R.id.iv_menu_open);
        iv_back_photo = findViewById(R.id.iv_menu_back);

        preference = new MySharedPreference(this);

        ivMenuOpen.setVisibility(View.GONE);
        iv_back_photo.setVisibility(View.VISIBLE);
        TextView title = findViewById(R.id.tv_title);
        TextView tv_gallery = findViewById(R.id.tv_gallery);
        title.setText("Meeting");

        iv_back_photo.setOnClickListener(view -> finish());

        images = getIntent().getStringArrayListExtra("imagePaths");
        rvHome.setLayoutManager(new GridLayoutManager(this,3));
        tv_gallery.setText("Camera Gallery( "+images.size()+" Images )");
        for(String image : images){
            Image image1 = new Image();
            image1.setUrl(image);
            imageData.add(image1);
        }
        //rvHome.addItemDecoration(new GridDividerDecoration(getActivity()));
        ImagesAdapter adapterHome = new ImagesAdapter(this, imageData);
        rvHome.setAdapter(adapterHome);
        adapterHome.notifyDataSetChanged();

        btSubmit.setOnClickListener(view -> createPhotosToServer());
        rvHome.addOnItemTouchListener(new RecyclerTouchListener(this, rvHome, new RecyclerTouchListener.ClickListener() {
            @Override
            public void onClick(View view, int position) {

                if(isSelected){
                    if(imageData.get(position).getSelected()==0){
                        imageData.get(position).setSelected(1);
                        //itemSelect = true;
                        adapterHome.setSelected(imageData);
                    }else{
                        imageData.get(position).setSelected(0);
                        adapterHome.setSelected(imageData);
                    }


                } else {
                    Toast.makeText(MeetingImagesListActivity.this, "Long Press to select ", Toast.LENGTH_SHORT).show();

                }
            }

            @Override
            public void onLongClick(View view, int position) {
                if(isSelected){
                    isSelected  = false;
                    selectedImages= null;
                    adapterHome.setSelected(selectedImages);
                }else{
                    imageData.get(position).setSelected(1);
                    adapterHome.setSelected(imageData);
                    isSelected = true;
                }

            }
        }));
    }


    private void createPhotosToServer(){
        if(itemSelect) {
            Toast.makeText(this, "Uploading Photos Please Wait!", Toast.LENGTH_LONG).show();
            MultipartBody.Builder builder = new MultipartBody.Builder();
            builder.setType(MultipartBody.FORM);

            JsonObject final_object = new JsonObject();
            final_object.addProperty("group_id", preference.getPref(PrefKeys.GROUP_ID));
            final_object.addProperty("meetings_id", preference.getPref(PrefKeys.MEETING_ID));
            if (imageData.size() > 0) {
                for (Image image : imageData) {
                    Log.d(TAG, "createPhotosToServer: " + image);
                    if (image != null && image.getSelected() == 1) {
                        File file1 = new File(image.getUrl());
                        builder.addFormDataPart("images[]", file1.getName(),
                                RequestBody.create(okhttp3.MediaType.parse("image/*"), file1));
                    }
                }
            }

                    /*if(offline_list_students.get(i).getStudentDatfile()!=null&&offline_list_students.get(i).getStudentDatfile().length()!=0){
                    File dat_file = new File(offline_list_students.get(i).getStudentDatfile());
                    if(dat_file.exists()){
                        builder.addFormDataPart("dat_files[]", dat_file.getName(),
                                RequestBody.create(okhttp3.MediaType.parse("file/*"), dat_file));
                    }}*/


            //final_object.add("data", array);
            builder.addFormDataPart("content", final_object.toString());

            MultipartBody requestBody = builder.build();
            sendPhotosToServer(requestBody, MeetingImagesListActivity.this);
        }else{
            Toast.makeText(this, "Please select at'least one image", Toast.LENGTH_SHORT).show();
        }

    }


    private void sendPhotosToServer(MultipartBody multipartBody, Context context){
        ProgressBarDialog.showLoadingDialog(this);
        RetrofitInstance.getInstance(this)
                .getRestAdapter()
                .save_meetings_attachments(multipartBody)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<MeetingPhotosModel>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        ProgressBarDialog.cancelLoading();
                        Log.e(TAG, "onError: sendPhotosToServer"+e.getMessage() );
                    }

                    @Override
                    public void onNext(MeetingPhotosModel meeting) {
                        ProgressBarDialog.cancelLoading();
                        Log.d(TAG, "onNext: sendPhotosToServer"+meeting.getMessage());
                        startActivityBase(MeetingImagesListActivity.this, AddFeedbackActivity.class);
                        finish();
                    }
                });

    }


}
