Ext.namespace("Bio");
Ext.define('Bio.data.Model', {
    extend: 'Ext.data.Model'
});

Ext.define('Bio.data.Store', {
    extend: 'Ext.data.Store',
    alias: 'store.biorest',


    constructor: function(config) {
        var me = this;
        var lstnrs = Ext.apply(config.listeners, {
            load: function(records, successful, eOpts ) {
                var me = this,
                    offset = me.proxy.reader.jsonData.packet.offset;
                me.currentPage =  (offset / me.pageSize) + 1;
            }
        });
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
            },
            listeners: lstnrs

        }, config);

        me.callParent([config]);
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

    locate: function(location) {
        var me = this;
        me.load({locate: location});
    }
});
