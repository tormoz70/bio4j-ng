/**
 * @author nick
 */

Ext.override(Ext.grid.GridPanel, {
  /**
   * Добавляем событие "selectionchange" в GridPanel
   */
  initComponent: Ext.grid.GridPanel.prototype.initComponent.createSequence(function(){
    this.addEvents("selectionchange");
  }),
  onRender: Ext.grid.GridPanel.prototype.onRender.createSequence(function(){
    var vSelectionModel = this.getSelectionModel();
    if ((vSelectionModel instanceof Ext.grid.CellSelectionModel)) {
      vSelectionModel.on("cellselect", function(sm, rowIndex, colIndex){
        this.fireEvent("selectionchange", this, this.getStore().getAt(rowIndex), rowIndex, colIndex);
      }, this);
    }
    else if ((vSelectionModel instanceof Ext.grid.CheckboxSelectionModel) || (vSelectionModel instanceof Ext.grid.RowSelectionModel)) {
      vSelectionModel.on("rowselect", function(sm, rowIndex, record){
        this.fireEvent("selectionchange", this, record, rowIndex, null);
      }, this);
    }
  }),
  /**
   * Загружает данные в таблицу, используя метод store.load()
   * @param {Object} options Дополнительные параметры
   */
  load: function(options){
    var o = options || {};
    var pb = this.getBottomToolbar();
    if (pb) 
      Ext.applyIf(o, {
        params: {
          start: 0,
          limit: pb.pageSize
        }
      });
    this.store.load(o);
  },
  reload: function(options){
    this.load(Ext.applyIf(options||{}, this.store.lastOptions));
  }
});

Ext.apply(Ext.ux.grid.filter.DateFilter.prototype, {
  dateFormat: "Ymd"
});

Ext.override(Ext.PagingToolbar, {
  /**
   * Перегружаем метод для обработки ситуации, когда номер текущей
   * страницы больше общего числа страниц 
   * @param {Object} store
   * @param {Object} r
   * @param {Object} o
   */
  onLoad : function(store, r, o){
    if(!this.rendered){
      this.dsLoaded = [store, r, o];
      return;
    }
    this.cursor = o.params ? o.params[this.paramNames.start] : 0;
    var d = this.getPageData(), ap = d.activePage;
    //Bio
    var ps = ap > d.pages ? ap : d.pages;
    //
    
    this.afterTextEl.el.innerHTML = String.format(this.afterPageText, d.pages);
    this.field.dom.value = ap;
    this.first.setDisabled(ap == 1);
    this.prev.setDisabled(ap == 1);
    this.next.setDisabled(ap == ps);
    this.last.setDisabled(ap == ps);
    this.loading.enable();
    this.updateInfo();
    this.fireEvent('change', this, d);
  },
  /**
   * Перегружаем метод для вызова store.reload вместо store.load при нажатии на кнопку обновления
   * @param {Object} start
   */
  doLoad: function(start){
    if (start != this.cursor) {
      var o = {}, pn = this.paramNames;
      o[pn.start] = start;
      o[pn.limit] = this.pageSize;
      if (this.fireEvent('beforechange', this, o) !== false) {
        this.store.load({
          params: o
        });
      }
    }
    else 
      this.store.reload();
  }
});

Ext.namespace("Bio.grid");
/**
 * Автоматическая нумерация строк в таблице с поддержкой постраничного вывода
 * @param {Object} config
 */
Bio.grid.PagedRowNumberer = function(config){
  Ext.apply(this, config);
  if (this.rowspan) {
    this.renderer = this.renderer.createDelegate(this);
  }
};

Bio.grid.PagedRowNumberer.prototype = {
  header: "",
  width: 23,
  sortable: false,
  fixed: true,
  hideable: false,
  dataIndex: '',
  id: 'numberer',
  rowspan: undefined,
  
  renderer: function(v, p, record, rowIndex, colIndex, store){
    if (this.rowspan) {
      p.cellAttr = 'rowspan="' + this.rowspan + '"';
    }
    if (record.dirty)
      p.attr = 'style="color:red;font-weight:bold;" qtip="Запись изменена"'; 
    var i = store.lastOptions.params ? store.lastOptions.params.start : 0;
    if (isNaN(i)) {
      i = 0;
    }
    i = i + rowIndex + 1;
    //i = Number(i).toLocaleString(); //May not work in all browsers.
    return i;
  }
};

