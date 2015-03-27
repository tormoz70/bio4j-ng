Ext.override(Ext.data.Store, {
  load: Ext.data.Store.prototype.load.createInterceptor(function(options){
    if (options) {
      var vBioParamsStr = (options.bioParams) ? ((options.bioParams instanceof String) ? options.bioParams : Ext.encode(options.bioParams)) : null;
      var vNewFilterStr = null, vLastFilterStr = null;
      var re = /^filter\[.*/;
      for (var vItem in this.baseParams)
        if (re.test(vItem)) {
          var vItemStr = vItem + "=" + this.baseParams[vItem];
          vNewFilterStr = vNewFilterStr ? vNewFilterStr + vItemStr : vItemStr;
        }
      if (this.lastOptions)
        for (var vItem in this.lastOptions.params)
          if (re.test(vItem)) {
            var vItemStr = vItem + "=" + this.lastOptions.params[vItem];
            vLastFilterStr = vLastFilterStr ? vLastFilterStr + vItemStr : vItemStr;
          }
      if (vBioParamsStr || vNewFilterStr != vLastFilterStr) {
        delete this.baseParams.totalCount;
        if(this.lastOptions && this.lastOptions.params)
          delete this.lastOptions.params.totalCount;
        Ext.apply(this.baseParams, {
          ioprm: vBioParamsStr,
          endReached: false
        });
        delete options.bioParams;
      }
    }
  }),
  /**
   * Возвращает первую запись из локального набора данных по совпадению указанных значений полей
   * @param {String/Array} names Список имён полей
   * @param {String/Array} values Список значений соответствующих полей
   * @return {Ext.data.Record} Найденная запись или null
   */
  locateLocal: function(names, values){
    var lo = this._prepareLocateOptions(names, values);
    if (lo.count > 0) {
      var rnum = this.findBy(function(r){
        for(var i = 0, found = true; i < lo.count && found; i++){
          if(r.data[lo.names[i]] != lo.values[i])
            found = false;          
        }
        return (i == lo.count) && found;
      });
      if(rnum >= 0)
        return this.getAt(rnum)
    }
    return null;
  },
  /**
   * Ищет первую запись в наборе данных локально или отправляет запрос на сервер.
   * Затем вы зывает callback, в который передаётся найденная запись
   * @param {String/Array} names Список имён полей
   * @param {String/Array} values Список значений соответствующих полей
   * @param {Function} callback Функция, вызваемая по окончании поиска, с параметром найденной записи
   * @param {Object} scope Контекст, с которым вызовется callback
   */       
  locate: function(names, values, callback, scope){
    var r = this.locateLocal(names, values);
    if(!r && this.lastOptions && this.lastOptions.params && this.lastOptions.params.limit > 0){
      var locateParam = [];
      var lo = this._prepareLocateOptions(names, values);
      for(var i = 0; i < lo.count; i++){
        locateParam.push({
          name: lo.names[i],
          type: "string",
          value: lo.values[i]
        });
      }
      var locateStr = Ext.encode(locateParam);
      this.on("load", function(){
        var r = this.locateLocal(names, values);
        if(typeof callback == "function")
          callback.call(scope || this, r);
      }, this, {
        single: true
      });
      Ext.apply(this.lastOptions.params, {locate: locateStr});
      this.load(Ext.apply(this.lastOptions));
    } else if(typeof callback == "function")
      callback.call(scope || this, r);
  },
  
  _prepareLocateOptions: function(names, values){
    var n = [], v = [];
    if(typeof names == "string"){
      n = names.split(",");
    } else if(names instanceof Array){
      n = names.slice(0);
    }
    if(typeof values == "string"){
      v = values.split(",");
    }else if(values instanceof Array){
      v = values.slice(0);
    }else
      v[0] = values;  
    return {names: n, values: v, count: Math.min(n.length, v.length)};
  }
  
});
