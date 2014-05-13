Ext.define('Bio.data.RestReader', {
    extend: 'Ext.data.reader.Reader',
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

    /**
     * @cfg {Boolean} useSimpleAccessors True to ensure that field names/mappings are treated as literals when
     * reading values.
     *
     * For example, by default, using the mapping "foo.bar.baz" will try and read a property foo from the root, then a property bar
     * from foo, then a property baz from bar. Setting the simple accessors to true will read the property with the name
     * "foo.bar.baz" direct from the root object.
     */
    useSimpleAccessors: false,

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
                Ext.Array.forEach(meta.fields, function(f){
                    f.type = f.columnType;
                }, this);
                me.onMetaChange(meta);
            }
        } else if (data.metaData) {
            me.onMetaChange(data.metaData);
        }

        /**
         * @property {Object} jsonData
         * A copy of this.rawData.
         * @deprecated Will be removed in Ext JS 5.0. This is just a copy of this.rawData - use that instead.
         */
        me.jsonData = data;
        return me.callParent([data]);
    },

    //inherit docs
    getResponseData: function(response) {
        var data, error;

        try {
            data = Ext.decode(response.responseText);
            return this.readRecords(data);
        } catch (ex) {
            error = new Ext.data.ResultSet({
                total  : 0,
                count  : 0,
                records: [],
                success: false,
                message: ex.message
            });

            this.fireEvent('exception', this, response, error);

            Ext.Logger.warn('Unable to parse the JSON returned by the server');

            return error;
        }
    },

    //inherit docs
    buildExtractors : function() {
        var me = this,
            metaProp = me.metaProperty;

        me.callParent(arguments);

        if (me.root) {
            me.getRoot = me.createAccessor(me.root);
        } else {
            me.getRoot = Ext.identityFn;
        }

//        me.getMeta = me.getBioMetaData;
        if (metaProp) {
            me.getMeta = me.createAccessor(metaProp);
        }
    },

    /**
     * @private
     * We're just preparing the data for the superclass by pulling out the record objects we want. If a {@link #record}
     * was specified we have to pull those out of the larger JSON object, which is most of what this function is doing
     * @param {Object} root The JSON root node
     * @return {Ext.data.Model[]} The records
     */
    extractData: function(root) {
        var recordName = this.record,
            data = [],
            length, i;

        if (recordName) {
            length = root.length;

            if (!length && Ext.isObject(root)) {
                length = 1;
                root = [root];
            }

            for (i = 0; i < length; i++) {
                data[i] = root[i][recordName];
            }
        } else {
            data = root;
        }
        return this.callParent([data]);
    },

    /**
     * @private
     * @method
     * Returns an accessor function for the given property string. Gives support for properties such as the following:
     *
     * - 'someProperty'
     * - 'some.property'
     * - '["someProperty"]'
     * - 'values[0]'
     *
     * This is used by {@link #buildExtractors} to create optimized extractor functions for properties that are looked
     * up directly on the source object (e.g. {@link #successProperty}, {@link #messageProperty}, etc.).
     */
    createAccessor: (function() {
        var re = /[\[\.]/;

        return function(expr) {
            if (Ext.isEmpty(expr)) {
                return Ext.emptyFn;
            }
            if (Ext.isFunction(expr)) {
                return expr;
            }
            if (this.useSimpleAccessors !== true) {
                var i = String(expr).search(re);
                if (i >= 0) {
                    return Ext.functionFactory('obj', 'return obj' + (i > 0 ? '.' : '') + expr);
                }
            }
            return function(obj) {
                return obj[expr];
            };
        };
    }()),

    /**
     * @private
     * @method
     * Returns an accessor expression for the passed Field. Gives support for properties such as the following:
     *
     * - 'someProperty'
     * - 'some.property'
     * - '["someProperty"]'
     * - 'values[0]'
     *
     * This is used by {@link #buildRecordDataExtractor} to create optimized extractor expressions when converting raw
     * data into model instances. This method is used at the field level to dynamically map values to model fields.
     */
//    createFieldAccessExpression: (function() {
//        var re = /[\[\.]/;
//
//        return function(field, fieldVarName, dataName) {
//            var mapping = field.mapping,
//                hasMap = mapping || mapping === 0,
//                map    = hasMap ? mapping : field.name,
//                result,
//                operatorIndex;
//
//            // mapping: false means that the Field will never be read from server data.
//            if (mapping === false) {
//                return;
//            }
//
//            if (typeof map === 'function') {
//                result = fieldVarName + '.mapping(' + dataName + ', this)';
//            } else if (this.useSimpleAccessors === true || ((operatorIndex = String(map).search(re)) < 0)) {
//                if (!hasMap || isNaN(map)) {
//                    // If we don't provide a mapping, we may have a field name that is numeric
//                    map = '"' + map + '"';
//                }
//                result = dataName + "[" + map + "]";
//            } else if (operatorIndex === 0) {
//                // If it matched at index 0 then it must be bracket syntax (e.g. ["foo"]). In this case simply
//                // join the two, e.g. 'field["foo"]':
//                result = dataName + map;
//            } else {
//                // If it matched at index > 0 it must be either dot syntax (e.g. field.foo) or a values array
//                // item (e.g. values[0]). For the latter, we can simply concatenate the values reference to
//                // the source directly like 'field.values[0]'. For dot notation we have to support arbitrary
//                // levels (field.foo.bar), any of which could be null or undefined, so we have to create the
//                // returned value such that the references will be assigned defensively in the calling code.
//                // The output should look like 'field.foo && field.foo.bar' in that case.
//                var parts = map.split('.'),
//                    len = parts.length,
//                    i = 1,
//                    tempResult = dataName + '.' + parts[0],
//                    buffer = [tempResult]; // for 'field.values[0]' this will be the returned result
//
//                for (; i < len; i++) {
//                    tempResult += '.' + parts[i];
//                    buffer.push(tempResult);
//                }
//                result = buffer.join(' && ');
//            }
//            return result;
//        };
//    }())


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