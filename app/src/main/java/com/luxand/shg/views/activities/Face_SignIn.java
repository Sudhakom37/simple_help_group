package com.luxand.shg.views.activities;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.hardware.Camera;
import android.os.Bundle;
import android.os.Environment;
import android.os.SystemClock;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.JsonObject;
import com.luxand.FSDK;
import com.luxand.shg.R;
import com.luxand.shg.api.RetrofitInstance;
import com.luxand.shg.helper.Helper;
import com.luxand.shg.helper.MySharedPreference;
import com.luxand.shg.helper.PrefKeys;
import com.luxand.shg.model.Login;
import com.luxand.shg.util.ProgressBarDialog;
import com.luxand.shg.views.activities.division_offcer.DivisionOfficerMainActivity;
import com.luxand.shg.views.activities.state_officer.StateOfficerMainActivity;
import com.luxand.shg.views.activities.village_officer.VillageOfficerMainActivity;

import java.io.File;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

import static com.luxand.shg.views.activities.Face_SignIn.backCameraFound;
import static com.luxand.shg.views.activities.Face_SignIn.confidenceEyesOpenPercent;
import static com.luxand.shg.views.activities.Face_SignIn.facenotrecognized;
import static com.luxand.shg.views.activities.Face_SignIn.faceservicecheck;
import static com.luxand.shg.views.activities.Face_SignIn.flag;
import static com.luxand.shg.views.activities.Face_SignIn.mySharedPreference;
import static com.luxand.shg.views.activities.Face_SignIn.named1;
import static com.luxand.shg.views.activities.Face_SignIn.oldeyes;
import static com.luxand.shg.views.activities.Face_SignIn.percent;
import static com.luxand.shg.views.activities.Face_SignIn.progressbar;
import static com.luxand.shg.views.activities.Face_SignIn.st_id;
import static com.luxand.shg.views.activities.Face_SignIn.st_name;
import static com.luxand.shg.views.activities.Face_SignIn.status;


public class Face_SignIn extends BaseActivity implements View.OnClickListener{

