Ext.define('Bio.data.RestReader', {
    extend: 'Ext.data.reader.Json',
    alias: 'reader.biorest',

    root: 'packet.rows',

    metaProperty: 'packet.metadata',
    totalProperty: 'packet.results',
    messageProperty: 'errMessage',
    useSimpleAccessors: false,

    prepareMeta: function(meta) {
        if(meta.fields)
            meta.fields.forEach(function(e) {
                if(e.pk === true){
                    meta.idProperty = e.name;
                    return false;
                }
            });
    },

    readRecords: function(data) {
//        console.log(data);
        var me = this,
            meta;

        //this has to be before the call to super because we use the meta data in the superclass readRecords
        if (me.getMeta) {
            meta = me.getMeta(data);
            if (meta) {
                me.prepareMeta(meta);
            }
        }

        if(data.exception)
            data.errMessage = data.exception.message;

        return me.callParent([data]);
    },

    createFieldAccessExpression: function(field, fieldVarName, dataName) {
        // In the absence of a mapping property, use the original ordinal position
        // at which the Model inserted the field into its collection.
        var index  = (field.mapping == null) ? field.originalIndex : field.mapping,
            result;

        if (typeof index === 'function') {
            result = fieldVarName + '.mapping(' + dataName + ', this)';
        } else {
            if (isNaN(index)) {
                index = '"' + index + '"';
            }
            result = dataName + ".values[" + index + "]";
        }
        return result;
    }

});