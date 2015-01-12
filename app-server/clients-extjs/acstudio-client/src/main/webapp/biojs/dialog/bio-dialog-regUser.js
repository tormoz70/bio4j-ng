Ext.namespace("Bio.dialog");
Ext.define('Bio.dialog.RegUser', {
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
                labelWidth: 130,
                labelAlign: 'right'
            },

            //baseCls: 'x-panel-body-default-framed',
            items:[
                {xtype:"hidden",name: "FA_USER_UID"},
                {
                    xtype: 'fieldset',
                    autoHeight: true,
                    title: 'Идентификация',
                    //baseCls: 'x-panel-body-default-framed',
                    items: [{
                        layout: 'column',
                        border: false,
                        defaults: {
                            baseCls: 'x-panel-body-default-framed',
                            columnWidth: .5,
                            layout: 'anchor',
                            border: false,
                            autoHeight: true,
                            defaults: {
                                labelAlign: 'right',
                                anchor: '100%',
                                labelWidth: 100
                            },
                            defaultType: 'textfield'
                        },
                        items: [{
                            items: [{
                                fieldLabel: "Пользователь",
                                name: "FA_USER_NAME",
                                allowBlank: false,
                                vtype: "alphanum",
                                vtypeText: "Вы использовали недпустимый символ в имени ползователя!"
                            }, {
                                fieldLabel: "Пароль",
                                name: "FA_PASSWORD",
                                inputType: "password",
                                allowBlank: false
                            }, {
                                fieldLabel: "Повтор пароля",
                                name: "FA_PASSWORD1",
                                inputType: "password",
                                allowBlank: false
                            }]
                        }, {
                            items: [{
                                fieldLabel: "Фамилия",
                                name: "FA_FIO_FAM",
                                allowBlank: false
                            }, {
                                fieldLabel: "Имя",
                                name: "FA_FIO_NAME",
                                allowBlank: false
                            }, {
                                fieldLabel: "Отчество",
                                name: "FA_FIO_SNAME",
                                allowBlank: false
                            }]
                        }]
                    }]
                },{
                    xtype: 'fieldset',
                    title: 'Адрес',
                    layout: 'anchor',
                    autoHeight:true,
                    defaults: {
                        labelAlign: 'right',
                        anchor: '100%',
                        labelWidth: 100
                    },
                    items: [
                        {
                            xtype: 'textfield',
                            fieldLabel: "Город",
                            name: 'FA_CITY'
                        },
                        {
                            xtype: 'textfield',
                            fieldLabel: "Округ",
                            name: 'FA_CDEP'
                        },
                        {
                            xtype: 'textfield',
                            width: 438,
                            autoWidth:false,
                            fieldLabel: "Организация",
                            name: "FA_ORG",
                            allowBlank: false
                        }
                    ]
                },{
                    xtype: 'fieldset',
                    autoHeight:true,
                    title: 'Контакты',
                    items: [{
                        layout:'column',
                        border:false,
                        autoHeight:true,
                        defaults: {
                            baseCls: 'x-panel-body-default-framed',
                            columnWidth: .5,
                            layout: 'anchor',
                            border: false,
                            autoHeight: true,
                            defaults: {
                                labelAlign: 'right',
                                anchor: '100%',
                                labelWidth: 70
                            },
                            defaultType: 'textfield'
                        },
                        items:[{
                            items: [{
                                fieldLabel: "Телефон",
                                name: "FA_PHONE"
                            }]
                        },{
                            items: [{
                                fieldLabel: "e-mail",
                                name: "FA_EMAIL",
                                vtype: "email",
                                vtypeText: "Введен некоректный адрес электронной почты!",
                                allowBlank: false
                            }]
                        }]
                    }]
                }
            ],
            buttons:[
                {
                    text: "Сохранить",
                    handler: me.doOK.bind(me)
                },
                {
                    text: "Отмена",
                    handler: me.doCancel.bind(me)
                }
            ]
        });

        config = Ext.apply({
            id: 'login-win',
            title: 'Регистрация нового пользователя',
            width: 640,
            height: 480,
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

    transaction: null,
    lastLogin: null,
    doOK: function (sender) {
        var me = this;

        //var userNameFld = me.findField("FA_USER_NAME");
        //var userPasswordFld = me.findField("FA_PASSWORD");

        //var userName = (userNameFld) ? userNameFld.getValue() : null;
        //var userPassword = (userPasswordFld) ? userPasswordFld.getValue() : null;
        //me.closeDialog({
        //    modalResult: 1,
        //    login: userName+"/"+userPassword
        //});

        var form = Bio.tools.parentFormByClassName(me, 'Bio.dialog.RegUser');

        form.postData({
            callback: {
                fn: function(operation) {
                    var me = this;
                    //alert("Saved!!!");
                    if(operation.responseData.success === true)
                        me.up('window').close();
                },
                scope: form
            }
        });



    },

    doCancel: function () {
        this.closeDialog({
            modalResult: 0//,
            //login: null
        });
    },

    showDialog: function () {
        var me = this;
        me.show(null, me.focuseControl, me);
    }

});
