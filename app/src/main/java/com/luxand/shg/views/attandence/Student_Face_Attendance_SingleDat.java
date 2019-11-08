package com.luxand.shg.views.attandence;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.ColorDrawable;
import android.hardware.Camera;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;


import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.alibaba.fastjson.JSON;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.luxand.FSDK;

import com.luxand.shg.R;
import com.luxand.shg.views.activities.BaseActivity;
import com.luxand.shg.api.RetrofitInstance;
import com.luxand.shg.db.Student_Attendance_Local;
import com.luxand.shg.db.Student_Attendance_Local_Table;
import com.luxand.shg.db.Students_TwinsList_Local;
import com.luxand.shg.db.Students_TwinsList_Local_Table;
import com.luxand.shg.views.activities.CreateMeetingActivity;
import com.luxand.shg.views.activities.MeetingPhotosActivity;
import com.luxand.shg.helper.Helper;
import com.luxand.shg.helper.MySharedPreference;
import com.luxand.shg.helper.PrefKeys;
import com.luxand.shg.helper.Twin;
import com.luxand.shg.helper.Twin_uuid;
import com.luxand.shg.model.Attendance;
import com.luxand.shg.model.GroupMember;
import com.luxand.shg.util.CircleTransform;
import com.luxand.shg.util.ProgressBarDialog;
import com.raizlabs.android.dbflow.sql.language.SQLite;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

import static com.luxand.shg.views.attandence.Student_Face_Attendance_SingleDat.al_twins;
import static com.luxand.shg.views.attandence.Student_Face_Attendance_SingleDat.al_twins_uuid;
import static com.luxand.shg.views.attandence.Student_Face_Attendance_SingleDat.backCameraFound;
import static com.luxand.shg.views.attandence.Student_Face_Attendance_SingleDat.confidenceEyesOpenPercent;
import static com.luxand.shg.views.attandence.Student_Face_Attendance_SingleDat.flag;
import static com.luxand.shg.views.attandence.Student_Face_Attendance_SingleDat.flag2;
import static com.luxand.shg.views.attandence.Student_Face_Attendance_SingleDat.flagtime;
import static com.luxand.shg.views.attandence.Student_Face_Attendance_SingleDat.mstudentlist;
import static com.luxand.shg.views.attandence.Student_Face_Attendance_SingleDat.mySharedPreference;
import static com.luxand.shg.views.attandence.Student_Face_Attendance_SingleDat.named1;
import static com.luxand.shg.views.attandence.Student_Face_Attendance_SingleDat.oldeyes;
import static com.luxand.shg.views.attandence.Student_Face_Attendance_SingleDat.oldtime;
import static com.luxand.shg.views.attandence.Student_Face_Attendance_SingleDat.percent;
import static com.luxand.shg.views.attandence.Student_Face_Attendance_SingleDat.st_id;
import static com.luxand.shg.views.attandence.Student_Face_Attendance_SingleDat.st_name;


public class Student_Face_Attendance_SingleDat extends BaseActivity implements View.OnClickListener {

    private static final String TAG = "Student_Face_Attendance";
    private boolean mIsFailed = false;
    private Preview mPreview;
    private ProcessImageAndDrawResults mDraw;
    public static float sDensity = 1.0f;
    public static String database = "",st_name="",st_id,DATFILE="",IMAGES="";
    public static int confidenceEyesOpenPercent=0;
    public static int flag=0,oldeyes,flag2=0,percent=50,flagtime=0;
    public static boolean named1 = false;
    public static long oldtime;
    public static Context mContext;
    public static MySharedPreference mySharedPreference;
    public static boolean backCameraFound = false;

    public static List<Twin.Data> al_twins=new ArrayList<>();
    public static List<Twin_uuid.Data> al_twins_uuid=new ArrayList<>();
    public static Dialog dialog_Twins;
    public static List<GroupMember.Data> mstudentlist = new ArrayList<>();


