Ext.namespace("Bio.form");
Ext.define('Bio.form.ComboBox', {
    extend: 'Ext.form.ComboBox',
    alias: ['widget.biocombobox', 'widget.biocombo'],

    superSetValue: Ext.emptyFn(),

    constructor: function(config) {
        var me = this;
        me.superSetValue = Ext.Function.bind(Bio.form.ComboBox.superclass.setValue, me);
        if(config.pageSize) {
            if (config.store && config.store)
                config.store.pageSize = config.pageSize;
        } else {
            if (config.store && config.store.pageSize && config.store.pageSize > 0)
                config.pageSize = config.store.pageSize;
            else
                config.store.pageSize = config.pageSize = 25;
        }
        me.callParent([config]);
    },

    initComponent: function () {
        var me = this;
        me.callParent(arguments);
    },

    setValue: function (v) {
        var me = this,
            args = arguments,
            locateVal = v;
        if((v instanceof Array) && (v.length > 0) && v[0].data)
            locateVal = v[0].data[me.valueField];
        if (locateVal) {
            if (!me.store.loading && me.valueField && me.store) {
                me.store.locate(locateVal, 1,
                    {
                        fn: function(records, eOpts, successful) {
                            var me = this;
                            //me.setValue0(locateVal);
                            me.superSetValue(locateVal);
                        },
                        scope: me
                    }
                );
            }
        } else
            me.callParent(arguments);
    },

    doQuery: function(queryString, forceAll, rawQuery) {
        var me = this,


            queryPlan = me.beforeQuery({
                query: queryString || '',
                rawQuery: rawQuery,
                forceAll: forceAll,
                combo: me,
                cancel: false
            });


        if (queryPlan === false || queryPlan.cancel) {
            return false;
        }


        //if (me.queryCaching && queryPlan.query === me.lastQuery) {
        //    me.expand();
        //}


        else {
            me.lastQuery = queryPlan.query;

            if (me.queryMode === 'local') {
                me.doLocalQuery(queryPlan);

            } else {
                me.doRemoteQuery(queryPlan);
            }
        }

        return true;
    },

    expand: function(queryPlan) {
        var me = this,
            bodyEl, picker, collapseIf;

        if (me.rendered && !me.isExpanded && !me.isDestroyed) {
            me.expanding = true;
            bodyEl = me.bodyEl;
            picker = me.getPicker();
            collapseIf = me.collapseIf;


            picker.show();
            me.isExpanded = true;
            me.alignPicker();
            bodyEl.addCls(me.openCls);


            me.mon(Ext.getDoc(), {
                mousewheel: collapseIf,
                mousedown: collapseIf,
                scope: me
            });
            Ext.EventManager.onWindowResize(me.alignPicker, me);
            me.fireEvent('expand', me);
            me.onExpand(queryPlan);
            delete me.expanding;
        }
    },

    doRemoteQuery: function(queryPlan) {
        var me = this;
//            loadCallback = function() {
//                me.afterQuery(queryPlan);
//            };

        // expand before loading so LoadMask can position itself correctly
        //me.suspendEvents();
        me.expand(queryPlan);
        //me.resumeEvents();

        // In queryMode: 'remote', we assume Store filters are added by the developer as remote filters,
        // and these are automatically passed as params with every load call, so we do *not* call clearFilter.
//        if (me.pageSize) {
//            // if we're paging, we've changed the query so start at page 1.
//            me.loadPage(1, {
//                rawQuery: queryPlan.rawQuery,
//                callback: loadCallback
//            });
//        } else {
//            me.store.load({
//                params: me.getParams(queryPlan.query),
//                rawQuery: queryPlan.rawQuery,
//                callback: loadCallback
//            });
//        }

        if (!this.store.loading && me.valueField && me.store) {
            var tryLocateValue = me.value;
            //if(tryLocateValue)
                me.store.locate(tryLocateValue, 1,
                    {
                        fn: function (records, eOpts, successful) {
                            var me = this;
                            me.afterQuery(queryPlan);
                        },
                        scope: me
                    },
                    me.getParams(queryPlan.query));
        } else
            me.afterQuery(queryPlan);
    },


    onLoad: function(store, records, success) {
        var me = this;

        if (me.ignoreSelection > 0) {
            --me.ignoreSelection;
        }


        if (success && !store.lastOptions.rawQuery) {


//            if(!store.locateLocal(me.value)) {
//                me.value = null;
//                me.suspendEvents();
//                me.superSetValue(me.value);
//                me.resumeEvents();
//            }
//
//            if (me.value == null) {
//
//                if (me.store.getCount()) {
//                    me.doAutoSelect();
//                } else {
//                    me.suspendEvents();
//                    me.superSetValue(me.value);
//                    me.resumeEvents();
//                }
//            } else {
//                me.suspendEvents();
//                me.superSetValue(me.value);
//                me.resumeEvents();
//            }

            if(store.locateLocal(me.value)) {
                me.suspendEvents();
                me.superSetValue(me.value);
                me.resumeEvents();
            } else {
                me.doAutoSelect();
            }

        }
    },

    onExpand: function(queryPlan) {
        var me = this;
        me.callParent(arguments);
        if (!this.store.loading && me.valueField && me.store) {
            var tryLocateValue = me.value;
            //if(tryLocateValue)
            me.store.locate(tryLocateValue, 1,
                {
                    fn: function (records, eOpts, successful) {
                        var me = this;
                        me.afterQuery(queryPlan);
                    },
                    scope: me
                }
                ,me.getParams(queryPlan.query)
            );
        }
    },

    onCollapse: function() {
        var me = this;
        me.callParent(arguments);
    }

})
