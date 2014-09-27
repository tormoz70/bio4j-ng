Ext.namespace("Bio");
Ext.define('Bio.login', {
    singleton: true,
    storeLastSuccessUser: function(user) {
        if(user) {
            if(!Bio.app.curUsr)
                Bio.app.curUsr = {};
            Ext.apply(Bio.app.curUsr, user);
            Bio.app.curUsr.login = null;
            //Bio.cooks.setCookie("cUserUID", user.uid);
            Bio.cooks.setCookie("cUserName", user.login);
        }
    },

    restoreLastSuccessUserName: function() {
        return Bio.cooks.getCookie('cUserName');
    },
    restoreLastSuccessUserUID: function() {
        return Bio.cooks.getCookie('cUserUID');
    },
    removeLastSuccessUserStore: function() {
        Bio.cooks.deleteCookie("cUserUID");
        //Bio.cooks.deleteCookie("cUserName");
    },

    showDialog: function (callback) {
        var dialog = new Bio.dialog.Login({callback: callback});
        dialog.showDialog();
    },

    getUser: function(callback) {
        var me = this;
        var usr = Bio.app.curUsr;
        if(Bio.tools.isDefined(usr))
            Ext.callback(callback.fn, callback.scope, [usr]);
        else {
            var storedUserUID = me.restoreLastSuccessUserUID();
            if(Bio.tools.isDefined(storedUserUID)) {
                usr = new Bio.User({uid:storedUserUID});
                Ext.callback(callback.fn, callback.scope, [usr]);
                return;
            }
            Bio.login.showDialog({
                fn: function(dr) {
                    if(dr.modalResult === 1) {
                        Bio.app.waitMaskShow("Вход в систему...");
                        usr = new Bio.User({login:dr.login});
                        Ext.callback(callback.fn, callback.scope, [usr]);
                    }
                }
            });
        }
    },

    processUser: function(bioResponse, callback) {
        var me = this;
        if (bioResponse) {
            if (bioResponse.exception) {
                if (bioResponse.exception.class === "ru.bio4j.ng.model.transport.BioError$Login$BadLogin") {
                    Bio.app.curUsr = undefined;
                    me.removeLastSuccessUserStore();
                    Bio.dlg.showMsg("Вход", "Ошибка в имени или пароле пользователя!", 400, 120, callback);
                    return false;
                }
                if (bioResponse.exception.class === "ru.bio4j.ng.model.transport.BioError$Login$LoginExpired") {
                    Bio.app.curUsr = undefined;
                    me.removeLastSuccessUserStore();
                    if (callback) {
                        var cb = Bio.tools.wrapCallback(callback);
                        Ext.callback(cb.fn, cb.scope, [
                            {modalResult: 1}
                        ]);
                    }
                    return false;
                }
            }
            if (bioResponse.user)
                me.storeLastSuccessUser(bioResponse.user);
            return true;
        }
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
//            Bio.dlg.showMsg("Ошибка на сервере", Bio.tools.objToStr(response));
//        }
//        if (Bio.Login.lastLoginResult != null) {
//            var vEx = Bio.Login.lastLoginResult["ebio"];
//            if (vEx != null) {
//                if (vEx.type == "EBioLoggedOut") {
//                    var vURL = Bio.tools.bldBioUrl();
//                    self.document.location = vURL;
//                }
//            }
//        }
//    },
//
//    processLogout: function (btn) {
//        if (btn == "yes") {
//            Bio.dlg.showGWait("Завершение работы...");
//            var vURL = Bio.tools.bldBioUrl("doLogout");
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