Ext.Loader.setConfig({enabled: true});

Ext.Loader.setPath('Ext.ux', '../../../ux/');
Ext.Loader.setPath('Idbm.grid', './');
Ext.Loader.setPath('Idbm.form', './');
Ext.require([
    'Ext.ux.PreviewPlugin',
    'Idbm.form.AntiquesEdit',
    'Idbm.form.EmpEdit'
]);

Ext.onReady(function () {

    Ext.ns('Idbm');

    Ext.tip.QuickTipManager.init();

    var grid = Ext.create('Bio.grid.Panel', {
        id: 'bio-antiques-grid',
        title: 'Default Tab',
        //forceFit: true,
//        columns: [
//            {
//                text: "Hire date - cli",
//                dataIndex: "HIREDATE",
//                renderer: function (value, p, r) {
//                    return Ext.String.format('{0}', value+" - string");//Ext.Date.dateFormat(value, 'd.m.Y'));
//                }
//            }
//        ],
        storeCfg: {
            bioCode:'idcardantq.list',
            autoLoad: true
        },
        tbar: Ext.create('Ext.toolbar.Toolbar', {
            items: [
                {
                    text: 'Редактировать',
                    scope: this,
                    handler: function () {
                        var grd = Ext.getCmp('bio-antiques-grid');
                        var win2 = Ext.create('Ext.window.Window', {
                            //autoHeight: true,
                            height: 600,
                            //autoWidth: true,
                            width: 800,
                            //x: 50,
                            //y: 50,
                            title: 'Редактировать предмет антиквариата',
                            closable: true,
                            plain: true,
                            layout: 'fit',
                            items: Ext.create('Idbm.form.AntiquesEdit', {bioOwnerGrid:grd})
                        });
                        win2.show();
                    }
                }
            ]
        })
    });

    Ext.define('Idbm.Viewport', {
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
                //html: '<a href="http://google.com" id="logo"><img src="../../../shared/images/baboon_48.png" alt="Bio4j"/></a>',
                border: false,
                margins: '0 0 5 0',
                items: [
                    {
                        xtype: "panel",
                        frame: true,
                        html: '<a href="http://mvd.ru/mvd/structure1/Centri/Glavnij_informacionno_analiticheskij_cen" id="logo"><img src="../../../shared/images/giac.png" alt="Bio4j"/></a>'
                    }
                ]
            },
            {
                region: 'west',
                collapsible: true,
                collapsed: true,
                title: 'Navigation',
                width: 150
                // could use a TreePanel or AccordionLayout for navigational items
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
//                items: Ext.create("Idbm.grid.Antiques", {
//                    store: Ext.create('Bio.data.Store', {
//                        bioCode: 'emp.test',
//                        pageSize: 10,
//                        autoLoad: true
//                    })
//                })
                items: grid
            }
        ]
    });

    Ext.create('Idbm.Viewport', {
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