/**
 * CheckboxSelectionModel с запоминанием выбора для постраничного вывода
 * @param {Object} cfg
 */
Bio.grid.PagedCheckboxSelectionModel = function(config){
  Ext.apply(this, config);
  this.globalSelections = {
    selall: false,
    items: []
  };
  Bio.grid.PagedCheckboxSelectionModel.superclass.constructor.call(this);
  
  this.addSelection = function(sel){
    if (this.globalSelections.items.indexOf(sel) == -1) 
      this.globalSelections.items.push(sel);
  };
  
  this.removeSelection = function(sel){
    this.globalSelections.items = this.globalSelections.items.removeAll(sel);
  }
};

Ext.extend(Bio.grid.PagedCheckboxSelectionModel, Ext.grid.CheckboxSelectionModel, {
  selallTipChecked: "Отменить все",
  selallTipUnChecked: "Выбрать все",
  
  initEvents: function(){
    Bio.grid.PagedCheckboxSelectionModel.superclass.initEvents.call(this);
    this.on('rowdeselect', this.onRowDeselect, this);
    this.on('rowselect', this.onRowSelect, this);
    this.grid.store.on('load', this.onLoad, this);
    this.grid.on('render', this.onRender, this);
  },
  
  onRender: function(s){
    var a = Ext.DomQuery.selectNode("div[class='x-grid3-hd-checker']", s.getEl().dom);
    Ext.QuickTips.register({
      target: a,
      text: this.selallTipUnChecked
    });
  },
  
  onRowSelect: function(s, i, r){
    var id = s.grid.store.getAt(i).id;
    if (!this.globalSelections.selall)
      this.addSelection(id);
    else
      this.removeSelection(id);
  },
  
  onRowDeselect: function(s, i, r){
    var id = s.grid.store.getAt(i).id;
    if (!this.globalSelections.selall)
      this.removeSelection(id);
    else
      this.addSelection(id);
  },
  
  onHdMouseDown: function(e, t){
    if (t.className == 'x-grid3-hd-checker') {
      this.globalSelections.selall = !Ext.fly(t.parentNode).hasClass('x-grid3-hd-checker-on');
      this.globalSelections.items = [];
      Ext.QuickTips.unregister(t);
      if(this.globalSelections.selall){
        Ext.QuickTips.register({
          target: t,
          text: this.selallTipChecked
        });
      }else{
        Ext.QuickTips.register({
          target: t,
          text: this.selallTipUnChecked
        });
      }
    }
    Bio.grid.PagedCheckboxSelectionModel.superclass.onHdMouseDown.call(this, e, t);
  },
  
  onLoad: function(s, r, o){
    if(!this.globalSelections.selall)
      Ext.each(this.globalSelections.items, function(e){
        for (var idx = 0, l = r.length; (idx < l) && (r[idx].id != e); idx++);
        if(idx < l) this.selectRow(idx, true);
      }, this);
    else
      for (var i = 0, ln = r.length; i < ln; i++){
        for (var idx = 0, l = this.globalSelections.items.length; (idx < l) && (this.globalSelections.items[idx] != r[i].id); idx++);
        if(idx == l) this.selectRow(i, true);
      };
  }
});

