Ext.namespace("Bio");
Ext.define('Bio.login', {
    singleton: true,
    storeLastSuccessUser: function(user) {
        if(user) {
            if(!Bio.app.curUsr)
                Bio.app.curUsr = {};
            Ext.apply(Bio.app.curUsr, user);
            Bio.app.curUsr.login = null;
//            var cdt = new Date();
//            var vm = cdt.getMonth();
//            vm++;
//            cdt.setMonth(vm);
//            if (Bio.cooks) {
//                Bio.cooks.setCookie("cUserUID", user.uid, cdt);
//                Bio.cooks.setCookie("cUserName", user.name, cdt);
//            }
            Ext.util.Cookies.set("cUserUID", user.uid);
            Ext.util.Cookies.set("cUserName", user.name);
        }
    },

    restoreLastSuccessUserName: function() {
//        if (Bio.cooks)
//            return Bio.cooks.getCookie("cUserName");
        return Ext.util.Cookies.get('cUserName');
    },
    restoreLastSuccessUserUID: function() {
//        if (Bio.cooks)
//            return Bio.cooks.getCookie("cUserUID");
        return Ext.util.Cookies.get('cUserUID');
    },
    removeLastSuccessUserName: function() {
//        if (Bio.cooks) {
//            Bio.cooks.deleteCookie("cUserUID");
//            Bio.cooks.deleteCookie("cUserName");
//        }
        Ext.util.Cookies.clear("cUserUID");
        Ext.util.Cookies.clear("cUserName");
    },

    showDialog: function (callback) {
        var dialog = new Bio.dialog.Login({callback: callback});
        dialog.showDialog();
    },

    getUser: function(callback) {
        var me = this;
        var usr = Bio.app.curUsr;
        if(usr)
            Ext.callback(callback.fn, callback.scope, [usr]);
        else {
            var storedUserUID = me.restoreLastSuccessUserUID();
            if(storedUserUID) {
                usr = new Bio.User({uid:storedUserUID});
                Ext.callback(callback.fn, callback.scope, [usr]);
                return;
            }
            Bio.login.showDialog({
                fn: function(dr) {
                    if(dr.modalResult === 1) {
                        usr = new Bio.User({login:dr.login});
                        Ext.callback(callback.fn, callback.scope, [usr]);
                    }
                }
            });
        }
    },

    processUser: function(response, callback) {
        var me = this;
        if(response.responseText) {
            var bioResponse = Ext.decode(response.responseText);
            if (bioResponse) {
                if (bioResponse.exceptions instanceof Array && bioResponse.exceptions.length > 0) {
                    if (bioResponse.exceptions[0].class === "ru.bio4j.ng.model.transport.BioError$Login$BadLogin") {
                        Bio.app.curUsr = undefined;
                        me.removeLastSuccessUserName();
                        Bio.dlg.showMsg("Вход", "Ошибка в имени или пароле пользователя!", 400, 120, callback);
                        return false;
                    }
                    if (bioResponse.exceptions[0].class === "ru.bio4j.ng.model.transport.BioError$Login$LoginExpired") {
                        Bio.app.curUsr = undefined;
                        me.removeLastSuccessUserName();
                        if (callback) {
                            var cb = Bio.Tools.wrapCallback(callback);
                            Ext.callback(cb.fn, cb.scope, [
                                {modalResult: 1}
                            ]);
                        }
                        return false;
                    }
                    Bio.dlg.showErr("Ошибка", bioResponse.exceptions[0], 400, 300, null);
                    return false;
                }
                if (bioResponse.user)
                    me.storeLastSuccessUser(bioResponse.user);
                return true;
            }
        }
        Bio.dlg.showErr("Ошибка", "Unknown response recived. responseText: "+(response.responseText||"<null>"), 400, 300, null);
        return false;
    }

//    doOnLogedout: function (options, success, response) {
//        this.transaction = null;
//        Bio.dlg.hideGWait();
//        var vRspText = response.responseText;
//        try {
//            Bio.Login.lastLoginResult = Ext.util.JSON.decode(vRspText);
//        } catch (e) {
//            Bio.Login.lastLoginResult = null;
//            Bio.dlg.showMsg("Ошибка на сервере", Bio.Tools.ObjToStr(response));
//        }
//        if (Bio.Login.lastLoginResult != null) {
//            var vEx = Bio.Login.lastLoginResult["ebio"];
//            if (vEx != null) {
//                if (vEx.type == "EBioLoggedOut") {
//                    var vURL = Bio.Tools.bldBioUrl();
//                    self.document.location = vURL;
//                }
//            }
//        }
//    },
//
//    processLogout: function (btn) {
//        if (btn == "yes") {
//            Bio.dlg.showGWait("Завершение работы...");
//            var vURL = Bio.Tools.bldBioUrl("doLogout");
//            var vCfg = {
//                url: vURL,
//                success: undefined,
//                failure: undefined,
//                callback: Bio.Login.doOnLogedout,
//                scope: Bio.Login,
//                method: "POST"
//            };
//            Bio.Login.transaction = Ext.Ajax.request(vCfg);
//        }
//    },
//
//    doLogout: function () {
//        Ext.Msg.show({
//            title: 'Выход',
//            msg: 'Вы действительно хотите выйты из системы?',
//            buttons: Ext.Msg.YESNO,
//            fn: Bio.Login.processLogout,
//            animEl: 'ux-taskbar-start',
//            icon: Ext.MessageBox.QUESTION
//        });
//    }

});

//Bio.login = new Bio.Login();