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
    },

    listeners: {
        load: function(records, successful, eOpts ) {
            //
        }
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
