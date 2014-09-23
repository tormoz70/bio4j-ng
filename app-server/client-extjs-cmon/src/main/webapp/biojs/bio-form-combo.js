Ext.namespace("Bio.form");
Ext.define('Bio.form.ComboBox', {
    extend: 'Ext.form.ComboBox',
    alias: ['widget.biocombobox', 'widget.biocombo'],

    superSetValue: Ext.emptyFn(),

    bioStoreAssigned: false,

    constructor: function(config) {
        var me = this,
            valueFieldDefault = 'id',
            displayFieldDefault = 'caption',
            configDefaults = {
                allowBlank: true,
                valueField: valueFieldDefault,
                displayField: displayFieldDefault,
                minChars: 0,
                queryParam: 'query',
                emptyText: "<не выбрано>",
                selectOnFocus: true,
                tpl: Ext.create('Ext.XTemplate',
                    '<tpl for=".">',
                        '<div class="x-boundlist-item" style="text-align: left">{' + (config.displayField || displayFieldDefault) + '}</div>',
                    '</tpl>'
                ),
                displayTpl: Ext.create('Ext.XTemplate',
                        '<tpl for=".">{' + (config.displayField || displayFieldDefault) + '}</tpl>'
                )
            };

        config = (config) ? Ext.apply(configDefaults, config) : configDefaults;

        me.bioStoreAssigned = config.store && config.store.$className == 'Bio.data.Store';

        if (me.bioStoreAssigned === true) {

            me.superSetValue = Ext.Function.bind(me.superclass.setValue, me);
            config.store.ownerCombo = me;
            if (config.pageSize) {
                config.store.pageSize = config.pageSize;
                if(config.pageSize == -1)
                    config.pageSize = undefined;
            } else {
                if (config.store.pageSize && config.store.pageSize > 0)
                    config.pageSize = config.store.pageSize;
                else
                    config.store.pageSize = config.pageSize = 25;
            }
        }
        me.callParent([config]);
    },

    initComponent: function () {
        var me = this;
        me.callParent(arguments);
    },


    permValueSetted: true,
    tmpValue: undefined,

    getValue: function() {
        var me = this;
        if (me.bioStoreAssigned === true) {
            return (me.permValueSetted === false) ? me.tmpValue : me.value;
        } else {
            var getValueSuper = Ext.Function.bind(Bio.form.ComboBox.superclass.getValue, me);
            return getValueSuper();
        }
    },

    setValue: function (v) {
        var me = this,
            vname = me.name;
        vname = vname + '';
        if (me.bioStoreAssigned === true) {
            var locateVal = v;
            me.tmpValue = locateVal; // lets set temValue? it will returned by getValue while permValueSetted === false
            me.permValueSetted = false;
            if ((v instanceof Array) && (v.length > 0) && v[0].data)
                locateVal = v[0].data[me.valueField];
            if (Bio.tools.isDefined(locateVal)) {
                if (!me.store.loading && me.valueField && me.store) {
                    //console.log('try to locate '+locateVal+' for '+me.name+' combo...');
                    me.store.locate(locateVal, 1,
                        {
                            fn: function (records, eOpts, successful) {
                                var me = this;
                                //console.log('location for '+me.name+' combo done - '+successful+'.');
                                me.superSetValue(locateVal);
                                me.permValueSetted = true; //
                            },
                            scope: me
                        }
                    );
                }
            } else {
                me.callParent(arguments);
                me.permValueSetted = true;
            }
        } else
            me.callParent(arguments);
    },

    expand: function(queryPlan) {
        var me = this,
            bodyEl, picker, collapseIf;
        if (me.bioStoreAssigned === true) {
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
        } else
            me.callParent(arguments);
    },

    doRemoteQuery: function(queryPlan) {
        var me = this;
        if (me.bioStoreAssigned === true) {
            me.expand(queryPlan);

            if (!this.store.loading && me.valueField && me.store) {
                var prms = me.getParams(queryPlan.query);
                me.store.loadPage(1, {
                    params: prms,
                    scope: me,
                    callback: function (records, eOpts, successful) {
                        var me = this;
                        me.afterQuery(queryPlan);
                    }
                });
            } else
                me.afterQuery(queryPlan);
        } else
            me.callParent(arguments);
    },


    onLoad: function(store, records, success) {
        var me = this;
        if (me.bioStoreAssigned === true) {
            if (me.ignoreSelection > 0) {
                --me.ignoreSelection;
            }

            if (success && !store.lastOptions.rawQuery) {
                if (store.locateLocal(me.value)) {
                    me.suspendEvents();
                    me.superSetValue(me.value);
                    me.resumeEvents();
                } else {
                    me.doAutoSelect();
                }

            }
        } else
            me.callParent(arguments);
    },

    onExpand: function(queryPlan) {
        var me = this;
        me.callParent(arguments);
        if (me.bioStoreAssigned === true && me.store.locate && me.valueField && !this.store.loading) {
            var skipLocate = (queryPlan && queryPlan.query && queryPlan.rawQuery === true);

            if(!skipLocate) {
                var tryLocateValue = me.value;
                me.store.locate(tryLocateValue, 1,
                    {
                        fn: function (records, eOpts, successful) {
                            var me = this;
                            me.afterQuery(queryPlan);
                        },
                        scope: me
                    }
                    , me.getParams(queryPlan.query)
                );
            }
        }
    },

    onCollapse: function() {
        var me = this;
        me.callParent(arguments);
        if (me.bioStoreAssigned === true) {
            if (me.displayTplData instanceof Array && me.displayTplData.length > 0) {
                var dispData = me.displayTplData[0],
                    rowVal = dispData[me.displayField];
                me.setRawValue(rowVal);
            }
        }
    }

})