package com.android.camera.actor;

import android.content.ActivityNotFoundException;
import android.hardware.Camera.PictureCallback;
import android.hardware.Camera.PreviewCallback;
import android.os.Handler;
import android.os.Message;

import com.android.camera.Camera;
import com.android.camera.Log;
import com.android.camera.SaveRequest;
import com.android.camera.actor.PhotoActor.CameraCategory;
import com.android.camera.manager.MMProfileManager;
import com.android.camera.manager.ModePicker;
import com.android.camera.manager.ShutterManager;
import com.android.camera.ui.ShutterButton.OnShutterButtonListener;
import com.mediatek.camera.ext.ExtensionHelper;
import com.mediatek.camera.ext.IFeatureExtension;
import android.graphics.ImageFormat;
import com.android.camera.ui.ShutterButton;
import com.android.camera.SettingChecker;
import com.android.camera.R;


/**
 * gangyun tech
 */
public class GYFaceArtActor extends PhotoActor {
    private static final String TAG = "GYFaceArtActor";

    private static final boolean LOG = Log.LOGV;
    private Handler mFaceArtHandler;
    private static final int MSG_RESET_BEAUTY = 0;
    private static final int MSG_SET_BEAUTY = 1;
    private static int isInGyFaceArtActor = 0;
  //  private final AutoFocusMoveCallback mAutoFocusMoveCallback = new AutoFocusMoveCallback();
    
    private static boolean isHaveExposureText = false; //是否显示美肤级别 
    private boolean isInPopupSetting = false;

    public GYFaceArtActor(Camera context) {
        super(context);
        if (LOG) {
            Log.i(TAG, "GYFaceArtActor initialize");
        }
        isInGyFaceArtActor = 1;
	mFaceArtHandler	= new Handler(){
	@Override
       	public void handleMessage(Message msg){
	    if (msg.what == MSG_RESET_BEAUTY){
               //restartPreview(true);              
                initBeautifySkinLevelBar2();
                  
	    }else if (msg.what == MSG_SET_BEAUTY){
	 	startGYBeauty(true, mSmoothValue, mWhiteningValue);
	    }
       	  }
	};
        mCameraCategory = new FaceArtCameraCategory();
    }

    @Override
    public int getMode() {
        return ModePicker.MODE_FACEART;
    }
    
    @Override
    public void release() {
        super.release();

        startGYBeauty(false, mSmoothValue, mWhiteningValue);
        mCameraCategory.doCancelCapture();
	mCamera.removeOnFullScreenChangedListener(mFullScreenChangedListener);
    }

    @Override
    public void onCameraOpenDone() {
        super.onCameraOpenDone();
    }

    @Override
    public void onCameraParameterReady(boolean startPreview) {
        super.onCameraParameterReady(startPreview);
        if (LOG) {
            Log.i(TAG, "FaceArtActor onCameraParameterReady");
        }
    }

    @Override
    public OnShutterButtonListener getPhotoShutterButtonListener() {
        return this;

    }
 
    private Camera.OnFullScreenChangedListener mFullScreenChangedListener = new Camera.OnFullScreenChangedListener() {

        @Override
        public void onFullScreenChanged(boolean full) {
             if (LOG) {
               Log.i(TAG, "onFullScreenChanged isInPopupSetting="+isInPopupSetting);
            }
            if (full && getMode() == ModePicker.MODE_FACEART ) {
	         initBeautifySkinLevelBar2();
            }
        }
    };
    
    @Override
    protected void restartPreview(boolean needStop) {
        startPreview(needStop);
        mCamera.setCameraState(Camera.STATE_IDLE);
        mCamera.restoreViewState();
        mCamera.setSwipingEnabled(true);
		startFaceDetection();
    }
	
    @Override
    protected void onPreviewStartDone(){
	    super.onPreviewStartDone();
	    mFaceArtHandler.sendEmptyMessageDelayed(MSG_RESET_BEAUTY,  700);
    }

    @Override
    public void onShutterButtonLongPressed(ShutterButton button) {
        mCamera.showInfo(mCamera.getString(R.string.gy_faceart_guide_capture) +
                mCamera.getString(R.string.camera_continuous_not_supported));
    
   }

    public void onViewManagerEnable(boolean enabled) {   
        Log.i(TAG, "FaceArtActor onViewManagerEnable1");
        if(isInGyFaceArtActor==1)
        {
          mCamera.findViewById(R.id.gy_beatifySeekBar).setEnabled(enabled);
          mCamera.findViewById(R.id.gy_beatifySeekBar).setClickable(enabled);
	}
     } 
	
     public void onGangyunViewManagerHide(boolean visible ,boolean isSetting){     //   
           Log.i(TAG, "FaceArtActor onGangyunViewManagerHide "+visible);  
           isInPopupSetting = isSetting;
           if (visible){
		if(mSeekBar!=null ){
			mCamera.findViewById(R.id.gy_beatifySeekBar).setVisibility(android.view.View.VISIBLE); 
		}   

		if(isHaveExposureText){
			mCamera.findViewById(R.id.gy_exposureTextView).setVisibility(android.view.View.VISIBLE);
		}	       
	  }else{
	      mCamera.findViewById(R.id.gy_beatifySeekBar).setVisibility(android.view.View.INVISIBLE);
              if(isHaveExposureText){
                 mCamera.findViewById(R.id.gy_exposureTextView).setVisibility(android.view.View.GONE);
              }	    
	 }  
     }

