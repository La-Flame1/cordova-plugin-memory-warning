package com.counterplay.memory;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.LOG;
import org.apache.cordova.PluginResult;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.ActivityManager;
import android.app.ActivityManager.MemoryInfo;
import android.content.Context;

public class CordovaPluginMemoryWarning extends CordovaPlugin {

    private static final String TAG = "CordovaPluginMemoryWarning";
    private static final String ACTION_GET_MEMORY_INFO = "getMemoryInfo";
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
        if (!ACTION_GET_MEMORY_INFO.equals(action)) {
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

                    JSONObject result = new JSONObject();
                    result.put("availMem", memoryInfo.availMem);
                    result.put("threshold", memoryInfo.threshold);
                    result.put("lowMemory", memoryInfo.lowMemory);
                    result.put("totalMem", memoryInfo.totalMem);

                    LOG.d(
                            TAG,
                            "availMemMB=" + memoryInfo.availMem / BYTES_PER_MB
                                    + ", thresholdMB=" + memoryInfo.threshold / BYTES_PER_MB
                                    + ", totalMemMB=" + memoryInfo.totalMem / BYTES_PER_MB
                                    + ", lowMemory=" + memoryInfo.lowMemory
                    );

                    callbackContext.sendPluginResult(new PluginResult(
                            PluginResult.Status.OK,
                            result
                    ));
                } catch (Exception exception) {
                    LOG.e(TAG, "Unable to get memory information", exception);
                    callbackContext.error(
                            "Unable to get memory information: " + exception.getMessage()
                    );
                }
            }
        });

        return true;
    }
}