    public void showErrorAndClose(String error, int code) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(error + ": " + code)
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        android.os.Process.killProcess(android.os.Process.myPid());
                    }
                })
                .show();
    }
    private void resetTrackerParameters() {
        int errpos[] = new int[1];
        FSDK.SetTrackerMultipleParameters(mDraw.mTracker, "RecognizeFaces=true;DetectFacialFeatures=true;ContinuousVideoFeed=true;RecognitionPrecision=1;Threshold=0.996;Threshold2=0.9995;ThresholdFeed=0.97;MemoryLimit=1000;HandleArbitraryRotations=false;DetermineFaceRotationAngle=false;InternalResizeWidth=70;FaceDetectionThreshold=3;DetectGender=false;DetectExpression=true", errpos);
        if (errpos[0] != 0) {
            showErrorAndClose("Error setting tracker parameters, position", errpos[0]);
        }
    }

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sDensity = getResources().getDisplayMetrics().scaledDensity;
        mySharedPreference=new MySharedPreference(getApplicationContext());

        DATFILE=""+ Helper.DATFILE+"/"+Helper.GROUP_DATFILE+"/"+mySharedPreference.getPref(PrefKeys.GROUP_ID);
        IMAGES=""+Helper.IMAGES+"/"+Helper.GROUP_IMAGES+"/"+mySharedPreference.getPref(PrefKeys.GROUP_ID);
        database=mySharedPreference.getPref(PrefKeys.GROUP_ID)+".dat";

        int res = FSDK.ActivateLibrary(""+mySharedPreference.getPref(PrefKeys.FACEURL));
        //int res = FSDK.ActivateLibrary("vQixnQVi9sOqwH4JyL7ENCEaETPz6PwzyJqxIM1izi0rPrRdddVWK/DhdTrj2KJSzlGjEEgFSrsgERmVdoc7rg2qm8fBFBewoQPGR1wxe5YdbJQOi5bKTYGO6ghz9n/QfGfpptGq9i+1PHoyqSQBL+cvAemn/Gphhg+DY4t1ESs=");
        if (res != FSDK.FSDKE_OK) {
            mIsFailed = true;
            showErrorAndClose("FaceSDK activation failed", res);
        } else {
            FSDK.Initialize();

            // Hide the window title (it is done in manifest too)
            //requestWindowFeature(Window.FEATURE_NO_TITLE);
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);


            // Lock orientation
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);


            // Camera layer and drawing layer
            mDraw = new ProcessImageAndDrawResults(this);
            mPreview = new Preview(this, mDraw);
            mDraw.mTracker = new FSDK.HTracker();
            File myDir=new File(DATFILE);
            String templatePath =""+myDir + "/" + database;
            if (FSDK.FSDKE_OK != FSDK.LoadTrackerMemoryFromFile(mDraw.mTracker, templatePath)) {
                res = FSDK.CreateTracker(mDraw.mTracker);
                if (FSDK.FSDKE_OK != res) {
                    showErrorAndClose("Error creating tracker", res);
                }
            }
            al_twins= CreateMeetingActivity.list_identical;
            mstudentlist= CreateMeetingActivity.mstudentlist;
            Log.d(TAG, "onCreate: mstudentlist"+mstudentlist.size());
            resetTrackerParameters();

            this.getWindow().setBackgroundDrawable(new ColorDrawable()); //black background

            setContentView(mPreview); //creates MainActivity contents
            addContentView(mDraw, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));

            // Menu
            LayoutInflater inflater = (LayoutInflater)this.getSystemService( Context.LAYOUT_INFLATER_SERVICE );
            View buttons = inflater.inflate(R.layout.activity_student_attendance_singledat, null );
            buttons.findViewById(R.id.helpButton).setOnClickListener(this);
            buttons.findViewById(R.id.clearButton).setOnClickListener(this);
            buttons.findViewById(R.id.submit).setOnClickListener(this);
            addContentView(buttons, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));

        }
    }
    public boolean isNetWorkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnected();
    }
    @Override
    public void onClick(View view) {

        if (view.getId() == R.id.clearButton) {
            //mySharedPreference.setPref(PreferenceKeys.update,"update");
            finish();
        }if (view.getId() == R.id.submit) {

            if (isNetWorkAvailable()) {
                List<Student_Attendance_Local> list_students = SQLite.select().from(Student_Attendance_Local.class)
                        .where(Student_Attendance_Local_Table.group_id.is(mySharedPreference.getPref(PrefKeys.GROUP_ID))).queryList();

                if (list_students.size() > 0) {

                    JsonObject object = new JsonObject();
                    JsonArray array = new JsonArray();
                    for (int i = 0; i < list_students.size(); i++) {
                        JsonObject object1 = new JsonObject();
                        object1.addProperty("uuid", "" + list_students.get(i).getStudent_id());
                        object1.addProperty("date", "" + list_students.get(i).getDate_time());
                        array.add(object1);
                    }
                    object.add("users", array);
                    object.addProperty("group_id", "" + mySharedPreference.getPref(PrefKeys.GROUP_ID));
                    object.addProperty("mac_address", "" + RetrofitInstance.getMacAddress());

                    Log.d(TAG, "onClick: object"+object.toString());
                    //Save_Member_Attendance(object);
                    finish();
                } else {
                    Toast.makeText(Student_Face_Attendance_SingleDat.this, "Please take atleast one person attendance", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(Student_Face_Attendance_SingleDat.this, "Please check internet connection", Toast.LENGTH_SHORT).show();
            }
        }
    }

    /**
     * save attendance
     *
     *
     */

    public void Save_Member_Attendance(JsonObject object) {


        Log.d(TAG, "Save_Member_Attendance: "+object);
        ProgressBarDialog.showLoadingDialog(Student_Face_Attendance_SingleDat.this);
        RetrofitInstance.getInstance(Student_Face_Attendance_SingleDat.this).
                getRestAdapter()
                .Save_Member_Attendance(object)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<Attendance>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        ProgressBarDialog.cancelLoading();
                        String msg=e.getMessage();
                        Log.d("error",msg);
                    }

                    @Override
                    public void onNext(Attendance successModel) {


                        ProgressBarDialog.cancelLoading();
                        if (successModel.getSuccess()) {

                            Toast.makeText(Student_Face_Attendance_SingleDat.this, successModel.getMessage(), Toast.LENGTH_SHORT).show();
                            SQLite.delete(Student_Attendance_Local.class)
                                    .where(Student_Attendance_Local_Table.group_id.is(mySharedPreference.getPref(PrefKeys.GROUP_ID)))
                                    .async()
                                    .execute();

                            startActivityBase(Student_Face_Attendance_SingleDat.this, MeetingPhotosActivity.class);

                        }
                    }
                });

    }

    @Override
    public void onPause() {
        super.onPause();
        pauseProcessingFrames();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mIsFailed)
            return;
        resumeProcessingFrames();
    }

    private void pauseProcessingFrames() {
        mDraw.mStopping = 1;

        // It is essential to limit wait time, because mStopped will not be set to 0, if no frames are feeded to mDraw
        for (int i=0; i<100; ++i) {
            if (mDraw.mStopped != 0) break;
            try { Thread.sleep(10); }
            catch (Exception ex) {}
        }
    }

    private void resumeProcessingFrames() {
        mDraw.mStopped = 0;
        mDraw.mStopping = 0;
    }

    public  void Dialog_Twins(Context mContext){
        dialog_Twins = new Dialog(mContext);
        dialog_Twins.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog_Twins.setCancelable(false);
        dialog_Twins.setContentView(R.layout.twins_selection_dialog);

        RecyclerView twinse_list = (RecyclerView) dialog_Twins.findViewById(R.id.twinse_list);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(mContext);
        twinse_list.setLayoutManager(linearLayoutManager);
        twinse_list.setAdapter(new adapter_twin(mContext));
        dialog_Twins.show();
    }

    public void getTwinsByUserid(Context context, String id) {
        RetrofitInstance.getInstance(Student_Face_Attendance_SingleDat.this).
                getRestAdapter()
                .getIdenticalStudents(id)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<Twin_uuid>() {
                    @Override
                    public void onCompleted() {
                        Dialog_Twins(context);
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(Twin_uuid twin_uuids) {
                        al_twins_uuid = twin_uuids.getData();

                    }
                });
    }

    public class adapter_twin extends RecyclerView.Adapter<adapter_twin.Myviewholder>{
        Dialog dialog_offline;
        Context nContext;
        adapter_twin(Context mCotext) {
            this.nContext=mCotext;
        }
        @Override
        public adapter_twin.Myviewholder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.twinse_list_item, null);
            return new Myviewholder(v);
        }

        @Override
        public void onBindViewHolder(adapter_twin.Myviewholder holder, final int position) {

            holder.name.setText("Name: "+al_twins_uuid.get(position).getFirstName());
            holder.enrool_id.setText("ID: "+al_twins_uuid.get(position).getUuid());
            holder.liner.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(dialog_Twins.isShowing()){
                        dialog_Twins.dismiss();
                    }
                    st_name="";
                    dialog_offline = new Dialog(nContext);
                    dialog_offline.setCancelable(false);
                    Window window = dialog_offline.getWindow();
                    window.setGravity(Gravity.CENTER);
                    dialog_offline.setContentView(R.layout.twinse_conform_dialog);
                    TextView back = (TextView) dialog_offline.findViewById(R.id.txt_subject_body);
                    Button btn_ok = (Button) dialog_offline.findViewById(R.id.btn_ok);
                    Button btn_cancel = (Button) dialog_offline.findViewById(R.id.btn_cancel);
                    RelativeLayout parent_rl = (RelativeLayout) dialog_offline.findViewById(R.id.parent_rl);
                    //============  Menu =============
                    back.setText(nContext.getResources().getString(R.string.twins_conform_msg)+" "+al_twins_uuid.get(position).getFirstName());


                    btn_ok.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                            String formattedDate = sdf.format(new Date());

                            SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd");
                            String formattedDate1 = sdf1.format(new Date());
                            flag=0;
                            flag2=0;
                            named1=false;
                            percent=50;
                            flagtime=0;

                            if(dialog_Twins.isShowing()){
                                dialog_Twins.dismiss();
                            }
                            Student_Attendance_Local student_attendance=new Student_Attendance_Local();
                            student_attendance.setStudent_id(""+al_twins_uuid.get(position).getUuid());
                            student_attendance.setId(""+st_id);
                            student_attendance.setDate(""+formattedDate1);
                            student_attendance.setDate_time(""+formattedDate);
                            student_attendance.setPresent(1);
                            student_attendance.setGroup_id(""+mySharedPreference.getPref(PrefKeys.GROUP_ID));
                            student_attendance.save();

                            dialog_offline.dismiss();


                        }
                    });

                    btn_cancel.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            st_name="";
                            flag=0;
                            flag2=0;
                            named1=false;
                            percent=50;
                            flagtime=0;
                            dialog_offline.dismiss();
                            dialog_Twins.dismiss();

                        }
                    });
                    dialog_offline.getWindow().getAttributes().windowAnimations = R.style.DialogAnimationOpen;
                    dialog_offline.show();


                }
            });

        }

        @Override
        public int getItemCount() {
            return al_twins_uuid.size();
        }

        public class Myviewholder extends RecyclerView.ViewHolder {
            TextView name,enrool_id;
            LinearLayout liner;
            public Myviewholder(View itemView) {
                super(itemView);
                name=(TextView)itemView.findViewById(R.id.name);
                enrool_id=(TextView)itemView.findViewById(R.id.enrool_id);
                liner=(LinearLayout) itemView.findViewById(R.id.liner);

            }
        }
    }

    /*
     * dialog_attendance
     *
     * */
    public void Saveattendance_local(final Dialog d){

        Thread splashThread = new Thread() {
            public void run() {
                try {
                    sleep(2000);
                } catch (InterruptedException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                } finally {
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    String formattedDate = sdf.format(new Date());

                    SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd");
                    String formattedDate1 = sdf1.format(new Date());
                    flag=0;
                    flag2=0;
                    named1=false;
                    percent=50;
                    flagtime=0;
                    d.cancel();
                    Student_Attendance_Local student_attendance=new Student_Attendance_Local();
                    student_attendance.setStudent_id(""+st_name);
                    student_attendance.setId(""+st_id);
                    student_attendance.setDate(""+formattedDate1);
                    student_attendance.setDate_time(""+formattedDate);
                    student_attendance.setPresent(1);
                    student_attendance.setGroup_id(""+mySharedPreference.getPref(PrefKeys.GROUP_ID));
                    student_attendance.save();
                }
            }
        };
        splashThread.start();
        d.show();

    }
}