    private android.widget.SeekBar mSeekBar;
    private android.widget.SeekBar mSeekBar2;
    private static int mSmoothValue = -1;
    private static int mWhiteningValue = 0;
    private int  mWhitening = 3;

   private void initBeautifySkinLevelBar2(){
    
            if (LOG) {
               Log.i(TAG, "initBeautifySkinLevelBar ");
            }

   	 if (mCamera.findViewById(R.id.gy_rl_leveltip) != null && isHaveExposureText){
		((com.android.camera.ui.Rotatable)mCamera.findViewById(R.id.gy_rl_leveltip)).setOrientation(90, true);
 	 }
	 if (mCamera.findViewById(R.id.gy_rl_levelseek) != null){
		((com.android.camera.ui.Rotatable)mCamera.findViewById(R.id.gy_rl_levelseek)).setOrientation(180, true);
   	 }


   	 mSeekBar = (android.widget.SeekBar)mCamera.findViewById(R.id.gy_beatifySeekBar);
        
        int seekbarmax = 160;
        if (mSmoothValue == -1){
       	    mSmoothValue = seekbarmax/2;
	    mWhiteningValue = mSmoothValue / mWhitening;
        }

        mSeekBar.setMax(seekbarmax);
        mSeekBar.setProgress(mSmoothValue);
        mSeekBar.setOnSeekBarChangeListener(new android.widget.SeekBar.OnSeekBarChangeListener(){

		@Override
		public void onProgressChanged(android.widget.SeekBar seekBar, int progress, boolean fromUser) {
			// TODO Auto-generated method stub
			if (!mCameraClosed && null != mCamera.getCameraDevice())
			{
			       	mCamera.getCameraDevice().setGYBeautySmooth(progress);
				mSmoothValue = progress;
				
				mCamera.getCameraDevice().setGYBeautyWhitening(progress/mWhitening);
				mWhiteningValue = progress/mWhitening;
			}
		}
	
		@Override
		public void onStartTrackingTouch(android.widget.SeekBar arg0) {
			// TODO Auto-generated method stub
		
		}

		@Override
		public void onStopTrackingTouch(android.widget.SeekBar arg0) {
			// TODO Auto-generated method stub
			
	}});
        
         if(!isInPopupSetting){
	    mSeekBar.setVisibility(android.view.View.VISIBLE);
	    mSeekBar.postInvalidate();
        } 
        if(isHaveExposureText){
            mCamera.findViewById(R.id.gy_exposureTextView).setVisibility(android.view.View.VISIBLE);
        }	
	startGYBeauty(true, mSmoothValue, mWhiteningValue);
    }

    
    class FaceArtCameraCategory extends CameraCategory {
        public void initializeFirstTime() {
            mCamera.showInfo(mCamera.getString(R.string.gy_faceart_guide_capture), Camera.SHOW_INFO_LENGTH_LONG);
	    mCamera.addOnFullScreenChangedListener(mFullScreenChangedListener);
	    mFaceArtHandler.sendEmptyMessageDelayed(MSG_RESET_BEAUTY,  500);
        }

        @Override
        public boolean supportContinuousShot() {
            return false;
        }

        public boolean applySpecialCapture() {
            return false;
        }

        @Override
        public void animateCapture(Camera camera) { }

        @Override
        public void doOnPictureTaken() {
            if (LOG) {
                Log.v(TAG, "GYFaceArtActor.doOnPictureTaken");
            }
             super.animateCapture(mCamera);
	        mFaceArtHandler.sendEmptyMessageDelayed(MSG_SET_BEAUTY,  400);
       }
        
        @Override
        public boolean doCancelCapture() {
            if (LOG) {
                Log.v(TAG, "mCamera.getCameraDevice()=" + mCamera.getCameraDevice());
            }
            mCamera.setSwipingEnabled(true);
            if (mCamera.getCameraDevice() == null) {
                return false;
            }
            return false;
        }
        
        @Override
        public void onLeaveActor() {
            if (LOG) {
               Log.i(TAG, "FaceArtActor onLeaveActor"); 
            }

             mFaceArtHandler.removeMessages(0);	            
             mCamera.restoreViewState();
             mCamera.findViewById(R.id.gy_beatifySeekBar).setVisibility(android.view.View.INVISIBLE);
             mCamera.findViewById(R.id.gy_exposureTextView).setVisibility(android.view.View.GONE);  
             isInGyFaceArtActor = 0;
        }

    }

   private void startGYBeauty(boolean isEnable, int smooth, int whitening){
          if(mCamera == null)
          {
              Log.i(TAG, "11startGYBeauty mCamera null"); 
              return ;
          }
          if(mCamera.getCameraDevice()==null){
                Log.i(TAG, "11startGYBeauty getCameraDevice null "); 
               return ;
          }
	  mCamera.getCameraDevice().setGYBeautySmooth(smooth);
	  mCamera.getCameraDevice().setGYBeautyWhitening(whitening);
	  mCamera.getCameraDevice().startGYBeauty(isEnable);
   }
}

