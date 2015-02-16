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
        if(me.waitMask == undefined) {
            me.waitMask = new Ext.LoadMask({
                contentEl: "loading-mask", focusOnToFront: true,
                target: target || Ext.getBody(), msg: message, toFrontOnShow: true
            });
            //me.waitMask.el.dom.style.zIndex = '99999';
//            me.waitMask.on("show", function(eOpts) {
//                var m = this.maskEl; //Ext.get("bio-global-root-mask");
//                m.dom.style.zIndex = '99999';
////                m = Ext.getBody().down(".x-mask-msg");
////                m.dom.style.zIndex = '99999';
//            }, me.waitMask);
        } else
            me.waitMask.msg = message;
        me.waitMask.show();
        //var m = me.waitMask.maskEl;
        //m.dom.style.zIndex = '99999';
        //me.waitMask.setZIndex(99999);
        //me.waitMask.getMaskEl().setStyle('zIndex', 99999 - 1);
        me.waitMaskVisible = true;
    },

    waitMaskHide: function(){
        var me = this;
        if(me.waitMask != undefined)
            me.waitMask.hide();
        me.waitMaskVisible = false;
    }


});


