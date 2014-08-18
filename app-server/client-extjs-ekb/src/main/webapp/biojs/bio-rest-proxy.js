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

        request['jsonData'] = Bio.request.store.GetData.jsonData({
            bioCode: store.bioCode,
            bioParams: params,
            totalCount: store.totalCount,
            offset: offset,
            pageSize: operation.limit,
            sort: sort,
            location: operation.locate,
            id: operation.id
        });
        request.params = {
            rqt: 'crud.dt.gt'
        };
        return request;
    }

});