class FaceRectangle {
    public int x1, y1, x2, y2;
}

// Draw graphics on top of the video
class ProcessImageAndDrawResults extends View {

    private static final String TAG = "ProcessImageAndDrawResu";
    public FSDK.HTracker mTracker;
    final int MAX_FACES = 5;
    final String[] mAttributeValues = new String[MAX_FACES];
    final FSDK.FSDK_Features[] mFacialFeatures = new FSDK.FSDK_Features[MAX_FACES];
    final FaceRectangle[] mFacePositions = new FaceRectangle[MAX_FACES];
    final long[] mIDs = new long[MAX_FACES];
    final Lock faceLock = new ReentrantLock();
    int mTouchedIndex;
    int mStopping;
    int mStopped;
    Context mContext;
    Paint mPaintGreen, mPaintBlue, mPaintBlueTransparent, mPaintGreenTransparent;
    byte[] mYUVData;
    byte[] mRGBData;
    int mImageWidth, mImageHeight;
    boolean first_frame_saved;
    boolean rotated;

    int GetFaceFrame(FSDK.FSDK_Features Features, FaceRectangle fr)
    {
        if (Features == null || fr == null)
            return FSDK.FSDKE_INVALID_ARGUMENT;

        float u1 = Features.features[0].x;
        float v1 = Features.features[0].y;
        float u2 = Features.features[1].x;
        float v2 = Features.features[1].y;
        float xc = (u1 + u2) / 2;
        float yc = (v1 + v2) / 2;
        int w = (int) Math.pow((u2 - u1) * (u2 - u1) + (v2 - v1) * (v2 - v1), 0.5);

        fr.x1 = (int)(xc - w * 1.6 * 0.9);
        fr.y1 = (int)(yc - w * 1.1 * 0.9);
        fr.x2 = (int)(xc + w * 1.6 * 0.9);
        fr.y2 = (int)(yc + w * 2.1 * 0.9);
        if (fr.x2 - fr.x1 > fr.y2 - fr.y1) {
            fr.x2 = fr.x1 + fr.y2 - fr.y1;
        } else {
            fr.y2 = fr.y1 + fr.x2 - fr.x1;
        }
        return 0;
    }
    public ProcessImageAndDrawResults(Context context) {
        super(context);

        mTouchedIndex = -1;
        for (int i = 0; i < MAX_FACES; ++i) {
            mFacialFeatures[i] = new FSDK.FSDK_Features();
        }
        mStopping = 0;
        mStopped = 0;
        rotated = false;
        mContext = context;
        mPaintGreen = new Paint();
        mPaintGreen.setStyle(Paint.Style.FILL);
        mPaintGreen.setColor(Color.GREEN);
        mPaintGreen.setTextSize(25 * Student_Face_Attendance_SingleDat.sDensity);
        mPaintGreen.setTextAlign(Paint.Align.CENTER);

        mPaintBlue = new Paint();
        mPaintBlue.setStyle(Paint.Style.FILL);
        mPaintBlue.setColor(Color.BLUE);
        mPaintBlue.setStrokeWidth(5);
        mPaintBlue.setTextSize(25 * Student_Face_Attendance_SingleDat.sDensity);
        mPaintBlue.setTextAlign(Paint.Align.CENTER);

        mPaintBlueTransparent = new Paint();
        mPaintBlueTransparent.setStyle(Paint.Style.STROKE);
        mPaintBlueTransparent.setStrokeWidth(8);
        mPaintBlueTransparent.setColor(Color.BLUE);
        mPaintBlueTransparent.setTextSize(25* Student_Face_Attendance_SingleDat.sDensity);

        mPaintGreenTransparent = new Paint();
        mPaintGreenTransparent.setStyle(Paint.Style.STROKE);
        mPaintGreenTransparent.setStrokeWidth(10);
        mPaintGreenTransparent.setColor(Color.GREEN);
        mPaintGreenTransparent.setTextSize(25* Student_Face_Attendance_SingleDat.sDensity);
        mYUVData = null;
        mRGBData = null;

        first_frame_saved = false;
    }

