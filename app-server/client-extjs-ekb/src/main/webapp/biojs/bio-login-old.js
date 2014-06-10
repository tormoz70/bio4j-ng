Ext.namespace("Bio");
Bio.Login = {
    transaction: null,
    //currentIO: null,
    dialog: null,

    initControlKeyMap: function (frm, ctrl) {
        if (ctrl) {
            /*var map = new Ext.KeyMap(pControl, [
             {key: Ext.EventObject.ENTER,
             fn: Bio.Login.doOK,
             scope: pForm},
             {key: Ext.EventObject.ESC,
             fn: Bio.Login.doCancel,
             scope: pForm}
             ]);*/
            //alert(1);
            ctrl.on("specialkey", function (c, e) {
                if (e.getKey() == Ext.EventObject.ENTER)
                    Bio.Login.doOK.createDelegate(frm)(frm);
                else if (e.getKey() == Ext.EventObject.ESC)
                    Bio.Login.doCancel.createDelegate(frm)(frm);
            });
        }
    },

    focuseControl: function () {
        var vUserNameFld = this.items.find(function (c) {
            return (c.name) && (c.name === "FA_USER_NAME");
        });
        var vUserPasswordFld = this.items.find(function (c) {
            return (c.name) && (c.name === "FA_PASSWORD");
        });
        //Bio.dlg.showMsg("vUserNameFld.ownerCt", Bio.Tools.ObjToStr(vUserNameFld.ownerCt));
        Bio.Login.initControlKeyMap(this, vUserNameFld);
        Bio.Login.initControlKeyMap(this, vUserPasswordFld);
        if (vUserNameFld && (!vUserNameFld.getValue())) {
            vUserNameFld.focus(true, 100);
        } else if (vUserPasswordFld) {
            vUserPasswordFld.focus(true, 100);
        }
    },

    appendCallback: function (callback) {
        var vLgw = Bio.Login.dialog;
        if (vLgw) {
            if (!vLgw.bio.Callback)
                vLgw.bio.Callback = [];
            if (callback instanceof Array) {
                Ext.each(callback, function (clbck) {
                    vLgw.bio.Callback[vLgw.bio.Callback.length] = clbck;
                });
            } else
                vLgw.bio.Callback[vLgw.bio.Callback.length] = callback;
        }
    },

    /**
     *
     * @param {Object} callback = {fn:function, scope:Object, params:Object} - вызывается при удачном входе
     */
    showDialog: function (callback, showRegBtn) {
        if (!this.dialog) { /* Эта проверка необходима, чтобы не выскакивало несколько окон логина */
            this.lastLoginResult = null;

            var vStoredUserName = null;
            if (Bio.cooks)
                vStoredUserName = Bio.cooks.getCookie("cUserName");
            if (vStoredUserName == null) {
                vStoredUserName = "";
            }

            var vBtns = null;
            if (showRegBtn) {
                vBtns = [
                    {
                        text: "Вход",
                        handler: this.doOK
                    },
                    {
                        text: "Отмена",
                        handler: this.doCancel
                    }
                ];
            }
            else {
                vBtns = [
                    {
                        text: "Вход",
                        handler: this.doOK
                    },
                    {
                        text: "Отмена",
                        handler: this.doCancel
                    }
                ];
            }

            var vFrm = new Ext.form.Panel({
                url: 'save-form.php',
                frame: true,
                //title: 'Simple Form',
                bodyStyle: 'padding:5px 5px 0',
                //width: 250,
                autoWidth: true,

                fieldDefaults: {
                    labelWidth: 120,
                    align: "right",
                    anchor: '100%'
                },
                layout: {
                    type: 'vbox',
                    align: 'stretch'  // Child items are stretched to full width
                },
                defaultType: 'textfield',
                items: [
                    {
                        fieldLabel: "Имя пользователя",
                        name: "FA_USER_NAME",
                        allowBlank: false,
                        value: vStoredUserName,
                        vtype: "alphanum",
                        vtypeText: "Вы использовали недпустимый символ в имени ползователя!"
                    },
                    {
                        fieldLabel: "Пароль",
                        name: "FA_PASSWORD",
                        inputType: "password",
                        value: ""
                    }
                ],
                buttons:vBtns
            });

            this.dialog = Bio.Tools.createWindow({
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
                items: vFrm,
                bio: {
                    Callback: null
                }
            }, null, {
                event: "activate",
                handler: this.focuseControl,
                scope: vFrm
            });
        }

        var capsLock = {
            init: function () {
                var id = Ext.id();
                this.alertBox = Ext.DomHelper.append(document.body, {
                    tag: 'div',
                    style: 'width:10em; z-index:100000',
                    children: [
                        {
                            tag: 'div',
                            style: 'text-align: center; color: red;',
                            html: 'Нажата клавиша Caps Lock.',
                            id: id
                        }
                    ]
                }, true);
                Ext.fly(id).boxWrap();
                this.alertBox.hide();

                var pwds = Ext.query("INPUT[type='password']");
                for (var i = 0; i < pwds.length; i++) {
                    Ext.get(pwds[i].id).on('keypress', Ext.Function.bind(this.keypress, this, pwds[i], true), this);
                }
            },
            keypress: function (e, el) {
                var charCode = e.getCharCode();
                if ((e.shiftKey && charCode >= 97 && charCode <= 122) || (!e.shiftKey && charCode >= 65 && charCode <= 90)) {
                    this.showWarning(el);
                } else {
                    this.hideWarning();
                }
            },
            showWarning: function (el) {
                var x = Ext.fly(el).getX();
                var width = Ext.fly(el).getWidth();
                var y = Ext.fly(el).getY();

                this.alertBox.setXY([x + width + 6, y]);
                this.alertBox.show();
            },
            hideWarning: function () {
                this.alertBox.hide();
            }
        }


        this.dialog.show(null, capsLock.init, capsLock);
        Bio.Login.appendCallback(callback);
    },

    processCallbacksOnOK: function (callbacks) {
        if (callbacks instanceof Array) {
            Ext.each(callbacks, function (callback) {
                if (callback && callback.fn)
                    Ext.Function.bind(callback.fn, callback.scope)();
            });
        }
    },

    doLoginPosted: function (options, success, response) {
        var dlg = Bio.Login.dialog;
        Bio.Login.transaction = null;
        // Заглушка
//        Bio.Login.processCallbacksOnOK(dlg.bio.Callback);
//        Bio.Login.dialog = null;
//        return;
        // Заглушка
        if (dlg) {
            Bio.dlg.hideGWait();
            var rspText = response.responseText;
            try {
                Bio.Login.lastLoginResult = Ext.decode(rspText);
            }
            catch (e) {
                Bio.Login.lastLoginResult = null;
                Bio.dlg.showMsg("Ошибка на сервере", Bio.Tools.ObjToStr(response));
            }
            if (Bio.Login.lastLoginResult != null) {
                var vEx = Bio.Login.lastLoginResult["ebio"];
                if (vEx != null) {
                    if (vEx.type == "EBioOk") {
                        if (dlg.bio)
                            Bio.Login.processCallbacksOnOK(dlg.bio.Callback);
                    }
                    else if (vEx.type == "EBioBadLogin") {
                        Bio.dlg.showMsg("Ошибка входа в систему", "Не верная структура {Имя пользователя}/{Пароль}", null, null, function () {
                            Bio.Login.showDialog(dlg.bio.Callback);
                        });
                    }
                    else if (vEx.type == 'EBioBadUser') {
                        Bio.dlg.showMsg("Ошибка входа в систему", "Пользователь в системе не зарегистрирован.", null, null, function () {
                            Bio.Login.showDialog(dlg.bio.Callback);
                        });
                    }
                    else if (vEx.type == 'EBioUncnfrmdUser') {
                        Bio.dlg.showMsg("Ошибка входа в систему", "Пользователь не активирован.", null, null, function () {
                            Bio.Login.showDialog(dlg.bio.Callback);
                        });
                    }
                    else if (vEx.type == "EBioBadPwd") {
                        Bio.dlg.showMsg("Ошибка входа в систему", "Не верный пароль.", null, null, function () {
                            Bio.Login.showDialog(dlg.bio.Callback);
                        });
                    }
                    else if (vEx.type == "EBioException") {
                        Bio.dlg.showMsg("Непредвиденная ошибка", vEx.message, null, null, function () {
                            Bio.Login.showDialog(dlg.bio.Callback);
                        });
                    }
                }
            }
            Bio.Login.dialog = null;
        }
    },

    //myMask : undefined,
    doLoginPost: function (login) {
        Bio.app.waitMaskShow("Проверка имени пользователя и пароля...");

        //Bio.dlg.showGWait("Проверка имени пользователя и пароля...");
        var vURL = Bio.Tools.bldBioUrl("/login");
        var cfg = {
            url: vURL,
            params: {login: login},
            success: undefined,
            failure: undefined,
            callback: function(){
                Bio.app.waitMaskHide();
                this.doLoginPosted();
            },
            scope: this,
            method: "POST"
        };
        this.transaction = Ext.Ajax.request(cfg);
//        var symulateRequestTask = new Ext.util.DelayedTask(function(){
//            Ext.Function.bind(cfg.callback, this)();
//        }, this);
//        symulateRequestTask.delay(1000);
    },

    doOK: function (sender) {
        //alert(btn.getXType());
        var frm = (sender.getXType() === "button") ? sender.ownerCt : sender;
        //alert('doOK');

        //Bio.dlg.showMsg("this", Bio.Tools.ObjToStr(c));
        //if(Bio.Login.dialog){
        /*var vUserNameFld = vFrm.findBy(function(c){
         return (c.name) && (c.name === "FA_USER_NAME");
         });
         alert(vUserNameFld.length);*/

        var userNameFld = frm.query("name", "FA_USER_NAME")[0];
        var userPasswordFld = frm.query("name", "FA_PASSWORD")[0];

        //alert(vUserNameFld.name);
        var userName = (userNameFld) ? userNameFld.getValue() : null;
        var userPassword = (userPasswordFld) ? userPasswordFld.getValue() : null;

        var cdt = new Date();
        var vm = cdt.getMonth();
        vm++;
        cdt.setMonth(vm);
        if (Bio.cooks) {
            Bio.cooks.setCookie("cUserName", userName, cdt);
        }

        //alert(vUserName+"/"+vUserPassword);
        Bio.Login.doLoginPost(userName + "/" + userPassword);
        Bio.Login.dialog.close();
        //}

    },

    doCancel: function () {
        //alert('doCancel');
        if (Bio.Login.dialog)
            Bio.Login.dialog.close();
    },

    doOnLogedout: function (options, success, response) {
        //alert("doOnLogedout!!!");
        this.transaction = null;
        Bio.dlg.hideGWait();
        var vRspText = response.responseText;
        //Ext.MessageBox.hide();
        try {
            Bio.Login.lastLoginResult = Ext.util.JSON.decode(vRspText);
        } catch (e) {
            Bio.Login.lastLoginResult = null;
            Bio.dlg.showMsg("Ошибка на сервере", Bio.Tools.ObjToStr(response));
        }
        if (Bio.Login.lastLoginResult != null) {
            var vEx = Bio.Login.lastLoginResult["ebio"];
            if (vEx != null) {
                if (vEx.type == "EBioLoggedOut") {
                    var vURL = Bio.Tools.bldBioUrl();
                    self.document.location = vURL;
                }
            }
        }
    },

    processLogout: function (btn) {
        if (btn == "yes") {
            Bio.dlg.showGWait("Завершение работы...");
            var vURL = Bio.Tools.bldBioUrl("doLogout");
            var vCfg = {
                url: vURL,
                //params: {FLOGIN:pLogin},
                success: undefined,
                failure: undefined,
                callback: Bio.Login.doOnLogedout,
                scope: Bio.Login,
                method: "POST"
            };
            Bio.Login.transaction = Ext.Ajax.request(vCfg);
        }
    },

    doLogout: function () {
        Ext.Msg.show({
            title: 'Выход',
            msg: 'Вы действительно хотите выйты из системы?',
            buttons: Ext.Msg.YESNO,
            fn: Bio.Login.processLogout,
            animEl: 'ux-taskbar-start',
            icon: Ext.MessageBox.QUESTION
        });
    }

}


