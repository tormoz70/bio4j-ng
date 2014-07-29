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
                bioCode: cfg.bioCode,
                bioParams: cfg.bioParams,
                listeners: {
                    'metachange': function(store, meta) {

                        var bool_renderer = function(value){
                            if(value === true){
                                return "<img src='"+Bio.app.APP_URL+"/biojs/res/checked.gif' />";
                            }else{
                                return "<img src='"+Bio.app.APP_URL+"/biojs/res/unchecked.gif' />";
                            }
                            return value;
                        };
                        var cols = [];
                        Ext.Array.forEach(meta.columns, function(f) {
                            if (f && f != undefined && f.hidden === false) {
                                var xtp, rdrr;
                                switch (f.type) {
                                    case "boolean" :
                                        xtp = 'checkcolumn';
                                        rdrr = bool_renderer;
                                        break;
                                    case "date" :
                                        xtp = 'datecolumn';
                                        rdrr = Ext.util.Format.dateRenderer(f.format);
                                        break;
                                    case "integer" :
                                        xtp = 'numbercolumn';
                                        break;
                                    case "decimal" :
                                        xtp = 'numbercolumn';
                                        break;
                                    default : xtp = 'gridcolumn'; break;
                                }

                                var newColCfg = {
                                    dataIndex: f.name,
                                    text: f.title,
                                    type: f.type,
                                    mandatory: f.mandatory,
                                    readonly: f.readonly,
                                    pk: f.pk,
                                    width: f.width,
                                    xtype: xtp,
                                    format: f.format,
                                    editor : (f.type === "boolean" ? {xtype: 'checkboxfield', boxLabel: 'Box Label'} : f.editor),

                                    trueText: 'Да',
                                    falseText: 'Нет',
                                    renderer : rdrr
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

        var lsnrs = config.listeners;
        lsnrs = Ext.apply(lsnrs||{}, {
            'headerclick': function (ct, column, e, t, eOpts) {
                var me = this;
                //alert('Chpok!');
                me.store.reload();
            }
        });
        config.listeners = lsnrs;

        me.callParent(arguments);
    }

});