    @Override
    protected void onDraw(Canvas canvas) {


        if (mStopping == 1) {
            mStopped = 1;
            super.onDraw(canvas);
            return;
        }

        if (mYUVData == null || mTouchedIndex != -1) {
            super.onDraw(canvas);
            return; //nothing to process or name is being entered now
        }

        int canvasWidth = canvas.getWidth();
        int canvasHeight = canvas.getHeight();


        // Convert from YUV to RGB
        decodeYUV420SP(mRGBData, mYUVData, mImageWidth, mImageHeight);

        // Load image to FaceSDK
        FSDK.HImage Image = new FSDK.HImage();
        FSDK.FSDK_IMAGEMODE imagemode = new FSDK.FSDK_IMAGEMODE();
        imagemode.mode = FSDK.FSDK_IMAGEMODE.FSDK_IMAGE_COLOR_24BIT;
        FSDK.LoadImageFromBuffer(Image, mRGBData, mImageWidth, mImageHeight, mImageWidth*3, imagemode);
        FSDK.MirrorImage(Image, false);
        FSDK.HImage RotatedImage = new FSDK.HImage();
        FSDK.CreateEmptyImage(RotatedImage);

        //it is necessary to work with local variables (onDraw called not the time when mImageWidth,... being reassigned, so swapping mImageWidth and mImageHeight may be not safe)
        int ImageWidth = mImageWidth;
        //int ImageHeight = mImageHeight;
        if (rotated) {
            ImageWidth = mImageHeight;
            //ImageHeight = mImageWidth;

            if(mySharedPreference.getPref(PrefKeys.CAM_CONFIG).equalsIgnoreCase("front")){
                /*
                 * for front camera
                 * */
                FSDK.RotateImage90(Image, -1, RotatedImage);
            }else{

                /*
                 * for back camera
                 * */
                FSDK.RotateImage90(Image, 1, RotatedImage);
            }

        } else {
            FSDK.CopyImage(Image, RotatedImage);
        }
        FSDK.FreeImage(Image);

        float ratio = (canvasWidth * 1.0f) / ImageWidth;

        long IDs[] = new long[MAX_FACES];
        long face_count[] = new long[1];

        FSDK.FeedFrame(mTracker, 0, RotatedImage, face_count, IDs);
        FSDK.FreeImage(RotatedImage);

        faceLock.lock();

        for (int i=0; i<MAX_FACES; ++i) {
            mFacePositions[i] = new FaceRectangle();
            mFacePositions[i].x1 = 0;
            mFacePositions[i].y1 = 0;
            mFacePositions[i].x2 = 0;
            mFacePositions[i].y2 = 0;
            mIDs[i] = IDs[i];
        }
        for (int i = 0; i < (int)face_count[0]; ++i) {
            FSDK.FSDK_Features Eyes = new FSDK.FSDK_Features();
            FSDK.GetTrackerEyes(mTracker, 0, mIDs[i], Eyes);

            GetFaceFrame(Eyes, mFacePositions[i]);
            mFacePositions[i].x1 *= ratio;
            mFacePositions[i].y1 *= ratio;
            mFacePositions[i].x2 *= ratio;
            mFacePositions[i].y2 *= ratio;

            Eyes.features[0].x *= ratio;
            Eyes.features[0].y *= ratio;
            Eyes.features[1].x *= ratio;
            Eyes.features[1].y *= ratio;

            FSDK.GetTrackerFacialFeatures(mTracker, 0, IDs[i], mFacialFeatures[i]);

            String values[] = new String[1];
            FSDK.GetTrackerFacialAttribute(mTracker, 0, IDs[i], "Expression", values, 1024);
            float[] confidenceSmile = new float[1];
            float[] confidenceEyesOpen = new float[1];
            FSDK.GetValueConfidence(values[0], "Smile", confidenceSmile);
            FSDK.GetValueConfidence(values[0], "EyesOpen", confidenceEyesOpen);
            int confidenceSmilePercent = (int)(confidenceSmile[0] * 100);
            confidenceEyesOpenPercent = (int)(confidenceEyesOpen[0] * 100);
            mAttributeValues[i] = String.format("Smile:%d%%\nEyes open:%d%%\n", confidenceSmilePercent, confidenceEyesOpenPercent);


                if (flag == 0) {
                    flag = 1; oldeyes=confidenceEyesOpenPercent;
                }
                if (flag == 1 && ((oldeyes-confidenceEyesOpenPercent)>percent) || (confidenceEyesOpenPercent-oldeyes)>percent) {
                    flag = 3;
                }

        }

        faceLock.unlock();

        int shift = (int)(22 * Student_Face_Attendance_SingleDat.sDensity);

        // Mark and name faces
        for (int i=0; i<face_count[0]; ++i) {
            canvas.drawRect(mFacePositions[i].x1, mFacePositions[i].y1, mFacePositions[i].x2, mFacePositions[i].y2, mPaintBlueTransparent);
            boolean named = false;
            long now1 = SystemClock.uptimeMillis();

                if (IDs[i] != -1) {
                    String names[] = new String[1];
                    st_id=""+IDs[i];
                    FSDK.GetAllNames(mTracker, IDs[i], names, 1024);
                    if (names[0] != null && names[0].length() > 0) {

                        canvas.drawText(" Slowly Blink your EYES ", (mFacePositions[i].x1+mFacePositions[i].x2)/2, mFacePositions[i].y2+shift, mPaintGreen);

                        named = true;
                        named1=true;
                        st_name=names[0];
                        if(flagtime==0){
                            oldtime = now1; flagtime=1;
                        }
                        /*if((now1-oldtime)>500){percent=40;}
                        if((now1-oldtime)>1000){percent=20;}
                        else{
                            percent=20;
                        }*/

                    }
                }
                if (!named) {
                    named1=false;
                }
            }
        if(flag==3 &&flag2==0){
            if (named1) {
                flag=4;flag2=1;

                /*
                 * dialog_attendance
                 *
                 * */

                final Dialog d=new Dialog(mContext);
                d.setContentView(R.layout.face_detect_dialog);
                TextView id=(TextView)d.findViewById(R.id.id);
                TextView tv_name=(TextView)d.findViewById(R.id.name);
                ImageView imgg=d.findViewById(R.id.imgg);
                TextView ok=(TextView)d.findViewById(R.id.ok);
                String path = "",name="",cam_config = "";
                int k=0;

                Log.d(TAG, "onDraw: st_name "+st_name);
                if(st_name.contains(";")){

                    String currentString = st_name;
                    String[] separated = currentString.split(";");


                    List<String> list = Arrays.asList(separated);
                    Log.d("list size",""+list.size());

                    if(list.size()>0){
                        al_twins_uuid.clear();
                        for(int i=0;i< mstudentlist.size();i++) {
                            for (int j = 0; j < list.size(); j++) {

                                if (String.valueOf(mstudentlist.get(i).getUuid()).equalsIgnoreCase(list.get(j))) {
                                    name = mstudentlist.get(i).getName();
                                    path = "" + mstudentlist.get(i).getImage();
                                    cam_config = "" + mstudentlist.get(i).getCameraConfig();

                                    Log.d("getCameraConfig", "onDraw: path "+path+" cam_config"+cam_config);
                                    Twin_uuid.Data matchedList = new Twin_uuid.Data();
                                    matchedList.setUuid(String.valueOf(mstudentlist.get(i).getUuid()));
                                    matchedList.setFirstName(mstudentlist.get(i).getName());
                                    al_twins_uuid.add(matchedList);

                                }
                            }
                        }

                        new Student_Face_Attendance_SingleDat().Dialog_Twins(mContext);

                    }
                }
                else{
                    al_twins_uuid.clear();
                    try {
                        if(mstudentlist!=null){
                            if(mstudentlist.size()>0){
                                for(int i=0;i< mstudentlist.size();i++){

                                    String st_id=""+mstudentlist.get(i).getUuid();
                                    if(st_id.equalsIgnoreCase(st_name)){
                                        name=mstudentlist.get(i).getName();
                                        path=""+mstudentlist.get(i).getImage();
                                        cam_config=""+mstudentlist.get(i).getCameraConfig();

                                        Log.d("mstudentlist", "onDraw: "+cam_config);

                                    }
                                }}}
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    if(al_twins.size()>0){

                        for(int i=0;i<al_twins.size();i++){
                            if(st_name.equalsIgnoreCase(al_twins.get(i).getUuid())){
                                k=1;
                            }
                        }

                        if(k==1){

                            if(CreateMeetingActivity.isNetwork) {
                                new Student_Face_Attendance_SingleDat().getTwinsByUserid(mContext,st_name);

                            }else{

                                /*
                                 * offline twins start
                                 * */
                                Students_TwinsList_Local data = SQLite.select().from(Students_TwinsList_Local.class)
                                        .where(Students_TwinsList_Local_Table.section_id.is(mySharedPreference.getPref(PrefKeys.USER_ID)))
                                        .querySingle();
                                if(data!=null){
                                    al_twins = JSON.parseArray(data.getData(), Twin.Data.class);
                                }
                                for(int i=0;i<al_twins.size();i++) {

                                    if (String.valueOf(al_twins.get(i).getUuid()).equalsIgnoreCase(st_name)) {
                                        al_twins_uuid.add(al_twins.get(i).getMatchingUser().get(i));

                                    }
                                }
                                new Student_Face_Attendance_SingleDat().Dialog_Twins(mContext);

                                /*
                                 * offline twins end
                                 * */

                            }

                        }else{
                            id.setText(""+st_name);
                            tv_name.setText(""+name);
                            try {

                                Log.d(TAG, "onDraw: path "+path);
                                if(cam_config.equalsIgnoreCase("front")){
                                    Picasso.with(mContext)
                                            .load(path)
                                            .rotate(270f)
                                            .placeholder(R.drawable.ic_user)
                                            .into(imgg);
                                }else if(cam_config.equalsIgnoreCase("back")){
                                    Picasso.with(mContext)
                                            .load(path)
                                            .rotate(90f)
                                            .placeholder(R.drawable.ic_user)
                                            .into(imgg);
                                }else{
                                    Picasso.with(mContext)
                                            .load(path)
                                            .placeholder(R.drawable.ic_user)
                                            .into(imgg);
                                }


                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            d.setCanceledOnTouchOutside(false);


                            new Student_Face_Attendance_SingleDat().Saveattendance_local(d);

                            d.show();
                        }

                    }else{

                        id.setText(""+st_name);
                        tv_name.setText(""+name);
                        Log.d(TAG, "onDraw: path "+path);
                        if(cam_config.equalsIgnoreCase("front")) {
                            try {
                                if(!path.equalsIgnoreCase("")){
                                    Picasso.with(mContext)
                                            .load(path)
                                            .transform(new CircleTransform())
                                            .placeholder(R.drawable.ic_user)
                                            .rotate(270f)
                                            .into(imgg);
                                }else {
                                    Picasso.with(mContext)
                                            .load(R.drawable.ic_user)
                                            .placeholder(R.drawable.ic_user)
                                            .into(imgg);
                                }

                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }else if(cam_config.equalsIgnoreCase("back")){
                            if(!path.equalsIgnoreCase("")){
                                Picasso.with(mContext)
                                        .load(path)
                                        .transform(new CircleTransform())
                                        .placeholder(R.drawable.ic_user)
                                        .rotate(90f)
                                        .into(imgg);
                            }else {
                                Picasso.with(mContext)
                                        .load(R.drawable.ic_user)
                                        .placeholder(R.drawable.ic_user)
                                        .into(imgg);
                            }
                        }else{
                            if(!path.equalsIgnoreCase("")){
                                Picasso.with(mContext)
                                        .load(path)
                                        .transform(new CircleTransform())
                                        .placeholder(R.drawable.ic_user)
                                        .into(imgg);
                            }else {
                                Picasso.with(mContext)
                                        .load(R.drawable.ic_user)
                                        .placeholder(R.drawable.ic_user)
                                        .into(imgg);
                            }
                        }
                        d.setCanceledOnTouchOutside(false);


                        new Student_Face_Attendance_SingleDat().Saveattendance_local(d);

                        d.show();

                    }
                }



            }}
        super.onDraw(canvas);
    } // end onDraw method


    static public void decodeYUV420SP(byte[] rgb, byte[] yuv420sp, int width, int height) {
        final int frameSize = width * height;
        int yp = 0;
        for (int j = 0; j < height; j++) {
            int uvp = frameSize + (j >> 1) * width, u = 0, v = 0;
            for (int i = 0; i < width; i++) {
                int y = (0xff & ((int) yuv420sp[yp])) - 16;
                if (y < 0) y = 0;
                if ((i & 1) == 0) {
                    v = (0xff & yuv420sp[uvp++]) - 128;
                    u = (0xff & yuv420sp[uvp++]) - 128;
                }
                int y1192 = 1192 * y;
                int r = (y1192 + 1634 * v);
                int g = (y1192 - 833 * v - 400 * u);
                int b = (y1192 + 2066 * u);
                if (r < 0) r = 0; else if (r > 262143) r = 262143;
                if (g < 0) g = 0; else if (g > 262143) g = 262143;
                if (b < 0) b = 0; else if (b > 262143) b = 262143;

                rgb[3*yp] = (byte) ((r >> 10) & 0xff);
                rgb[3*yp+1] = (byte) ((g >> 10) & 0xff);
                rgb[3*yp+2] = (byte) ((b >> 10) & 0xff);
                ++yp;
            }
        }
    }
}

// end of ProcessImageAndDrawResults class

// Show video from camera and pass frames to ProcessImageAndDraw class
class Preview extends SurfaceView implements SurfaceHolder.Callback {
    Context mContext;
    SurfaceHolder mHolder;
    Camera mCamera;
    ProcessImageAndDrawResults mDraw;
    boolean mFinished;
    int cameraId_front = 0;
    int cameraId_back = 0;

    Preview(Context context, ProcessImageAndDrawResults draw) {
        super(context);
        mContext = context;
        mDraw = draw;

        //Install a SurfaceHolder.Callback so we get notified when the underlying surface is created and destroyed.
        mHolder = getHolder();
        mHolder.addCallback(this);
        mHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
    }

    //SurfaceView callback
    public void surfaceCreated(SurfaceHolder holder) {
        mFinished = false;

        // Find the ID of the camera
        boolean frontCameraFound = false;
        Camera.CameraInfo cameraInfo = new Camera.CameraInfo();
        for (int i = 0; i < Camera.getNumberOfCameras(); i++) {
            Camera.getCameraInfo(i, cameraInfo);
            if (cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_BACK){
                cameraId_back = i;
                backCameraFound=true;
            }
            if (cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
                cameraId_front = i;
                frontCameraFound = true;
            }
        }

        if(mySharedPreference.getPref(PrefKeys.CAM_CONFIG).equalsIgnoreCase("front")){
            if(frontCameraFound){
                mCamera = Camera.open(cameraId_front);
            }

        }else{
            if(backCameraFound){
                mCamera = Camera.open(cameraId_back);
            }
        }


        try {
            mCamera.setPreviewDisplay(holder);

            // Preview callback used whenever new viewfinder frame is available
            mCamera.setPreviewCallback(new Camera.PreviewCallback() {
                public void onPreviewFrame(byte[] data, Camera camera) {
                    if ( (mDraw == null) || mFinished )
                        return;

                    if (mDraw.mYUVData == null) {
                        // Initialize the draw-on-top companion
                        Camera.Parameters params = camera.getParameters();
                        mDraw.mImageWidth = params.getPreviewSize().width;
                        mDraw.mImageHeight = params.getPreviewSize().height;
                        mDraw.mRGBData = new byte[3 * mDraw.mImageWidth * mDraw.mImageHeight];
                        mDraw.mYUVData = new byte[data.length];
                    }

                    // Pass YUV data to draw-on-top companion
                    System.arraycopy(data, 0, mDraw.mYUVData, 0, data.length);
                    mDraw.invalidate();
                }
            });
        }
        catch (Exception exception) {
            AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
            builder.setMessage("Cannot open camera" )
                    .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            android.os.Process.killProcess(android.os.Process.myPid());
                        }
                    })
                    .show();
            if (mCamera != null) {
                mCamera.release();
                mCamera = null;
            }
        }
    }

