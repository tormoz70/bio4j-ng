/*Bio.DataTypes = {
	DateType : "DATE",
	BooleanType : "BOOLEAN",
	NumberType : "NUMBER",
	CurrencyType : "CURRENCY"
};  
*/
/*
function Point(iX, iY){
	this.x = iX;
	this.y = iY;
};
*/
/**
 * Удаляет все найденные элементы из массива
 * @param {Object} o Элемент массива, который нужно удалить
 * @return {Array} Результирующий массив
 */
Array.prototype.removeAll = function(o){
  for(var i = 0; i < this.length;)
    this[i] == o ? this.splice(i, 1) : i++;
  return this;
}

/**
 * Форматирует число как денежную единицу
 * @param {Number} v
 * @return {String}
 */
Ext.util.Format.ruMoney = function(v){
  v = (Math.round((v - 0) * 100)) / 100;
  v = (v == Math.floor(v)) ? v + ".00" : ((v * 10 == Math.floor(v * 10)) ? v + "0" : v);
  v = String(v);
  var ps = v.split('.');
  var whole = ps[0];
  var sub = ps[1] ? '.'+ ps[1] : '.00';
  var r = /(\d+)(\d{3})/;
  while (r.test(whole)) {
    whole = whole.replace(r, '$1' + ',' + '$2');
  }
  return whole + sub + 'р.';
};
/**
 * Добавляем в объект Ext.MessageBox метод showHover для вывода выплывающего окна с сообщением
 */
Ext.MessageBox.hover = function(){
  var msgCt;

  function createBox(t, s){
    return ['<div class="msg">',
            '<div class="x-box-tl"><div class="x-box-tr"><div class="x-box-tc"></div></div></div>',
            '<div class="x-box-ml"><div class="x-box-mr"><div class="x-box-mc"><h3>', t, '</h3>', s, '</div></div></div>',
            '<div class="x-box-bl"><div class="x-box-br"><div class="x-box-bc"></div></div></div>',
            '</div>'].join('');
  }
  return {
    /**
     * Выводит выплывающее окно с сообщением
     * @param {Object} el Элемент, к которому будет привязано окно
     * @param {Object} title Заголовок в окне
     * @param {Object} format Текст сообщения в окне
     */
    show: function(el, title, format){
      if(!msgCt){
        msgCt = Ext.DomHelper.insertFirst(document.body, {id:'msg-div'}, true);
        msgCt.setStyle({
          position:"absolute",
          //left:"35%",
          //top:"10px",
          width:"250px",
          "z-index":"20000"
        });
      }
      msgCt.alignTo(el, 't-b');
      var s = String.format.apply(String, Array.prototype.slice.call(arguments, 2));
      var m = Ext.DomHelper.append(msgCt, {html:createBox(title, s)}, true);
      m.slideIn('t').pause(1).ghost("t", {remove:true});
    }
  };
}();
Ext.MessageBox.showHover = Ext.MessageBox.hover.show;

