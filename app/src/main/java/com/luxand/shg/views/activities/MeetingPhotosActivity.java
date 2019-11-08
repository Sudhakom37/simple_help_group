package com.luxand.shg.views.activities;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.gson.JsonObject;
import com.luxand.shg.BuildConfig;
import com.luxand.shg.R;
import com.luxand.shg.adapters.ImagesAdapter;
import com.luxand.shg.api.RetrofitInstance;
import com.luxand.shg.helper.MySharedPreference;
import com.luxand.shg.helper.PrefKeys;
import com.luxand.shg.model.MeetingPhotosModel;
import com.luxand.shg.util.ProgressBarDialog;

import java.io.File;
import java.util.ArrayList;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

import static com.luxand.shg.BuildConfig.APPLICATION_ID;


public class MeetingPhotosActivity extends BaseActivity {

    private static final String TAG = "MeetingPhotosActivity";
    TextView tvTakeExtraPhotos,tv_title;
    ImageView ivPreview,iv_back_photo,iv_preview_list,ivMenuOpen;
    Button btSubmit;
    RecyclerView rvGroupPhotos;
    MySharedPreference preference;
    String path,extraImagePath;
    ArrayList<String> imagePaths= new ArrayList<>();
    int i;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_meeting_photos);
        initViews();
        preference = new MySharedPreference(this);

        ivMenuOpen.setVisibility(View.GONE);
        iv_back_photo.setVisibility(View.VISIBLE);
        tv_title.setText(R.string.meeting);
        iv_back_photo.setOnClickListener(view -> finish());
        ivPreview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                open_CameraExtraPhotos();
            }
        });
        /*iv_preview_list.setOnClickListener(view -> {
            if(imagePaths!= null && imagePaths.size()>0) {
                Intent intent = new Intent(MeetingPhotosActivity.this, MeetingImagesListActivity.class);
                intent.putStringArrayListExtra("imagePaths", imagePaths);
                startActivity(intent);
            }
        });*/
        btSubmit.setOnClickListener(view -> createPhotosToServer());
        rvGroupPhotos.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL,false));
        //tvTakeExtraPhotos.setOnClickListener(view -> open_CameraExtraPhotos());

    }

    private void initViews(){

        ivMenuOpen = findViewById(R.id.iv_menu_open);
        tv_title = findViewById(R.id.tv_title);
        rvGroupPhotos = findViewById(R.id.rv_meeting_images);
        ivPreview = findViewById(R.id.iv_preview);
        iv_back_photo = findViewById(R.id.iv_menu_back);


        btSubmit = findViewById(R.id.bt_submit);
    }


    public void open_Camera() {

        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, setImageUri());
        startActivityForResult(intent, 3);
    }
    public void open_CameraExtraPhotos() {
        i++;
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, setImageUriForMultiPhotos());
        startActivityForResult(intent, 2);
    }

    public Uri setImageUri() {
        File file ;
        File testExistDir = new File(Environment.getExternalStorageDirectory() + "/DCIM/SHG/");
        if (testExistDir.exists()) {
                testExistDir.delete();

               File makedir = new File(Environment.getExternalStorageDirectory() + "/DCIM/SHG/");
               makedir.mkdir();
               file = new File(Environment.getExternalStorageDirectory() + "/DCIM/SHG/", "image" + preference.getPref(PrefKeys.GROUP_ID) + ".png");

        } else {

            File makedir = new File(Environment.getExternalStorageDirectory() + "/DCIM/SHG/");
            makedir.mkdir();
            file = new File(Environment.getExternalStorageDirectory() + "/DCIM/SHG/", "image" + preference.getPref(PrefKeys.GROUP_ID) + ".png");

        }
        Uri imgUri = null;
        try {
            if (Build.VERSION.SDK_INT > 21) { //use this if Lollipop_Mr1 (API 22) or above
                imgUri = FileProvider.getUriForFile(MeetingPhotosActivity.this, BuildConfig.APPLICATION_ID + ".provider", file);
            } else {
                imgUri = Uri.fromFile(file);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        path = file.getAbsolutePath();
        if(!imagePaths.contains(path)){
            imagePaths.add(path);
        }

        return imgUri;
    }
    public Uri setImageUriForMultiPhotos() {
        File file = null;
        File testExistDir = new File(Environment.getExternalStorageDirectory() + "/DCIM/SHG/Extra");
        if (testExistDir.exists()) {
                testExistDir.delete();
                File makeDir = new File(Environment.getExternalStorageDirectory() + "/DCIM/SHG/Extra");
                makeDir.mkdir();
                file = new File(Environment.getExternalStorageDirectory() + "/DCIM/SHG/Extra", "image" + preference.getPref(PrefKeys.GROUP_ID)+i + ".png");

            //file = new File(Environment.getExternalStorageDirectory() + "/DCIM/SHG/Extra", "image" + preference.getPref(PrefKeys.GROUP_ID)+i + ".png");

        } else {

            File makedir = new File(Environment.getExternalStorageDirectory() + "/DCIM/SHG/Extra");
            makedir.mkdir();
            file = new File(Environment.getExternalStorageDirectory() + "/DCIM/SHG/Extra", "image" + preference.getPref(PrefKeys.GROUP_ID)+i + ".png");

        }
        Uri imgUri = null;
        try {
            //use this if Lollipop_Mr1 (API 22) or above
            imgUri = FileProvider.getUriForFile(MeetingPhotosActivity.this, APPLICATION_ID + ".provider", file);
        } catch (Exception e) {
            e.printStackTrace();
        }

        path = file.getAbsolutePath();
        return imgUri;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == 2 && resultCode == RESULT_OK) {

            if (path != null) {
                try {
                    ivPreview.setVisibility(View.VISIBLE);
                    /*compresspath = CameraUtills.compressImage(path);
                    Bitmap bitmap = BitmapFactory.decodeFile(compresspath);
                    ivPreview.setImageBitmap(bitmap);*/
                    //refreshGallery(path,MeetingPhotosActivity.this);
                    /*Glide.with(this).load(path)
                            .placeholder(R.drawable.ph_meeting_group_photo)
                            .diskCacheStrategy(DiskCacheStrategy.NONE)
                            .skipMemoryCache(true)
                            .into(ivPreview);
                    Glide.with(this).load(path)
                            .placeholder(R.drawable.ph_meeting_group_photo)
                            .diskCacheStrategy(DiskCacheStrategy.NONE)
                            .skipMemoryCache(true)
                            .into(iv_preview_list);*/
                    imagePaths.add(path);
                    PhotosAdapter adapterHome = new PhotosAdapter();
                    rvGroupPhotos.setAdapter(adapterHome);
                    adapterHome.notifyDataSetChanged();
                    Log.d(TAG, "onActivityResult: compresspath"+path);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }else if(requestCode == 3 && resultCode == RESULT_OK){
            Log.d(TAG, "onActivityResult: extraImagePath"+extraImagePath);
            //refreshGallery(extraImagePath,MeetingPhotosActivity.this);
            Glide.with(this).load(extraImagePath)
                    .placeholder(R.drawable.ph_meeting_group_photo)
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .skipMemoryCache(true)
                    .into(iv_preview_list);

            imagePaths.add(extraImagePath);
            //adapter.notifyDataSetChanged();
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private static void refreshGallery(String mCurrentPhotoPath, Context context) {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        File f = new File(mCurrentPhotoPath);
        Uri contentUri = Uri.fromFile(f);
        mediaScanIntent.setData(contentUri);
        context.sendBroadcast(mediaScanIntent);
    }
    private void createPhotosToServer(){
        Toast.makeText(this, "Uploading Photos Please Wait!", Toast.LENGTH_LONG).show();
        ProgressBarDialog.showLoadingDialog(this);
            MultipartBody.Builder builder = new MultipartBody.Builder();
            builder.setType(MultipartBody.FORM);

            JsonObject final_object = new JsonObject();
            final_object.addProperty("user_id", preference.getPref(PrefKeys.USER_ID));
            final_object.addProperty("meetings_id", preference.getPref(PrefKeys.MEETING_ID));

            File file = new File(path);
            /*builder.addFormDataPart("groupPhoto", file.getName(),
                RequestBody.create(okhttp3.MediaType.parse("image/*"), file));*/
            if(imagePaths.size()>0){
                for(String image : imagePaths) {
                    Log.d(TAG, "createPhotosToServer: " + image);
                    if (image != null && !image.equalsIgnoreCase("")) {
                        File file1 = new File(image);
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
            sendPhotosToServer(requestBody, MeetingPhotosActivity.this);

    }


    private void sendPhotosToServer(MultipartBody multipartBody,Context context){

        RetrofitInstance.getInstance(this)
                .getRestAdapter()
                .save_meetings_attachments(multipartBody)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<MeetingPhotosModel>() {
                    @Override
                    public void onCompleted() {
                        ProgressBarDialog.cancelLoading();
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
                        if(preference.getPref(PrefKeys.ROLE_ID).equalsIgnoreCase("6")){

                        }else{
                            startActivityBase(MeetingPhotosActivity.this,AddFeedbackActivity.class);
                            finish();
                        }

                    }
                });

    }

    class PhotosAdapter extends RecyclerView.Adapter<PhotosAdapter.PhotosViewHolder>{

        @NonNull
        @Override
        public PhotosAdapter.PhotosViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            LayoutInflater inflater = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View view = inflater.inflate(R.layout.adapter_photos,parent,false);
            return new PhotosViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull PhotosAdapter.PhotosViewHolder holder, int position) {
            Log.d(TAG, "onBindViewHolder: "+imagePaths.get(position));
            Glide.with(MeetingPhotosActivity.this)
                    .load(imagePaths.get(position))
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .into(holder.ivGroupImage);
        }

        @Override
        public int getItemCount() {
            return imagePaths.size();
        }

        class PhotosViewHolder extends RecyclerView.ViewHolder{

            ImageView ivGroupImage;
            PhotosViewHolder(@NonNull View itemView) {
                super(itemView);
                ivGroupImage = itemView.findViewById(R.id.iv_group_photo);
            }
        }
    }


}
