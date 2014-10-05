Ext.Loader.setConfig({enabled: true});
Ext.Loader.setPath('Ext.ux', '../../../ux');
Ext.Loader.setPath('Cmon.form', '.');
Ext.require([
    'Ext.ux.PreviewPlugin'
]);

Ext.onReady(function () {

    document.title = document.title + " - [Реестр счетчиков]";
    Ext.ns('Cmon');

    Ext.tip.QuickTipManager.init();

    var grid = Ext.create('Bio.grid.Panel', {
        id: 'cmon-cntrs-grid',
        title: 'Default Tab',
        rowLines: true,
        columnLines: true,
        storeCfg: {
            bioCode:'nsi.cntrs.list',
            autoLoad: true,
            remoteSort: true
        },
        tbar: Ext.create('Ext.toolbar.Toolbar', {
            items: [
                {
                    text: 'Правка',
                    scope: this,
                    handler: function () {
                        var grd = Ext.getCmp('cmon-cntrs-grid');
                        var seldId = grd.getSelectedId();
                        var win2 = Ext.create('Ext.window.Window', {
                            title: 'Редактировать счетчик',
                            closable: true,
                            plain: true,
                            modal: true,
                            layout: 'fit',
                            items: Ext.create('Cmon.form.CntrDetails', {
                                ekb: { orgId: seldId }
                            })
                        });
                        win2.show();
                    }
                }

            ]
        })
    });

    Ext.define('Cmon.Viewport', {
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
                        html: '<a href="https://bitbucket.org/tormoz70/bio4j-ng/wiki/Home" id="logo"><img height="50" width="50" src="../../../shared/images/smart_home_big.png" alt="КИНОБИЛЕТ"/></a>'
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

    Ext.create('Cmon.Viewport', {
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

});