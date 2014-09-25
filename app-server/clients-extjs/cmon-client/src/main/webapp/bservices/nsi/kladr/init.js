Ext.Loader.setConfig({enabled: true});

Ext.onReady(function () {

    document.title = document.title + " - [Справочник адресов]";

    var hideMask = function () {
        Ext.get('loading').remove();
        Ext.fly('loading-mask').animate({
            opacity: 0,
            remove: true,
            callback: null //firebugWarning
        });
    };

    Ext.defer(hideMask, 250);

});