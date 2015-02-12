Ext.namespace("Bio");
Ext.define('Bio.Application', {
    extend: "Ext.util.Observable",
    config: {
        APP_MODULE_KEY: null,
        APP_URL: null,
        APP_TITLE: null,
        сurUsr: null
    },

    constructor: function(config) {
        var me = this;
        config.APP_MODULE_KEY = config.APP_URL.substring(1);
        Ext.apply(me, config);
    },

    waitMask : undefined,
    waitMaskVisible : false,
    waitMaskShow: function(message, target){
        var me = this;
        if(me.waitMask == undefined)
            me.waitMask = new Ext.LoadMask({
                target:target || Ext.getBody() , msg:message, toFrontOnShow:true
            });
        else
            me.waitMask.msg = message;
        me.waitMask.show();
        me.waitMaskVisible = true;
    },

    waitMaskHide: function(){
        var me = this;
        if(me.waitMask != undefined)
            me.waitMask.hide();
        me.waitMaskVisible = false;
    }

});