Ext.override(Ext.grid.EditorGridPanel, {
  /**
   * Вставляет новую запись в таблицу и если определена форма редактирования, то вызывает её.
   * @param {Object} frmName Имя формы редактирования.
   */
  insertNewRow: function(frmName){
    var grid = this;
    grid.stopEditing();
    var store = grid.store;
    store.insert(0, store.createNewRecord());
    var sm = grid.getSelectionModel();
    var len = grid.colModel.config.length;
    if (typeof(grid.showEditor) == "function") {
      for (var c = 0; c < len && grid.colModel.isFixed(c); c++);
      if (c == len) c = 0;
      if (typeof(sm.select) == "function") 
        sm.select(0, c);
      else if (typeof(sm.selectFirstRow) == "function") 
        sm.selectFirstRow();
      grid.showEditor(null, frmName, {
        fn: function(){
          if (this.ModalResult == Bio.form.ModalResult.CANCEL) {
            var row = Bio.Tools.getSeldRow(sm);
            if (row) 
              store.remove(row);
          }
          return true;
        }
      });
    } else {
      for (var c = 0; c < len && !grid.colModel.isCellEditable(c, 0); c++);
      if (c < len) 
        grid.startEditing(0, c);
    }
  },
  /**
   * Вызывает редактор выделенной записи.
   * @param {Object} frmName Имя формы редактирования.
   */
  editSelectedRow: function(frmName){
    var grid = this;
    grid.stopEditing();
    if (typeof(grid.showEditor) == "function") 
      grid.showEditor(null, frmName, {
        fn: function(){
          return true;
        }
      });
    else {
      var row = Bio.Tools.getSeldRow(grid.getSelectionModel());
      if (row) {
        var i = grid.store.indexOf(row);
        for (var c = 0; c < len && !grid.colModel.isCellEditable(c, i); c++);
        if (c < len) 
          grid.startEditing(i, c);
      }
    }
  },
  /**
   * Удаляет выделенные записи.
   */
  deleteSelectedRows: function(){
    var grid = this;
    var sm = grid.getSelectionModel();
    if (typeof(sm.getSelectedCell) == "function") {
      var cell = sm.getSelectedCell();
      if (cell != null) 
        grid.store.remove(grid.store.getAt(cell[0]));
    } else if (typeof(sm.getSelections) == "function") {
      var rows = sm.getSelections();
      if (Ext.isArray(rows))
        Ext.each(rows, function(row) {
          grid.store.remove(row);
        });
    }
  }
});

/**
 * Создаём вуаль для процесса сохранения данных
 */
Ext.override(Ext.grid.EditorGridPanel, {
  initEvents: Ext.grid.EditorGridPanel.prototype.initEvents.createSequence(function(){
    if (this.writeMask) {
      this.writeMask = new Bio.WriteMask(this.bwrap, Ext.apply({
        store: this.store
      }, this.writeMask));
    }
  }),
  
  reconfigure: function(store, colModel){
    if (this.writeMask) {
      this.writeMask.destroy();
      this.writeMask = new Bio.WriteMask(this.bwrap, Ext.apply({
        store: store
      }, this.initialConfig.writeMask));
    }
    Ext.grid.EditorGridPanel.superclass.reconfigure.call(this, store, colModel);
  },
  
  onDestroy: function(){
    if (this.rendered) {
      if (this.writeMask) {
        this.writeMask.destroy();
      }
    }
    Ext.grid.EditorGridPanel.superclass.onDestroy.call(this);
  }
});

Bio.WriteMask = function(el, config){
  this.el = Ext.get(el);
  Ext.apply(this, config);
  if (this.store) {
    this.store.on('beforewrite', this.onBeforeWrite, this);
    this.store.on('write', this.onWrite, this);
    this.store.on('writeexception', this.onWrite, this);
    this.removeMask = Ext.value(this.removeMask, false);
  }
};

Bio.WriteMask.prototype = {
  msg: 'Сохранение...',
  msgCls: 'x-mask-loading',
  disabled: false,
  
  disable: function(){
    this.disabled = true;
  },
  
  enable: function(){
    this.disabled = false;
  },
  
  onWrite: function(){
    this.el.unmask(this.removeMask);
  },
  
  onBeforeWrite: function(){
    if (!this.disabled) {
      this.el.mask(this.msg, this.msgCls);
    }
  },
  
  show: function(){
    this.onBeforeWrite();
  },
  
  hide: function(){
    this.onWrite();
  },
  
  destroy: function(){
    if (this.store) {
      this.store.un('beforewrite', this.onBeforeWrite, this);
      this.store.un('write', this.onWrite, this);
      this.store.un('writeexception', this.onWrite, this);
    }
  }
};
