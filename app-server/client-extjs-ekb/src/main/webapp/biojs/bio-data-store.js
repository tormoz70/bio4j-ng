Ext.namespace("Bio");

Ext.define('Bio.data.Store', {
    extend: 'Ext.data.Store',
    alias: 'store.biorest',

    calcCurPage: function(offset) {
        var me = this;
        return (me.pageSize > 0) ? Bio.tools.truncDec(offset / me.pageSize) + 1 : 1;
    },

    constructor: function(config) {
        var me = this,
            implicitModelId = 'Bio.data.Model' + (me.storeId || Ext.id()),
            implicitModel = Ext.define(implicitModelId, {
                extend: 'Ext.data.Model'
            });

        config = Ext.apply({
            model: implicitModelId,
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

        // adding system event handlers
        me.addListener("load", function(records, eOpts, successful) {
            var me = this;
            if(me.proxy.reader.jsonData) {
                var locate = me.lastOptions.locate,
                    grid = me.ownerGrid,
                    bbar = (grid && grid.initialConfig) ? grid.initialConfig.bbar : null,
                    offset = me.proxy.reader.jsonData.packet.offset;
                me.currentPage = me.calcCurPage(offset);
                me.lastOptions.locate = undefined;
                me.lastOptions.page = me.currentPage;
                me.lastOptions.start = offset;

                var showNotAllDataLoadedAlert = (me.pageSize < 0) && me.data.items && (me.data.items.length < me.totalCount);
                if(showNotAllDataLoadedAlert && bbar && bbar.items && bbar.items.items) {
                    bbar.items.items[0].setValue("Внимание превышен лимит загрузки данных!!! Загруженно записей "+me.data.items.length+", всего "+me.totalCount);
                    bbar.setVisible(true);
                }
                me.locateLocal(locate);
            }
        }, me);
        me.addListener("metachange", function(store, meta) {
            var grid = config.ownerGrid;
            if(grid)
                grid.recreateCols(store, meta);
        }, me);
    },

    loadForm: function(form, id, callback) {
        var me = this;
        me.load({
            id: id,
            callback: function(records, operation, success) {
                var f = form.getForm();
                if(records && (records.length > 0))
                    f.loadRecord(records[0]);
                if(callback)
                    callback();
            }
        });
    },

    storeForm: function(form) {
        var me = this;
        form.updateRecord();
    },

    locateLocal: function(location) {
        var me = this,
            grid = me.ownerGrid,
            idProp = me.proxy.reader.getIdProperty(),
            rowIndex = me.find(idProp, location),
            result = rowIndex >= 0;

        if (grid && (result))
            grid.getView().select(rowIndex);

        return result;
    },

    locate: function(location, startFrom, callback, params) {
        var me = this,
            callbackFn = (typeof callback == "function" ? callback : callback.fn),
            callbackScope = (typeof callback == "function" ? me : callback.scope),
            qParamName = me.ownerCombo ? me.ownerCombo.queryParam : null,
            forceRemoteQuery = params && Ext.isDefined(params[qParamName]);
        if(!forceRemoteQuery && me.locateLocal(location)) {
            if(callbackFn)
                Ext.Function.bind(callbackFn, callbackScope).call(me.data.items, null, true);
        } else {
            if (startFrom > 0)
                me.currentPage = me.calcCurPage(startFrom);
            me.load({
                params: params,
                locate: location,
                scope: callbackScope,
                callback: callbackFn
            });
        }
    },

    save: function(options) {
        var me = this,
            operations = {},
            toCreate = me.getNewRecords(),
            toUpdate = me.getUpdatedRecords(),
            toDestroy = me.getRemovedRecords();

        if (toCreate.length > 0) {
            operations.create = toCreate;
        }

        if (toUpdate.length > 0) {
            operations.update = toUpdate;
        }

        if (toDestroy.length > 0) {
            operations.destroy = toDestroy;
        }

        if (me.fireEvent('beforesave', operations) !== false) {
            options = options || {};

            var callback = function(operation) {
                var hasException = operation.hasException();

                if (hasException) {
                    me.hasException = true;
                    me.exceptions.push(operation);
                    me.fireEvent('exception', me, operation);
                }

                if (hasException && me.pauseOnException) {
                    me.pause();
                } else {
                    operation.setCompleted();
                    me.fireEvent('operationcomplete', me, operation);
                    me.runNextOperation();
                }
            };

            me.proxy.doRequest(Ext.apply(options, {
                action: 'crupdel',
                operations: operations,
                allowWrite: function() { return false; }
            }), callback, me.proxy);
        }

        return me;
    }
});
