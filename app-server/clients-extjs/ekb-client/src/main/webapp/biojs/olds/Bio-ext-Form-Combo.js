/**
 * @author Ayrat
 */
Ext.override(Ext.form.ComboBox, {
  /**
   * Добавляем событие afterchange
   */
  initComponent: Ext.form.ComboBox.prototype.initComponent.createSequence(function(){
    this.addEvents('afterchange');
		if (this.store) {
			this.store.on("load", function(pStore){
				this.suspendEvents();
				this.setValue(this.getValue());
				this.resumeEvents();
			}, this);
		}
  }),
  
  doQuery: function(q, forceAll){
    if (q === undefined || q === null) {
      q = '';
    }
    var qe = {
      query: q,
      forceAll: forceAll,
      combo: this,
      cancel: false
    };
    if (this.fireEvent('beforequery', qe) === false || qe.cancel) {
      return false;
    }
    q = qe.query;
    forceAll = qe.forceAll;
    if (forceAll === true || (q.length >= this.minChars) || q.length == 0) {
      if (this.lastQuery !== q) {
        this.lastQuery = q;
        if (this.mode == 'local') {
          this.selectedIndex = -1;
          if (forceAll) {
            this.store.clearFilter();
          } else {
            this.store.filter(this.displayField, q);
          }
          this.onLoad();
        } else {
          // Bio
					if (this.store) {
				  	if (q) {
				  		this.store.baseParams["filter[0][field]"] = this.displayField;
				  		this.store.baseParams["filter[0][type]"] = "string";
				  		this.store.baseParams["filter[0][value]"] = q;
				  		this.store.baseParams["filter[0][comparison]"] = "eq";
				  	} else {
				  		delete this.store.baseParams["filter[0][field]"];
				  		delete this.store.baseParams["filter[0][type]"];
				  		delete this.store.baseParams["filter[0][value]"];
				  		delete this.store.baseParams["filter[0][comparison]"];
				  	}
	          this.store.load({
	            params: this.getParams(q)
	          });
		  		}
          //
          this.expand();
        }
      } else {
        this.selectedIndex = -1;
        this.onLoad();
      }
    }
  },
  
  setValue: function(v){
    this.prevSelectionText = this.lastSelectionText;
    
    var text = v;
    
    var internalSet = function(){
      this.lastSelectionText = text;
      if (this.hiddenField) {
        this.hiddenField.value = v;
      }
      Ext.form.ComboBox.superclass.setValue.call(this, text);
      this.value = v;
      
      if (this.prevSelectionText != this.lastSelectionText) 
        this.fireEvent('afterchange', this);
    }.createDelegate(this);
    
    if (this.valueField && this.store) {
      var r = null;
      this.store.locate(this.valueField, v, function(r){
        if (r)
          text = r.data[this.displayField];
        else if (this.valueNotFoundText !== undefined)
          text = this.valueNotFoundText;
        internalSet();
      }, this);
    } else
      internalSet();
  }
})
