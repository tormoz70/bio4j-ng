Ext.override(Ext.data.Store, {
    constructor: function(config) {
        var me = this, data;
        if(config && config.proxy)
            config.proxy['store'] = me;
        //me.initConfig(config);
        var storeAutoLoad = config.autoLoad;
        config.autoLoad = false;
        me.callParent([config]);
        me.setModel = function(m){me.model = m};
        me.autoLoad = storeAutoLoad;
        data = me.inlineData;
        if(!data && me.autoLoad)
            Ext.defer(me.load, 1, me, [ typeof me.autoLoad === 'object' ? me.autoLoad : undefined ]);
    }
});
