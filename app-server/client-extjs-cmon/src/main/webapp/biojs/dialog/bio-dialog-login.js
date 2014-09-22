Ext.namespace("Bio.dialog");
Ext.define('Bio.dialog.Login', {
    extend: 'Bio.dialog.Base',

    form: null,

    constructor: function(config) {
        var me = this;

        var storedUserName = "root"; //Bio.login.restoreLastSuccessUserName() || "";
        var storedUserPwd = "root";

        me.form = new Ext.form.Panel({
            url: 'dummy.php',
            frame: true,
            bodyStyle: 'padding:5px 5px 0',
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
                    value: storedUserPwd
                }
            ],
            buttons:[
                {
                    height: 30,
                    text: "Вход",
                    handler: me.doOK.bind(me)
                },
                {
                    height: 30,
                    text: "Отмена",
                    handler: me.doCancel.bind(me)
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
            items: me.form
        }, config||{ });

        me.callParent([config]);
    },

    findField: function(fieldName) {
        var me = this;
        var fld = me.form.items.find(function (c) {
            return (c.name) && (c.name === fieldName);
        });
        return fld;
    },

    transaction: null,
    lastLogin: null,
    doOK: function (sender) {
        var me = this;

        var userNameFld = me.findField("FA_USER_NAME");
        var userPasswordFld = me.findField("FA_PASSWORD");

        var userName = (userNameFld) ? userNameFld.getValue() : null;
        var userPassword = (userPasswordFld) ? userPasswordFld.getValue() : null;
        me.closeDialog({
            modalResult: 1,
            login: userName+"/"+userPassword
        });
    },

    doCancel: function () {
        this.closeDialog({
            modalResult: 0,
            login: null
        });
    },

    focuseControl: function () {
        var me = this;
        var userNameFld = me.findField("FA_USER_NAME");
        var userPasswordFld = me.findField("FA_PASSWORD");

        var initControlKeyMap = function (frm, ctrl) {
            if (ctrl) {
                ctrl.on("specialkey", function (c, e) {
                    if (e.getKey() == Ext.EventObject.ENTER)
                        me.doOK.bind(me)();
                    else if (e.getKey() == Ext.EventObject.ESC)
                        me.doCancel.bind(me)();
                });
            }
        };

        initControlKeyMap(me.form, userNameFld);
        initControlKeyMap(me.form, userPasswordFld);
        if (userNameFld && (!userNameFld.getValue())) {
            userNameFld.focus(true, 100);
        } else if (userPasswordFld) {
            userPasswordFld.focus(true, 100);
        }
    },

    showDialog: function () {
        var me = this;
        me.show(null, me.focuseControl, me);
    }

});
