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

    myWaitMask : undefined,
    waitMaskShow: function(message){
        if(this.myWaitMask == undefined)
            this.myWaitMask = new Ext.LoadMask(Ext.getBody(), {msg:message});
        else
            this.myWaitMask.msg = message;
        this.myWaitMask.show();
    },

    waitMaskHide: function(){
        if(this.myWaitMask != undefined)
            this.myWaitMask.hide();
    }

});


