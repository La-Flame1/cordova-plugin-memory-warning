var exec = require("cordova/exec");

exports.getMemoryInfo = function(successCallback, errorCallback) {
    exec(
        successCallback,
        errorCallback,
        "CordovaPluginMemoryWarning",
        "getMemoryInfo",
        []
    );
};
