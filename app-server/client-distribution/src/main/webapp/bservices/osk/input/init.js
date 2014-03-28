Ext.Loader.setConfig({enabled: true});
Ext.Loader.setPath('Ext', '/extjs');
Ext.require([
    'Ext.Msg'
]);
Ext.onReady(function(){
    Ext.Msg.alert('Приветствие','Hello "Ввод данных"!');
});
