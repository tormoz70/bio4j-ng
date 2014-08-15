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
        me.addListener("load", function(records, successful, eOpts) {
            var me = this;
            if(me.proxy.reader.jsonData) {
                var locate = me.lastOptions.locate,
                    grid = me.ownerGrid,
                    offset = me.proxy.reader.jsonData.packet.offset;
                me.currentPage = (me.pageSize != 0) ? (offset / me.pageSize) + 1 : 1;
                me.lastOptions.locate = undefined;
                me.lastOptions.page = me.currentPage;
                me.lastOptions.start = offset;

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
            grid = me.ownerGrid;
        if (grid) {
            var idProp = me.proxy.reader.getIdProperty();
            var rowIndex = me.find(idProp, location);
            if(rowIndex >= 0) {
                grid.getView().select(rowIndex);
                return true;
            }
        }
        return false;
    },

    locate: function(location, startfrom) {
        var me = this;
        if(me.locateLocal(location) === false) {
            if (startfrom > 0)
                me.currentPage = (me.pageSize != 0) ? (startfrom / me.pageSize) + 1 : 1;
            me.load({locate: location});
        }
    }
});
