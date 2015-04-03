Ext.define('Ims.form.FileDetails', {
    extend: 'Ext.form.Panel',
    //title   : 'FieldContainers',

    listeners: {
        afterrender: function() {
            var selection = this.bioOwnerGrid.getSelectionModel().getSelection();
            var row = (selection && selection.length > 0) ? selection[0].data : { };
            var store  = Ext.create('Bio.data.Store', {
                bioCode: 'files.details',
                bioParams: {docuid:{value:row.DOC_UID}}
            });
            store.loadForm(this);
        }
    },

    frame: true,
    //autoHeight: true,
    height: 600,
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
            title: 'А. Регистр преступления',
            collapsible: false,
            defaults: {
                labelWidth: 100,
                anchor: '100%',
                layout: {
                    type: 'hbox',
                    defaultMargins: {top: 0, right: 5, bottom: 0, left: 0}
                }
            },
            items: {
                xtype: 'fieldcontainer',
                //fieldLabel: 'Phone',
                combineErrors: true,
                msgTarget: 'under',
//                    defaults: {
//                        hideLabel: true
//                    },
                layout: 'column',
                items: [
                    {
                        xtype: 'textfield',
                        columnWidth: 0.5,
                        name: 'DOC_UID',
                        fieldLabel: 'Номер документа',
                        labelWidth: 120,
                        margin: '0 5 0 0',
                        allowBlank: false
                    },
                    {
                        xtype: 'datefield',
                        columnWidth: 0.5,
                        name: 'credate',
                        fieldLabel: 'Дата заведения',
                        margin: '0 5 0 0',
                        allowBlank: false
                    }
                ]
            }
        },
        {
            xtype: 'fieldset',
            title: 'Б. Сведения о предмете',
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
                        labelAlign: 'right'
                    },
                    layout: 'column',
                    items: [
                        {xtype: 'biocombo', name: 'TYPANTQ_ID',
                            store: Ext.create('Bio.data.Store', {bioCode: 'idcardantq_combo.typantq'}), valueField: 'ID', displayField: 'AVALUE',
                            columnWidth: 0.5, fieldLabel: 'Вид предмета', labelWidth: 85, allowBlank: false},
                        {xtype: 'textfield', name: 'ANAME', columnWidth: 0.5, fieldLabel: 'Название предмета', labelWidth: 120, flex: 1, allowBlank: false}
                    ]
                },
                {
                    xtype: 'fieldcontainer',
                    defaults: {
                        labelAlign: 'right'
                    },
                    layout: 'column',
                    items: [
                        {xtype: 'biocombo', name: 'AUTORTHNG_ID',
                            store: Ext.create('Bio.data.Store', {bioCode: 'idcardantq_combo.autorthng'}), valueField: 'ID', displayField: 'AVALUE',
                            columnWidth: 0.6, fieldLabel: 'Автор', labelWidth: 40, flex: 1, allowBlank: false},
                        {xtype: 'textfield', name: 'PRODTIME', columnWidth: 0.4, fieldLabel: 'Время изготовления', labelWidth: 130, maxLength: 4, allowBlank: false}
                    ]
                },
                {
                    xtype: 'fieldcontainer',
                    labelWidth: 95,
                    fieldLabel: 'Размер (объем)',
                    defaults: {
                        labelAlign: 'right'
                    },
                    layout: 'column',
                    items: [
                        {xtype: 'numberfield', name: 'SIZELEN', columnWidth: 0.2, fieldLabel: 'дл, см', labelWidth: 50, maxLength: 7, allowBlank: true},
                        {xtype: 'numberfield', name: 'SIZEWDTH', columnWidth: 0.2, fieldLabel: 'шр, см', labelWidth: 50, maxLength: 7, allowBlank: true},
                        {xtype: 'numberfield', name: 'SIZEHGHT', columnWidth: 0.2, fieldLabel: 'вс, см', labelWidth: 50, maxLength: 7, allowBlank: true},
                        {xtype: 'numberfield', name: 'WEIGHT', columnWidth: 0.2, fieldLabel: 'Вес', labelWidth: 30, width: 100, maxLength: 5, allowBlank: true},
                        {xtype: 'combo', name: 'WEIGHTUNIT', style:{padding:'5,0,0,0'}, columnWidth: 0.15, allowBlank: true}
                    ]
                },
                {
                    xtype: 'fieldcontainer',
                    defaults: {
                        labelAlign: 'right'
                    },
                    layout: 'column',
                    items: [
                        {xtype: 'numberfield', name: 'VPRICE', columnWidth: 0.4, fieldLabel: 'Оценочная стоимость (руб)', labelWidth: 165, allowBlank: true},
                        {xtype: 'biocombo', name: 'AQURDTHNG_ID',
                            store: Ext.create('Bio.data.Store', {bioCode: 'idcardantq_combo.aqurdthng'}), valueField: 'ID', displayField: 'AVALUE',
                            columnWidth: 0.3, fieldLabel: 'Преобретен', labelWidth: 80, allowBlank: true},
                        {xtype: 'biocombo', name: 'STORYTHNG1_ID',
                            store: Ext.create('Bio.data.Store', {bioCode: 'idcardantq_combo.storythng'}), valueField: 'ID', displayField: 'AVALUE',
                            columnWidth: 0.2, fieldLabel: 'Сюжет', labelWidth: 50, allowBlank: true},
                        {xtype: 'displayfield', columnWidth: 0, value:";"},
                        {xtype: 'biocombo', name: 'STORYTHNG2_ID', width: 70, allowBlank: true}
                    ]
                },
                {
                    xtype: 'fieldcontainer',
                    autoHeight: true,
                    defaults: {
                        labelAlign: 'right'
                    },
                    items: [
                        {xtype: 'textfield', name: 'MAKESTYLE1', fieldLabel: 'Стиль', labelWidth: 40, minWidth: 140, flex: 1, allowBlank: false},
                        {xtype: 'displayfield', width: 6, value:";"},
                        {xtype: 'textfield', name: 'MAKESTYLE2', minWidth: 40, flex: 1, allowBlank: false},
                        {xtype: 'biocombo', name: 'TECHTHNG1_ID',
                            store: Ext.create('Bio.data.Store', {bioCode: 'idcardantq_combo.techthng'}), valueField: 'ID', displayField: 'AVALUE',
                            fieldLabel: 'Техника исполнения', labelWidth: 130, minWidth: 220, flex: 1, allowBlank: false},
                        {xtype: 'displayfield', width: 6, value:";"},
                        {xtype: 'biocombo', name: 'TECHTHNG2_ID',
                            store: Ext.create('Bio.data.Store', {bioCode: 'idcardantq_combo.techthng'}), valueField: 'ID', displayField: 'AVALUE',
                            minWidth: 40, flex: 1, allowBlank: false},
                        {xtype: 'textfield', name: 'COLORING1', fieldLabel: 'Колорит', labelWidth: 50, minWidth: 130, flex: 1, allowBlank: false},
                        {xtype: 'displayfield', width: 6, value:";"},
                        {xtype: 'textfield', name: 'COLORING2', minWidth: 40, flex: 1, allowBlank: false}
                    ]
                },
                {
                    xtype: 'fieldcontainer',
                    autoHeight: true,
                    defaults: {
                        labelAlign: 'right'
                    },
                    items: [
                        {xtype: 'biocombo', name: 'MATRLTHNG1_ID',
                            store: Ext.create('Bio.data.Store', {bioCode: 'idcardantq_combo.matrlthng'}), valueField: 'ID', displayField: 'AVALUE',
                            fieldLabel: 'Материал', labelWidth: 60, minWidth: 140, flex: 1, allowBlank: false},
                        {xtype: 'displayfield', width: 6, value:";"},
                        {xtype: 'biocombo', name: 'MATRLTHNG2_ID',
                            store: Ext.create('Bio.data.Store', {bioCode: 'idcardantq_combo.matrlthng'}), valueField: 'ID', displayField: 'AVALUE',
                            minWidth: 40, flex: 1, allowBlank: false},
                        {xtype: 'biocombo', name: 'DECORTHNG_ID',
                            store: Ext.create('Bio.data.Store', {bioCode: 'idcardantq_combo.decorthng'}), valueField: 'ID', displayField: 'AVALUE',
                            matchFieldWidth:false,
                            listConfig: {resizable:true, maxWidth: 250},
                            fieldLabel: 'Оформление', labelWidth: 75, minWidth: 150, flex: 1, allowBlank: false},
                        {xtype: 'displayfield', width: 6, value:";"},
                        {xtype: 'biocombo', name: 'DECORTHNG_ID',
                            store: Ext.create('Bio.data.Store', {bioCode: 'idcardantq_combo.decorthng'}), valueField: 'ID', displayField: 'AVALUE',
                            minWidth: 40, flex: 1, allowBlank: false},
                        {xtype: 'biocombo', name: 'FACINGTHNG1_ID',
                            store: Ext.create('Bio.data.Store', {bioCode: 'idcardantq_combo.facingthng'}), valueField: 'ID', displayField: 'AVALUE',
                            fieldLabel: 'Отделака', labelWidth: 60, minWidth: 130, flex: 1, allowBlank: false},
                        {xtype: 'displayfield', width: 6, value:";"},
                        {xtype: 'biocombo', name: 'FACINGTHNG2_ID',
                            store: Ext.create('Bio.data.Store', {bioCode: 'idcardantq_combo.facingthng'}), valueField: 'ID', displayField: 'AVALUE',
                            minWidth: 40, flex: 1, allowBlank: false}
                    ]
                },
                {
                    xtype: 'fieldcontainer',
                    autoHeight: true,
                    defaults: {
                        labelAlign: 'right'
                    },
                    items: [
                        {xtype: 'textfield', name: 'LETTERING', fieldLabel: 'Надписи', labelWidth: 60, flex: 1, allowBlank: false}
                    ]
                },
                {
                    xtype: 'fieldcontainer',
                    autoHeight: true,
                    defaults: {
                        labelAlign: 'right'
                    },
                    items: [
                        {xtype: 'textarea', name: 'ADESC', fieldLabel: 'Описание', height: 50, labelWidth: 60, flex: 1, allowBlank: false}
                    ]
                },
                {
                    xtype: 'fieldcontainer',
                    autoHeight: true,
                    defaults: {
                        labelAlign: 'right'
                    },
                    items: [
                        {xtype: 'biocombo', name: 'CREREASON_ID',
                            store: Ext.create('Bio.data.Store', {bioCode: 'idcardantq_combo.crereason'}), valueField: 'ID', displayField: 'AVALUE',
                            fieldLabel: 'Причина заполнения', labelWidth: 125, flex: 1, allowBlank: false},
                        {xtype: 'datefield', name: 'THEFTDATE', fieldLabel: 'Дата хищения', labelWidth: 90, width: 220, allowBlank: false}
                    ]
                },
                {
                    xtype: 'fieldcontainer',
                    autoHeight: true,
                    defaults: {
                        labelAlign: 'right'
                    },
                    items: [
                        {xtype: 'biocombo', name: 'THEFTMTHD_ID',
                            store: Ext.create('Bio.data.Store', {bioCode: 'idcardantq_combo.theftmthd'}), valueField: 'ID', displayField: 'AVALUE',
                            fieldLabel: 'Способ хищения', labelWidth: 125, flex: 1, allowBlank: false},
                        {xtype: 'textfield', name: 'CRIMCODARTCL', fieldLabel: 'Статья УК', labelWidth: 90, width: 220, allowBlank: false}
                    ]
                },
                {
                    xtype: 'fieldcontainer',
                    autoHeight: true,
                    defaults: {
                        labelAlign: 'right'
                    },
                    items: [
                        {xtype: 'textarea', name: 'STORY', fieldLabel: 'Фабула', height: 50, labelWidth: 60, flex: 1, allowBlank: false}
                    ]
                }
            ]
        },

        {
            xtype: 'fieldset',
            title: "В. Сведения о владельце предмета",
            collapsible: true,
            autoHeight: true,
            frame: true,
            style: {padding:'5px'},
            layout: 'column',

            items: [
                {
                    xtype: 'panel',
                    frame: true,
                    columnWidth: 0.7,
                    bodyPadding: 5,
                    bodyBorder: false,
                    baseCls: 'x-panel-mc',
                    layout: 'anchor',
                    defaults: {
                        anchor: '100%',
                        labelWidth: 120
                    },
                    items: [
                        {xtype: 'textfield', name: 'aname', fieldLabel: "Принадлежность", allowBlank: true},
                        {xtype: 'textfield', name: 'aname', fieldLabel: "Наименование орг.", allowBlank: true},
                        {xtype: 'textfield', name: 'OWNER_SURNAME', fieldLabel: "Фамилия", allowBlank: true},
                        {xtype: 'textfield', name: 'OWNER_NAME', fieldLabel: "Имя", allowBlank: true},
                        {xtype: 'textfield', name: 'OWNER_PATRONYMIC', fieldLabel: "Отчество", allowBlank: true},
                        {xtype: 'datefield', name: 'OWNER_DOB', fieldLabel: "Дата рождения", allowBlank: true},
                        {xtype: 'textarea', name: 'OWNER_POB', fieldLabel: 'Регистрация по месту жительства, юр. адрес организации',
                            height: 50, allowBlank: true}
                    ]
                },
                {
                    xtype: 'panel',
                    frame: true,
                    columnWidth: 0.3,
                    bodyPadding: 5,
                    //layout: 'vbox',
                    items: [
                        {xtype:'displayfield', margins: "4,2,2,2", value: "Эскиз, фотография"},
                        {
                            xtype: "panel",
                            rowspan: 4,
                            frame: false,
                            height: 250,
                            html: '<img style="height:auto; width:100%;" src="../../../shared/images/baboon_48.png" alt="Bio4j"/>'
                        }
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
