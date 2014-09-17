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
            implicitModelId = 'Bio.data.Model-' + (me.storeId || Ext.id()),
            implicitModel = Ext.define(implicitModelId, {
                extend: 'Ext.data.Model'
            });

        config = Ext.apply({
            storeId: config.bioCode + "-" + Ext.id(),
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

//    loadForm: function(form, id, callback) {
//        var me = this;
//        me.load({
//            id: id,
//            callback: function(records, operation, success) {
//                var f = form.getForm();
//                f.trackResetOnLoad = true;
//                if(records && (records.length > 0)) {
//                    f.loadRecord(records[0]);
//                    f.resetOriginal();
//                    //f.reset();
//                }
//                if(callback)
//                    callback();
//            }
//        });
//    },

//    storeForm: function(form) {
//        var me = this;
//        if(form.isDirty())
//            form.updateRecord();
//        else
//            Ext.MessageBox.showDialog("Нет изменений!!!");
//    },

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

    /**
     * Если forcePost === true, то даже если не было изменений, то всеравно возвращается первая стора
     * @param forcePost
     * @returns {Array}
     */
    getPostRows: function(forcePost) {
        var me = this,
            operations = {},
            toCreate = me.getNewRecords(),
            toUpdate = me.getUpdatedRecords(),
            toDestroy = me.getRemovedRecords(),
            rows = [];
        if (toCreate.length > 0)
            operations.create = toCreate;
        if (toUpdate.length > 0)
            operations.update = toUpdate;
        if (toDestroy.length > 0)
            operations.destroy = toDestroy;
        for(var operName in operations) {
            var oper = operations[operName];
            oper.forEach(function(r) {
                rows.push({
                    internalId: r.data.internalId,
                    changeType: operName,
                    data: r.data
                });
            });
        }
        if(rows.length == 0 && forcePost === true) {
            var firstRow = (me.data.items && (me.data.items.length > 0)) ? me.data.items[0].data : undefined;
            if(firstRow)
                rows.push({
                    internalId: firstRow.internalId,
                    changeType: 'update',
                    data: firstRow
                });
        }
        return rows;
    },

    getPostData: function(options) {
        var me = this,
            slaveStores = (options.slaveStores) ? (options.slaveStores instanceof Array ? options.slaveStores : [options.slaveStores]) : undefined,
            slavePostData,
            forcePost = options.forcePost === true,
            rows = me.getPostRows(forcePost);
        options.forcePost = undefined;

        if(slaveStores) {
            slavePostData = [];
            slaveStores.forEach(function(s) {
                var post = s.getPostData({});
                if (post)
                    slavePostData.push(post);
            });
        }

        if(rows.length > 0) {
            return new Bio.request.store.PostData({
                storeId: me.storeId,
                bioCode: me.bioCode,
                modified: rows,
                slavePostData: slavePostData
            });
        } else
            return undefined;
    },

    commitSaved: function(operation) {
        // TODO update data in client datasets from response !!!!



        operation.setCompleted();
    },

    save: function(options) {
        var me = this;

        if (me.fireEvent('beforesave', options) !== false) {
            options = options || {};

            var pd = me.getPostData(options);
            me.proxy.doRequest(new Ext.data.Operation(Ext.apply(options, {
                action: 'crupdel',
                postData: pd,
                //allowWrite: function() { return false; },
                callback: function(operation) {
                    var me = this;

                    me.commitSaved(operation);

                    me.fireEvent('savecomplete', me, operation);
                    if(operation.callback && (typeof operation.callback.fn == 'function'))
                        operation.callback.fn.call(operation.callback.scope || me, operation);
                },
                scope: me
            })));
        }

        return me;
    }
});
