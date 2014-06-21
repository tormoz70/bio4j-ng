Ext.namespace("Bio.dialog");
Ext.define('Bio.dialog.Login', {
    extend: 'Bio.dialog.Base',

    form: null,

    getLastError: function(brsp) {
        var exs = brsp["exceptions"];
        if(exs && (exs instanceof Array) && (exs.length > 0))
            return exs[0];
        else
            return exs;
    },

    constructor: function(config) {
        var me = this;

        var storedUserName = Bio.login.restoreLastSuccessUserName() || "";

        me.form = new Ext.form.Panel({
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
    lastLoginResult: null,
    doOK: function (sender) {
        var me = this;

        var userNameFld = me.findField("FA_USER_NAME");
        var userPasswordFld = me.findField("FA_PASSWORD");

        //alert(vUserNameFld.name);
        var userName = (userNameFld) ? userNameFld.getValue() : null;
        var userPassword = (userPasswordFld) ? userPasswordFld.getValue() : null;
        var login = userName+"/"+userPassword;


        Bio.app.waitMaskShow("Проверка имени пользователя и пароля...");
        var vURL = Bio.Tools.bldBioUrl("/login");
        var cfg = {
            url: vURL,
            params: {login: login},
            success: undefined,
            failure: undefined,
            callback: function(options, success, response){
                var me = this;
                me.transaction = null;
                Bio.app.waitMaskHide();
                var rspText = response.responseText;
                try {
                    me.lastLoginResult = Ext.decode(rspText);
                }
                catch (e) {
                    me.lastLoginResult = null;
                    Bio.dlg.showMsg("Ошибка на сервере", Bio.Tools.ObjToStr(response));
                }
                if (me.lastLoginResult != null) {
                    var ex = me.getLastError(me.lastLoginResult);
                    if (ex != null) {
                        if (ex.class == "ru.bio4j.ng.model.transport.BioError$Login$BadLogin") {
                            Bio.dlg.showMsg("Ошибка входа в систему", "Не верный логин!", null, null, function () {
                                //Bio.Login.showDialog(dlg.bio.Callback);
                            });
                        }
                        else if (ex.type == 'EBioBadUser') {
                            Bio.dlg.showMsg("Ошибка входа в систему", "Пользователь в системе не зарегистрирован.", null, null, function () {
                                //Bio.Login.showDialog(dlg.bio.Callback);
                            });
                        }
                        else if (ex.type == 'EBioUncnfrmdUser') {
                            Bio.dlg.showMsg("Ошибка входа в систему", "Пользователь не активирован.", null, null, function () {
                                //Bio.Login.showDialog(dlg.bio.Callback);
                            });
                        }
                        else if (ex.type == "EBioBadPwd") {
                            Bio.dlg.showMsg("Ошибка входа в систему", "Не верный пароль.", null, null, function () {
                                //Bio.Login.showDialog(dlg.bio.Callback);
                            });
                        }
                        else if (ex.type == "EBioException") {
                            Bio.dlg.showMsg("Непредвиденная ошибка", ex.message, null, null, function () {
                                //Bio.Login.showDialog(dlg.bio.Callback);
                            });
                        }
                    } else {
                        if (me.lastLoginResult.user) {
                            me.closeDialog({
                                modalResult: 1,
                                user: me.lastLoginResult.user
                            });
                        }

                    }
                }
            },
            scope: me,
            method: "POST"
        };

        me.transaction = Ext.Ajax.request(cfg);
    },

    doCancel: function () {
        this.closeDialog({
            modalResult: 0,
            user: null
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
