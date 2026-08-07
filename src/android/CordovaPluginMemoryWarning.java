package com.counterplay.memory;

import android.app.ActivityManager;
import android.content.Context;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaPlugin;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class CordovaPluginMemoryWarning extends CordovaPlugin {
    private static final String ACTION_GET_CAMERA_MEMORY_STATUS = "getCameraMemoryStatus";
    private static final int DEFAULT_CAMERA_RESERVE_MB = 128;
    private static final long BYTES_PER_MB = 1024L * 1024L;

    @Override
    public boolean execute(String action, JSONArray args, final CallbackContext callbackContext) {
        if (!ACTION_GET_CAMERA_MEMORY_STATUS.equals(action)) {
            return false;
        }

        cordova.getThreadPool().execute(new Runnable() {
            @Override
            public void run() {
                try {
                    callbackContext.success(getCameraMemoryStatus());
                } catch (Exception exception) {
                    callbackContext.error("Unable to read device memory: " + exception.getMessage());
                }
            }
        });

        return true;
    }

    private JSONObject getCameraMemoryStatus() throws JSONException {
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

        long cameraReserveBytes = cameraReserveMB * BYTES_PER_MB;
        long headroomBytes = memoryInfo.availMem - memoryInfo.threshold;
        boolean safeToOpenCamera =
                !memoryInfo.lowMemory && headroomBytes >= cameraReserveBytes;

        JSONObject status = new JSONObject();
        status.put("lowMemory", memoryInfo.lowMemory);
        status.put("availableMemMB", memoryInfo.availMem / BYTES_PER_MB);
        status.put("thresholdMB", memoryInfo.threshold / BYTES_PER_MB);
        status.put("headroomMB", headroomBytes / BYTES_PER_MB);
        status.put("cameraReserveMB", cameraReserveMB);
        status.put("safeToOpenCamera", safeToOpenCamera);
        return status;
    }
}
