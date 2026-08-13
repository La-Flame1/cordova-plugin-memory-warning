var exec = require("cordova/exec");

// Requests one current system-memory snapshot from Android. The native result
// includes exact byte values plus convenient GB values for camera decisions.
exports.getMemoryInfo = function(successCallback, errorCallback) {
    exec(
        successCallback,
        errorCallback,
        "CordovaPluginMemoryWarning",
        "getMemoryInfo",
        []
    );
};
