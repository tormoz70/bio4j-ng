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
        operation.url = Bio.Tools.bldBioUrl("/biosrv");
        var request = me.callParent([operation]);
        request.method = 'POST';
        var offset = ((operation.page - 1) * operation.limit);
        var params = store.bioParams;
//        params = Ext.apply(params, operation.params);
//        params = {
//            query:{value: "SAL%", type: 'string'}
//        };
        request['jsonData'] = Bio.request.store.GetData.jsonData({
            bioModuleKey: store.bioModuleKey,
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