    //SurfaceView callback
    public void surfaceDestroyed(SurfaceHolder holder) {
        // Surface will be destroyed when we return, so stop the preview.
        // Because the CameraDevice object is not a shared resource, it's very
        // important to release it when the activity is paused.
        mFinished = true;
        if (mCamera != null) {
            mCamera.setPreviewCallback(null);
            mCamera.stopPreview();
            mCamera.release();
            mCamera = null;
        }
    }

    //SurfaceView callback, configuring camera
    public void surfaceChanged(SurfaceHolder holder, int format, int w, int h) {
        if (mCamera == null) return;

        // Now that the size is known, set up the camera parameters and begin
        // the preview.
        Camera.Parameters parameters = mCamera.getParameters();

        //Keep uncommented to work correctly on phones:
        //This is an undocumented although widely known feature
        /**/
        if (this.getResources().getConfiguration().orientation != Configuration.ORIENTATION_LANDSCAPE) {
            parameters.set("orientation", "portrait");
            mCamera.setDisplayOrientation(90); // For Android 2.2 and above
            mDraw.rotated = true;
        } else {
            parameters.set("orientation", "landscape");
            mCamera.setDisplayOrientation(0); // For Android 2.2 and above
        }
        /**/

        // choose preview size closer to 640x480 for optimal performance
        List<Camera.Size> supportedSizes = parameters.getSupportedPreviewSizes();
        int width = 0;
        int height = 0;
        for (Camera.Size s: supportedSizes) {
            if ((width - 640)*(width - 640) + (height - 480)*(height - 480) >
                    (s.width - 640)*(s.width - 640) + (s.height - 480)*(s.height - 480)) {
                width = s.width;
                height = s.height;
            }
        }

        //try to set preferred parameters
        try {
            if (width*height > 0) {
                parameters.setPreviewSize(width, height);
            }

            parameters.setSceneMode(Camera.Parameters.SCENE_MODE_PORTRAIT);
            parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_VIDEO);
            mCamera.setParameters(parameters);
        } catch (Exception ex) {
        }
        mCamera.startPreview();

        parameters = mCamera.getParameters();
        Camera.Size previewSize = parameters.getPreviewSize();
        makeResizeForCameraAspect(1.0f / ((1.0f * previewSize.width) / previewSize.height));
    }

    private void makeResizeForCameraAspect(float cameraAspectRatio){
        ViewGroup.LayoutParams layoutParams = this.getLayoutParams();
        int matchParentWidth = this.getWidth();
        int newHeight = (int)(matchParentWidth/cameraAspectRatio);
        if (newHeight != layoutParams.height) {
            layoutParams.height = newHeight;
            layoutParams.width = matchParentWidth;
            this.setLayoutParams(layoutParams);
            this.invalidate();
        }
    }


} // end of Preview class
