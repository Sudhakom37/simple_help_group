package com.luxand.shg.views.activities.division_offcer;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.ColorDrawable;
import android.hardware.Camera;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Environment;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.luxand.FSDK;
import com.luxand.shg.R;
import com.luxand.shg.api.RetrofitInstance;
import com.luxand.shg.helper.Helper;
import com.luxand.shg.helper.MySharedPreference;
import com.luxand.shg.helper.PrefKeys;
import com.luxand.shg.model.Registration;
import com.luxand.shg.views.activities.BaseActivity;
import com.luxand.shg.views.activities.ResetPinActivity;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.FileOutputStream;
import java.util.List;
import java.util.Random;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

import static com.luxand.shg.views.activities.division_offcer.DivisionMemberEnrollActivity.DATFILE;
import static com.luxand.shg.views.activities.division_offcer.DivisionMemberEnrollActivity.IMAGES;
import static com.luxand.shg.views.activities.division_offcer.DivisionMemberEnrollActivity.already_face_exit;
import static com.luxand.shg.views.activities.division_offcer.DivisionMemberEnrollActivity.backCameraFound;
import static com.luxand.shg.views.activities.division_offcer.DivisionMemberEnrollActivity.canvasHeight;
import static com.luxand.shg.views.activities.division_offcer.DivisionMemberEnrollActivity.confidenceEyesOpenPercent;
import static com.luxand.shg.views.activities.division_offcer.DivisionMemberEnrollActivity.counttime;
import static com.luxand.shg.views.activities.division_offcer.DivisionMemberEnrollActivity.datFile;
import static com.luxand.shg.views.activities.division_offcer.DivisionMemberEnrollActivity.image;
import static com.luxand.shg.views.activities.division_offcer.DivisionMemberEnrollActivity.database;
import static com.luxand.shg.views.activities.division_offcer.DivisionMemberEnrollActivity.detected;
import static com.luxand.shg.views.activities.division_offcer.DivisionMemberEnrollActivity.filePath;
import static com.luxand.shg.views.activities.division_offcer.DivisionMemberEnrollActivity.mCountDownTimer;
import static com.luxand.shg.views.activities.division_offcer.DivisionMemberEnrollActivity.mDraw;
import static com.luxand.shg.views.activities.division_offcer.DivisionMemberEnrollActivity.mac_address;
import static com.luxand.shg.views.activities.division_offcer.DivisionMemberEnrollActivity.mySharedPreference;
import static com.luxand.shg.views.activities.division_offcer.DivisionMemberEnrollActivity.named1;
import static com.luxand.shg.views.activities.division_offcer.DivisionMemberEnrollActivity.progressbar;
import static com.luxand.shg.views.activities.division_offcer.DivisionMemberEnrollActivity.squarelength;
import static com.luxand.shg.views.activities.division_offcer.DivisionMemberEnrollActivity.st_id;
import static com.luxand.shg.views.activities.division_offcer.DivisionMemberEnrollActivity.st_name;
import static com.luxand.shg.views.activities.division_offcer.DivisionMemberEnrollActivity.tap;
import static com.luxand.shg.views.activities.division_offcer.DivisionMemberEnrollActivity.taptoenroll;
import static com.luxand.shg.views.activities.division_offcer.DivisionMemberEnrollActivity.timer_updatecompl;


public class DivisionMemberEnrollActivity extends BaseActivity implements View.OnClickListener {


