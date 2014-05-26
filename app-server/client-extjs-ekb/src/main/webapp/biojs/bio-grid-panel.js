Ext.namespace("Bio.grid");
Ext.define('Bio.grid.Panel', {
    extend: 'Ext.grid.Panel',
    alias: 'widget.biogrid',

    initComponent: function() {
        var me = this;
        me.callParent(arguments);
    },

    constructor: function(config) {
        var me = this;

        if(!config.columns){
            config.columns = [];
        }
        me.creConfig = Ext.apply({}, config);
        if(config.storeCfg) {
            var cfg = Ext.apply({}, config.storeCfg, {
                url: undefined,
                autoLoad: false,
                remoteSort: false,
                remoteFilter: false,
                remoteGroup: false
            });
            config.store = Ext.create('Bio.data.Store', {
                url: cfg.url,
                bioModuleKey: cfg.bioModuleKey,
                bioCode: cfg.bioCode,
                bioParams: cfg.bioParams,
                listeners: {
                    'metachange': function(store, meta) {

                        var cols = [];
                        Ext.Array.forEach(meta.fields, function(f) {
                            if (f && f != undefined) {
                                var newColCfg = {
                                    dataIndex: f.name,
                                    text: f.title
                                };
                                var cexists = Ext.Array.findBy(me.creConfig.columns, function(c) {return c.dataIndex === f.name;});
                                if(cexists)
                                    newColCfg = Ext.apply(newColCfg, cexists);
                                Ext.Array.push(cols, newColCfg);
                            }
                        }, me);

                        me.reconfigure(store, cols);
                    }
                },
                autoLoad: cfg.autoLoad,
                remoteSort: cfg.remoteSort,
                remoteFilter: cfg.remoteFilter,
                remoteGroup: cfg.remoteGroup
            });
            me.bbar = Ext.create('Ext.PagingToolbar', {
                store: config.store,
                displayInfo: true,
                beforePageText: "Страница",
                afterPageText: "из {0}",
                displayMsg: "Загружено {0} - {1} of {2}",
                emptyMsg: "Нет данных"
            });
        }

        me.callParent(arguments);
    }

});
