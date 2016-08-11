package com.zxingEmbedded;
import android.content.Intent;

import com.facebook.react.bridge.Callback;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.ActivityEventListener;
import com.facebook.react.bridge.Promise;
import android.text.TextUtils;
import com.zxingEmbedded.CustomScannerActivity;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import android.app.Activity;
/**
 * Created by tx36326 on 2016/7/26.
 */
public class BDscan extends ReactContextBaseJavaModule implements ActivityEventListener {

    private static final String E_ACTIVITY_DOES_NOT_EXIST = "E_ACTIVITY_DOES_NOT_EXIST";
    private static final String E_PICKER_CANCELLED = "E_PICKER_CANCELLED";
    private static final String E_FAILED_TO_SHOW_PICKER = "E_FAILED_TO_SHOW_PICKER";
    private static final String E_NO_IMAGE_DATA_FOUND = "E_NO_IMAGE_DATA_FOUND";

    private ReactApplicationContext context;
    private Promise mPickerPromise;
	public BDscan(ReactApplicationContext reactContext) {
		super(reactContext);
		context = reactContext;
        reactContext.addActivityEventListener(this);
	}
    @Override
    public void onNewIntent(Intent intent){}
	@Override
	public String getName() {
		return "BDscan";
	}

	@ReactMethod
	public void scan(final Promise promise) {
        Activity currentActivity = getCurrentActivity();
		// Store the promise to resolve/reject when picker returns data
        mPickerPromise = promise;
        if (currentActivity == null) {
            promise.reject(E_ACTIVITY_DOES_NOT_EXIST, "Activity doesn't exist");
            return;
        }
		try {


            new IntentIntegrator(currentActivity).setOrientationLocked(false).setCaptureActivity(CustomScannerActivity.class).initiateScan();


		} catch (Exception e) {
            mPickerPromise.reject(E_FAILED_TO_SHOW_PICKER, e);
            mPickerPromise = null;
		}
	}
    @ReactMethod
    public void scan1D(final Promise promise) {
        Activity currentActivity = getCurrentActivity();
        // Store the promise to resolve/reject when picker returns data
        mPickerPromise = promise;
        if (currentActivity == null) {
            promise.reject(E_ACTIVITY_DOES_NOT_EXIST, "Activity doesn't exist");
            return;
        }
        try {


            new IntentIntegrator(currentActivity).setOrientationLocked(false).setCaptureActivity(CustomScannerActivity1D.class).initiateScan();


        } catch (Exception e) {
            mPickerPromise.reject(E_FAILED_TO_SHOW_PICKER, e);
            mPickerPromise = null;
        }
    }




    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        String scanResult;
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if(result != null) {
            scanResult = result.getContents();
            if(scanResult == null) {
                mPickerPromise.reject(E_NO_IMAGE_DATA_FOUND, "No image data found");
            } else if( resultCode == Activity.RESULT_OK){

                scanResult=  result.getContents();

                if (TextUtils.isEmpty(scanResult)) {
                    mPickerPromise.reject(E_NO_IMAGE_DATA_FOUND, "No image data found");
                } else {
                    mPickerPromise.resolve(scanResult);
                }


            }
            else
                mPickerPromise.reject(E_NO_IMAGE_DATA_FOUND, "No image data found");
        } else  {
            // This is important, otherwise the result will not be passed to the fragment
           // super.onActivityResult(requestCode, resultCode, data);
        }


        mPickerPromise = null;
    }
}
