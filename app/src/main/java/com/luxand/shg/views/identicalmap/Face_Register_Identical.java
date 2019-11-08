package com.luxand.shg.views.identicalmap;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.ColorDrawable;
import android.hardware.Camera;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.luxand.FSDK;
import com.luxand.shg.R;
import com.luxand.shg.views.activities.BaseActivity;
import com.luxand.shg.api.RetrofitInstance;
import com.luxand.shg.db.Student_EnroolList_Local;
import com.luxand.shg.db.Student_EnroolList_Local_Table;
import com.luxand.shg.helper.Helper;
import com.luxand.shg.helper.MySharedPreference;
import com.luxand.shg.helper.PrefKeys;
import com.luxand.shg.model.SuccessModel;
import com.luxand.shg.util.CircleTransform;
import com.luxand.shg.util.ProgressBarDialog;
import com.raizlabs.android.dbflow.sql.language.SQLite;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.Picasso;


import java.io.File;
import java.io.FileOutputStream;
import java.util.List;
import java.util.Random;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

import static com.luxand.shg.views.identicalmap.Face_Register_Identical.DATFILE;
import static com.luxand.shg.views.identicalmap.Face_Register_Identical.IMAGES;
import static com.luxand.shg.views.identicalmap.Face_Register_Identical.already_face_exit;
import static com.luxand.shg.views.identicalmap.Face_Register_Identical.backCameraFound;
import static com.luxand.shg.views.identicalmap.Face_Register_Identical.canvasHeight;
import static com.luxand.shg.views.identicalmap.Face_Register_Identical.confidenceEyesOpenPercent;
import static com.luxand.shg.views.identicalmap.Face_Register_Identical.counttime;
import static com.luxand.shg.views.identicalmap.Face_Register_Identical.database;
import static com.luxand.shg.views.identicalmap.Face_Register_Identical.detected;
import static com.luxand.shg.views.identicalmap.Face_Register_Identical.filePath;
import static com.luxand.shg.views.identicalmap.Face_Register_Identical.flag;
import static com.luxand.shg.views.identicalmap.Face_Register_Identical.flag_outofbox;
import static com.luxand.shg.views.identicalmap.Face_Register_Identical.flag_toast;
import static com.luxand.shg.views.identicalmap.Face_Register_Identical.frontCameraFound;
import static com.luxand.shg.views.identicalmap.Face_Register_Identical.mCountDownTimer;
import static com.luxand.shg.views.identicalmap.Face_Register_Identical.mDraw;
import static com.luxand.shg.views.identicalmap.Face_Register_Identical.mySharedPreference;
import static com.luxand.shg.views.identicalmap.Face_Register_Identical.named1;
import static com.luxand.shg.views.identicalmap.Face_Register_Identical.progressbar;
import static com.luxand.shg.views.identicalmap.Face_Register_Identical.squarelength;
import static com.luxand.shg.views.identicalmap.Face_Register_Identical.st_id;
import static com.luxand.shg.views.identicalmap.Face_Register_Identical.st_name;
import static com.luxand.shg.views.identicalmap.Face_Register_Identical.tap;
import static com.luxand.shg.views.identicalmap.Face_Register_Identical.timer_updatecompl;
import static com.luxand.shg.views.identicalmap.Face_Register_Identical.type;

public class Face_Register_Identical extends BaseActivity implements View.OnClickListener {

    private boolean mIsFailed = false;
    private Preview1 mPreview;
    public static ProcessImageAndDrawResults1 mDraw;
    public static float sDensity = 1.0f;
    public static String FROM,database = "",st_name="",st_id,path="",compresspath="",DATFILE="",IMAGES="",filePath_twin_img,st_image_status,st_MatchStatus,filePath,APPLICATION_ID = "com.luxand.iams_schools_v4_single";
    public static int canvasHeight,flag=0,flag_outofbox=1,flag_toast=0,counttime=0,confidenceEyesOpenPercent=0;
    public static boolean named1 = false,detected=false,frontCameraFound = false,backCameraFound = false,already_face_exit = false,timer_updatecompl = false;
    public static int tap = 0;
    public static MySharedPreference mySharedPreference;
    public static double squarelength;
    public static ProgressBar progressbar;
    public static CountDownTimer mCountDownTimer = null;
    public static CircleImageView imgg;
    public static TextView ok,submit;
    public static String type;

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

