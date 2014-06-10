Ext.namespace("Bio.login");
Ext.define('Bio.login.Dialog', {
    extend: 'Ext.Window',

    initComponent: function() {
        var me = this;
        me.callParent(arguments);
    },

    constructor: function(config) {
        var me = this;

        var storedUserName = null;
        if (Bio.cooks)
            storedUserName = Bio.cooks.getCookie("cUserName");
        if (storedUserName == null) {
            storedUserName = "";
        }

        var frm = new Ext.form.Panel({
            url: 'dummy.php',
            frame: true,
            //title: 'Simple Form',
            bodyStyle: 'padding:5px 5px 0',
            //width: 250,
            autoWidth: true,

            layout: 'anchor',
            defaults: {
                anchor: '100%',
                labelWidth: 130
            },
            defaultType: 'textfield',
            items: [
                {
                    fieldLabel: "Имя пользователя",
                    labelAlign: "right",
                    name: "FA_USER_NAME",
                    allowBlank: false,
                    value: storedUserName,
                    vtype: "alphanum",
                    vtypeText: "Вы использовали недпустимый символ в имени ползователя!"
                },
                {
                    fieldLabel: "Пароль",
                    labelAlign: "right",
                    name: "FA_PASSWORD",
                    inputType: "password",
                    value: ""
                }
            ],
            buttons:[
                {
                    height: 30,
                    text: "Вход",
                    handler: this.doOK
                },
                {
                    height: 30,
                    text: "Отмена",
                    handler: this.doCancel
                }
            ]
        });

        config = Ext.apply({
            id: 'login-win',
            title: 'Вход в систему',
            width: 440,
            height: 150,
            iconCls: 'icon-grid',
            shim: false,
            animCollapse: false,
            constrainHeader: true,
            closable: false,
            maximizable: false,
            minimizable: false,
            resizable: false,
            modal: true,
            layout: 'fit',
            items: frm,
            callback: null
        }, config||{ });

        me.callParent([config]);
    },

    onShow: function() {
        var me = this;

        me.callParent(arguments);
    }

});
