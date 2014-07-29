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
            },
            remoteSort: true
        }, config);

        me.callParent([config]);
    },

    listeners: {
        load: function(records, successful, eOpts ) {
            //
        }
    },

    loadForm: function(form) {
        var me = this;
        me.load({
            scope: form.getForm(),
            callback: function(records, operation, success) {
                if(records && (records.length > 0))
                    this.loadRecord(records[0]);
            }
        });

    }
});
