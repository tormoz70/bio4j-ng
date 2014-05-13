Ext.define('Bio.data.RestWriter', {
    extend: 'Ext.data.writer.Json',
    alias : 'writer.biorest',

    //inherit docs
    buildRequest: function(operation) {
        var me = this;
        return me.callParent(arguments);
    },

    write: function(request) {
        return request;
    }
});
