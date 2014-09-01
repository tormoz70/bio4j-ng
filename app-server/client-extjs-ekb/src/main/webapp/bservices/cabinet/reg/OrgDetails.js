Ext.define('Ekb.form.OrgDetails', {
    extend: 'Ext.form.Panel',
    //title   : 'FieldContainers',

    frame: true,
    //autoHeight: true,
    height: 600,
    width: 800,
    bodyPadding: 0,
    autoScroll: true,
    layout: 'anchor',
    defaults: {
        anchor: '100%'
    },
    items: [
        {
            xtype: 'fieldset',
            collapsible: false,
            collapsed: false,
            autoHeight: true,
            layout: {
                type:'vbox',
                align:'stretch',
                padding: '0'
            },
            defaults: {
                defaults: {
                    anchor: '100%',
                    labelWidth: 90,
                    labelAlign: 'right'
                }
            },
            items: [
                {
                    xtype: 'fieldcontainer',
                    layout: 'anchor',
                    items: [
                        {
                            xtype: 'textfield',
                            name: 'orgname',
                            fieldLabel: '*Название',
                            margin: '2 2 2 0',
                            labelWidth: 90,
                            labelAlign: 'right',
                            allowBlank: false
                        }
                    ]
                },
                {
                    xtype: 'fieldcontainer',
                    layout: 'hbox',
                    defaults: {
                        margin: '2 2 2 0',
                        labelWidth: 90,
                        labelAlign: 'right'
                    },
                    items: [
                        {
                            xtype: 'textfield',
                            name: 'email',
                            fieldLabel: 'Email',
                            allowBlank: false,
                            flex: 1
                        },
                        {
                            xtype: 'checkbox',
                            labelWidth: 110,
                            name: 'registred',
                            fieldLabel: 'Зарегистрирован'
                        },
                        {
                            xtype: 'combo',
                            labelWidth: 70,
                            name: 'verstate',
                            fieldLabel: 'Выверка',
                            allowBlank: false,
                            flex: 1,
                            valueField: 'id',
                            displayField: 'caption',
                            store: Ext.create('Ext.data.Store', {
                                fields: ['id', 'caption'],
                                data : [
                                    {id:"0", caption:"не выверен"},
                                    {id:"1", caption:"в процессе"},
                                    {id:"2", caption:"адрес выверен"},
                                    {id:"9", caption:"выверен"}
                                ]
                            })
                        }

                    ]
                },
                {
                    xtype: 'fieldcontainer',
                    layout: 'anchor',
                    items: [
                        {
                            xtype: 'biocombo', name: 'holding_id',
                            minChars: 0,
                            store: Ext.create('Bio.data.Store', {bioCode: 'ekbp@cabinet.combo.holding'}),
                            valueField: 'org_id', displayField: 'org_name',
                            fieldLabel: "Киносеть", allowBlank: true,
                            queryParam: 'orgname',
                            emptyText: "<нет сети>",
                            selectOnFocus: true,
                            tpl: Ext.create('Ext.XTemplate',
                                '<tpl for=".">',
                                '<div class="x-boundlist-item" style="text-align: left">{org_id} - {org_name}</div>',
                                '</tpl>'
                            ),
                            displayTpl: Ext.create('Ext.XTemplate',
                                '<tpl for=".">{org_id} - {org_name}</tpl>'
                            )
                        }
                    ]
                }

            ]
        },
        {
            xtype: 'fieldset',
            title: "Адрес",
            collapsible: false,
            collapsed: false,
            autoHeight: true,
            layout: {
                type:'vbox',
                align:'stretch',
                padding: '0'
            },
            items: [
                {
                    xtype: 'fieldcontainer',
                    layout: 'anchor',
                    defaults: {
                        margin: '2 2 2 0',
                        labelWidth: 90,
                        labelAlign: 'right',
                        anchor: '100%'
                    },
                    items: [
                        {
                            xtype: 'biocombo', name: 'kladr_code_r',
                            minChars: 0,
                            store: Ext.create('Bio.data.Store', {bioCode: 'ekbp@cabinet.combo.region'}),
                            valueField: 'region_uid', displayField: 'region',
                            fieldLabel: "Регион", allowBlank: true,
                            queryParam: 'region',
                            emptyText: "<регион не выбран>",
                            selectOnFocus: true,
                            tpl: Ext.create('Ext.XTemplate',
                                '<tpl for=".">',
                                '<div class="x-boundlist-item" style="text-align: left">{region}</div>',
                                '</tpl>'
                            ),
                            displayTpl: Ext.create('Ext.XTemplate',
                                '<tpl for=".">{region}</tpl>'
                            ),
                            listeners: {
                                change: function(newValue, oldValue, eOpts) {
                                    var me = this;
                                    if(newValue != oldValue) {
                                        var frm = me.up('form').getForm();
                                        cityCombo = (frm ? frm.findField('kladr_code_np') : null);
//                                        if(cityCombo)
//                                            cityCombo.setValue(null);
                                    }

                                }
                            }
                        },
                        {
                            xtype: 'biocombo', name: 'kladr_code_np',
                            minChars: 0,
                            store: Ext.create('Bio.data.Store', {
                                bioCode: 'ekbp@cabinet.combo.city',
                                listeners: {
                                    beforeload: function(store, operation, eOpts) {
                                        var me = store,
                                            frm = (me.ownerCombo ? me.ownerCombo.up('form').getForm() : null),
                                            regionSeldParam = {region_uid:(frm ? frm.findField('kladr_code_r').getValue() : null)};
                                        me.bioParams = Bio.tools.setBioParam(me.bioParams, regionSeldParam);
                                        console.log(Bio.tools.objToStr(regionSeldParam));
                                    }
                                }
                            }),
                            valueField: 'city_uid', displayField: 'city',
                            fieldLabel: "Город/Нас. пункт", labelWidth: 150, allowBlank: true,
                            queryParam: 'city',
                            emptyText: "<город не выбран>",
                            selectOnFocus: true,
                            tpl: Ext.create('Ext.XTemplate',
                                '<tpl for=".">',
                                '<div class="x-boundlist-item" style="text-align: left">{city}</div>',
                                '</tpl>'
                            ),
                            displayTpl: Ext.create('Ext.XTemplate',
                                '<tpl for=".">{city}</tpl>'
                            ),
                            listeners: {
                                change: function(newValue, oldValue, eOpts) {
                                    var me = this;
                                    if(newValue != oldValue) {
                                        var frm = me.up('form').getForm();
                                        streetCombo = (frm ? frm.findField('kladr_code') : null);
//                                        if(streetCombo)
//                                            streetCombo.setValue(null);
                                    }

                                }
                            }
                        },
                        {
                            xtype: 'biocombo', name: 'kladr_code',
                            minChars: 0,
                            store: Ext.create('Bio.data.Store', {
                                bioCode: 'ekbp@cabinet.combo.street',
                                listeners: {
                                    beforeload: function(store, operation, eOpts) {
                                        var me = store,
                                            frm = (me.ownerCombo ? me.ownerCombo.up('form').getForm() : null),
                                            citySeldParam = {city_uid:(frm ? frm.findField('kladr_code_np').getValue() : null)};
                                        me.bioParams = Bio.tools.setBioParam(me.bioParams, citySeldParam);
                                        console.log(Bio.tools.objToStr(citySeldParam));
                                    }
                                }
                            }),
                            valueField: 'street_uid', displayField: 'street',
                            fieldLabel: "Улица", labelWidth: 150, allowBlank: true,
                            queryParam: 'street',
                            emptyText: "<улица не выбрана>",
                            selectOnFocus: true,
                            tpl: Ext.create('Ext.XTemplate',
                                '<tpl for=".">',
                                '<div class="x-boundlist-item" style="text-align: left">{street}</div>',
                                '</tpl>'
                            ),
                            displayTpl: Ext.create('Ext.XTemplate',
                                '<tpl for=".">{street}</tpl>'
                            )
                        },
                        {
                            xtype: 'textfield',
                            name: 'address',
                            fieldLabel: '',
                            labelAlign: 'right',
                            allowBlank: false
                        }

                    ]
                }

            ]
        },
        {
            xtype: 'fieldset',
            title: "",
            collapsible: false,
            collapsed: false,
            autoHeight: true,
            layout: {
                type: 'vbox',
                align: 'stretch',
                padding: '0'
            },
            items: [

            ]
        }
    ],
    buttons: [
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
