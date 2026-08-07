package com.counterplay.memory;

import android.app.ActivityManager;
import android.content.Context;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaPlugin;
import org.json.JSONArray;

public class CordovaPluginMemoryWarning extends CordovaPlugin {
    private static final String ACTION_IS_SAFE_TO_OPEN_CAMERA = "isSafeToOpenCamera";
    private static final int DEFAULT_CAMERA_RESERVE_MB = 128;
    private static final long BYTES_PER_MB = 1024L * 1024L;

    @Override
    public boolean execute(String action, JSONArray args, final CallbackContext callbackContext) {
        if (!ACTION_IS_SAFE_TO_OPEN_CAMERA.equals(action)) {
            return false;
        }

        cordova.getThreadPool().execute(new Runnable() {
            @Override
            public void run() {
                try {
                    callbackContext.success(isSafeToOpenCamera() ? 1 : 0);
                } catch (Exception exception) {
                    callbackContext.error("Unable to check camera memory safety: " + exception.getMessage());
                }
            }
        });

        return true;
    }

    private boolean isSafeToOpenCamera() {
        Context context = cordova.getContext();
        ActivityManager activityManager =
                (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);

        if (activityManager == null) {
            throw new IllegalStateException("ActivityManager is unavailable");
        }

        ActivityManager.MemoryInfo memoryInfo = new ActivityManager.MemoryInfo();
        activityManager.getMemoryInfo(memoryInfo);

        int cameraReserveMB = preferences.getInteger(
                "CameraMemoryReserveMB",
                DEFAULT_CAMERA_RESERVE_MB
        );
        if (cameraReserveMB < 0) {
            cameraReserveMB = DEFAULT_CAMERA_RESERVE_MB;
        }

        long headroomBytes = memoryInfo.availMem - memoryInfo.threshold;
        long cameraReserveBytes = cameraReserveMB * BYTES_PER_MB;
        return !memoryInfo.lowMemory && headroomBytes >= cameraReserveBytes;
    }
}
