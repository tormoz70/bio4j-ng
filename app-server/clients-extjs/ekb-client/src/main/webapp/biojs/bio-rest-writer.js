Ext.define('Bio.data.RestWriter', {
    extend: 'Ext.data.writer.Json',
    alias : 'writer.biorest',

    write: function(request) {
        return request;
    }
});
