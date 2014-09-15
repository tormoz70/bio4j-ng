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
            sorters.forEach(function(e) {
                sort.push({
                    fieldName: e.property,
                    direction: e.direction
                });
            });
        }

        if(options.id)
            request.jsonData = new Bio.request.store.GetRecord({
                bioCode: store.bioCode,
                bioParams: params,
                id: options.id
            });
        else
            request.jsonData = new Bio.request.store.GetDataSet({
                bioCode: store.bioCode,
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
            reader,
            result;

        if (success === true) {
            reader = me.getReader();

            reader.applyDefaults = operation.action === 'read';

            result = reader.read(me.extractResponseData(response));

            Ext.apply(operation, {
                response: response,
                resultSet: result
            });

            operation.setCompleted();

            if (result.success !== false) {

                operation.commitRecords(result.records);
                operation.setSuccessful();
            } else {
                operation.setException(result.message);
                me.fireEvent('exception', this, response, operation);
            }
        } else {
            me.setException(operation, response);
            me.fireEvent('exception', this, response, operation);
        }


        if (typeof callback == 'function') {
            callback.call(scope || me, operation);
        }

        me.afterRequest(request, success);
    }
});
