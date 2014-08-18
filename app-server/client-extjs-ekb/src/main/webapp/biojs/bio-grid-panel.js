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
        if(config.storeCfg) {
            var storeCfg = Ext.apply({}, config.storeCfg, {
                url: undefined,
                autoLoad: false,
                remoteSort: false,
                remoteFilter: false,
                remoteGroup: false
            });
            storeCfg.ownerGrid = me;
            config.store = Ext.create('Bio.data.Store', storeCfg);
            me.bbar = Ext.create('Ext.PagingToolbar', {
                store: config.store,
                displayInfo: true,
                beforePageText: "Страница",
                afterPageText: "из {0}",
                displayMsg: "Загружено {0} - {1} of {2}",
                emptyMsg: "Нет данных",
                inputItemWidth: 60
            });
        }

        var viewCfg = Ext.apply(config.viewConfig||{}, {
            loadingText: "Загрузка данных..."
        });
        config.viewConfig = viewCfg;

        me.callParent(arguments);

        // adding system event handlers
        me.addListener("headerclick", function (ct, column, e, t, eOpts) {
            var me = this;
            //alert('Chpok!');
            //me.bbar.doRefresh();
        }, me);

    },

    recreateCols: function(store, meta) {
        var me = this;
        var bool_renderer = function (value) {
            if (value === true) {
                return "<img src='" + Bio.app.APP_URL + "/biojs/res/checked.gif' />";
            } else {
                return "<img src='" + Bio.app.APP_URL + "/biojs/res/unchecked.gif' />";
            }
            return value;
        };
        var cols = [{xtype: 'rownumberer', text: "#"}];
        Ext.Array.forEach(meta.columns, function (f) {
            if (f && f != undefined && f.hidden === false) {
                var xtp, rdrr;
                switch (f.type) {
                    case "boolean" :
                        xtp = (f.readonly ? 'gridcolumn' : 'checkcolumn');
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
                    default :
                        xtp = 'gridcolumn';
                        break;
                }
                var ttl = f.title;
                if(ttl) ttl = ttl.replace("\\n", "<br/>");
                var newColCfg = {
                    dataIndex: f.name,
                    text: ttl,
                    type: f.type,
                    mandatory: f.mandatory,
                    readonly: f.readonly,
                    pk: f.pk,
                    width: Bio.Tools.tryParsInt(f.width),
                    xtype: xtp,
                    format: f.format,
                    editor: (f.type === "boolean" ? {xtype: 'checkboxfield', boxLabel: 'Box Label'} : f.editor),

                    trueText: 'Да',
                    falseText: 'Нет',
                    renderer: rdrr
                };
                var cexists = Ext.Array.findBy(me.initialConfig.columns, function (c) {
                    return c.dataIndex === f.name;
                });
                if (cexists)
                    newColCfg = Ext.apply(newColCfg, cexists);
                Ext.Array.push(cols, newColCfg);
            }
        }, me);

        me.reconfigure(store, cols);
    },

    getSelectedId: function() {
        var me = this,
            idProp = me.store.proxy.reader.getIdProperty();
        var selection = me.getSelectionModel().getSelection();
        var row = (selection && selection.length > 0) ? selection[0].data : {};
        var result = row[idProp];
        return result || null;
    }
});
