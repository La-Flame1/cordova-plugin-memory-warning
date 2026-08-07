var exec = require("cordova/exec");

exports.isSafeToOpenCamera = function(successCallback, errorCallback) {
    exec(successCallback, errorCallback, "MemoryGuard", "isSafeToOpenCamera", []);
};