Ext.namespace("Bio");
Bio.Tools = function(){
	/**
	 * Шаблон для абстрактного контейнера
	 * @type {Ext.Template}
	 */
	var _absContTmpl = null;
	
	return {
		addEvent: function(p_target, p_functionref, p_tasktype){ 
			p_target.attachEvent(p_tasktype, p_functionref);
		},
	
		ObjToStr:function(pObj){
			var vStr = "";
			for(var vProp in pObj)
				vStr += pObj + '.' + vProp + '=' + pObj[vProp] + '\n';
			return vStr; 
		},
		
		showObj:function(pObj){
			alert(this.ObjToStr(pObj)); 
		},
		
		findPropOfObj:function(pObj, pPropName){
			var vStr = "";
			for(var vProp in pObj){
				var re = new RegExp(pPropName, 'gi');
				var vFound = vProp.match(re);
				if(vFound) vStr += pObj + '.' + vProp + '=' + pObj[vProp] + '\n';
			}
			alert(vStr); 
		},
		
		findParentElementByClass:function(pNode, pClassName){
			var vParentCell = pNode.parentElement;
			while(vParentCell.className != pClassName){ 
				vParentCell = vParentCell.parentElement;
				if(vParentCell.tagName == "BODY") break;
			}
			return (vParentCell.className == pClassName) ? vParentCell : null;  
		},
	
		findChildElementByTagAndClass:function(pNode, pTagName, pClassName){
			var vChildTags = pNode.getElementsByTagName(pTagName);
			for(var i=0; i<vChildTags.length; i++)
				if(vChildTags[i].className == pClassName)
					return vChildTags[i]; 				 
			return null;  
		},
		
		calcXY_Left : 0,
		calcXY_Top : 0,
		calcXY:function(pObj, pRelateObj, pContinue){
			if(!pContinue){
				this.calcXY_Left = 0;
				this.calcXY_Top = 0;
			}
			if(pObj){
				this.calcXY_Left += pObj.offsetLeft;
				this.calcXY_Top += pObj.offsetTop;
				if((pObj.offsetParent != null)&&
				   (pObj.offsetParent != pRelateObj) &&
				   (pObj.offsetParent.tagName != "BODY")){
					this.calcXY(pObj.offsetParent, pRelateObj, true);
				}
			}
		},
		
		prepareURL:function(pUrl){
			var vURL = new String(pUrl);
			vURL = vURL.replace(/\bSYS_APP_URL\b/, csSYS_APP_URL);
			return vURL;
		},
	
		LoadXMLDoc:function(pUrl){
		  var Result = new ActiveXObject("Microsoft.XMLDOM");
		  Result.async = false;
		  Result.load(pUrl);
			return Result;
		},
		/**
		 * Создаёт пустой контейнер (DIV), встроенный в pOwner
		 * @param {HTMLElement|Ext.Element|String} pOwner родительский контейнер
		 * @param {String|Array} pAddClass Этот класс или массив классов добавится в контейнер
		 * @return {Ext.Element} Вновь созданный контейнер
		 */
		creAbstractContainer: function(pOwner, pAddClass){
			if(!_absContTmpl) {
				_absContTmpl = Ext.DomHelper.createTemplate({tag: "DIV", cls: "bio-container {0}"});
				_absContTmpl.compile();
			}
			return _absContTmpl.append(pOwner, pAddClass instanceof Array ? [pAddClass.join(" ")] : (pAddClass ? [pAddClass] : []), true);
		},
		
		/**
		 * Прицепляет Пару pParamName-pParamValue к объекту pTargetObj,
		 * Доступ к прицепленным параметрам: getBioParams(pTargetObj)
		 * @param {Object} pTargetObj
		 * @param {Object} pParams = {param_name: param_value, ...}
		 */
		setBioParams:function(pTargetObj, pParams){
			if(!pTargetObj.bioParams){
				pTargetObj.bioParams = {};
			}
			for(var vProp in pParams){
				pTargetObj.bioParams[vProp] = pParams[vProp];
			}
			return true;
		},
		
		/**
		 * Возвращает именованый массив Параметров прицепленных к pTargetObj с помощью setBioParam
		 * @param {Object} pTargetObj
		 */
		getBioParamsAsJSONArray:function(pTargetObj){
			var vPrms = pTargetObj.bioParams;
			if(vPrms){
				return Ext.encode(vPrms);
			}else
				return null;
		},
		
    bldQStr:function(pParamsObj){
      var vResult = null;
			for(var vProp in pParamsObj){
        if (pParamsObj[vProp]) {
          if (vResult === null) 
            vResult = vProp + '=' + pParamsObj[vProp];
          else 
            vResult = vResult + "&" + vProp + "=" + pParamsObj[vProp];
        }
			}
      return vResult;
    },

	setIOPrm:function(pBioParams, vQParams){
      var vBioParamsStr = (pBioParams)?((pBioParams instanceof String)?pBioParams:Ext.encode(pBioParams)):null;
      var vQPrms = {ioprm: vBioParamsStr};
      Ext.apply(vQPrms, vQParams);
			return vQPrms;
	},

    bldQPrms:function(pBioMsgType, pBioCD, pBioParams, pExtParams){
      var vQPrms = {mtp: pBioMsgType,
                    iocd: pBioCD};
      Ext.apply(vQPrms, pExtParams)
      return Bio.Tools.setIOPrm(pBioParams, vQPrms);
    },

    /**
     * @param {Object} pBioMsgType - тип запроса см. ini\regmsgs.xml.
     * @param {Object} pBioCD - код ИО.
     * @param {Object} pBioParams - Параметры ИО.
     * @param {Object} pExtParams - дополнительные параметры запроса к серверу
     */

    bldBioUrl:function(url, params){
        var qprmsStr = Bio.Tools.bldQStr(params);

  		return Bio.app.APP_URL+url+((qprmsStr)?("?"+qprmsStr):"");
    },

    bldAjaxCfg: function (pBioMsgType, pBioCD, pBioParams, pExtParams, pCallback, pScope){
      var vQPrms = Bio.Tools.bldQPrms(pBioMsgType, pBioCD, pBioParams, pExtParams);
      return {
          url: Bio.Tools.bldBioUrl(), 
          params: vQPrms,
          success: undefined,
          failure: undefined,
          callback: pCallback,
          scope: pScope,
          method: "POST"
      };
    },
    
    createWindow:function(config, cls, events){
        var win = new (cls||Ext.Window)(config);
        if(events){
            if(events instanceof Array)
                for(var i=0; i<events.length; i++)
                    win.on(events[i].event, events[i].handler, events[i].scope);
            else
                win.on(events.event, events.handler, events.scope||win);
        }
        return win;
    },
    
    transaction: null,
    
    doOnLoadComponent:function(options, success, response){
      Bio.dlg.hideGWait(options.Container.getEl());
      this.transaction = null;
      if (success) {
        var vObjStr = response.responseText;
        //bioDlg.hideGWait(this);
        //Ext.Msg.alert("vObjStr", vObjStr);
        try {
          var vObj = Ext.decode(vObjStr);
          //alert(1);
          vObj.id = Bio.Tools.bldBioUrl(options.params.mtp, options.params.iocd, options.params.ioprm);
          options.Container.items.clear();
          var vNewComp = options.Container.add(vObj);
          options.Container.doLayout();
          if(options && options.DoOnLoad && options.DoOnLoad.fn)
            options.DoOnLoad.fn.createDelegate(options.DoOnLoad.scope)(vNewComp);
        } catch (e) {
          Bio.dlg.showMsg('Ошибка на сервере', e.message + "\n" + vObjStr);
        }
      }
    },
    
    /**
     * 
     * @param {Object} pContainer  - Ext.Container
     * @param {Object} pBioCode    - Код объекта
     * @param {Object} pBioParams  - Параметры объекта
     * @param {Object} pExtParams  - Дополнительные параметры запроса
     * @param {Object} pDoOnLoad   - {fn:function(pComponent), scope:Object}
     */
    loadComponent:function(pContainer, pBioCode, pBioParams, pExtParams, pDoOnLoad){
      //bldAjaxCfg(pBioMsgType, pBioCD, pBioParams, pExtParams, pCallback, pScope);
      //alert(1);
      var vMTP = "getMItm";
      var vID = Bio.Tools.bldBioUrl(vMTP,pBioCode,pBioParams);
      var vItem = pContainer.items.find(function(c){if(c.id == vID) return c;});  
      if (!vItem) {
        Bio.dlg.showGWait("Загрузка...", pContainer.getEl());
        this.ajaxR(vMTP, pBioCode, pBioParams, pExtParams, {fn:this.doOnLoadComponent, scope:this}, {Container:pContainer, DoOnLoad:pDoOnLoad});
      }
    },
    
    tryDecodeJSON:function(pJSON){
      var vObj = null;
      try {
        vObj = Ext.decode(pJSON);
      } catch (e) {
        vObj = null;
      }
      return vObj;
    },
    
    /**
     * Произвольный Ajax запрос с функцией восстановления пароля при обрыве соединения
     * @param {Object} pMType      - см. ini\regmsgs.xml 
     * @param {Object} pBioCode    - Код объекта
     * @param {Object} pBioParams  - Параметры объекта
     * @param {Object} pExtParams  - Дополнительные параметры запроса
     * @param {Object} pCallback   - {fn:function(options, success, response), scope:Object}
     * @param {Object} pExtCfg     - Дополнительные параметры конфигурации запроса
     */
		ajaxR:function(pMType, pBioCode, pBioParams, pExtParams, pCallback, pExtCfg){
      var vAjaxCfg = Bio.Tools.bldAjaxCfg(pMType, pBioCode, pBioParams, pExtParams, this.doOnAjaxRe, this);
      vAjaxCfg.ExtCallback = pCallback;
      if(pExtCfg)
        Ext.apply(vAjaxCfg, pExtCfg);
      this.transaction = Ext.Ajax.request(vAjaxCfg);
    },
    doOnAjaxRe:function(options, success, response){
      this.transaction = null;
      if (success) {
        var vObjStr = response.responseText;
        var vObj = this.tryDecodeJSON(vObjStr);
        if (vObj && vObj.ebio && (vObj.ebio.type == "EBioStart")) {
          Bio.Login.showDialog({
            fn: function(params){
              this.transaction = Ext.Ajax.request(params);
            }, scope:this, params:options
          });
        }
        else {
          if(options && options.ExtCallback && options.ExtCallback.fn)
            options.ExtCallback.fn.createDelegate(options.ExtCallback.scope)(options, success, response);
          
        }
      }
    },
    
    tryDecode: function(pJsonStr){
      var vObj = null;
      try {
        vObj = Ext.decode(pJsonStr);
      } 
      catch (e) {
        vObj = null;
      }
      return vObj
    },
    
    getSeldRow: function(pSelectionModel){
      if(pSelectionModel){
        if((pSelectionModel instanceof Ext.grid.CellSelectionModel) && typeof(pSelectionModel.getSelectedCell) == "function"){
          var vSeldCell = pSelectionModel.getSelectedCell();
          var vRowIndx = (vSeldCell) ? vSeldCell[0] : -1;
          return pSelectionModel.grid.getStore().getAt(vRowIndx);
        }else if((pSelectionModel instanceof Ext.grid.CheckboxSelectionModel) && typeof(pSelectionModel.getSelected) == "function")
          return pSelectionModel.getSelected();
        else if((pSelectionModel instanceof Ext.grid.RowSelectionModel) && typeof(pSelectionModel.getSelected) == "function")
          return pSelectionModel.getSelected();
      }else
        return null;
    },

    getFrmFld:function(pPanel, pFldName){
      var vFlds = pPanel.findBy(function(c){
                      if((c.isXType && (c.dataBind || c.name) && c.setValue && c.getValue)){
                        if(c.isXType("field") && ((pFldName == c.dataBind) || (pFldName == c.name)))
                          return true;
                      }else
                        return false;
                    });
      if (vFlds instanceof Array) {
        return vFlds[0];
      }else{
        return vFlds;
      }
    },
    
    getBioFormPanel:function(pControl){
      if((pControl.ownerCt) && (pControl.ownerCt instanceof Bio.form.FormPanel))
        return pControl.ownerCt;
      else{
        if(!pControl.ownerCt) 
          return null;
        else
          return this.getBioFormPanel(pControl.ownerCt);
      }
      
    },

    downloadFile:function(pUrl){
      var vIFrm = Ext.get("ifrm_downloader");
      if (vIFrm) {
        vIFrm.dom.setAttribute("src", pUrl);
      } 
    },

    wrapCallback: function(callback){
        var cb = callback;
        var cbType = typeof callback;
        if(cb && cbType === "function")
            cb = { fn: cb };
        return cb;
    }

    //{"@class":"ru.bio4j.model.transport.BioRequest","bioCode":"emp.test","bioParams":{"query":{"value":"SAL%","type":"string","left":"SAL%","right":"string"}}}
    //{query:{value:"SAL%",type:"string",left:"SAL%",right:"string"}}

	}
}()
