Ext.define('Ekb.form.OrgDetails', {
    extend: 'Ext.form.Panel',

    //frame: true,
    //autoHeight: true,
    baseCls: 'x-panel-body-default-framed',

    height: 700,
    width: 800,
    bodyPadding: 0,
    autoScroll: true,
    layout: 'anchor',
    defaults: {
        margin: '5',
        padding: '5',
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
                align:'stretch'
            },
            defaults: {
                margin: '2 0 0 0',
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
                            xtype: 'biocombo',
                            labelWidth: 70,
                            name: 'verstate',
                            fieldLabel: 'Выверка',
                            allowBlank: false,
                            flex: 1,
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
                align:'stretch'
            },
            items: [
                {
                    xtype: 'fieldcontainer',
                    layout: 'anchor',
                    defaults: {
                        margin: '2 0 0 0',
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
                            margin: '0',
                            queryParam: 'region',
                            emptyText: "<регион не выбран>",
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
                            fieldLabel: "Улица", labelWidth: 150,
                            emptyText: "<улица не выбрана>"
                        },
                        {
                            xtype: 'textfield',
                            name: 'address',
                            fieldLabel: '',
                            allowBlank: true
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
                align: 'stretch'
            },
            defaults: {
                layout: 'hbox',
                margin: '2 0 0 0',
                defaults: {
                    margin: '0',
                    labelWidth: 140,
                    labelAlign: 'right',
                    allowBlank: true,
                    flex: 1
                }
            },
            items: [
                {
                    xtype: 'fieldcontainer',
                    items: [
                        {
                            xtype: 'textfield',
                            name: 'ogrn',
                            fieldLabel: 'ОГРН'
                        },
                        {
                            xtype: 'textfield',
                            name: 'inn',
                            fieldLabel: 'ИНН'
                        }
                    ]
                },
                {
                    xtype: 'fieldcontainer',
                    items: [
                        {
                            xtype: 'textfield',
                            name: 'dog_num',
                            fieldLabel: 'Рег. номер (РН)'
                        },
                        {
                            xtype: 'datefield',
                            name: 'dog_date',
                            fieldLabel: 'Дата формирования РН',
                            labelWidth: 150
                        }
                    ]
                },
                {
                    xtype: 'fieldcontainer',
                    items: [
                        {
                            xtype: 'textfield',
                            name: 'phone_fax',
                            fieldLabel: 'Телефон/факс'
                        },
                        {
                            xtype: 'textfield',
                            name: 'respons_person',
                            fieldLabel: 'Ответственное лицо'
                        }
                    ]
                },
                {
                    xtype: 'fieldcontainer',
                    items: [
                        {
                            xtype: 'textfield',
                            name: 'glavbuch_name',
                            fieldLabel: 'ФИО гл. бухгалтера'
                        },
                        {
                            xtype: 'textfield',
                            name: 'glavbuch_email',
                            fieldLabel: 'Email бухгалтерии'
                        }
                    ]
                },
                {
                    xtype: 'fieldcontainer',
                    items: [
                        {
                            xtype: 'textfield',
                            name: 'glavbuch_phone',
                            fieldLabel: 'Телефон бухгалтерии'
                        },
                        {
                            xtype: 'textfield',
                            name: 'autoinfo_phone',
                            fieldLabel: 'Тел. автоинформатора'
                        }
                    ]
                },
                {
                    xtype: 'fieldcontainer',
                    items: [
                        {
                            xtype: 'textfield',
                            name: 'orgname_jur',
                            fieldLabel: 'Наименование юр. лица',
                            labelWidth: 150
                        }
                    ]
                },
                {
                    xtype: 'fieldcontainer',
                    items: [
                        {
                            xtype: 'textfield',
                            name: 'address_jur',
                            fieldLabel: 'Адрес юр. лица',
                            labelWidth: 150
                        }
                    ]
                },
                {
                    xtype: 'fieldcontainer',
                    items: [
                        {
                            xtype: 'textfield',
                            name: 'sroom_cnt',
                            fieldLabel: 'Кол-во кинозалов',
                            flex: 0,
                            width: 250,
                            labelWidth: 150
                        },
                        {
                            xtype: 'textfield',
                            name: 'splace_cnt',
                            fieldLabel: 'Кол-во мест',
                            flex: 0,
                            width: 190,
                            labelWidth: 90
                        },
                        {
                            xtype: 'textfield',
                            name: 'space_ttl',
                            fieldLabel: 'Площадь (общ)',
                            labelWidth: 100
                        }
                    ]
                }

            ]
        },
        {
            xtype: 'tabpanel',
            autoHeight: true,
            //height: 200,
            padding: '0',
            layout: {
                align: 'stretch'
            },
            defaults: {
                xtype: 'panel',
                baseCls: 'x-panel-body-default-framed',
                padding: '5'
            },
            items: [
                {
                    title: "Свойства",
                    layout: {
                        type: 'vbox',
                        align: 'stretch'
                    },
                    defaults: {
                        layout: 'hbox',
                        margin: '2 0 0 0',
                        defaults: {
                            margin: '0',
                            labelAlign: 'right',
                            allowBlank: true,
                            flex: 1
                        }
                    },
                    items: [
                        {
                            xtype: 'fieldcontainer',
                            items: [
                                {
                                    xtype: 'textfield',
                                    name: 'org_www',
                                    fieldLabel: 'Сайт',
                                    labelWidth: 80
                                },
                                {
                                    xtype: 'biocombo',
                                    labelWidth: 140,
                                    width: 380,
                                    name: 'id_prop',
                                    fieldLabel: 'Форма собственности',
                                    flex: 0,
                                    store: Ext.create('Bio.data.Store', {bioCode: 'ekbp@cabinet.combo.prop'}),
                                    matchFieldWidth: false,
                                    listConfig: {
                                        width: 380
                                    }
                                }
                            ]
                        },
                        {
                            xtype: 'fieldcontainer',
                            items: [
                                {
                                    xtype: 'checkbox',
                                    labelWidth: 170,
                                    name: 'automatic',
                                    fieldLabel: 'Наличие автоматизации СПБ'
                                },
                                {
                                    xtype: 'checkbox',
                                    labelWidth: 170,
                                    name: 'modern',
                                    fieldLabel: 'Современный кинотеатр'
                                },
                                {
                                    xtype: 'checkbox',
                                    labelWidth: 150,
                                    name: 'original_dog',
                                    fieldLabel: 'Наличие оригинала РК'
                                },
                                {
                                    xtype: 'checkbox',
                                    labelWidth: 70,
                                    name: 'test',
                                    fieldLabel: 'Тестовый'
                                }
                            ]
                        },
                        {
                            xtype: 'fieldcontainer',
                            items: [
                                {
                                    xtype: 'biocombo',
                                    labelWidth: 110,
                                    name: 'state',
                                    fieldLabel: 'Состояние',
                                    store: Ext.create('Bio.data.Store', {bioCode: 'ekbp@cabinet.combo.state'})
                                },
                                {
                                    xtype: 'datefield',
                                    name: 'state_date',
                                    fieldLabel: 'Дата состояния c',
                                    flex: 0,
                                    labelWidth: 120,
                                    width: 110+120

                                },
                                {
                                    xtype: 'datefield',
                                    name: 'suspended',
                                    fieldLabel: 'по',
                                    flex: 0,
                                    labelWidth: 30,
                                    width: 110+30
                                }

                            ]
                        },
                        {
                            xtype: 'fieldcontainer',
                            items: [
                                {
                                    xtype: 'biocombo',
                                    labelWidth: 110,
                                    name: 'closereason_id',
                                    fieldLabel: 'Прим. к сост.',
                                    store: Ext.create('Bio.data.Store', {bioCode: 'ekbp@cabinet.combo.closereason'})
                                },
                                {
                                    xtype: 'biocombo',
                                    labelWidth: 110,
                                    name: 'location',
                                    fieldLabel: 'Расположение',
                                    store: Ext.create('Bio.data.Store', {bioCode: 'ekbp@cabinet.combo.location'})
                                }
                            ]
                        },
                        {
                            xtype: 'fieldcontainer',
                            items: [
                                {
                                    xtype: 'biocombo',
                                    labelWidth: 110,
                                    name: 'id_vnd',
                                    fieldLabel: 'Поставщик ПО',
                                    store: Ext.create('Bio.data.Store', {bioCode: 'ekbp@cabinet.combo.softvendor'})
                                },
                                {
                                    xtype: 'biocombo',
                                    labelWidth: 110,
                                    name: 'state_mat',
                                    fieldLabel: 'Имущ. состояние',
                                    store: Ext.create('Ext.data.Store', {
                                        fields: ['id', 'caption'],
                                        data : [
                                            {id:"0", caption:"<не определено>"},
                                            {id:"1", caption:"требует кап. ремонта"},
                                            {id:"2", caption:"не удовлетворительное"},
                                            {id:"3", caption:"удовлетворительное"},
                                            {id:"4", caption:"хорошее"},
                                            {id:"5", caption:"отличное"}
                                        ]
                                    })
                                }
                            ]
                        }
//                        {
//                            xtype: 'fieldcontainer',
//                            items: [
//                                {
//                                    xtype: 'biocombo',
//                                    labelWidth: 110,
//                                    name: 'time_zone',
//                                    fieldLabel: 'Часовой пояс',
//                                    store: Ext.create('Ext.data.Store', {
//                                        fields: ['id', 'caption'],
//                                        data : [
//                                            {id:"0", caption:"<не определено>"},
//                                            {id:"1", caption:"требует кап. ремонта"},
//                                            {id:"2", caption:"не удовлетворительное"},
//                                            {id:"3", caption:"удовлетворительное"},
//                                            {id:"4", caption:"хорошее"},
//                                            {id:"5", caption:"отличное"}
//                                        ]
//                                    })
//                                },
//                                {
//                                    xtype: 'textfield',
//                                    name: 'silence_period',
//                                    fieldLabel: 'Допустимый период молчания [дней]',
//                                    labelWidth: 200
//                                }
//                            ]
//                        }
                    ]
                },
                {
                    title: "Оборудование"
                },
                {
                    title: "Кинозалы"
                }
            ]
        }
    ],
    buttons: [
        {
            text: 'Сохранить',
            margin: '0 5 25 0',
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
            margin: '0 20 25 0',
            handler: function () {
                this.up('form').getForm().reset();
            }
        }
    ]
});
