Ext.namespace("Bio");

Bio.Application = function (cfg) {
    Ext.apply(this, cfg);
    this.addEvents({
        'ready': true,
        'beforeunload': true
    });

    //Ext.onReady(this.initApp, this);
};

Ext.extend(Bio.Application, Ext.util.Observable, {
    isReady: false,
    startMenu: null,

    getStartConfig: Ext.emptyFn,

    initApp: function () {
        this.startConfig = this.startConfig || this.getStartConfig();

        Ext.EventManager.on(window, 'beforeunload', this.onUnload, this);
        this.fireEvent('ready', this);
        this.isReady = true;
    },

    onReady: function (fn, scope) {
        if (!this.isReady) {
            this.on('ready', fn, scope);
        } else {
            fn.call(scope, this);
        }
    },

    onUnload: function (e) {
        if (this.fireEvent('beforeunload', this) === false) {
            e.stopEvent();
        }
    },

    /**
     *
     * @param {Object} e = {name:String,
   *                      handler:function, 
   *                      scope:Object, 
   *                      options:Object}
     */
    regEvent: function (e) {
        if (e && e.name && e.handler) {
            if (!this.hasListener(e.name)) {
                var eve = {};
                eve[e.name] = true;
                this.addEvents(eve);
            }
            this.addListener(e.name, e.handler, e.scope, e.options);
        }
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


Bio.app = new Bio.Application({});
