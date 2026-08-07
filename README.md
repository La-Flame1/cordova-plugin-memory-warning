# cordova-plugin-memory-warning

A maintained Cordova Android plugin that answers one question immediately before
the app opens an external camera: is there enough memory headroom?

The plugin does not modify generated `MainActivity.java` and does not return
diagnostic memory measurements.

## Installation

```shell
cordova plugin add https://github.com/La-Flame1/cordova-plugin-memory-warning.git#camera-memory-guard
```

## Configuration

The default camera reserve is 128 MB. Override it in the Cordova application's root
`config.xml`:

```xml
<preference name="CameraMemoryReserveMB" value="128" />
```

## API

```javascript
cordova.plugins.memoryGuard.isSafeToOpenCamera(function(safeToOpenCamera) {
    if (safeToOpenCamera) {
        navigator.camera.getPicture(successCallback, errorCallback, options);
    }
}, function(error) {
    // Preserve existing camera behavior if the safety check fails.
    navigator.camera.getPicture(successCallback, errorCallback, options);
});
```

The success callback receives only a boolean. Android calculates it from
`ActivityManager.MemoryInfo` on Cordova's thread pool:

```java
long headroomBytes = memoryInfo.availMem - memoryInfo.threshold;
return !memoryInfo.lowMemory && headroomBytes >= cameraReserveBytes;
```

The result is a point-in-time safety check; memory conditions can change afterward.
