Ext.define('Bio.data.RestProxy', {
    extend: 'Ext.data.proxy.Ajax',
    alias : 'proxy.biorest',

    actionMethods: {
        create : 'POST',
        read   : 'POST',
        update : 'POST',
        destroy: 'POST'
    },

    //inherit docs
    buildRequest: function(operation) {
        var me = this;
        var store = me.store;
        var request = me.callParent(arguments);
        request.url = "/ekb/biosrvfwd";
        request.method = 'POST';
        var offset = ((operation.page - 1) * operation.limit);
        var params = Ext.apply({}, store.bioParams);
//        params = Ext.apply(params, operation.params);
//        params = {
//            query:{value: "SAL%", type: 'string'}
//        };
        request['jsonData'] = Bio.request.store.GetData.jsonData({
            bioCode: store.bioCode,
            bioParams: params,
            offset: offset,
            pagesize: operation.limit
        });
        request.params = {
            rqt: 'crud.dt.gt'
        };
        return request;
    }

});
