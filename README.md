# cordova-plugin-memory-warning

A maintained Cordova Android plugin for checking device-memory headroom immediately
before an app launches an external camera. This fork does not modify generated
`MainActivity.java` and does not collect process or WebView diagnostics.

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

For a forced-unsafe test, temporarily configure a reserve larger than the device's
expected headroom, rebuild, verify that the camera is blocked, and restore 128 MB.

## API

```javascript
cordova.plugins.memoryGuard.getCameraMemoryStatus(function(status) {
    console.log("[MEMORY-GUARD] " + JSON.stringify(status));
}, function(error) {
    console.warn("[MEMORY-GUARD] Check failed: " + JSON.stringify(error));
});
```

The native result contains only:

```json
{
  "lowMemory": false,
  "availableMemMB": 239,
  "thresholdMB": 205,
  "headroomMB": 34,
  "cameraReserveMB": 128,
  "safeToOpenCamera": false
}
```

The safety calculation uses Android `ActivityManager.MemoryInfo`:

```java
long headroomBytes = memoryInfo.availMem - memoryInfo.threshold;
boolean safeToOpenCamera =
        !memoryInfo.lowMemory && headroomBytes >= cameraReserveBytes;
```

The check runs on Cordova's thread pool. It is a point-in-time guard only: memory
conditions can change afterward, and Android does not permit the calling app to
terminate unrelated applications.
