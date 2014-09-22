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
            storeCfg.pageSize = config.store.pageSize;
            if(storeCfg.pageSize >= 0) {
                config.bbar = Ext.create('Ext.PagingToolbar', {
                    store: config.store
                });
            } else {
                config.bbar = Ext.create('Ext.toolbar.Toolbar', {
                    hidden: true,
                    items: {
                        xtype: 'displayfield',
                        margin: '0 0 0 5',
                        readonly: true,
                        text: "..."
                    }
                });
            }
        }

        var viewCfg = Ext.apply(config.viewConfig||{}, {
            loadingText: "Загрузка данных..."
        });
        config.viewConfig = viewCfg;

        me.callParent(arguments);

        // adding system event handlers
//        me.addListener("headerclick", function (ct, column, e, t, eOpts) {
//        }, me);
//
//        me.addListener("afterrender", function (eOpts) {
//        }, me);
    },

    metaIsChanged: function(meta) {
        var me = this,
            oldColumns = me.columnManager.columns,
            fieldIndex, fieldsFound = 0, fieldsCount = 0;
        if(oldColumns.length == 0)
            return true;
        Ext.Array.forEach(oldColumns, function(c0) {
            if(c0.xtype != 'rownumberer') {
                field = Ext.Array.findBy(meta.fields, function (f) {
                    return f.name.toLowerCase() == c0.dataIndex.toLowerCase() &&
                            f.type.toLowerCase() == c0.type.toLowerCase();
                });
                if(fieldIndex != -1)
                    fieldsFound++;
            }
        });
        Ext.Array.forEach(meta.fields, function (f) {
            if(f.hidden !== true)
                fieldsCount++;
        });
        return fieldsCount != fieldsFound;
    },

    recreateCols: function(store, meta) {
        var me = this;
        if(!me.metaIsChanged(meta))
            return;

        var bool_renderer = function (value) {
            if (value === true) {
                return "<img src='" + Bio.app.APP_URL + "/biojs/res/checked.gif' />";
            } else {
                return "<img src='" + Bio.app.APP_URL + "/biojs/res/unchecked.gif' />";
            }
            return value;
        };
        var cols = [{xtype: 'rownumberer', text: "#", resizable: true, width: 30}];
        Ext.Array.forEach(meta.fields, function (f) {
            if (f && f != undefined && f.hidden === false) {
                var xtp, etp, rdrr;
                switch (f.type) {
                    case "boolean" :
                        xtp = (f.readonly ? 'gridcolumn' : 'checkcolumn');
                        rdrr = bool_renderer;
                        etp = 'checkboxfield';
                        break;
                    case "date" :
                        xtp = 'datecolumn';
                        rdrr = Ext.util.Format.dateRenderer(f.format);
                        etp = 'datefield';
                        break;
                    case "integer" :
                        xtp = 'numbercolumn';
                        etp = 'numberfield';
                        break;
                    case "decimal" :
                        xtp = 'numbercolumn';
                        etp = 'numberfield';
                        break;
                    default :
                        xtp = 'gridcolumn';
                        etp = 'textfield';
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
                    width: Bio.tools.parsWidthGetWidth(f.width),
                    flex: Bio.tools.parsWidthGetFlex(f.width),
                    xtype: xtp,
                    format: f.format,
                    editor: (f.readonly !== true) ? (
                            Bio.tools.isDefined(f.editor) ? f.editor : {xtype: etp, allowBlank: f.mandatory !== true}
                        ) : undefined,

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
        me.reconfigure(undefined, cols);
        me.getView().refresh();
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
