package com.questingsoftware.ucamcoder.view;

import java.io.IOException;

import android.content.Context;
import android.hardware.Camera;
import android.hardware.Camera.CameraInfo;
import android.hardware.Camera.Parameters;
import android.hardware.Camera.Size;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class CameraView extends SurfaceView implements SurfaceHolder.Callback {

	private Camera mCamera;
	private SurfaceHolder mHolder;
	private int displayOrientation;

	private static final String LOG_TAG = CameraView.class.getSimpleName();

	private static final int CAMERA_SIZE = 480;
	private static final int ORIENTATION_PORTRAIT = 0,
			ORIENTATION_LANDSCAPE = 1;

	public CameraView(Context context) {
		super(context);

		// Install a SurfaceHolder.Callback so we get notified when the
		// underlying surface is created and destroyed.
		mHolder = getHolder();
		mHolder.addCallback(this);
	}

	public CameraView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);

		// Install a SurfaceHolder.Callback so we get notified when the
		// underlying surface is created and destroyed.
		mHolder = getHolder();
		mHolder.addCallback(this);
	}

	public CameraView(Context context, AttributeSet attrs) {
		super(context, attrs);

		// Install a SurfaceHolder.Callback so we get notified when the
		// underlying surface is created and destroyed.
		mHolder = getHolder();
		mHolder.addCallback(this);
	}

	public CameraView(Context context, Camera camera) {
		this(context);
		setCamera(camera);
	}

	public void setCamera(Camera camera) {
		mCamera = camera;
	}

	public Camera getCamera() {
		return mCamera;
	}

	public void startCameraPreview() {
		if (mCamera != null && mHolder != null && mHolder.getSurface() != null) {
			try {
				mCamera.setPreviewDisplay(mHolder);
				mCamera.startPreview();
			} catch (IOException e) {
				Log.d(LOG_TAG,
						"Error setting camera preview: " + e.getMessage());
			}
		}
	}

	public void stopCameraPreview() {
		if (mCamera != null) {
			try {
				mCamera.stopPreview();
			} catch (Exception e) {
				// ignore: tried to stop a non-existent preview
			}
		}
	}

	public void surfaceCreated(SurfaceHolder holder) {
		// Do nothing, we configure the camera at the surfaceChanged method
	}

	public void surfaceDestroyed(SurfaceHolder holder) {
		// empty. Take care of releasing the Camera preview in your activity.
	}

	public int getDisplayOrientation() {
		return displayOrientation;
	}

	public void setDisplayOrientation(int displayOrientation) {
		this.displayOrientation = displayOrientation;
	}

	private void setCameraDisplayOrientation() {
		CameraInfo info = new CameraInfo();
		Camera.getCameraInfo(0, info);

		int degrees = 0;
		switch (this.displayOrientation) {
		case Surface.ROTATION_0:
			degrees = 0;
			break;
		case Surface.ROTATION_90:
			degrees = 90;
			break;
		case Surface.ROTATION_180:
			degrees = 180;
			break;
		case Surface.ROTATION_270:
			degrees = 270;
			break;
		}

		int result;
		// if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
		 result = (info.orientation + degrees) % 360;
		 result = (360 - result) % 360; // compensate the mirror
		// } else { // back-facing
		//result = (info.orientation - degrees + 360) % 360;
		// }
		mCamera.setDisplayOrientation(result);
	}

	public void surfaceChanged(SurfaceHolder holder, int format, int w, int h) {
		// If your preview can change or rotate, take care of those events here.
		// Make sure to stop the preview before resizing or reformatting it.

		if (mHolder.getSurface() == null) {
			// preview surface does not exist
			return;
		}

		// stop preview before making changes
		stopCameraPreview();

		// set preview size and make any resize, rotate or
		// reformatting changes here
		if (mCamera != null) {
			Parameters cameraParameters = mCamera.getParameters();

			int orientation = ORIENTATION_PORTRAIT;
			if (w >= h) {
				orientation = ORIENTATION_LANDSCAPE;
			}

			setCameraDisplayOrientation();

			for (Size size : cameraParameters.getSupportedPreviewSizes()) {
				if (orientation == ORIENTATION_LANDSCAPE) {
					if (size.height == CAMERA_SIZE) {
						cameraParameters
								.setPreviewSize(size.width, size.height);
						break;
					}
				} else {
					if (size.width == CAMERA_SIZE) {
						cameraParameters
								.setPreviewSize(size.width, size.height);
						break;
					}
				}
			}

			mCamera.setParameters(cameraParameters);
		}

		// start preview with new settings
		startCameraPreview();
	}

}
