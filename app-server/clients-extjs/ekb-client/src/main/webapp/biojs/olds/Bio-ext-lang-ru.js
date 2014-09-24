/**
 * @author nick
 */
if(Ext.PagingToolbar){
  Ext.apply(Ext.PagingToolbar.prototype, {
    beforePageText : "Стр."
  });
};

if(Ext.ux.grid.GridFilters){
  Ext.apply(Ext.ux.grid.GridFilters.prototype, {
    menuFilterText: "Фильтр"
  });
};
if(Ext.ux.grid.filter.BooleanFilter){
  Ext.apply(Ext.ux.grid.filter.BooleanFilter.prototype, {
    yesText: "Да",
    noText: "Нет"
  });
};
if(Ext.ux.grid.filter.DateFilter){
  Ext.apply(Ext.ux.grid.filter.DateFilter.prototype, {
    beforeText: "До",
    afterText: "После",
    onText: "На"
  });
};
if(Ext.ux.grid.filter.ListFilter){
  Ext.apply(Ext.ux.grid.filter.ListFilter.prototype, {
    loadingText: "Загрузка..."
  });
};
