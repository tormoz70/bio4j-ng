Ext.define('Bio.data.RestProxy', {
    extend: 'Ext.data.proxy.Ajax',
    alias : 'proxy.biorest',

    actionMethods: {
        create : 'POST',
        read   : 'POST',
        update : 'POST',
        destroy: 'POST'
    },

    prepareBioParams: function(store, operation) {
        var params = store.bioParams;
        if(operation && operation.params) {
            for(var p in operation.params){
                if(!Ext.Array.contains(["page", "start", "limit"], p)) {
                    params = params || [];
                    params.push({
                        type: 'string',
                        name: p,
                        value: operation.params[p]
                    });
                }
            }
        }
        return params;
    },

    buildRequestRead: function(superMethod, operation) {
        var me = this;
        var store = me.store;
        operation.url = Bio.tools.bldBioUrl("/biosrv");
        var request = superMethod(operation);
        request.method = 'POST';
        var offset = ((operation.page - 1) * operation.limit);
        var params = me.prepareBioParams(store, operation);

        var sort = null, sorters = operation.sorters;
        if(sorters instanceof Array){
            sort = [];
            sorters.forEach(function(e) {
                sort.push({
                    fieldName: e.property,
                    direction: e.direction
                });
            });
        }

        if(operation.id)
            request.jsonData = Bio.request.store.GetRecord.jsonData({
                bioCode: store.bioCode,
                bioParams: params,
                id: operation.id
            });
        else
            request.jsonData = Bio.request.store.GetDataSet.jsonData({
                bioCode: store.bioCode,
                bioParams: params,
                totalCount: store.totalCount,
                offset: offset,
                pageSize: operation.limit,
                sort: sort,
                location: operation.locate
            });
        return request;
    },

    buildRequestCUD: function(superMethod, operation) {
        var me = this;
        var store = me.store;
        operation.url = Bio.tools.bldBioUrl("/biosrv");
        var request = superMethod(operation);
        request.method = 'POST';
        var params = me.prepareBioParams(store, operation),
            operations = operation.operations,
            rows = [];
        if(operations)
            for(var operName in operations) {
                var oper = operations[operName];
                oper.forEach(function(r) {
                    rows.push({
                        changeType: operName,
                        class: 'ru.bio4j.ng.model.transport.jstore.StoreRow',
                        values: Bio.tools.objToArray(r.data)
                    });
                });
            }

        request.jsonData = Bio.request.store.PostData.jsonData({
            bioCode: store.bioCode,
            bioParams: params,
            modified: rows
        });

        return request;
    },

    //inherit docs
    buildRequest: function(operation) {
        var me = this,
            superMethod = Ext.Function.bind(me.superclass.buildRequest, me),
            request;
        if(operation.action == 'read')
            request = me.buildRequestRead(superMethod, operation);
        else if(operation.action == 'crupdel')
            request = me.buildRequestCUD(superMethod, operation);
        if(request) {
            request.params = {
                rqt: request.jsonData.rqt
            };
            return request;
        } else
            return undefined;
    }

});
