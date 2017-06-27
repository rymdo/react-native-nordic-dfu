
package com.pilloxa;

import android.net.Uri;
import android.util.Log;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.Callback;
import no.nordicsemi.android.dfu.DfuServiceController;
import no.nordicsemi.android.dfu.DfuServiceInitiator;

public class RNNordicDfuModule extends ReactContextBaseJavaModule {

    private final ReactApplicationContext reactContext;
    int mFileType = DfuService.TYPE_AUTO;

    public RNNordicDfuModule(ReactApplicationContext reactContext) {
        super(reactContext);
        this.reactContext = reactContext;
    }

    @ReactMethod
    public void startDFU(String address, String name, String filePath, Callback callback) {
//        mFileStreamUri = Uri.parse(filePath);
        Log.d(getName(), "File: "+filePath);
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
        callback.invoke("Hejsan " + address);
    }

    @Override
    public String getName() {
        return "RNNordicDfu";
    }


}