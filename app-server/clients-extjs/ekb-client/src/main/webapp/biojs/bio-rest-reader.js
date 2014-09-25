Ext.define('Bio.data.RestReader', {
    extend: 'Ext.data.reader.Json',
    alias: 'reader.biorest',

    root: 'packet.rows',

    metaProperty: 'packet.metadata',
    totalProperty: 'packet.results',
    record: 'data',
    messageProperty: 'errMessage',
    useSimpleAccessors: false,

    getResponseData: function(response) {
        var data;
        data = Ext.decode(response.responseText);
        return this.readRecords(data);
    },

    readRecords: function(data) {
//        console.log(data);
        var me = this;

        if(data.exception)
            data.errMessage = data.exception.message;

        return me.callParent([data]);
    }

});