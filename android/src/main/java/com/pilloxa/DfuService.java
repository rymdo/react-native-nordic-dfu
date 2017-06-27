package com.pilloxa;

import android.app.Activity;
import no.nordicsemi.android.dfu.DfuBaseService;

/**
 * Created by viktor on 2017-06-26.
 */

public class DfuService extends DfuBaseService {
    @Override
    protected Class<? extends Activity> getNotificationTarget() {
        return null;
    }

    @Override
    protected boolean isDebug() {
//        return super.isDebug();
        return true;
    }
}
