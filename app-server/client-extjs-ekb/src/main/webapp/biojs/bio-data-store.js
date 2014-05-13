Ext.namespace("Bio");
Ext.define('Bio.data.Model', {
    extend: 'Ext.data.Model'
});

Ext.define('Bio.data.Store', {
    extend: 'Ext.data.Store',
    alias: 'store.biorest',


    constructor: function(config) {
        var me = this;
//        var url = config.url || "http://vps-nexus-bio4j.cloud.tilaa.com:9090/bio4j-spi/test/sproc";
        var url = config.url || "http://localhost:9090/bio4j-spi/test/sproc";
        config = Ext.apply({
            model: 'Bio.data.Model',
            proxy: {
                type: 'biorest',
                url: url
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
