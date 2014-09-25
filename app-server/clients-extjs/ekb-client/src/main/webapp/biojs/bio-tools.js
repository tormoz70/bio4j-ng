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
//Array.prototype.removeAll = function(o){
//  for(var i = 0; i < this.length;)
//    this[i] == o ? this.splice(i, 1) : i++;
//  return this;
//}

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
Bio.tools = function(){
	/**
	 * Шаблон для абстрактного контейнера
	 * @type {Ext.Template}
	 */
	var _absContTmpl = null;
	
	return {
        tryParsInt: function(v) {
            if(typeof(v) == "number")
                return v;
            if(typeof(v) == "string" && v.match(/\d+/) == v)
                return parseInt(v);
            return v;
        },

        parsWidthGetWidth: function (width) {
            if(width !== '*')
                return Bio.tools.tryParsInt(width);
            return undefined;
        },
        parsWidthGetFlex: function (width) {
            if(width === '*')
                return 1;
            return undefined;
        },

		addEvent: function(target, functionRef, taskType){
			target.attachEvent(taskType, functionRef);
		},
	
		dumpObject:function(obj){
			var str = "";
			for(var prop in obj)
				str += obj + '.' + prop + '=' + obj[prop] + '\n';
			return str;
		},

        objToArray:function(obj){
            var result = [];
            for(var prop in obj)
                result.push(obj[prop]);
            return result;
        },

		showObj:function(obj){
			alert(this.ObjToStr(obj));
		},
		
		findPropOfObj:function(obj, propName){
			var str = "";
			for(var prop in obj){
				var re = new RegExp(propName, 'gi');
				var vFound = prop.match(re);
				if(vFound) str += obj + '.' + prop + '=' + obj[prop] + '\n';
			}
			alert(str);
		},
		
        parentObjectByClassName: function(fromObj, className) {
            if(fromObj && fromObj.findParentBy)
                return fromObj.findParentBy(function(c) {
                    return c.$className == className;
                });
            return undefined;
        },

        parentFormByClassName: function(comp, className) {
            if (comp && comp.findParentBy) {
                var r = comp.findParentBy(function (c) {
                    return c.$className == className;
                });
                return r;
            }
            return undefined;
        },

        childByName: function (comp, name) {
            var r = comp.queryBy(function(c) {
                return c.name == name;
            });
            if(Bio.tools.isDefined(r) && r instanceof Array && r.length > 0)
                return r[0];
            return undefined;
        },

        childByClassName: function (comp, className) {
            var r = comp.queryBy(function(c) {
                return c.$className == className;
            });
            return r;
        },

        parentForm: function(comp) {
            var frmEl = (comp && comp.up ? comp.up('form') : null)
            return (frmEl && frmEl.getForm ? frmEl.getForm() : null);
        },

        isDefined: function(v) {
            return (v !== null) && (typeof v !== 'undefined');
        },
		
		calcXY_Left : 0,
		calcXY_Top : 0,
		calcXY:function(obj, relateObj, doContinue){
			if(!doContinue){
				this.calcXY_Left = 0;
				this.calcXY_Top = 0;
			}
			if(obj){
				this.calcXY_Left += obj.offsetLeft;
				this.calcXY_Top += obj.offsetTop;
				if((obj.offsetParent != null)&&
				   (obj.offsetParent != relateObj) &&
				   (obj.offsetParent.tagName != "BODY")){
					this.calcXY(obj.offsetParent, relateObj, true);
				}
			}
		},
		
		prepareURL:function(url){
			var vURL = new String(url);
			vURL = vURL.replace(/\bSYS_APP_URL\b/, csSYS_APP_URL);
			return vURL;
		},
	
		loadXMLDoc:function(url){
		  var Result = new ActiveXObject("Microsoft.XMLDOM");
		  Result.async = false;
		  Result.load(url);
			return Result;
		},
		/**
		 * Создаёт пустой контейнер (DIV), встроенный в pOwner
		 * @param {HTMLElement|Ext.Element|String} owner родительский контейнер
		 * @param {String|Array} addClass Этот класс или массив классов добавится в контейнер
		 * @return {Ext.Element} Вновь созданный контейнер
		 */
		creAbstractContainer: function(owner, addClass){
			if(!_absContTmpl) {
				_absContTmpl = Ext.DomHelper.createTemplate({tag: "DIV", cls: "bio-container {0}"});
				_absContTmpl.compile();
			}
			return _absContTmpl.append(owner, addClass instanceof Array ? [addClass.join(" ")] : (addClass ? [addClass] : []), true);
		},
		
        setBioParamValue:function(bioParams, paramName, paramValue, paramType){
            if(!(bioParams instanceof Array))
                bioParams = [];
            var paramExists = Ext.Array.findBy(bioParams, function(item, index) {
                return item.name == paramName;
            });
            if(paramExists) {
                paramExists.value = paramValue;
                paramExists.type = paramType;
            } else {
                bioParams.push({name: paramName, value: paramValue, type: paramType});
            }
            return bioParams;

        },

        setBioParam:function(bioParams, params){
            if(params.name)
                return Bio.tools.setBioParamValue(bioParams, params.name, params.value, params.type);
            else {
                for(var p in params)
                    bioParams = Bio.tools.setBioParamValue(bioParams, p, params[p]);
                return bioParams;
            }

        },

		/**
		 * Возвращает именованый массив Параметров прицепленных к targetObj с помощью setBioParam
		 * @param {Object} targetObj
		 */
		getBioParamsAsJSONArray:function(targetObj){
			var prms = targetObj.bioParams;
			if(prms){
				return Ext.encode(prms);
			}else
				return null;
		},
		
        bldQStr:function(params){
            var result = null;
            for(var prop in params){
                if (params[prop]) {
                    if (result === null)
                        result = prop + '=' + params[prop];
                    else
                        result = result + "&" + prop + "=" + params[prop];
                }
            }
            return result;
        },

        /**
         * @param {String} url - url.
         * @param {Object} params - Параметры.
         */
        bldBioUrl:function(url, params){
            var qprmsStr = Bio.tools.bldQStr(params);

            return Bio.app.APP_URL+url+((qprmsStr)?("?"+qprmsStr):"");
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
    
        tryDecode: function(jsonStr){
          return Ext.decode(jsonStr, true);
        },
    
        wrapCallback: function(callback){
            var cb = callback;
            var cbType = typeof callback;
            if(cb && cbType === "function")
                cb = { fn: cb };
            return cb;
        },

        processCallback: function(callback, scope, args) {
            if(callback) {
                if(typeof callback == 'function')
                    callback.call(scope || me, args);
                if (typeof callback.fn == 'function')
                    callback.fn.call(callback.scope || scope, args);
            }
        },

        truncDec: function(number) {
            return Math[number < 0 ? 'ceil' : 'floor'](number);
        }
	}
}()
