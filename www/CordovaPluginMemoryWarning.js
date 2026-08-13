var exec = require("cordova/exec");

// Requests one current system-memory snapshot from Android. Memory sizes use
// ActivityManager.MemoryInfo's standard byte format.
exports.getMemoryInfo = function(successCallback, errorCallback) {
    exec(
        successCallback,
        errorCallback,
        "CordovaPluginMemoryWarning",
        "getMemoryInfo",
        []
    );
};
