Ext.override(Ext.form.Panel, {
    /*
     * resets originalValue in all fields
     * which makes isDirty false
     */
    resetOriginal: function(){
        var fields = this.getForm().getFields();
        for(var i = 0; i < this.getForm().getFields().length; ++i){
            fields.items[i].resetOriginalValue();
        }
    },

    loadData: function(store, id, callback, scope) {
        var me = this;
        store.load({
            id: id,
            callback: function(records, operation, success) {
                var store = this,
                    f = me.getForm();
                me.trackResetOnLoad = true;
                if(store.data.items.length == 0)
                    store.insert(0, {});
                var rec = store.data.items[0];
                if(rec) {
                    me.getForm().loadRecord(rec);
                    me.resetOriginal();
                }
                if(callback && typeof callback == 'function')
                    callback.call(scope || me, rec, operation, success);
            }
        });
    },

    applyResponseToStore: function(response, store) {
        if(Bio.tools.isDefined(response.packet)) {
            var responseRows = response.packet.rows,
                recIndex, rec, fld;
            Ext.Array.forEach(responseRows, function (r) {
                recIndex = store.findBy(function (r0) {
                    return r0.id == r.internalId;
                });
                if (recIndex != -1) {
                    rec = store.getAt(recIndex);
                    for (fld in r.data)
                        if (fld) rec.set(fld, r.data[fld]);
                    rec.commit();
                }
            });
        }
    },

    processResponse: function(operation) {
        var me = this,
            store = me.getRecord().store,
            slaveStores = operation.slaveStores,
            responseData = operation.responseData,
            slaveResponses = (responseData) ? responseData.slaveResponses : undefined;
        if(slaveResponses && slaveResponses instanceof Array && slaveResponses.length > 0)
            Ext.Array.forEach(slaveResponses, function(rsp) {
                if(slaveStores && slaveStores instanceof Array && slaveStores.length > 0) {
                    var s = Ext.Array.findBy(slaveStores, function (n) {
                        return n.storeId == rsp.packet.storeId
                    });
                    me.applyResponseToStore(rsp, s);
                }
            });
        me.applyResponseToStore(responseData, store);
    },

    innerStoresHasChanges: function() {
        var me = this,
            innerGrids = Bio.tools.childByClassName(me, 'Bio.grid.Panel'),
            innerStores = [];
        if(innerGrids && innerGrids instanceof Array)
            Ext.Array.forEach(innerGrids, function(g) { innerStores.push(g.store); });
        var store, modified;
        for (var i = 0; i < innerStores.length; i++) {
            store = innerStores[i];
            modified = store.getModifiedRecords();
            if(modified && Ext.isArray(modified) && modified.length > 0)
                return true;
            modified = store.getRemovedRecords();
            if(modified && Ext.isArray(modified) && modified.length > 0)
                return true;
        }
        return false;
    },

    postData: function(options) {
        var me = this;

        if ((me && me.isValid()) && (me.isDirty() || me.innerStoresHasChanges())) {
            me.updateRecord();
            var store = me.getRecord().store;
            if(store) {
                var innerGrids = Bio.tools.childByClassName(me, 'Bio.grid.Panel'),
                    innerStores = [];
                if(innerGrids && innerGrids instanceof Array)
                    Ext.Array.forEach(innerGrids, function(g) { innerStores.push(g.store); });

                Ext.MessageBox.wait("Сохранение...", "Подождите");

                store.save({
                    slaveStores: innerStores,
                    forcePost: true,
                    callback: {
                        fn: function (operation) {
                            Ext.MessageBox.hide();
                            me.processResponse(operation);
                            Bio.tools.processCallback(options.callback, options.scope || me, operation);
                        },
                        scope: me
                    }
                });
            }
        } else
            Ext.Msg.show({
                title: "Сохранение",
                msg: "Нет чего сохранять!!!",
                width: 300,
                buttons: Ext.Msg.OK,
                multiline: false,
                fn: Ext.emptyFn(),
                //animateTarget: 'addAddressBtn',
                icon: Ext.MessageBox.INFO
            });
    }

});
