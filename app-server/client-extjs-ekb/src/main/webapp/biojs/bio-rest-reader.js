Ext.define('Bio.data.RestReader', {
    extend: 'Ext.data.reader.Json',
    alias: 'reader.biorest',

    root: 'packet.rows',

    /**
     * @cfg {String} record The optional location within the JSON response that the record data itself can be found at.
     * See the JsonReader intro docs for more details. This is not often needed.
     */

    /**
     * @cfg {String} [metaProperty="metaData"]
     * Name of the property from which to retrieve the `metaData` attribute. See {@link #metaData}.
     */
    metaProperty: 'packet.metadata',
    totalProperty: 'packet.results',
    messageProperty: 'errMessage',

    /**
     * @cfg {Boolean} useSimpleAccessors True to ensure that field names/mappings are treated as literals when
     * reading values.
     *
     * For example, by default, using the mapping "foo.bar.baz" will try and read a property foo from the root, then a property bar
     * from foo, then a property baz from bar. Setting the simple accessors to true will read the property with the name
     * "foo.bar.baz" direct from the root object.
     */
    useSimpleAccessors: false,

    onMetaChange : function(meta) {
        var me = this,
            fields = meta.columns,
            newModel,
            clientIdProperty;


        me.metaData = meta;


        me.root = meta.root || me.root;
        me.idProperty = meta.idProperty || me.idProperty;
        me.totalProperty = meta.totalProperty || me.totalProperty;
        me.successProperty = meta.successProperty || me.successProperty;
        me.messageProperty = meta.messageProperty || me.messageProperty;
        clientIdProperty = meta.clientIdProperty;

        if (me.model) {
            me.model.setFields(fields, me.idProperty, clientIdProperty);
            me.setModel(me.model, true);
        }
        else {
            newModel = Ext.define("Ext.data.reader.Json-Model" + Ext.id(), {
                extend: 'Ext.data.Model',
                fields: fields,
                clientIdProperty: clientIdProperty
            });
            if (me.idProperty) {
                newModel.idProperty = me.idProperty;
            }
            me.setModel(newModel, true);
        }
    },


    prepareMeta: function(meta) {
        if(meta.columns)
            meta.columns.forEach(function(e) {
                if(e.pk === true){
                    meta.idProperty = e.name;
                    return false;
                }
            });
    },

    /**
     * Reads a JSON object and returns a ResultSet. Uses the internal getTotal and getSuccess extractors to
     * retrieve meta data from the response, and extractData to turn the JSON data into model instances.
     * @param {Object} data The raw JSON data
     * @return {Ext.data.ResultSet} A ResultSet containing model instances and meta data about the results
     */
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