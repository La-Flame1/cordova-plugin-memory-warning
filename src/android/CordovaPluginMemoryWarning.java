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

public class CordovaPluginMemoryWarning extends CordovaPlugin {

    private static final String TAG = "CordovaPluginMemoryWarning";
    private static final String ACTION_IS_MEMORY_USAGE_UNSAFE = "isMemoryUsageUnsafe";
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

                    if (memoryInfo.lowMemory) {
                        LOG.d(TAG, "Android reports a low-memory condition");
                    }

                    callbackContext.sendPluginResult(new PluginResult(
                            PluginResult.Status.OK,
                            memoryInfo.lowMemory
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