    /*
     * Reset Tracker Parameters
     *
     * */
    private void resetTrackerParameters() {
        int errpos[] = new int[1];
        FSDK.SetTrackerMultipleParameters(mDraw.mTracker, "RecognizeFaces=true;DetectFacialFeatures=true;ContinuousVideoFeed=true;RecognitionPrecision=1;Threshold=0.996;Threshold2=0.9995;ThresholdFeed=0.97;MemoryLimit=1000;HandleArbitraryRotations=false;DetermineFaceRotationAngle=false;InternalResizeWidth=70;FaceDetectionThreshold=3;DetectGender=false;DetectExpression=true", errpos);
        if (errpos[0] != 0) {
            showErrorAndClose("Error setting tracker parameters, position", errpos[0]);
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mySharedPreference=new MySharedPreference(getApplicationContext());
        sDensity = getResources().getDisplayMetrics().scaledDensity;
        DATFILE= Helper.DATFILE+"/"+Helper.GROUP_DATFILE+"/"+mySharedPreference.getPref(PrefKeys.GROUP_ID);
        IMAGES= Helper.IMAGES+"/"+Helper.GROUP_IMAGES+"/"+mySharedPreference.getPref(PrefKeys.GROUP_ID);
        database=mySharedPreference.getPref(PrefKeys.GROUP_ID)+".dat";

        type=getIntent().getStringExtra("type");

        int res = FSDK.ActivateLibrary(""+mySharedPreference.getPref(PrefKeys.FACEURL));

        if (res != FSDK.FSDKE_OK) {
            mIsFailed = true;
            showErrorAndClose("FaceSDK activation failed", res);
        } else {
            FSDK.Initialize();

            // Hide the window title (it is done in manifest too)
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
            //requestWindowFeature(Window.FEATURE_NO_TITLE);

            // Lock orientation
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

            // Camera layer and drawing layer
            mDraw = new ProcessImageAndDrawResults1(this);
            mPreview = new Preview1(this, mDraw);
            mDraw.mTracker = new FSDK.HTracker();

            File myDir=new File(DATFILE);
            myDir.mkdirs();
            String templatePath = ""+myDir + "/" + database;

            if (FSDK.FSDKE_OK != FSDK.LoadTrackerMemoryFromFile(mDraw.mTracker, templatePath)) {
                res = FSDK.CreateTracker(mDraw.mTracker);
                if (FSDK.FSDKE_OK != res) {
                    showErrorAndClose("Error creating tracker", res);
                }
            }
            resetTrackerParameters();
            this.getWindow().setBackgroundDrawable(new ColorDrawable()); //black background
            setContentView(mPreview);
            addContentView(mDraw, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));

            // Menu
            LayoutInflater inflater = (LayoutInflater)this.getSystemService( Context.LAYOUT_INFLATER_SERVICE );
            View buttons = inflater.inflate(R.layout.activity_face_enrollment, null );
            buttons.findViewById(R.id.clearButton).setOnClickListener(this);
            buttons.setVisibility(View.VISIBLE);
            progressbar =buttons.findViewById(R.id.progressbar);
            addContentView(buttons, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));

        }
    }

    @Override
    public void onClick(View view) {

        if (view.getId() == R.id.clearButton) {
            already_face_exit=false;
            finish();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        pauseProcessingFrames();
    }

    @Override
    protected void onStop() {
        super.onStop();
        counttime=0;
        timer_updatecompl=false;
        if(mCountDownTimer!=null){
            mCountDownTimer.cancel();
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

}


class FaceRectangle1 {
    public int x1, y1, x2, y2;
}

// Draw graphics on top of the video
class ProcessImageAndDrawResults1 extends View {
    public FSDK.HTracker mTracker;
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
    Paint mPaintGreen, mPaintBlue, mPaintBlueTransparent, mPaintGreenTransparent,mPaintWhiteTransparent;
    byte[] mYUVData;
    byte[] mRGBData;
    int mImageWidth, mImageHeight;
    boolean first_frame_saved;
    boolean rotated;

    int GetFaceFrame(FSDK.FSDK_Features Features, FaceRectangle1 fr)
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
        mPaintGreen.setColor(Color.GREEN);
        mPaintGreen.setTextSize(25 * Face_Register_Identical.sDensity);
        mPaintGreen.setTextAlign(Paint.Align.CENTER);

        mPaintBlue = new Paint();
        mPaintBlue.setStyle(Paint.Style.FILL);
        mPaintBlue.setColor(Color.BLUE);
        mPaintBlue.setStrokeWidth(5);
        mPaintBlue.setTextSize(25 * Face_Register_Identical.sDensity);
        mPaintBlue.setTextAlign(Paint.Align.CENTER);

        mPaintBlueTransparent = new Paint();
        mPaintBlueTransparent.setStyle(Paint.Style.STROKE);
        mPaintBlueTransparent.setStrokeWidth(5);
        mPaintBlueTransparent.setColor(Color.BLUE);
        mPaintBlueTransparent.setTextSize(25 * Face_Register_Identical.sDensity);

        mPaintGreenTransparent = new Paint();
        mPaintGreenTransparent.setStyle(Paint.Style.STROKE);
        mPaintGreenTransparent.setStrokeWidth(8);
        mPaintGreenTransparent.setColor(Color.GREEN);
        mPaintGreenTransparent.setTextSize(25 * Face_Register_Identical.sDensity);

        mPaintWhiteTransparent = new Paint();
        mPaintWhiteTransparent.setStyle(Paint.Style.STROKE);
        mPaintWhiteTransparent.setStrokeWidth(7);
        mPaintWhiteTransparent.setColor(Color.WHITE);
        mPaintWhiteTransparent.setTextSize(40);

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
        canvasHeight = canvas.getHeight();

        // Convert from YUV to RGB
        decodeYUV420SP(mRGBData, mYUVData, mImageWidth, mImageHeight);

        // Load image to FaceSDK
        FSDK.HImage Image = new FSDK.HImage();
        FSDK.FSDK_IMAGEMODE imagemode = new FSDK.FSDK_IMAGEMODE();
        imagemode.mode = FSDK.FSDK_IMAGEMODE.FSDK_IMAGE_COLOR_24BIT;
        FSDK.LoadImageFromBuffer(Image, mRGBData, mImageWidth, mImageHeight, mImageWidth*3, imagemode);

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
        if (rotated) {
            ImageWidth = mImageHeight;
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
            mFacePositions[i] = new FaceRectangle1();
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
            if (flag == 0 && confidenceEyesOpenPercent > 75) {
                flag = 1;
            }
            if (flag == 1 && confidenceEyesOpenPercent < 25) {
                flag = 2;
            }
            if (flag == 2 && confidenceEyesOpenPercent > 75) {
                flag = 3;
            }
        }
        faceLock.unlock();
        int shift = (int)(22 * Face_Register_Identical.sDensity);

        // Mark and name faces
        for (int i=0; i<face_count[0]; ++i) {
            squarelength = ( mFacePositions[i].x2-mFacePositions[i].x1);
            flag_toast=0;
            /*
             * Countdown Timer
             * */
            progressbar.setProgress(counttime);
            if(mCountDownTimer!=null){
                mCountDownTimer.cancel();
            }
            mCountDownTimer=new CountDownTimer(2000,1000) {

                @Override
                public void onTick(long millisUntilFinished) {
                    counttime++;
                    int progress = (int) (millisUntilFinished/1000);
                    progressbar.setProgress(progressbar.getMax()-progress);
                    if(counttime==2){
                        timer_updatecompl=true;
                    }
                }

                @Override
                public void onFinish() {
                    timer_updatecompl=true;

                }
            };
            mCountDownTimer.start();
            canvas.drawRect(mFacePositions[i].x1, mFacePositions[i].y1, mFacePositions[i].x2, mFacePositions[i].y2, mPaintBlueTransparent);
            flag_outofbox=0;

            boolean named = false;
            if (IDs[i] != -1) {
                String names[] = new String[1];
                st_id=""+IDs[i];
                FSDK.GetAllNames(mTracker, IDs[i], names, 1024);
                if (names[0] != null && names[0].length() > 0) {
                    named = true;
                    named1=true;
                    st_name=names[0];
                }
            }

            if (!named) {

                if(timer_updatecompl) {
                    canvas.drawText("Tap to enroll", (mFacePositions[i].x1 + mFacePositions[i].x2) / 2, mFacePositions[i].y2 + shift, mPaintGreen);
                }
                named1 = false;
                detected = false;

            }else{
                if (timer_updatecompl){
                    canvas.drawText("Tap to enroll", (mFacePositions[i].x1 + mFacePositions[i].x2) / 2, mFacePositions[i].y2 + shift, mPaintGreen);
                    timer_updatecompl=true;
                    named1=false;
                }else{
                    named1=true;
                    timer_updatecompl=false;
                }

            }

        }
        if(flag==3){
            if (named1) {
                flag=4;

            }}
        super.onDraw(canvas);
    } // end onDraw method


    @Override
    public boolean onTouchEvent(MotionEvent event) { //NOTE: the method can be implemented in Preview class
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:

                if(timer_updatecompl) {

                    int x = (int)event.getX();
                    int y = (int)event.getY();

                    faceLock.lock();
                    FaceRectangle1 rects[] = new FaceRectangle1[MAX_FACES];
                    long IDs[] = new long[MAX_FACES];
                    for (int i=0; i<MAX_FACES; ++i) {
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
                    flag_outofbox=1;

                    for (int i=0; i<MAX_FACES; ++i) {
                        if (rects[i] != null && rects[i].x1 <= x && x <= rects[i].x2 && rects[i].y1 <= y && y <= rects[i].y2 + 30) {
                            mTouchedID = IDs[i];

                            mTouchedIndex = i;

                            // requesting name on tapping the face
                            final EditText input = new EditText(mContext);
                            input.setVisibility(GONE);

                            final MySharedPreference mySharedPreference=new MySharedPreference(mContext);
                            final String st_id=""+mySharedPreference.getPref(PrefKeys.UUID);
                            input.setText(""+st_id);

                            int[] mIntArray = new int[mImageWidth*mImageHeight];
                            decodeYUV420SP1(mIntArray, mYUVData, mImageWidth, mImageHeight);
                            Bitmap bmp = Bitmap.createBitmap(mIntArray, mImageWidth, mImageHeight, Bitmap.Config.ARGB_8888);
                            File myDir=new File(IMAGES);
                            myDir.mkdirs();

                            Random generator = new Random();
                            int n = 10000;
                            n = generator.nextInt(n);
                            String fname =""+st_id+".jpg";
                            final String st_path=myDir+""+st_id+".jpg";
                            final File file = new File(myDir, fname);
                            if (file.exists ()) file.delete ();
                            try {
                                FileOutputStream out = new FileOutputStream(file);
                                bmp.compress(Bitmap.CompressFormat.JPEG, 90, out);
                                out.flush();
                                out.close();

                            } catch (Exception e) {
                                e.printStackTrace();
                            }


                            if(tap==0){

                                counttime=0;
                                timer_updatecompl=false;
                                try {
                                    if(mCountDownTimer!=null){
                                        mCountDownTimer.cancel();
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                                already_face_exit=false;
                                progressbar.setProgress(counttime);

                                final Dialog d=new Dialog(mContext);
                                try {
                                    d.setContentView(R.layout.face_detect_register_dilog);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                                TextView id=(TextView)d.findViewById(R.id.id);
                                TextView ok=(TextView)d.findViewById(R.id.ok);
                                TextView cancel=(TextView)d.findViewById(R.id.cancel);
                                ImageView imgg = d.findViewById(R.id.imgg);
                                String st_uuid=""+mySharedPreference.getPref(PrefKeys.UUID);
                                id.setText(st_uuid);
                                d.setCanceledOnTouchOutside(false);

                                try {
                                    d.show();
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }

                                File myDir1=new File(IMAGES);
                                final String templatePath =""+myDir1 + "/" +st_uuid+".jpg";
                                Log.d("templatePath",""+templatePath);

                                if(mySharedPreference.getPref(PrefKeys.CAM_CONFIG).equalsIgnoreCase("front")){
                                    Picasso.with(mContext)
                                            .load(new File(templatePath))
                                            .fit()
                                            .transform(new CircleTransform())
                                            .memoryPolicy(MemoryPolicy.NO_CACHE)
                                            .rotate(270f)
                                            .into(imgg);
                                }else{
                                    Picasso.with(mContext)
                                            .load(new File(templatePath))
                                            .fit()
                                            .transform(new CircleTransform())
                                            .memoryPolicy(MemoryPolicy.NO_CACHE)
                                            .rotate(90f)
                                            .into(imgg);
                                }


                                cancel.setOnClickListener(new OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        d.cancel();
                                        mTouchedIndex = -1;
                                        tap=0;
                                        timer_updatecompl=false;
                                    }
                                });

                                ok.setOnClickListener(new OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        FSDK.LockID(mTracker, mTouchedID);
                                        String userName = ""+mySharedPreference.getPref(PrefKeys.UUID);
                                        input.setText(""+userName);
                                        FSDK.SetName(mTracker, mTouchedID, userName);
                                        if (userName.length() <= 0) FSDK.PurgeID(mTracker, mTouchedID);
                                        FSDK.UnlockID(mTracker, mTouchedID);

                                        File myDir_file=new File(DATFILE);
                                        myDir_file.mkdirs();
                                        filePath =""+myDir_file + "/" + database;
                                        FSDK.SaveTrackerMemoryToFile(mDraw.mTracker, filePath);

                                        d.cancel();


                                        if(type.equalsIgnoreCase("normal")){
                                            SQLite.update(Student_EnroolList_Local.class)
                                                    .set(Student_EnroolList_Local_Table.isEnrolled.eq("1"),
                                                            Student_EnroolList_Local_Table.isEnrolledLocal.eq("1"),
                                                            Student_EnroolList_Local_Table.image.eq(templatePath),
                                                            Student_EnroolList_Local_Table.studentdatfile.eq(filePath),
                                                            Student_EnroolList_Local_Table.camera_config.eq(mySharedPreference.getPref(PrefKeys.CAM_CONFIG)))
                                                    .where(Student_EnroolList_Local_Table.uuid.is(mySharedPreference.getPref(PrefKeys.UUID)))
                                                    .and(Student_EnroolList_Local_Table.group_id.is(mySharedPreference.getPref(PrefKeys.GROUP_ID)))
                                                    .async()
                                                    .execute();

                                            ((AppCompatActivity)mContext).finish();


                                        }else{

                                            MultipartBody.Builder builder = new MultipartBody.Builder();
                                            builder.setType(MultipartBody.FORM);
                                            builder.addFormDataPart("group_id",""+mySharedPreference.getPref(PrefKeys.GROUP_ID));
                                            builder.addFormDataPart("match_uuid",""+mySharedPreference.getPref(PrefKeys.MAPPING_ID));
                                            builder.addFormDataPart("dup_uuid",""+userName);
                                            builder.addFormDataPart("camera_config",""+mySharedPreference.getPref(PrefKeys.CAM_CONFIG));
                                            builder.addFormDataPart("mac_address",""+ RetrofitInstance.getMacAddress());
                                            File image = new File(templatePath);
                                            File datfile = new File(filePath);
                                            File datfile_students = new File(Helper.DATFILE + "/" + Helper.GROUP_DATFILE + "/" + mySharedPreference.getPref(PrefKeys.GROUP_ID)+ "/" + mySharedPreference.getPref(PrefKeys.GROUP_ID) + ".dat");
                                            builder.addFormDataPart("imagefile", image.getName(),
                                                    RequestBody.create(okhttp3.MediaType.parse("image/*"), image));

                                            builder.addFormDataPart("datfile", datfile.getName(),
                                                    RequestBody.create(okhttp3.MediaType.parse("file/*"), datfile));
                                            MultipartBody requestBody = builder.build();
                                            uploadImageandDat(requestBody,mContext);
                                        }
                                    }
                                });

                            }
                            break;
                        }
                    }}
        }
        return true;
    }

    public void uploadImageandDat(RequestBody body, Context mContext){
        ProgressBarDialog.showLoadingDialog(mContext);
        RetrofitInstance.getInstance(mContext).
                getRestAdapter()
                .duplicateMap(body)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<SuccessModel>() {
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
                    public void onNext(SuccessModel successModel) {
                        ProgressBarDialog.cancelLoading();

                        Toast.makeText(mContext, ""+successModel.getMsg(), Toast.LENGTH_SHORT).show();
                        ((AppCompatActivity) mContext).finish();
                        if(successModel.getStatus()) {

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

    static public void decodeYUV420SP1(int[] rgba, byte[] yuv420sp, int width,int height) {
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

                if (r < 0) r = 0; else if (r > 262143) r = 262143;
                if (g < 0) g = 0; else if (g > 262143) g = 262143;
                if (b < 0) b = 0; else if (b > 262143) b = 262143;

                rgba[yp] = 0xff000000 | ((r << 6) & 0xff0000) | ((g >> 2) & 0xff00) | ((b >> 10) & 0xff);
            }

        }
    }
}

// end of ProcessImageAndDrawResults class

// Show video from camera and pass frames to ProcessImageAndDraw class
class Preview1 extends SurfaceView implements SurfaceHolder.Callback {
    Context mContext;
    SurfaceHolder mHolder;
    static Camera mCamera;
    ProcessImageAndDrawResults1 mDraw;
    static boolean mFinished;
    int cameraId_front = 0;
    int cameraId_back = 0;


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
        mFinished = false;

        // Find the ID of the camera

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
