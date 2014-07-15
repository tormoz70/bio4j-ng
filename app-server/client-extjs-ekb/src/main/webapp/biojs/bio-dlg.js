Ext.namespace("Bio");
Ext.define('Bio.dlg', {
    singleton: true,
    showMsg: function (title, msg, width, height, callback) {
        var cb = Bio.Tools.wrapCallback(callback);

        var dialog = new Bio.dialog.Message({
            title:title,
            msg:msg,
            width:width,
            height:height,
            callback:cb});
        dialog.showDialog();
    },

    showErr: function (title, err, width, height, callback) {
        var msg = err;
        var errObj = null;
        if (typeof err == "string")
            errObj = Bio.Tools.tryDecodeJSON(err);
        else if (err)
            errObj = err;
        if (errObj && (typeof errObj == "object") && errObj.message)
            msg = errObj.message;
        this.showMsg(
            title,
            msg,
            width,
            height,
            callback
        );
    }
});