    private boolean mIsFailed = false;
    private Preview1 mPreview;
    public static ProcessImageAndDrawResults1 mDraw;
    private boolean wasStopped = false;
    public static String database = "",filePath = "";
    public static float sDensity = 1.0f;
    public static String st_name = "", DATFILE = "", IMAGES = "", st_id, path = "", compresspath;
    public static int confidenceEyesOpenPercent = 0;
    public static boolean named1 = false, detected = false;
    public static boolean backCameraFound = false;
    public static boolean timer_updatecompl = false;
    public static MySharedPreference mySharedPreference;
    public static int tap = 0;
    public static double squarelength;
    public static ProgressBar progressbar;
    public static TextView taptoenroll;
    public static int canvasHeight;
    public static int counttime = 0;
    public static CountDownTimer mCountDownTimer = null;
    public static TextView ok, submit;
    public static String filePath_twin_img;
    public static String FROM;
    public static boolean already_face_exit = false;
    private FrameLayout mLayout;
    //public static String imei,mac_address;
    public static String mac_address;
    public static File image=new File(""),datFile= new File("");

    private static final String TAG = "DivisionMemberEnrollAct";
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

    public void showMessage(String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(message)
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                    }
                })
                .setCancelable(false)
                .show();
    }

    private void resetTrackerParameters() {
        int errpos[] = new int[1];
        FSDK.SetTrackerMultipleParameters(mDraw.mTracker, "RecognizeFaces=true;DetectFacialFeatures=true;ContinuousVideoFeed=true;RecognitionPrecision=1;Threshold=0.996;Threshold2=0.9995;ThresholdFeed=0.97;MemoryLimit=1000;HandleArbitraryRotations=false;DetermineFaceRotationAngle=false;InternalResizeWidth=70;FaceDetectionThreshold=3;DetectGender=false;DetectExpression=true", errpos);
        if (errpos[0] != 0) {
            showErrorAndClose("Error setting tracker parameters, position", errpos[0]);
        }
    }


    @SuppressLint("WrongViewCast")
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        sDensity = getResources().getDisplayMetrics().scaledDensity;
        //imei=Helper.getInstance(Face_Register_Staff.this).getIMEI();

        mySharedPreference = new MySharedPreference(DivisionMemberEnrollActivity.this);
        mac_address= RetrofitInstance.getMacAddress();
        database =  "ss.dat";
        FROM = getIntent().getStringExtra("from");

        DATFILE = Helper.DATFILE;
        IMAGES = Helper.IMAGES;
        String imageFile = getIntent().getStringExtra(PrefKeys.MEMBER_IMAGE);
        String datFile1 = getIntent().getStringExtra(PrefKeys.DAT_FILE);
        if(imageFile!= null){
            image = new File(imageFile);
        }
        if(datFile1!= null){
            datFile = new File(datFile1);
        }

        Log.d(TAG, "onCreate: image"+image+" datFile "+datFile);
        detected = false;

        int res = FSDK.ActivateLibrary("" + mySharedPreference.getPref(PrefKeys.FACEURL));

        if (res != FSDK.FSDKE_OK) {
            mIsFailed = true;
            showErrorAndClose("Please Logout and Login", res);
        } else {
            FSDK.Initialize();

            mLayout = new FrameLayout(this);
            ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            mLayout.setLayoutParams(params);
            setContentView(mLayout);

            openCamera();

        }
    }



    private void openCamera() {
        View background = new View(this);
        background.setBackgroundColor(Color.BLACK);
        mDraw = new ProcessImageAndDrawResults1(this);
        mPreview = new Preview1(this, mDraw);
        mDraw.mTracker = new FSDK.HTracker();

        File myDir= new File(Environment.getExternalStorageDirectory().getPath(), ""+DATFILE);
        String templatePath = "" + myDir + "/" + database; // databasepath
        if (FSDK.FSDKE_OK != FSDK.LoadTrackerMemoryFromFile(mDraw.mTracker, templatePath)) {
            int res = FSDK.CreateTracker(mDraw.mTracker);
            if (FSDK.FSDKE_OK != res) {
                showErrorAndClose("Error creating tracker open Again", res);
            }
        }

        resetTrackerParameters();

        this.getWindow().setBackgroundDrawable(new ColorDrawable()); //black background

        mLayout.setVisibility(View.VISIBLE);
        addContentView(background, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        addContentView(mPreview, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)); //creates Login_Activity contents
        addContentView(mDraw, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));

        // Menu
        LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View buttons = inflater.inflate(R.layout.activity_face_signup, null);
        //buttons.findViewById(R.id.helpButton).setOnClickListener(this);
        buttons.findViewById(R.id.clearButton).setOnClickListener(this);
        progressbar = buttons.findViewById(R.id.progressbar);
        taptoenroll = buttons.findViewById(R.id.taptoenroll);
        addContentView(buttons, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));

    }

    @Override
    public void onClick(View view) {

        if (view.getId() == R.id.clearButton) {
            already_face_exit = false;
            finish();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (wasStopped && mDraw == null) {
            wasStopped = false;
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mDraw != null) {
            pauseProcessingFrames();

        }
        File myDir= new File(Environment.getExternalStorageDirectory().getPath(), ""+DATFILE);
        String templatePath = "" + myDir + "/" + database;


        FSDK.SaveTrackerMemoryToFile(mDraw.mTracker, templatePath);
    }

    @Override
    protected void onStop() {
        counttime = 0;
        if (mCountDownTimer != null) {
            mCountDownTimer.cancel();
        }
        super.onStop();
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
        for (int i = 0; i < 100; ++i) {
            if (mDraw.mStopped != 0) break;
            try {
                Thread.sleep(10);
            } catch (Exception ex) {
            }
        }
    }

    private void resumeProcessingFrames() {
        try {
            mDraw.mStopped = 0;
            mDraw.mStopping = 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

class FaceRectangle1 {
    public int x1, y1, x2, y2;
}

class ProcessImageAndDrawResults1 extends View {
    public FSDK.HTracker mTracker;
    private static final String TAG = "DivisionMemberEnrollActivity";
    final int MAX_FACES = 5;
    final String[] mAttributeValues = new String[MAX_FACES];
    final FSDK.FSDK_Features[] mFacialFeatures = new FSDK.FSDK_Features[MAX_FACES];
    final FaceRectangle1[] mFacePositions = new FaceRectangle1[MAX_FACES];
    final long[] mIDs = new long[MAX_FACES];
    final Lock faceLock = new ReentrantLock();
    int mTouchedIndex;
    long mTouchedID;
    int mStopping;
    int mStopped;

    Context mContext;
    Paint mPaintGreen, mPaintBlue, mPaintBlueTransparent, mPaintGreenTransparent, mPaintWhiteTransparent;
    byte[] mYUVData;
    byte[] mRGBData;
    int mImageWidth, mImageHeight;
    boolean first_frame_saved;
    boolean rotated;

    int GetFaceFrame(FSDK.FSDK_Features Features, FaceRectangle1 fr) {
        if (Features == null || fr == null)
            return FSDK.FSDKE_INVALID_ARGUMENT;

        float u1 = Features.features[0].x;
        float v1 = Features.features[0].y;
        float u2 = Features.features[1].x;
        float v2 = Features.features[1].y;
        float xc = (u1 + u2) / 2;
        float yc = (v1 + v2) / 2;
        int w = (int) Math.pow((u2 - u1) * (u2 - u1) + (v2 - v1) * (v2 - v1), 0.5);

        fr.x1 = (int) (xc - w * 1.6 * 0.9);
        fr.y1 = (int) (yc - w * 1.1 * 0.9);
        fr.x2 = (int) (xc + w * 1.6 * 0.9);
        fr.y2 = (int) (yc + w * 2.1 * 0.9);
        if (fr.x2 - fr.x1 > fr.y2 - fr.y1) {
            fr.x2 = fr.x1 + fr.y2 - fr.y1;
        } else {
            fr.y2 = fr.y1 + fr.x2 - fr.x1;
        }
        return 0;
    }


    public ProcessImageAndDrawResults1(Context context) {
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
        mPaintGreen.setColor(Color.TRANSPARENT);
        mPaintGreen.setTextSize(25 * DivisionMemberEnrollActivity.sDensity);
        mPaintGreen.setTextAlign(Paint.Align.CENTER);

        mPaintBlue = new Paint();
        mPaintBlue.setStyle(Paint.Style.FILL);
        mPaintBlue.setColor(Color.TRANSPARENT);
        mPaintBlue.setTextSize(25 * DivisionMemberEnrollActivity.sDensity);
        mPaintBlue.setTextAlign(Paint.Align.CENTER);

        mPaintBlueTransparent = new Paint();
        mPaintBlueTransparent.setStyle(Paint.Style.STROKE);
        mPaintBlueTransparent.setStrokeWidth(0);
        mPaintBlueTransparent.setColor(Color.TRANSPARENT);
        mPaintBlueTransparent.setTextSize(25 * DivisionMemberEnrollActivity.sDensity);

        mPaintGreenTransparent = new Paint();
        mPaintGreenTransparent.setStyle(Paint.Style.STROKE);
        mPaintGreenTransparent.setStrokeWidth(0);
        mPaintGreenTransparent.setColor(Color.TRANSPARENT);
        mPaintGreenTransparent.setTextSize(25 * DivisionMemberEnrollActivity.sDensity);

        /*
         * new change
         * */
        mPaintWhiteTransparent = new Paint();
        mPaintWhiteTransparent.setStyle(Paint.Style.STROKE);
        mPaintWhiteTransparent.setStrokeWidth(5);
        mPaintWhiteTransparent.setColor(Color.WHITE);
        mPaintWhiteTransparent.setTextSize(40);

        //mBitmap = null;
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
            return;
        }

        int canvasWidth = canvas.getWidth();
        canvasHeight = canvas.getHeight();
        decodeYUV420SP(mRGBData, mYUVData, mImageWidth, mImageHeight);
        FSDK.HImage Image = new FSDK.HImage();
        FSDK.FSDK_IMAGEMODE imagemode = new FSDK.FSDK_IMAGEMODE();
        imagemode.mode = FSDK.FSDK_IMAGEMODE.FSDK_IMAGE_COLOR_24BIT;
        FSDK.LoadImageFromBuffer(Image, mRGBData, mImageWidth, mImageHeight, mImageWidth * 3, imagemode);

        /*
         * camera activities
         *
         * */

        /*
         * for front camera and for back camera we need to comment this line
         * */
        FSDK.MirrorImage(Image, false);


        FSDK.HImage RotatedImage = new FSDK.HImage();
        FSDK.CreateEmptyImage(RotatedImage);

        //it is necessary to work with local variables (onDraw called not the time when mImageWidth,... being reassigned, so swapping mImageWidth and mImageHeight may be not safe)
        int ImageWidth = mImageWidth;
        //int ImageHeight = mImageHeight;
        if (rotated) {
            ImageWidth = mImageHeight;
            //ImageHeight = mImageWidth;

            if (mySharedPreference.getPref(PrefKeys.CAM_CONFIG).equalsIgnoreCase("front")) {
                /*
                 * for front camera
                 * */
                FSDK.RotateImage90(Image, -1, RotatedImage);
            } else {

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

        for (int i = 0; i < MAX_FACES; ++i) {
            mFacePositions[i] = new FaceRectangle1();
            mFacePositions[i].x1 = 0;
            mFacePositions[i].y1 = 0;
            mFacePositions[i].x2 = 0;
            mFacePositions[i].y2 = 0;
            mIDs[i] = IDs[i];
        }

        for (int i = 0; i < (int) face_count[0]; ++i) {
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
            int confidenceSmilePercent = (int) (confidenceSmile[0] * 100);
            confidenceEyesOpenPercent = (int) (confidenceEyesOpen[0] * 100);
            mAttributeValues[i] = String.format("Smile:%d%%\nEyes open:%d%%\n", confidenceSmilePercent, confidenceEyesOpenPercent);

        }

        faceLock.unlock();

        int shift = (int) (22 * DivisionMemberEnrollActivity.sDensity);

        // Mark and name faces
        for (int i = 0; i < face_count[0]; ++i) {
            squarelength = (mFacePositions[i].x2 - mFacePositions[i].x1);

                    /*
                     * new change
                     * */

                    progressbar.setProgress(counttime);
                    if (mCountDownTimer != null) {
                        mCountDownTimer.cancel();
                    }
                    mCountDownTimer = new CountDownTimer(15000, 1000) {

                        @Override
                        public void onTick(long millisUntilFinished) {
                            Log.v("Log_tag", "Tick of Progress" + counttime + millisUntilFinished);
                            counttime++;
                            int progress = (int) (millisUntilFinished / 1000);
                            progressbar.setProgress(progressbar.getMax() - progress);
                            if (counttime == 1) {
                                /*Toast toast = Toast.makeText(mContext,
                                        "Learning more about you.Turn Your Face Slightly Left", Toast.LENGTH_LONG);
                                toast.setGravity(Gravity.TOP | Gravity.CENTER_HORIZONTAL, 0, 0);
                                toast.show();*/


                            }
                            if (counttime == 2) {
//                                Toast toast = Toast.makeText(mContext,
//                                        "Learning more about you.Turn Your Face Slightly Right", Toast.LENGTH_LONG);
//                                toast.setGravity(Gravity.TOP | Gravity.CENTER_HORIZONTAL, 0, 0);
//                                toast.show();
                            }
                            if (counttime == 3) {

                                Toast toast = Toast.makeText(mContext,
                                        "Please tap on face to enroll", Toast.LENGTH_LONG);
                                toast.setGravity(Gravity.TOP | Gravity.CENTER_HORIZONTAL, 0, 0);
                                toast.show();
                                timer_updatecompl = true;
                                taptoenroll.setVisibility(VISIBLE);
                            }
                        }

                        @Override
                        public void onFinish() {
                            //Do what you want

                            Log.v("completed", "FInished timer 2");

                            timer_updatecompl = true;

                        }
                    };
                    mCountDownTimer.start();

                    canvas.drawRect(mFacePositions[i].x1, mFacePositions[i].y1, mFacePositions[i].x2, mFacePositions[i].y2, mPaintBlueTransparent);


            boolean named = false;
                if (IDs[i] != -1) {
                    String names[] = new String[1];
                    st_id = "" + IDs[i];
                    FSDK.GetAllNames(mTracker, IDs[i], names, 1024);
                    if (names[0] != null && names[0].length() > 0) {
                        //canvas.drawText(names[0]+" Slowly Blink your EYES "+confidenceEyesOpenPercent+" "+flag, (mFacePositions[i].x1+mFacePositions[i].x2)/2, mFacePositions[i].y2+shift, mPaintBlue);

                        counttime = 0;
                        if (mCountDownTimer != null) {
                            mCountDownTimer.cancel();
                        }

                        named = true;
                        named1 = true;
                        st_name = names[0];
                    }
                }
                if (!named) {
                        if (timer_updatecompl) {
                            canvas.drawText("Tap to enroll", (mFacePositions[i].x1 + mFacePositions[i].x2) / 2, mFacePositions[i].y2 + shift, mPaintGreen);
                        }
                    named1 = false;
                    detected = false;
                }
        }

        super.onDraw(canvas);
    }

    @SuppressLint("WrongThread")
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:

                if (timer_updatecompl) {
                    int x = (int) event.getX();
                    int y = (int) event.getY();

                    faceLock.lock();
                    FaceRectangle1 rects[] = new FaceRectangle1[MAX_FACES];
                    long IDs[] = new long[MAX_FACES];
                    for (int i = 0; i < MAX_FACES; ++i) {
                        rects[i] = new FaceRectangle1();
                        try {
                            rects[i].x1 = mFacePositions[i].x1;
                            rects[i].y1 = mFacePositions[i].y1;
                            rects[i].x2 = mFacePositions[i].x2;
                            rects[i].y2 = mFacePositions[i].y2;
                            IDs[i] = mIDs[i];
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    faceLock.unlock();

                    for (int i = 0; i < MAX_FACES; ++i) {
                        if (rects[i] != null && rects[i].x1 <= x && x <= rects[i].x2 && rects[i].y1 <= y && y <= rects[i].y2 + 30) {
                            mTouchedID = IDs[i];

                            mTouchedIndex = i;
                            final EditText input = new EditText(mContext);
                            input.setVisibility(GONE);

                            String st_id = mac_address;
                            input.setText("" + st_id);
                            final AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                            int[] mIntArray = new int[mImageWidth * mImageHeight];
                            decodeYUV420SP1(mIntArray, mYUVData, mImageWidth, mImageHeight);
                            Bitmap bmp = Bitmap.createBitmap(mIntArray, mImageWidth, mImageHeight, Bitmap.Config.ARGB_8888);

                            File myDir= new File(Environment.getExternalStorageDirectory().getPath(), ""+IMAGES);
                            //File myDir = new File(IMAGES);
                            myDir.mkdirs();

                            Random generator = new Random();
                            int n = 10000;
                            n = generator.nextInt(n);
                            String fname = "" + st_id + ".jpg";
                            final File file = new File(myDir, fname);
                            if (file.exists()) file.delete();
                            try {
                                FileOutputStream out = new FileOutputStream(file);
                                bmp.compress(Bitmap.CompressFormat.JPEG, 90, out);
                                out.flush();
                                out.close();

                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            if (tap == 0) {

                                counttime = 0;
                                timer_updatecompl = false;
                                try {
                                    if (mCountDownTimer != null) {
                                        mCountDownTimer.cancel();
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                                already_face_exit = false;
                                progressbar.setProgress(counttime);

                                final Dialog d = new Dialog(mContext);
                                d.setContentView(R.layout.face_detect_register_dilog);
                                TextView id = (TextView) d.findViewById(R.id.id);
                                TextView ok = (TextView) d.findViewById(R.id.ok);
                                TextView cancel = (TextView) d.findViewById(R.id.cancel);
                                ImageView imgg = d.findViewById(R.id.imgg);
                                id.setText(mac_address);
                                d.setCanceledOnTouchOutside(false);

                                try {
                                    d.show();
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                                File myDir1= new File(Environment.getExternalStorageDirectory().getPath(), ""+IMAGES);
                                //File myDir1 = new File(IMAGES);
                                final String templatePath = "" + myDir1 + "/" +mac_address + ".jpg";

                                if (mySharedPreference.getPref(PrefKeys.CAM_CONFIG).equalsIgnoreCase("front")) {

                                    Picasso.with(mContext)
                                            .load(new File(templatePath))
                                            .memoryPolicy(MemoryPolicy.NO_CACHE)
                                            .rotate(270f)
                                            .into(imgg);
                                } else {
                                    Picasso.with(mContext)
                                            .load(new File(templatePath))
                                            .memoryPolicy(MemoryPolicy.NO_CACHE)
                                            .rotate(90f)
                                            .into(imgg);
                                }

                                cancel.setOnClickListener(new OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        d.cancel();
                                        mTouchedIndex = -1;
                                        counttime = 0;
                                        if (mCountDownTimer != null) {
                                            mCountDownTimer.cancel();
                                        }
                                        progressbar.setProgress(counttime);
                                        timer_updatecompl = false;

                                    }
                                });

                                ok.setOnClickListener(new OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        d.cancel();
                                        FSDK.LockID(mTracker, mTouchedID);
                                        String userName =mac_address;
                                        input.setText("" + userName);
                                        FSDK.SetName(mTracker, mTouchedID, userName);
                                        if (userName.length() <= 0)
                                        FSDK.PurgeID(mTracker, mTouchedID);
                                        FSDK.UnlockID(mTracker, mTouchedID);

                                        File myDir_file= new File(Environment.getExternalStorageDirectory().getPath(), ""+DATFILE);
                                        //File myDir_file = new File(DATFILE);
                                        myDir_file.mkdirs();
                                        filePath = "" + myDir_file + "/" + database;
                                        FSDK.SaveTrackerMemoryToFile(mDraw.mTracker, filePath);

                                        /*
                                         *
                                         * image and values
                                         *
                                         * */

                                        //templatePath
                                        //filePath
                                        //((AppCompatActivity) mContext).finish();

                                        MultipartBody.Builder builder = new MultipartBody.Builder();
                                        builder.setType(MultipartBody.FORM);
                                        builder.addFormDataPart("sno",mySharedPreference.getPref(PrefKeys.SNO));
                                        builder.addFormDataPart("mac_address",""+RetrofitInstance.getMacAddress());
                                        File image1 = new File(templatePath);
                                        File datfile = new File(filePath);
                                        builder.addFormDataPart("image", image1.getName(),
                                                RequestBody.create(okhttp3.MediaType.parse("image/*"), image1));
                                        builder.addFormDataPart("ss_dat", datfile.getName(),
                                                RequestBody.create(okhttp3.MediaType.parse("file/*"), datfile));
                                        builder.addFormDataPart("datfile", datFile.getName(),
                                                RequestBody.create(okhttp3.MediaType.parse("file/*"), datFile));
                                        MultipartBody requestBody = builder.build();


                                        uploadImageandDat(requestBody,mContext);

                                    }
                                });
                            }
                            break;
                        }
                    }

                }
        }
        return true;
    }

    public void uploadImageandDat(RequestBody body, Context mContext){

        RetrofitInstance.getInstance(mContext).
               getRestAdapter()
                .uploadImageAndDat(body)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<Registration>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e(TAG, "onError: registration"+e.getMessage() );
                    }

                    @Override
                    public void onNext(Registration registration) {

                        Log.d(TAG, "onNext: registration"+registration.getData().getStatus());
                        Intent intent= new Intent(getContext(),ResetPinActivity.class);

                        getContext().startActivity(intent);
                    }
                });
    }

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
                if (r < 0) r = 0;
                else if (r > 262143) r = 262143;
                if (g < 0) g = 0;
                else if (g > 262143) g = 262143;
                if (b < 0) b = 0;
                else if (b > 262143) b = 262143;

                rgb[3 * yp] = (byte) ((r >> 10) & 0xff);
                rgb[3 * yp + 1] = (byte) ((g >> 10) & 0xff);
                rgb[3 * yp + 2] = (byte) ((b >> 10) & 0xff);
                ++yp;
            }
        }
    }

    static public void decodeYUV420SP1(int[] rgba, byte[] yuv420sp, int width, int height) {
        final int frameSize = width * height;

        for (int j = 0, yp = 0; j < height; j++) {
            int uvp = frameSize + (j >> 1) * width, u = 0, v = 0;
            for (int i = 0; i < width; i++, yp++) {
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

                if (r < 0) r = 0;
                else if (r > 262143) r = 262143;
                if (g < 0) g = 0;
                else if (g > 262143) g = 262143;
                if (b < 0) b = 0;
                else if (b > 262143) b = 262143;

                rgba[yp] = 0xff000000 | ((r << 6) & 0xff0000) | ((g >> 2) & 0xff00) | ((b >> 10) & 0xff);
            }

        }
    }
}

