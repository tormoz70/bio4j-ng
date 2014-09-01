Ext.namespace("Bio.dialog");
Ext.define('Bio.dialog.Message', {
    extend: 'Bio.dialog.Base',

    updateText: function (msg) {
        var me = this;
        var txtFld = Bio.tools.getFrmFld(me.items.items[0], "msgText");
        if (txtFld) txtFld.setValue(msg);
    },

    constructor: function(cfg) {
        var me = this;

        var config = Ext.apply({
            callback: cfg.callback,
            title: cfg.title,
            width: (cfg.width) ? cfg.width : 500,
            height: (cfg.height) ? cfg.height : 300,
            layout: 'fit',
            plain: true,
            buttonAlign: 'center',
            modal: true,
            buttons: [
                {text: "ОК", handler: me.doOK.bind(me)}
            ],
            keys: [
                {key: Ext.EventObject.ENTER,
                    fn: me.doOK.bind(me)}
            ],
            items: {
                xtype: 'form',
                baseCls: "x-plain",
                items: [
                    {
                        xtype: "textarea",
                        readOnly: true,
                        hideLabel: true,
                        name: "msgText",
                        anchor: "100% -5",
                        value: cfg.msg
                    }
                ]
            }
        }, { });

        me.callParent([config]);
    },

    showDialog: function (msg) {
        var me = this;
        me.callParent();
        if(msg)
            me.updateText(msg);
    },

    doOK: function () {
        this.closeDialog({modalResult: 1});
    }


});
