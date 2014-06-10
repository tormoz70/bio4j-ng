Ext.namespace("Bio");
Bio.dlg = {
    dialog: null,

    creDlgContent: function (pDlgTitle) {
        var vDlgDiv = bioTools.creAbstractContainer(Ext.get("all_div_container"));
        vDlgDiv.setStyle({visibility: "hidden", position: "absolute", top: "0px"});
        var vDlgHdr = bioTools.creAbstractContainer(vDlgDiv, "x-dlg-hd");
        vDlgHdr.insertHtml("afterBegin", pDlgTitle, false);
        vDlgDiv.XFldDlgHd = vDlgHdr;
        var vDlgBody = bioTools.creAbstractContainer(vDlgDiv, "x-dlg-bd");
        vDlgDiv.XFldDlgBody = vDlgBody;
        return vDlgDiv;
    },

    /**
     * Обработчик события
     * @param {Object} sender Объект-возбудитель события
     * @param {Number} width Ширина
     * @param {Number} height Высота
     */
    onDlgResize: function (sender, width, height) {
        var vHeight = sender.body.getHeight();
        var vTrgt = sender.XDlgForm.findField("MsgText");
        vTrgt.setSize({height: vHeight - 30});
    },

    /**
     * Выводит сообщение
     * @param {Object} pTitle Заголовок окна
     * @param {Object} pText Текст сообщения
     * @param {Object} pWidth Ширина окна
     * @param {Object} pHeight Высота окна
     * @param {function} pDoOnClosePrc Функция, которая будет выполнена на закрытии
     */

    showMsg: function (pTitle, pText, pWidth, pHeight, pDoOnClosePrc) {
        this.lastLoginResult = null;
        this.dialog = Bio.Tools.createWindow({
                title: pTitle,
                width: (pWidth) ? pWidth : 500,
                height: (pHeight) ? pHeight : 300,
                //minWidth: 300,
                //minHeight: 200,
                layout: 'fit',
                plain: true,
                //bodyStyle:'padding:5px;',
                buttonAlign: 'center',
                modal: true,
                buttons: [
                    {text: "ОК", handler: this.doOK}
                ],
                keys: [
                    {key: Ext.EventObject.ENTER,
                        fn: this.doOK}
                ],
                items: {
                    xtype: 'form',
                    baseCls: "x-plain",
                    //labelWidth: 55,
                    //frame:true,
                    items: [
                        {
                            xtype: "textarea",
                            hideLabel: true,
                            name: "msgText",
                            anchor: "100% -5",
                            value: pText
                        }
                    ]
                }
            },
            null,
            ((pDoOnClosePrc) ? {event: "close", handler: pDoOnClosePrc} : null));
        var map = new Ext.KeyMap(this.dialog.body, [
            {key: Ext.EventObject.ENTER,
                fn: this.doOK,
                scope: this.dialog}
        ]);
        this.dialog.updateText = function (pText) {
            var vTxtFld = Bio.Tools.getFrmFld(this.items.items[0], "msgText");
            if (vTxtFld) vTxtFld.setValue(pText);
        };
        var vShowWin = function () {
            this.dialog.show();
        }
        vShowWin.defer(20, this);
    },

    doOK: function () {
        var vWin = (this.ownerCt) ? this.ownerCt : this;
        if (vWin instanceof Ext.Window)
            vWin.close();
    },


    showErr: function (pTitle, pErr, pWidth, pHeight, pDoOnClosePrc) {
        var vMsg = pErr;
        var vRespObj = null;
        if (typeof pErr == "string")
            vRespObj = Bio.Tools.tryDecodeJSON(pErr);
        else if (pErr && pErr.ebio)
            vRespObj = pErr;
        if (vRespObj && vRespObj.ebio)
            vMsg = vRespObj.ebio.message;
        this.showMsg(
            pTitle,
            vMsg,
            pWidth,
            pHeight,
            pDoOnClosePrc
        );
    }
}
