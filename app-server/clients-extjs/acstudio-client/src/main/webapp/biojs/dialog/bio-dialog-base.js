Ext.namespace("Bio.dialog");
Ext.define('Bio.dialog.Base', {
    extend: 'Ext.Window',

    closeCallbacks: null,
    dialogResult: 0,

    appendCallback: function (callback) {
        var me = this;
        if (me) {
            if (!me.closeCallbacks)
                me.closeCallbacks = [];
            if (callback instanceof Array) {
                Ext.each(callback, function (c) {
                    me.closeCallbacks[me.closeCallbacks.length] = c;
                });
            } else
                me.closeCallbacks[me.closeCallbacks.length] = callback;
        }
    },

    constructor: function(config) {
        var me = this;
        me.appendCallback(config.callback);
        config.callback = null;
        me.callParent([config]);
        me.on("close", me.onClose, me);
    },

    processCallbacks: function () {
        var me = this;
        var callbacks = me.closeCallbacks;
        if (callbacks instanceof Array) {
            Ext.each(callbacks, function (callback) {
                if (callback && callback.fn)
                    callback.fn.bind(callback.scope)(me.dialogResult);
            });
        }
    },

    onClose: function() {
        this.processCallbacks();
    },

    closeDialog: function(dialogResult) {
        var me = this;
        me.dialogResult = dialogResult;
        me.close();
    },


    onShow: function() {
        this.callParent(arguments);
    },

    showDialog: function () {
        this.show();
    },

    findField: function(fieldName) {
        var me = this;
        var fld = me.form.items.find(function (c) {
            return (c.name) && (c.name === fieldName);
        });
        return fld;
    }

});
