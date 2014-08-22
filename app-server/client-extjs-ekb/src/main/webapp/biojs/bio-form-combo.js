Ext.namespace("Bio.form");
Ext.define('Bio.form.ComboBox', {
    extend: 'Ext.form.ComboBox',
    alias: ['widget.biocombobox', 'widget.biocombo'],

    constructor: function(config) {
        var me = this;
        if(config.pageSize) {
            if (config.store && config.store)
                config.store.pageSize = config.pageSize;
        } else {
            if (config.store && config.store.pageSize && config.store.pageSize > 0)
                config.pageSize = config.store.pageSize;
            else
                config.store.pageSize = config.pageSize = 25;
        }
        me.callParent([config]);
    },

    initComponent: function () {
        var me = this;
        me.callParent(arguments);
    },

    setValue0: function(value, doSelect) {
        var me = this,
            valueNotFoundText = me.valueNotFoundText,
            inputEl = me.inputEl,
            i, len, record,
            dataObj,
            matchedRecords = [],
            displayTplData = [],
            processedValue = [];

        if (me.store.loading) {

            me.value = value;
            me.setHiddenValue(me.value);
            return me;
        }


        value = Ext.Array.from(value);


        for (i = 0, len = value.length; i < len; i++) {
            record = value[i];
            if (!record || !record.isModel) {
                record = me.findRecordByValue(record);
            }

            if (record) {
                matchedRecords.push(record);
                displayTplData.push(record.data);
                processedValue.push(record.get(me.valueField));
            }


            else {


                if (!me.forceSelection) {
                    processedValue.push(value[i]);
                    dataObj = {};
                    dataObj[me.displayField] = value[i];
                    displayTplData.push(dataObj);

                }

                else if (Ext.isDefined(valueNotFoundText)) {
                    displayTplData.push(valueNotFoundText);
                }
            }
        }


        me.setHiddenValue(processedValue);
        me.value = me.multiSelect ? processedValue : processedValue[0];
        if (!Ext.isDefined(me.value)) {
            me.value = null;
        }
        me.displayTplData = displayTplData;
        me.lastSelection = me.valueModels = matchedRecords;

        if (inputEl && me.emptyText && !Ext.isEmpty(value)) {
            inputEl.removeCls(me.emptyCls);
        }


        me.setRawValue(me.getDisplayValue());
        me.checkChange();

        if (doSelect !== false) {
            me.syncSelection();
        }
        me.applyEmptyText();

        return me;
    },

    setValue: function (v) {
        var me = this,
            args = arguments,
            locateVal = v;
        if (locateVal) {
            if (!me.store.loading && me.valueField && me.store) {
                me.store.locate(locateVal, 1,
                    function(records, eOpts, successful) {
                        var me = this;
                        me.setValue0(locateVal);
                    }, me);
            }
        } else
            me.callParent(arguments);
    },

    doRemoteQuery1: function(queryPlan) {
        var me = this;
//            loadCallback = function() {
//                me.afterQuery(queryPlan);
//            };

        // expand before loading so LoadMask can position itself correctly
        me.expand();

        // In queryMode: 'remote', we assume Store filters are added by the developer as remote filters,
        // and these are automatically passed as params with every load call, so we do *not* call clearFilter.
//        if (me.pageSize) {
//            // if we're paging, we've changed the query so start at page 1.
//            me.loadPage(1, {
//                rawQuery: queryPlan.rawQuery,
//                callback: loadCallback
//            });
//        } else {
//            me.store.load({
//                params: me.getParams(queryPlan.query),
//                rawQuery: queryPlan.rawQuery,
//                callback: loadCallback
//            });
//        }

        if (!this.store.loading && me.valueField && me.store) {
            var tryLocateValue = me.value;
            if(tryLocateValue)
                me.store.locate(tryLocateValue, 1,
                    function (records, eOpts, successful) {
                        var me = this;
                        me.afterQuery(queryPlan);
                    }, me);
        } else
            me.afterQuery(queryPlan);
    },


    onLoad1: function(store, records, success) {
        var me = this;

        if (me.ignoreSelection > 0) {
            --me.ignoreSelection;
        }


        if (success && !store.lastOptions.rawQuery) {



            if (me.value == null) {

                if (me.store.getCount()) {
                    me.doAutoSelect();
                } else {

                    me.setValue(me.value);
                }
            } else {
                me.setValue(me.value);
            }
        }
    }
})
