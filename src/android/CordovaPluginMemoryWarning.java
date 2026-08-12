package com.counterplay.memory;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.LOG;
import org.apache.cordova.PluginResult;

import org.json.JSONArray;
import org.json.JSONException;

import android.app.ActivityManager;
import android.app.ActivityManager.MemoryInfo;
import android.content.Context;
import android.os.Debug;

public class CordovaPluginMemoryWarning extends CordovaPlugin {

    private static final String TAG = "CordovaPluginMemoryWarning";
    private static final String ACTION_IS_MEMORY_USAGE_UNSAFE = "isMemoryUsageUnsafe";
    private static final String CAMERA_MEMORY_RESERVE_PREFERENCE = "CameraMemoryReserveMB";
    private static final int DEFAULT_CAMERA_MEMORY_RESERVE_MB = 384;
    private static final long BYTES_PER_KB = 1024L;
    private static final long BYTES_PER_MB = 1024L * 1024L;
    private ActivityManager activityManager;

    @Override
    protected void pluginInitialize() {
        Context context = cordova.getContext();
        activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
    }

    @Override
    public boolean execute(
            final String action,
            final JSONArray args,
            final CallbackContext callbackContext
    ) throws JSONException {
        if (!ACTION_IS_MEMORY_USAGE_UNSAFE.equals(action)) {
            return false;
        }

        cordova.getThreadPool().execute(new Runnable() {
            @Override
            public void run() {
                try {
                    if (activityManager == null) {
                        throw new IllegalStateException("ActivityManager is unavailable");
                    }

                    MemoryInfo memoryInfo = new MemoryInfo();
                    activityManager.getMemoryInfo(memoryInfo);

                    int cameraReserveMB = preferences.getInteger(
                            CAMERA_MEMORY_RESERVE_PREFERENCE,
                            DEFAULT_CAMERA_MEMORY_RESERVE_MB
                    );
                    if (cameraReserveMB < 0) {
                        cameraReserveMB = DEFAULT_CAMERA_MEMORY_RESERVE_MB;
                    }

                    long appPssBytes = Debug.getPss() * BYTES_PER_KB;
                    long cameraReserveBytes = cameraReserveMB * BYTES_PER_MB;
                    long requiredHeadroomBytes = appPssBytes + cameraReserveBytes;
                    long headroomBytes = memoryInfo.availMem - memoryInfo.threshold;
                    boolean memoryUsageUnsafe = memoryInfo.lowMemory
                            || headroomBytes < requiredHeadroomBytes;

                    LOG.d(
                            TAG,
                            "lowMemory=" + memoryInfo.lowMemory
                                    + ", availableMemMB=" + memoryInfo.availMem / BYTES_PER_MB
                                    + ", thresholdMB=" + memoryInfo.threshold / BYTES_PER_MB
                                    + ", headroomMB=" + headroomBytes / BYTES_PER_MB
                                    + ", appPssMB=" + appPssBytes / BYTES_PER_MB
                                    + ", baseCameraReserveMB=" + cameraReserveMB
                                    + ", requiredHeadroomMB=" + requiredHeadroomBytes / BYTES_PER_MB
                                    + ", memoryUsageUnsafe=" + memoryUsageUnsafe
                    );

                    callbackContext.sendPluginResult(new PluginResult(
                            PluginResult.Status.OK,
                            memoryUsageUnsafe
                    ));
                } catch (Exception exception) {
                    LOG.e(TAG, "Unable to check memory usage", exception);
                    callbackContext.error(
                            "Unable to check memory usage: " + exception.getMessage()
                    );
                }
            }
        });

        return true;
    }
}
