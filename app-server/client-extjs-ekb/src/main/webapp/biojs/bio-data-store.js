Ext.namespace("Bio");
Ext.define('Bio.data.Model', {
    extend: 'Ext.data.Model'
});

Ext.define('Bio.data.Store', {
    extend: 'Ext.data.Store',
    alias: 'store.biorest',


    constructor: function(config) {
        var me = this;
        config = Ext.apply({
            model: 'Bio.data.Model',
            proxy: {
                type: 'biorest'
                ,reader: {
                    type: 'biorest'
                }
                ,writer: {
                    type: 'biorest'
                }
            }

        }, config);

        me.callParent([config]);

        // adding system event handlers
        me.addListener("load", function(records, eOpts, successful) {
            var me = this;
            if(me.proxy.reader.jsonData) {
                var locate = me.lastOptions.locate,
                    grid = me.ownerGrid,
                    bbar = (grid && grid.initialConfig) ? grid.initialConfig.bbar : null,
                    offset = me.proxy.reader.jsonData.packet.offset;
                me.currentPage = (me.pageSize > 0) ? (offset / me.pageSize) + 1 : 1;
                me.lastOptions.locate = undefined;
                me.lastOptions.page = me.currentPage;
                me.lastOptions.start = offset;

                var showNotAllDataLoadedAlert = (me.pageSize < 0) && me.data.items && (me.data.items.length < me.totalCount);
                if(showNotAllDataLoadedAlert && bbar && bbar.items && bbar.items.items) {
                    bbar.items.items[0].setValue("Внимание превышен лимит загрузки данных!!! Загруженно записей "+me.data.items.length+", всего "+me.totalCount);
                    bbar.setVisible(true);
                }
                me.locateLocal(locate);
            }
        }, me);
        me.addListener("metachange", function(store, meta) {
            var grid = config.ownerGrid;
            if(grid)
                grid.recreateCols(store, meta);
        }, me);
    },

    loadForm: function(form, id) {
        var me = this;
        me.load({
            id: id,
            callback: function(records, operation, success) {
                var f = form.getForm();
                if(records && (records.length > 0))
                    f.loadRecord(records[0]);
            }
        });
    },

    locateLocal: function(location) {
        var me = this,
            grid = me.ownerGrid,
            idProp = me.proxy.reader.getIdProperty(),
            rowIndex = me.find(idProp, location),
            result = rowIndex >= 0;

        if (grid && (result))
            grid.getView().select(rowIndex);

        return result;
    },

    locate: function(location, startFrom, callback, scope) {
        var me = this;
        if(me.locateLocal(location) === false) {
            if (startFrom > 0)
                me.currentPage = (me.pageSize != 0) ? (startFrom / me.pageSize) + 1 : 1;
            me.load({
                locate: location,
                scope: scope || me,
                callback: callback
            });
        }
    }
});
