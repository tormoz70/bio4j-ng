Ext.Loader.setConfig({enabled: true});
Ext.Loader.setPath('Ext.ux', '../../../ux');
//Ext.Loader.setPath('Ekb.grid', './');
Ext.Loader.setPath('Ekb.form', '.');
Ext.require([
    'Ext.ux.PreviewPlugin'
    //'Ekb.form.FilmDetails'
]);

//alert("Required loaded!");

Ext.onReady(function () {

    document.title = document.title + " - [Справочник фильмов]";
//    alert("Redy!!!");
    Ext.ns('Ekb');

    Ext.tip.QuickTipManager.init();

    var grid = Ext.create('Bio.grid.Panel', {
        id: 'ekb-films-grid',
        title: 'Default Tab',
        storeCfg: {
            bioCode:'ekbp@cabinet.film-registry',
            bioParams:[
                {
                    name:"prm1",
                    value:"qwe"
                },
                {
                    name:"prm2",
                    value:"asd"
                }
            ],
            //autoLoad: true,
            remoteSort: true
        },
//        columns: [
//            {
//                dataIndex: 'cre_date',
//                renderer : Ext.util.Format.dateRenderer('m/d/Y')
//            }
//        ],
        tbar: Ext.create('Ext.toolbar.Toolbar', {
            items: [
                {
                    text: 'Детали',
                    scope: this,
                    handler: function () {
                        var grd = Ext.getCmp('ekb-films-grid');
                        var win2 = Ext.create('Ext.window.Window', {
                            //autoHeight: true,
                            height: 600,
                            //autoWidth: true,
                            width: 800,
                            //x: 50,
                            //y: 50,
                            title: 'Детальная информация по фильму',
                            closable: true,
                            plain: true,
                            layout: 'fit'
                            //items: Ext.create('Ekb.form.FilmDetails', {bioOwnerGrid:grd})
                        });
                        win2.show();
                    }
                }
            ]
        })
    });

    Ext.define('Ekb.Viewport', {
        extend: 'Ext.container.Viewport',
        layout: 'border',
        items: [
            {
                region: 'north',
                xtype: "panel",
                frame: true,
                layout: {
                    type: 'hbox',
                    align: 'stretch'
                },
                border: false,
                margins: '0 0 5 0',
                items: [
                    {
                        xtype: "panel",
                        frame: true,
                        html: '<a href="http://ekinobilet.ru" id="logo"><img height="50" width="80" src="../../../shared/images/logo_gr.png" alt="КИНОБИЛЕТ"/></a>'
                    }
                ]
            },
            {
                region: 'west',
                collapsible: true,
                collapsed: true,
                title: 'Navigation',
                width: 150
            },
            {
                region: 'south',
                title: 'South Panel',
                collapsible: true,
                collapsed: true,
                html: 'Information goes here',
                split: true,
                height: 100,
                minHeight: 100
            },
            {
                region: 'east',
                title: 'East Panel',
                collapsible: true,
                collapsed: true,
                split: true,
                width: 150
            },
            {
                region: 'center',
                xtype: 'tabpanel', // TabPanel itself has no title
                activeTab: 0,      // First tab active by default
                items: grid
            }
        ]
    });

    Ext.create('Ekb.Viewport', {
        renderTo: 'root-container',
        store: null
    });

    var hideMask = function () {
        Ext.get('loading').remove();
        Ext.fly('loading-mask').animate({
            opacity: 0,
            remove: true,
            callback: null //firebugWarning
        });
    };

    Ext.defer(hideMask, 250);

    grid.store.locate(1182);

});