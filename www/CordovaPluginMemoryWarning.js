var exec = require("cordova/exec");

exports.isMemoryUsageUnsafe = function(successCallback, errorCallback) {
    exec(
        successCallback,
        errorCallback,
        "CordovaPluginMemoryWarning",
        "isMemoryUsageUnsafe",
        []
    );
};
