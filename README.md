# cordova-plugin-memory-warning

A maintained compatibility fork of
[`88dots/cordova-plugin-memory-warning`](https://github.com/88dots/cordova-plugin-memory-warning)
for modern Cordova projects.

The original plugin used build hooks to modify generated Android
`MainActivity.java`. Those hooks relied on obsolete Cordova APIs and an old
Cordova Android directory structure. This fork removes those hooks and uses a
standard `CordovaPlugin` implementation.

## Installation

Pin the dependency to a tested branch, tag, or commit:

```shell
cordova plugin add https://github.com/La-Flame1/cordova-plugin-memory-warning.git#camera-memory-guard
```

## API

```javascript
cordova.plugins.CordovaPluginMemoryWarning.getMemoryInfo(
    successCallback,
    errorCallback
);
```

On Android, the success callback receives one
`ActivityManager.MemoryInfo` snapshot:

```json
{
  "availMem": 1152385024,
  "threshold": 134217728,
  "totalMem": 3922640896,
  "lowMemory": false
}
```

All memory sizes use Android's standard byte format. Application JavaScript is
responsible for converting units and deciding whether a specific operation,
such as camera launch, is safe.

The native read runs on Cordova's thread pool. It does not require an Android
permission and does not inspect or terminate other applications.

## Converting bytes in JavaScript

```javascript
var bytesPerGB = 1024 * 1024 * 1024;
var availMemGB = memoryInfo.availMem / bytesPerGB;
var thresholdGB = memoryInfo.threshold / bytesPerGB;
var totalMemGB = memoryInfo.totalMem / bytesPerGB;
```

## Limitations

- The result is a point-in-time snapshot; memory conditions may change after
  the callback.
- Android may still terminate the application after an external camera opens.
- The plugin does not open the camera, display a popup, or kill other apps.
- Camera thresholds are application policy and are intentionally not built into
  this plugin.

The Android implementation has been build-tested with Cordova Android 15.1.0.
