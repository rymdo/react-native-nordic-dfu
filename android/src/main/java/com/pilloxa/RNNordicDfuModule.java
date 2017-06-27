
package com.pilloxa;

import android.net.Uri;
import android.support.annotation.Nullable;
import android.util.Log;
import com.facebook.react.bridge.*;
import com.facebook.react.modules.core.RCTNativeAppEventEmitter;
import no.nordicsemi.android.dfu.*;

public class RNNordicDfuModule extends ReactContextBaseJavaModule implements LifecycleEventListener {

    private final String dfuStateEvent = "DFUStateChanged";
    private static final String name = "RNNordicDfu";
    public static final String LOG_TAG = name;
    private final ReactApplicationContext reactContext;
    int mFileType = DfuService.TYPE_AUTO;
    private Promise mPromise = null;

    public RNNordicDfuModule(ReactApplicationContext reactContext) {
        super(reactContext);
        reactContext.addLifecycleEventListener(this);
        this.reactContext = reactContext;
    }

    @ReactMethod
    public void startDFU(String address, String name, String filePath, Promise promise) {
        mPromise = promise;
        Log.d(getName(), "File: " + filePath);
        final DfuServiceInitiator starter = new DfuServiceInitiator(address)
                .setDeviceName(name)
                .setKeepBond(false);
// If you want to have experimental buttonless DFU feature supported call additionally:
        starter.setUnsafeExperimentalButtonlessServiceInSecureDfuEnabled(true);
// but be aware of this: https://devzone.nordicsemi.com/question/100609/sdk-12-bootloader-erased-after-programming/
// and other issues related to this experimental service.

// Init packet is required by Bootloader/DFU from SDK 7.0+ if HEX or BIN file is given above.
// In case of a ZIP file, the init packet (a DAT file) must be included inside the ZIP file.
        starter.setZip(filePath);
        final DfuServiceController controller = starter.start(this.reactContext, DfuService.class);
    }

    @Override
    public String getName() {
        return name;
    }


    private void sendEvent(String eventName, @Nullable WritableMap params) {
        getReactApplicationContext()
                .getJSModule(RCTNativeAppEventEmitter.class)
                .emit(eventName, params);
    }

    private void sendStateUpdate(String state) {
        WritableMap map = new WritableNativeMap();
        Log.d(LOG_TAG, "State: "+state);
        map.putString("state", state);
        sendEvent(dfuStateEvent, map);
    }


    @Override
    public void onHostResume() {
        DfuServiceListenerHelper.registerProgressListener(this.reactContext, mDfuProgressListener);

    }

    @Override
    public void onHostPause() {
    }

    @Override
    public void onHostDestroy() {
        DfuServiceListenerHelper.unregisterProgressListener(this.reactContext, mDfuProgressListener);

    }




    /**
     * The progress listener receives events from the DFU Service.
     * If is registered in onCreate() and unregistered in onDestroy() so methods here may also be called
     * when the screen is locked or the app went to the background. This is because the UI needs to have the
     * correct information after user comes back to the activity and this information can't be read from the service
     * as it might have been killed already (DFU completed or finished with error).
     */
    private final DfuProgressListener mDfuProgressListener = new DfuProgressListenerAdapter() {
        @Override
        public void onDeviceConnecting(final String deviceAddress){
            sendStateUpdate("CONNECTING");
        }

        @Override
        public void onDfuProcessStarting(final String deviceAddress) {
            sendStateUpdate("DFU_PROCESS_STARTING");
        }

        @Override
        public void onEnablingDfuMode(final String deviceAddress) {
            sendStateUpdate("ENABLING_DFU_MODE");
        }

        @Override
        public void onFirmwareValidating(final String deviceAddress) {
            sendStateUpdate("FIRMWARE_VALIDATING");
        }

        @Override
        public void onDeviceDisconnecting(final String deviceAddress) {
            sendStateUpdate("DEVICE_DISCONNECTING");
        }

        @Override
        public void onDfuCompleted(final String deviceAddress) {
            if (mPromise != null) {
                WritableMap map = new WritableNativeMap();
                map.putString("deviceAddress",deviceAddress);
                mPromise.resolve(map);
            }
            sendStateUpdate("DFU_COMPLETED");

        }

        @Override
        public void onDfuAborted(final String deviceAddress) {
            sendStateUpdate("DFU_ABORTED");
            if (mPromise != null) {
                mPromise.reject("2", "DFU Aborted");
            }

        }

//        @Override
//        public void onProgressChanged(final String deviceAddress, final int percent, final float speed, final float avgSpeed, final int currentPart, final int partsTotal) {
//            mProgressBar.setIndeterminate(false);
//            mProgressBar.setProgress(percent);
//            mTextPercentage.setText(getString(R.string.dfu_uploading_percentage, percent));
//            if (partsTotal > 1)
//                mTextUploading.setText(getString(R.string.dfu_status_uploading_part, currentPart, partsTotal));
//            else
//                mTextUploading.setText(R.string.dfu_status_uploading);
//        }
//
//        @Override
//        public void onError(final String deviceAddress, final int error, final int errorType, final String message) {
//            sendStateUpdate("DFU_FAILED");
//            if (mResumed) {
//                showErrorMessage(message);
//
//                // We have to wait a bit before canceling notification. This is called before DfuService creates the last notification.
//                new Handler().postDelayed(new Runnable() {
//                    @Override
//                    public void run() {
//                        // if this activity is still open and upload process was completed, cancel the notification
//                        final NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
//                        manager.cancel(DfuService.NOTIFICATION_ID);
//                    }
//                }, 200);
//            } else {
//                mDfuError = message;
//            }
        };
    }