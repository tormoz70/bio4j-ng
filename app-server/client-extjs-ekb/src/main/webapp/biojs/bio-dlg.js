Ext.namespace("Bio");
Ext.define('Bio.dlg', {
    singleton: true,
    showMsg: function (title, msg, width, height, callback) {
        var dialog = new Bio.dialog.Message({
            title:title,
            msg:msg,
            width:width,
            height:height,
            callback:callback});
        dialog.showDialog();
    },

    showErr: function (pTitle, pErr, pWidth, pHeight, pDoOnClosePrc) {
        var vMsg = pErr;
        var vRespObj = null;
        if (typeof pErr == "string")
            vRespObj = Bio.Tools.tryDecodeJSON(pErr);
        else if (pErr && pErr.ebio)
            vRespObj = pErr;
        if (vRespObj && vRespObj.ebio)
            vMsg = vRespObj.ebio.message;
        this.showMsg(
            pTitle,
            vMsg,
            pWidth,
            pHeight,
            pDoOnClosePrc
        );
    }
});

//Bio.dlg = new Bio.Dialog();