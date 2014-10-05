Ext.define('Bio.data.RestProxy', {
    extend: 'Ext.data.proxy.Ajax',
    alias : 'proxy.biorest',

    actionMethods: {
        create : 'POST',
        read   : 'POST',
        update : 'POST',
        destroy: 'POST'
    },

    prepareBioParams: function(store, options) {
        var params = store.bioParams;
        if(options && options.params) {
            for(var p in options.params){
                if(!Ext.Array.contains(["page", "start", "limit"], p)) {
                    params = params || [];
                    params.push({
                        type: 'string',
                        name: p,
                        value: options.params[p]
                    });
                }
            }
        }
        return params;
    },

    buildRequestRead: function(superMethod, options) {
        var me = this;
        var store = me.store;
        var request = superMethod(options);
        request.method = 'POST';
        var offset = ((options.page - 1) * options.limit);
        var params = me.prepareBioParams(store, options);

        var sort = null, sorters = options.sorters;
        if(sorters instanceof Array){
            sort = [];
            Ext.Array.forEach(sorters, function(e) {
                sort.push({
                    fieldName: e.property,
                    direction: e.direction
                });
            });
        }

        if(Ext.isDefined(options.id))
            request.jsonData = new Bio.request.store.GetRecord({
                bioCode: store.bioCode,
                storeId: store.storeId,
                bioParams: params,
                id: options.id
            });
        else
            request.jsonData = new Bio.request.store.GetDataSet({
                bioCode: store.bioCode,
                storeId: store.storeId,
                bioParams: params,
                totalCount: store.totalCount,
                offset: offset,
                pageSize: options.limit,
                sort: sort,
                location: options.locate
            });
        return request;
    },

    buildRequestCUD: function(superMethod, options) {
        var me = this;
        var store = me.store;
        var request = superMethod(options);
        request.method = 'POST';
        var params = me.prepareBioParams(store, options),
            postData = options.postData;

        request.jsonData = Ext.apply(postData, {
            bioParams: params
        });

        return request;
    },

    //inherit docs
    buildRequest: function(options) {
        var me = this,
            superMethod = Ext.Function.bind(me.superclass.buildRequest, me),
            request;
        options.url = Bio.tools.bldBioUrl("/biosrv");
        if(options.action == 'read')
            request = me.buildRequestRead(superMethod, options);
        else if(options.action == 'crupdel')
            request = me.buildRequestCUD(superMethod, options);
        if(request) {
            request.params = {
                rqt: request.jsonData.rqt
            };
            return request;
        } else
            return undefined;
    },

    processResponse: function(success, operation, request, response, callback, scope) {
        var me = this,
            reader, responseData, result;

        if(operation.action !== 'crupdel') {
            me.callParent(arguments);
            return;
        }


        responseData =  data = Ext.decode(response.responseText);

        Ext.apply(operation, {
            response: response,
            responseData: responseData
        });

        if (success !== true) {
            me.fireEvent('exception', this, operation);
        }


        if (typeof operation.callback == 'function') {
            operation.callback.call(operation.scope || me, operation);
        }

        //me.afterRequest(request, success);
    }
});
