# cordova-plugin-memory-warning

A maintained compatibility fork of
[`88dots/cordova-plugin-memory-warning`](https://github.com/88dots/cordova-plugin-memory-warning)
for modern Cordova projects.

The original plugin used build hooks to modify generated Android
`MainActivity.java`. Those hooks relied on obsolete Cordova APIs and an old
Cordova Android directory structure. This fork removes those hooks and uses a
standard `CordovaPlugin` implementation.

## Installation

Pin the dependency to a tested branch, tag, or commit. For branch testing:

```shell
cordova plugin add https://github.com/La-Flame1/cordova-plugin-memory-warning.git#camera-memory-guard
```

## API

```javascript
cordova.plugins.CordovaPluginMemoryWarning.isMemoryUsageUnsafe(
    successCallback,
    errorCallback
);
```

On Android, the success callback receives the boolean value returned by:

```java
ActivityManager.MemoryInfo memoryInfo = new ActivityManager.MemoryInfo();
activityManager.getMemoryInfo(memoryInfo);
return memoryInfo.lowMemory;
```

The Android check runs on Cordova's thread pool. It does not require an Android
permission and does not inspect or terminate other applications.

On iOS, `isMemoryUsageUnsafe` preserves the original behavior and returns
`false`; iOS memory warnings continue to be exposed through Cordova's
`memorywarning` document event.

## Camera gating example

```javascript
function openCameraNow() {
    navigator.camera.getPicture(successCallback, errorCallback, options);
}

cordova.plugins.CordovaPluginMemoryWarning.isMemoryUsageUnsafe(function(lowMemory) {
    console.log("[MEMORY-GUARD] memoryInfo.lowMemory=" + lowMemory);

    if (lowMemory === true) {
        navigator.notification.alert(
            "Your device is low on available memory. " +
            "Close other applications and try taking the photograph again.",
            null,
            "Low memory",
            "OK"
        );
        return;
    }

    openCameraNow();
}, function(error) {
    console.warn("[MEMORY-GUARD] Check failed:", error);

    // Preserve existing camera behavior if the check itself fails.
    openCameraNow();
});
```

## Limitations

- The result is a point-in-time snapshot; memory conditions may change after
  the callback.
- The plugin prevents the caller from launching the camera only when Android
  reports `lowMemory=true`.
- It cannot prevent Android from terminating the application process.
- It does not kill the camera or any unrelated application.
- Persisting login, route, and form state remains a separate safeguard.

The Android implementation has been build-tested with Cordova Android 15.1.0.
