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
            items: [
                {
                    xtype: 'fieldcontainer',
                    layout: 'anchor',
                    items: [
                        {
                            xtype: 'textfield',
                            name: 'orgname',
                            fieldLabel: '*Название',
                            labelWidth: 120,
                            labelAlign: 'right',
                            margin: '2 2 2 0',
                            anchor: '100%',
                            allowBlank: false
                        }
                    ]
                },
                {
                    xtype: 'fieldcontainer',
                    layout: 'hbox',
                    defaults: {
                        labelWidth: 120,
                        labelAlign: 'right',
                        margin: '2 2 2 0'
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
                            name: 'registred',
                            fieldLabel: 'Зарегистрирован'
                        }
                        ,{
                            xtype: 'combo',
                            labelWidth: 80,
                            name: 'verstate',
                            fieldLabel: 'Выверка',
                            allowBlank: false,
                            flex: 1
                        }
                    ]
                },
                {
                    xtype: 'fieldcontainer',
                    layout: 'anchor',
                    items: [
                        {
                            xtype: 'textfield',
                            name: 'holding_id',
                            fieldLabel: 'Киносеть',
                            labelWidth: 120,
                            labelAlign: 'right',
                            margin: '2 2 2 0',
                            anchor: '100%',
                            allowBlank: false
                        }
                    ]
                },

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
