//Ext.namespace('Bio.form.Action');
////Bio.form.Action.Load = Ext.extend(Ext.form.Action.Load, {
//Ext.define('Bio.form.Action.Load', {
//    extend : 'Ext.form.Action.Load',
//    constructor: function(form, opts) {
//        Bio.form.Action.Load.superclass.constructor.call(this, form, opts);
//    },
//    xtype : 'formaction.bioload',
//
//    run : function(){
//        var args = this.getParams();
//        args.push(this.success, this);
//        //this.form.api.load.apply(window, args);
//    },
//
//    getParams : function() {
//        var buf = [], o = {};
//        var bp = this.form.baseParams;
//        var p = this.options.params;
//        Ext.apply(o, p, bp);
//        var paramOrder = this.form.paramOrder;
//        if(paramOrder){
//            for(var i = 0, len = paramOrder.length; i < len; i++){
//                buf.push(o[paramOrder[i]]);
//            }
//        }else if(this.form.paramsAsHash){
//            buf.push(o);
//        }
//        return buf;
//    },
//    // Direct actions have already been processed and therefore
//    // we can directly set the result; Direct Actions do not have
//    // a this.response property.
//    processResponse : function(result) {
//        this.result = result;
//        return result;
//    },
//
//    success : function(response, trans){
////        if(trans.type == Ext.Direct.exceptions.SERVER){
////            response = {};
////        }
//        Bio.form.Action.Load.superclass.success.call(this, response);
//    }
//});

////Ext.form.Action.ACTION_TYPES.bioload = Bio.form.Action.Load;

////Bio.form.FormPanel = Ext.extend(Ext.form.FormPanel, {
//Ext.define('Bio.form.FormPanel', {
//    extend: 'Ext.form.FormPanel',
//    load : function(options){
//        //this.form.doAction('bioload', options);
//        return this;
//    }
//});


Ext.define('Idbm.form.EmpEdit', {
    extend: 'Ext.form.Panel',
    listeners: {
        afterrender: function() {
            var selection = this.bioOwnerGrid.getSelectionModel().getSelection();
            var row = (selection && selection.length > 0) ? selection[0].data : { };
            var store  = Ext.create('Bio.data.Store', {
                bioCode: 'emp1.test',
                bioParams: {empno:{value:row.EMPNO}}
            });
            store.loadForm(this);
        }
    },

    frame: true,
    autoHeight: true,
    width: 800,
    bodyPadding: 0,
    autoScroll: true,
    defaults: {
        anchor: '100%',
        labelWidth: 100
    },
    items: [
        {
            xtype: 'fieldset',
            title: 'А. Идентификационные данные',
            collapsible: false,
            defaults: {
                anchor: '100%',
                layout: {
                    type: 'hbox',
                    defaultMargins: {top: 0, right: 5, bottom: 0, left: 0}
                }
            },
            items: [{
                xtype: 'fieldcontainer',
                defaults: {
                    labelWidth: 80,
                    labelAlign: 'right'
                },
                combineErrors: true,
                msgTarget: 'under',
                layout: 'column',
                items: [
                    {
                        xtype: 'textfield',
                        columnWidth: 0.5,
                        name: 'ENAME',
                        fieldLabel: 'Имя',
                        margin: '0 5 0 0',
                        allowBlank: false
                    },
                    {
                        xtype: 'textfield',
                        columnWidth: 0.5,
                        name: 'JOB',
                        fieldLabel: 'Должность',
                        margin: '0 5 0 0',
                        allowBlank: false
                    }

                ]
            },

                {
                    xtype: 'fieldcontainer',
                    defaults: {
                        labelWidth: 80,
                        labelAlign: 'right'
                    },
                    layout: 'column',
                    items: [
                        {
                            xtype: 'biocombo',
                            columnWidth: 1.0,
                            name: 'DEPTNO',
                            fieldLabel: 'Департамент',
                            margin: '0 5 0 0',
                            //allowBlank: false
                            emptyText: 'выбрать',
                            queryMode: 'remote',
                            store: Ext.create('Bio.data.Store', {
                                bioCode: 'dept.test'
//                                autoLoad: true,
//                                listeners: {
//                                    load: function(records, successful, eOpts ) {
//                                        console.log(records);
//                                    }
//                                }
                            }),
                            displayField: 'DNAME',
                            valueField: 'DEPTNO'
                        }
                    ]
                }
            ]
        },
        {
            xtype: 'fieldset',
            title: 'Б. Доп сведения',
            collapsible: true,
            collapsed: false,
            autoHeight: true,
            defaults: {
                layout: {
                    type: 'hbox',
                    defaultMargins: {top: 0, right: 3, bottom: 0, left: 0}
                }
            },
            items: [
                {
                    xtype: 'fieldcontainer',
                    defaults: {
                        labelWidth: 70,
                        labelAlign: 'right'
                    },
                    layout: 'column',
                    items: [
                        {xtype: 'textfield', name: 'MGR', columnWidth: 0.5, fieldLabel: 'Менеджер', allowBlank: false},
                        {xtype: 'textfield', name: 'HIREDATE', columnWidth: 0.5, fieldLabel: 'Дата', allowBlank: false}
                    ]
                },
                {
                    xtype: 'fieldcontainer',
                    defaults: {
                        labelWidth: 70,
                        labelAlign: 'right'
                    },
                    layout: 'column',
                    items: [
                        {xtype: 'textfield', name: 'SEL', columnWidth: 0.5, fieldLabel: 'Сумма', allowBlank: false},
                        {xtype: 'textfield', name: 'COMM', columnWidth: 0.5, fieldLabel: 'Комиссия', allowBlank: false}
                    ]
                }
            ]
        }

    ],
    buttons: [
//            {
//                text: 'Load test data',
//                handler: function () {
//                    this.up('form').getForm().loadRecord(Ext.create('Employee', {
//                        'email': 'abe@sencha.com',
//                        'title': 'mr',
//                        'firstName': 'Abraham',
//                        'lastName': 'Elias',
//                        'startDate': '01/10/2003',
//                        'endDate': '12/11/2009',
//                        'phone-1': '555',
//                        'phone-2': '123',
//                        'phone-3': '4567',
//                        'hours': 7,
//                        'minutes': 15
//                    }));
//                }
//            },
        {
            text: 'Сохранить',
            handler: function () {
                var form = this.up('form').getForm(),
                    encode = Ext.String.htmlEncode,
                    s = '';

                if (form.isValid()) {
                    Ext.iterate(form.getValues(), function (key, value) {
                        value = encode(value);

                        s += Ext.util.Format.format("{0} = {1}<br />", key, value);
                    }, this);

                    Ext.Msg.alert('Form Values', s);
                }
            }
        },

        {
            text: 'Отменить',
            handler: function () {
                this.up('form').getForm().reset();
            }
        }
    ]
});