    private boolean mIsFailed = false;
    private Preview mPreview;
    private ProcessImageAndDrawResults mDraw;
    private String database = "";
    private boolean wasStopped = false;
    private FrameLayout mLayout;
    public static float sDensity = 1.0f;
    public static String st_name="",st_id,DATFILE="",IMAGES="";
    public static int confidenceEyesOpenPercent=0;
    public static int flag=0;
    public static int oldeyes,percent=60;
    public static boolean named1 = false;
    public static boolean facenotrecognized = false;
    public static Context mContext;
    public static boolean backCameraFound = false;
    public static MySharedPreference mySharedPreference;
    public static String mac_address;
    public static int faceservicecheck=0;
    public static ProgressBar progressbar;
    public static int status=0;
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
        FSDK.SetTrackerMultipleParameters(mDraw.mTracker, "RecognizeFaces=true;DetectFacialFeatures=true;ContinuousVideoFeed=true;RecognitionPrecision=1;Threshold=0.996;Threshold2=0.9995;ThresholdFeed=0.97;MemoryLimit=2000;HandleArbitraryRotations=false;DetermineFaceRotationAngle=false;InternalResizeWidth=70;FaceDetectionThreshold=3;DetectGender=false;DetectExpression=true", errpos);
        if (errpos[0] != 0) {
            showErrorAndClose("Error setting tracker parameters, position", errpos[0]);
        }
    }

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sDensity = getResources().getDisplayMetrics().scaledDensity;
        //imei= Helper.getInstance(FaceRecognition_Blink_Staff.this).getIMEI();
        mac_address= RetrofitInstance.getMacAddress();
        mySharedPreference=new MySharedPreference(getApplicationContext());
        database=mySharedPreference.getPref(PrefKeys.SNO)+".dat";
        DATFILE = Helper.DATFILE;
        IMAGES = Helper.IMAGES;

        int res = FSDK.ActivateLibrary(""+mySharedPreference.getPref(PrefKeys.FACEURL));
        if (res != FSDK.FSDKE_OK) {
            mIsFailed = true;
            finish();
            //showErrorAndClose("FaceSDK activation failed", res);
            startActivityBase(this,LoginActivity.class);
        } else {
            FSDK.Initialize();

            // Hide the window title (it is done in manifest too)
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
            //requestWindowFeature(Window.FEATURE_NO_TITLE);

            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);


            mLayout = new FrameLayout(this);
            ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            mLayout.setLayoutParams(params);
            setContentView(mLayout);

            openCamera();

        }
    }

    private void openCamera() {
        // Camera layer and drawing layer
        View background = new View(this);
        background.setBackgroundColor(Color.BLACK);
        mDraw = new ProcessImageAndDrawResults(this);
        mPreview = new Preview(this, mDraw);
        //mPreview.setBackgroundColor(Color.GREEN);
        //mDraw.setBackgroundColor(Color.RED);
        mDraw.mTracker = new FSDK.HTracker();
        File myDir= new File(Environment.getExternalStorageDirectory().getPath(), ""+DATFILE);
        //File myDir=new File(DATFILE);
        String templatePath =""+myDir + "/" + database; // databasepath
        if (FSDK.FSDKE_OK != FSDK.LoadTrackerMemoryFromFile(mDraw.mTracker, templatePath)) {
            int res = FSDK.CreateTracker(mDraw.mTracker);
            if (FSDK.FSDKE_OK != res) {
                showErrorAndClose("Error creating tracker", res);
            }
        }

        resetTrackerParameters();

        this.getWindow().setBackgroundDrawable(new ColorDrawable()); //black background

        mLayout.setVisibility(View.VISIBLE);
        addContentView(background, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        addContentView(mPreview, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)); //creates Login_Activity contents
        addContentView(mDraw, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));


        // Menu
        LayoutInflater inflater = (LayoutInflater)this.getSystemService( Context.LAYOUT_INFLATER_SERVICE );
        View buttons = inflater.inflate(R.layout.activity_face_signin, null );
        buttons.findViewById(R.id.submit).setOnClickListener(this);
        buttons.findViewById(R.id.login).setOnClickListener(this);
        buttons.findViewById(R.id.clearButton).setOnClickListener(this);
        progressbar = buttons.findViewById(R.id.progressbar);
        addContentView(buttons, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));

    }

    @Override
    public void onClick(@NonNull View view) {


        if (view.getId() == R.id.submit) {

        }if (view.getId() == R.id.login) {

            Intent intent = new Intent(Face_SignIn.this, LoginActivity.class);
            intent.putExtra(PrefKeys.VERIFY_MODE,"login");
            startActivity(intent);
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

    }

    @Override
    public void onResume() {
        super.onResume();
        if (mIsFailed)
            return;
        resumeProcessingFrames();
    }

    private void pauseProcessingFrames() {
        if (mDraw != null) {
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
    }

    private void resumeProcessingFrames() {
        if (mDraw != null) {
            mDraw.mStopped = 0;
            mDraw.mStopping = 0;
        }
    }
}

class FaceRectangle {
    public int x1, y1, x2, y2;
}

// Draw graphics on top of the video
class ProcessImageAndDrawResults extends View {
    public FSDK.HTracker mTracker;
    //public HTracker mTracker1;

    final int MAX_FACES = 5;
    final String[] mAttributeValues = new String[MAX_FACES];
    final FSDK.FSDK_Features[] mFacialFeatures = new FSDK.FSDK_Features[MAX_FACES];
    final FaceRectangle[] mFacePositions = new FaceRectangle[MAX_FACES];
    final long[] mIDs = new long[MAX_FACES];
    final Lock faceLock = new ReentrantLock();
    int mTouchedIndex;
    long mTouchedID;
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
        mPaintGreen.setColor(Color.TRANSPARENT);
        mPaintGreen.setTextSize(25 * Face_SignIn.sDensity);
        mPaintGreen.setTextAlign(Paint.Align.CENTER);

        mPaintBlue = new Paint();
        mPaintBlue.setStyle(Paint.Style.FILL);
        mPaintBlue.setColor(Color.TRANSPARENT);
        mPaintBlue.setTextSize(25 * Face_SignIn.sDensity);
        mPaintBlue.setTextAlign(Paint.Align.CENTER);

        mPaintBlueTransparent = new Paint();
        mPaintBlueTransparent.setStyle(Paint.Style.STROKE);
        mPaintBlueTransparent.setStrokeWidth(0);
        mPaintBlueTransparent.setColor(Color.TRANSPARENT);
        mPaintBlueTransparent.setTextSize(25* Face_SignIn.sDensity);

        mPaintGreenTransparent = new Paint();
        mPaintGreenTransparent.setStyle(Paint.Style.STROKE);
        mPaintGreenTransparent.setStrokeWidth(0);
        mPaintGreenTransparent.setColor(Color.TRANSPARENT);
        mPaintGreenTransparent.setTextSize(25* Face_SignIn.sDensity);

        //mBitmap = null;
        mYUVData = null;
        mRGBData = null;

        first_frame_saved = false;
    }


    @SuppressLint("DrawAllocation")
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
        int ImageWidth = mImageWidth;
        if (rotated) {
            ImageWidth = mImageHeight;
            if(mySharedPreference.getPref(PrefKeys.CAM_CONFIG).equalsIgnoreCase("front")){
                FSDK.RotateImage90(Image, -1, RotatedImage);
            }else{
                FSDK.RotateImage90(Image, 1, RotatedImage);
            }
        } else {
            FSDK.CopyImage(Image, RotatedImage);
        }
        FSDK.FreeImage(Image);
        float ratio = (canvasWidth * 1.0f) / ImageWidth;
        if (!first_frame_saved) {
            first_frame_saved = true;
            if(!mySharedPreference.getPref(PrefKeys.CAM_CONFIG).equalsIgnoreCase("front")) {
                FSDK.SaveImageToFile(RotatedImage, "/sdcard/back_cam.jpg");
            }else {
                FSDK.SaveImageToFile(RotatedImage, "/sdcard/front_cam.jpg");
            }
        }
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
            }}

        faceLock.unlock();
        int shift = (int)(22 * Face_SignIn.sDensity);
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

                    //canvas.drawText(" Slowly Blink your EYES ", (mFacePositions[i].x1+mFacePositions[i].x2)/2, mFacePositions[i].y2+shift, mPaintGreen);
                    named = true;
                    named1=true;
                    st_name=names[0];

                    Drawable bgDrawable = progressbar.getProgressDrawable();
                    bgDrawable.setColorFilter(getResources().getColor(R.color.green), android.graphics.PorterDuff.Mode.MULTIPLY);
                    progressbar.setProgressDrawable(bgDrawable);

                }}
            if (!named) {
                named1=false;
                facenotrecognized=true;
            }
        }
        if(named1){
            named1=false;
            JsonObject object=new JsonObject();
            object.addProperty("pin","");
            object.addProperty("type","face");
            object.addProperty("mac_address",""+ RetrofitInstance.getMacAddress());

            if(faceservicecheck==0){
                faceservicecheck=1;
                login(object);
            }

            //Toast.makeText(mContext, "Present", Toast.LENGTH_SHORT).show();
        }else{
            if(facenotrecognized&&!named1) {
                facenotrecognized=false;
                Drawable bgDrawable = progressbar.getProgressDrawable();
                bgDrawable.setColorFilter(Color.RED, android.graphics.PorterDuff.Mode.MULTIPLY);
                progressbar.setProgressDrawable(bgDrawable);
                //Helper.getInstance(mContext).ToastMsg_Error(getResources().getString(R.string.facenotvalid));
            }else{
                Drawable bgDrawable = progressbar.getProgressDrawable();
                bgDrawable.setColorFilter(Color.LTGRAY, android.graphics.PorterDuff.Mode.MULTIPLY);
                progressbar.setProgressDrawable(bgDrawable);
            }
        }
        super.onDraw(canvas);
    }
    // end onDraw method
    public void login(JsonObject object){


        ProgressBarDialog.showLoadingDialog(mContext);
        RetrofitInstance.getInstance(mContext).
                getRestAdapter()
                .loginWithPin(object)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<Login>() {
                    @Override
                    public void onCompleted() {
                        faceservicecheck=0;
                        named1=false;
                        facenotrecognized=false;
                    }

                    @Override
                    public void onError(Throwable e) {
                        ProgressBarDialog.cancelLoading();
                        String msg=e.getMessage();
                        Log.d("error",msg);
                    }

                    @Override
                    public void onNext(Login login) {
                        ProgressBarDialog.cancelLoading();
                        String group_id=String.valueOf(login.getData().getGroupId());
                        String group_name=""+login.getData().getGroupName();
                        String user_image=""+login.getData().getImage();
                        String role_id = String.valueOf(login.getData().getRoleId());
                        mySharedPreference.setPref(PrefKeys.GROUP_ID,group_id);
                        mySharedPreference.setPref(PrefKeys.GROUP_NAME,group_name);
                        mySharedPreference.setPref(PrefKeys.USER_IMAGE,user_image);
                        mySharedPreference.setPref(PrefKeys.USER_ID,String.valueOf(login.getData().getUserId()));
                        mySharedPreference.setPref(PrefKeys.ROLE_ID,String.valueOf(login.getData().getUserId()));
                        if(role_id.equalsIgnoreCase("4")){
                            if(status==0) {
                                status=1;
                                ((BaseActivity)mContext).startActivityBase(mContext, MainActivity.class);
                                ((AppCompatActivity) mContext).finish();
                            }
                        }else if(role_id.equalsIgnoreCase("3")){
                            if(status==0) {
                                status = 1;
                                ((BaseActivity)mContext).startActivityBase(mContext, VillageOfficerMainActivity.class);
                                ((AppCompatActivity) mContext).finish();
                            }

                        }else if(role_id.equalsIgnoreCase("6")){
                            if(status==0) {
                                status = 1;
                                ((BaseActivity)mContext).startActivityBase(mContext, DivisionOfficerMainActivity.class);
                                ((AppCompatActivity) mContext).finish();
                            }
                        }else{
                            ((BaseActivity)mContext).startActivityBase(mContext, StateOfficerMainActivity.class);
                            ((AppCompatActivity) mContext).finish();
                        }
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
} // end of ProcessImageAndDrawResults class


// Show video from camera and pass frames to ProcessImageAndDraw class
class Preview extends SurfaceView implements SurfaceHolder.Callback {
    Context mContext;
    SurfaceHolder mHolder;
    Camera mCamera;
    ProcessImageAndDrawResults mDraw;
    boolean mFinished;
    boolean mIsCameraOpen = false;
    int cameraId_front = 0;
    int cameraId_back = 0;
    boolean mIsPreviewStarted = false;

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
                //mCamera = Camera.open();
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

                    // Pass YUV Users to draw-on-top companion
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
        for (Camera.Size s: supportedSizes) {
            if ((width - 800)*(width - 800) + (height - 480)*(height - 480) >
                    (s.width - 800)*(s.width - 800) + (s.height - 480)*(s.height - 480)) {
                width = s.width;
                height = s.height;
            }
        }

        //try to set preferred parameters
        try {
            if (width*height > 0) {
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