class Preview1 extends SurfaceView implements SurfaceHolder.Callback {
    Context mContext;
    SurfaceHolder mHolder;
    static Camera mCamera;
    ProcessImageAndDrawResults1 mDraw;
    static boolean mFinished;
    boolean mIsCameraOpen = false;
    int cameraId_front = 0;
    int cameraId_back = 0;
    boolean mIsPreviewStarted = false;

    Preview1(Context context, ProcessImageAndDrawResults1 draw) {
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
        if (mIsCameraOpen) return; // surfaceCreated can be called several times
        mIsCameraOpen = true;

        mFinished = false;

        // Find the ID of the camera
        int cameraId = 0;
        boolean frontCameraFound = false;
        Camera.CameraInfo cameraInfo = new Camera.CameraInfo();
        for (int i = 0; i < Camera.getNumberOfCameras(); i++) {
            Camera.getCameraInfo(i, cameraInfo);
            //if (cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_BACK)
            if (cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_BACK) {
                cameraId_back = i;
                backCameraFound = true;
            }
            if (cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
                cameraId_front = i;
                frontCameraFound = true;
            }
        }

        if (mySharedPreference.getPref(PrefKeys.CAM_CONFIG).equalsIgnoreCase("front")) {
            if (frontCameraFound) {
                //mCamera = Camera.open();
                mCamera = Camera.open(cameraId_front);
            }

        } else {
            if (backCameraFound) {
                mCamera = Camera.open(cameraId_back);
            }
        }
        try {
            mCamera.setPreviewDisplay(holder);

            // Preview callback used whenever new viewfinder frame is available
            mCamera.setPreviewCallback(new Camera.PreviewCallback() {
                public void onPreviewFrame(byte[] data, Camera camera) {
                    if ((mDraw == null) || mFinished)
                        return;

                    if (mDraw.mYUVData == null) {
                        // Initialize the draw-on-top companion
                        Camera.Parameters params = camera.getParameters();
                        mDraw.mImageWidth = params.getPreviewSize().width;
                        mDraw.mImageHeight = params.getPreviewSize().height;
                        mDraw.mRGBData = new byte[3 * mDraw.mImageWidth * mDraw.mImageHeight];
                        mDraw.mYUVData = new byte[data.length];
                    }

                    // Pass YUV Users to draw-on-top companion
                    System.arraycopy(data, 0, mDraw.mYUVData, 0, data.length);
                    mDraw.invalidate();
                }
            });
        } catch (Exception exception) {
            AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
            builder.setMessage("Cannot open camera")
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

        mIsCameraOpen = false;
        mIsPreviewStarted = false;
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
        for (Camera.Size s : supportedSizes) {
            if ((width - 800) * (width - 800) + (height - 480) * (height - 480) >
                    (s.width - 800) * (s.width - 800) + (s.height - 480) * (s.height - 480)) {
                width = s.width;
                height = s.height;
            }
        }

        //try to set preferred parameters
        try {
            if (width * height > 0) {
                parameters.setPreviewSize(width, height);
            }
            //parameters.setPreviewFrameRate(10);
            parameters.setSceneMode(Camera.Parameters.SCENE_MODE_PORTRAIT);
            parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_VIDEO);
            mCamera.setParameters(parameters);
        } catch (Exception ex) {
        }

        if (!mIsPreviewStarted) {
            mCamera.startPreview();
            mIsPreviewStarted = true;
        }

        parameters = mCamera.getParameters();
        Camera.Size previewSize = parameters.getPreviewSize();
        makeResizeForCameraAspect(1.0f / ((1.0f * previewSize.width) / previewSize.height));
    }

    private void makeResizeForCameraAspect(float cameraAspectRatio) {
        ViewGroup.LayoutParams layoutParams = this.getLayoutParams();
        int matchParentWidth = this.getWidth();
        int newHeight = (int) (matchParentWidth / cameraAspectRatio);
        if (newHeight != layoutParams.height) {
            layoutParams.height = newHeight;
            layoutParams.width = matchParentWidth;
            this.setLayoutParams(layoutParams);
            this.invalidate();
        }
    }
} // end of Preview class



