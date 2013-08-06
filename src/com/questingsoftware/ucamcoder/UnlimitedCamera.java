package com.questingsoftware.ucamcoder;

import com.questingsoftware.ucamcoder.view.CameraView;

import android.app.Activity;
import android.hardware.Camera;
import android.os.Bundle;
import android.util.Log;

public class UnlimitedCamera extends Activity {
	
	private CameraView cameraView;
	private static final String LOG_TAG = UnlimitedCamera.class.getSimpleName();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
	}

	@Override
	protected void onPause() {
		super.onPause();
		
		cameraView.stopCameraPreview();
		Camera camera = cameraView.getCamera();
		if (camera!=null){
			try{
				Log.d(LOG_TAG, "Releasing camera");
				camera.release();
				Log.d(LOG_TAG, "Camera released");
			}
			catch(Exception e){
				Log.d(LOG_TAG, "Could not release camera");
				//already released
			}
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		
		cameraView = (CameraView) findViewById(R.id.cameraView);
		cameraView.setDisplayOrientation(getWindowManager().getDefaultDisplay().getRotation());
		
		try{
			Log.d(LOG_TAG, "Opening camera");
			Camera camera = Camera.open();
			cameraView.setCamera(camera);
			cameraView.startCameraPreview();
			Log.d(LOG_TAG, "Camera finished opening");
		}
		catch(Exception e){
			Log.d(LOG_TAG, "Error opening camera");
			cameraView.stopCameraPreview();
		}
	}
	
	
}
