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
                            width: 350
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
                                '<tpl for=".">',
                                '{org_id} - {org_name}',
                                '</tpl>'
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
                            xtype: 'biocombo', name: 'region_uid',
                            minChars: 0,
                            store: Ext.create('Bio.data.Store', {bioCode: 'ekbp@cabinet.combo.region'}),
                            valueField: 'region_uid', displayField: 'region',
                            fieldLabel: "Регион", labelWidth: 85, allowBlank: true,
                            queryParam: 'region',
                            emptyText: "<регион не выбран>",
                            selectOnFocus: true,
                            tpl: Ext.create('Ext.XTemplate',
                                '<tpl for=".">',
                                '<div class="x-boundlist-item" style="text-align: left">{region}</div>',
                                '</tpl>'
                            ),
                            displayTpl: Ext.create('Ext.XTemplate',
                                '<tpl for=".">',
                                '{region}',
                                '</tpl>'
                            )
                        }

                    ]
                }

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
