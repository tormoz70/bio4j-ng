Ext.namespace("Bio.form");
Ext.define('Bio.form.ComboBox', {
    extend: 'Ext.form.ComboBox',
    alias: ['widget.biocombobox', 'widget.biocombo'],
    initComponent: function () {
        var me = this;
        me.callParent(arguments);
    },


    //badRemoteStore : false,
    storeLoadExecuted : false,
    setValue: function (v) {
        if(Ext.isDefined(v)) {
            var me = this;
            this.callParent(arguments);
            //this.badRemoteStore = me.storeLoadExecuted && me.valueField && me.store && (me.store.data.length === 0);
            if (!this.storeLoadExecuted && me.valueField && me.store && (me.store.data.length === 0)) {
                me.storeLoadExecuted = true;
                me.store.load({
                    scope: me,
                    callback: function(records, operation, success) {
                        //
                    }
                });
            }
        } else
            this.callParent(arguments);
    }
})
