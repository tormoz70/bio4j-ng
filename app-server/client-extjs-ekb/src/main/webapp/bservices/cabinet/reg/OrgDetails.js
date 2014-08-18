Ext.define('Ekb.form.OrgDetails', {
    extend: 'Ext.form.Panel',
    //title   : 'FieldContainers',

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
            //title: 'А. Регистр преступления',
            collapsible: false,
            defaults: {
                labelWidth: 100,
                anchor: '100%',
                layout: {
                    type: 'column',
                    defaultMargins: {top: 0, right: 5, bottom: 0, left: 0}
                }
            },
            items: [
                {
                    xtype: 'textfield',
                    //сolumnWidth: 0.5,
                    name: 'orgname',
                    fieldLabel: '*Название',
                    labelWidth: 120,
                    margin: '0 5 0 0',
                    allowBlank: false
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
