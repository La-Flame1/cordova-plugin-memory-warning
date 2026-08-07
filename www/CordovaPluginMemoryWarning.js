var exec = require("cordova/exec");

exports.getCameraMemoryStatus = function(successCallback, errorCallback) {
    exec(successCallback, errorCallback, "MemoryGuard", "getCameraMemoryStatus", []);
};
