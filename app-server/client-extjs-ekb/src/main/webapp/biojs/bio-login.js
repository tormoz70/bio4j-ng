Ext.namespace("Bio");
Ext.define('Bio.login', {
    singleton: true,
    storeLastSuccessUserName: function(userName) {
        var cdt = new Date();
        var vm = cdt.getMonth();
        vm++;
        cdt.setMonth(vm);
        if (Bio.cooks) {
            Bio.cooks.setCookie("cUserName", userName, cdt);
        }
    },

    restoreLastSuccessUserName: function() {
        if (Bio.cooks)
            return Bio.cooks.getCookie("cUserName");
        return null;
    },

    showDialog: function (callback) {
        var dialog = new Bio.dialog.Login({callback: callback});
        dialog.showDialog();
    },

    getUser: function(callback) {
        var usr = Bio.app.curUsr;
        if(usr)
            Ext.callback(callback.fn, callback.scope, [usr]);
        else {
            Bio.login.showDialog({
                fn: function(dr) {
                    if(dr.modalResult === 1) {
                        usr = new Bio.User({login:dr.login});
                        Ext.callback(callback.fn, callback.scope, [usr]);
                    }
                }
            });
        }
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