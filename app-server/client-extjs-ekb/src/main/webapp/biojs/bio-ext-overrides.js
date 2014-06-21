Ext.override(Ext.data.Store, {
    constructor: function(config) {
        var me = this, data;
        if(config && config.proxy)
            config.proxy['store'] = me;
        //me.initConfig(config);
        var storeAutoLoad = config.autoLoad;
        config.autoLoad = false;
        me.callParent([config]);
        me.setModel = function(m){me.model = m};
        me.autoLoad = storeAutoLoad;
        data = me.inlineData;
        if(!data && me.autoLoad)
            Ext.defer(me.load, 1, me, [ typeof me.autoLoad === 'object' ? me.autoLoad : undefined ]);
    }
});

Ext.override(Ext.data.Connection, {

    request : function(options) {
        var me = this;
        // get login from User object
        // if !login then get login from login form...
        if(options.url === "/login")
            return this.callParent([options]);
        else {
            return Bio.login.getUser({
                fn: function(user) {
                    return this.callParent([options]);
                },
                scope: me
            });
        }
    },

    onComplete0 : function(request, xdrResult) {
        var me = this,
            options = request.options,
            result,
            success,
            response;

        try {
            result = me.parseStatus(request.xhr.status);
        } catch (e) {
            // in some browsers we can't access the status if the readyState is not 4, so the request has failed
            result = {
                success : false,
                isException : false
            };

        }
        success = me.isXdr ? xdrResult : result.success;

        if (success) {
            response = me.createResponse(request);
            me.fireEvent('requestcomplete', me, response, options);
            Ext.callback(options.success, options.scope, [response, options]);
        } else {
            if (result.isException || request.aborted || request.timedout) {
                response = me.createException(request);
            } else {
                response = me.createResponse(request);
            }
            me.fireEvent('requestexception', me, response, options);
            Ext.callback(options.failure, options.scope, [response, options]);
        }
        Ext.callback(options.callback, options.scope, [options, success, response]);
        delete me.requests[request.id];
        return response;
    },

    onComplete : function(request, xdrResult) {
        var me = this,
            options = request.options,
            result,
            success,
            response;

        try {
            result = me.parseStatus(request.xhr.status);
        } catch (e) {
            // in some browsers we can't access the status if the readyState is not 4, so the request has failed
            result = {
                success : false,
                isException : false
            };

        }
        success = me.isXdr ? xdrResult : result.success;
        if (success)
            response = me.createResponse(request);
        else {
            if (result.isException || request.aborted || request.timedout)
                response = me.createException(request);
            else
                response = me.createResponse(request);
        }


        if (success) {
            me.fireEvent('requestcomplete', me, response, options);
            Ext.callback(options.success, options.scope, [response, options]);
        } else {
            me.fireEvent('requestexception', me, response, options);
            Ext.callback(options.failure, options.scope, [response, options]);
        }
        Ext.callback(options.callback, options.scope, [options, success, response]);
        delete me.requests[request.id];
        return response;
    }
});
