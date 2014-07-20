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

    request0 : function(options) {
        options = options || {};
        var me = this,
            scope = options.scope || window,
            username = options.username || me.username,
            password = options.password || me.password || '',
            async,
            requestOptions,
            request,
            headers,
            xhr;
        if (me.fireEvent('beforerequest', me, options) !== false) {

            requestOptions = me.setOptions(options, scope);

            if (me.isFormUpload(options)) {
                me.upload(options.form, requestOptions.url, requestOptions.data, options);
                return null;
            }


            if (options.autoAbort || me.autoAbort) {
                me.abort();
            }


            async = options.async !== false ? (options.async || me.async) : false;
            xhr = me.openRequest(options, requestOptions, async, username, password);


            if (!me.isXdr) {
                headers = me.setupHeaders(xhr, options, requestOptions.data, requestOptions.params);
            }


            request = {
                id: ++Ext.data.Connection.requestId,
                xhr: xhr,
                headers: headers,
                options: options,
                async: async,
                binary: options.binary || me.binary,
                timeout: setTimeout(function() {
                    request.timedout = true;
                    me.abort(request);
                }, options.timeout || me.timeout)
            };

            me.requests[request.id] = request;
            me.latestId = request.id;

            if (async) {
                if (!me.isXdr) {
                    xhr.onreadystatechange = Ext.Function.bind(me.onStateChange, me, [request]);
                }
            }

            if (me.isXdr) {
                me.processXdrRequest(request, xhr);
            }


            xhr.send(requestOptions.data);
            if (!async) {
                return me.onComplete(request);
            }
            return request;
        } else {
            Ext.callback(options.callback, options.scope, [options, undefined, undefined]);
            return null;
        }
    },

    request : function(options) {
        var me = this;
        return Bio.login.getUser({
            fn: function(usr) {
                var o = Ext.apply({}, options);
                var p = {
                    uid : usr.login||usr.uid
                };
//                if(usr.uid)
//                    p.uid = usr.uid;
//                if(usr.login)
//                    p.login = usr.login;
                Ext.apply(o.params, p);
                return me.request0(o);
            },
            scope: me
        });
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

        success = success && Bio.login.processUser(response, function(dr) { me.request(options); });

        if (success === true) {
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
