Ext.Loader.setPath('Idbm.form', './');

Ext.require([
    'Idbm.form.AntiquesEdit'
]);

Ext.define('Idbm.grid.Antiques', {
    extend: 'Ext.grid.Panel',
    store: null,
    constructor: function (config) {
        this.tbar = Ext.create('Ext.toolbar.Toolbar', {
            items: [
                {
                    text: 'Добавить',
                    scope: this,
                    handler: function () {
                        var win2 = Ext.create('Ext.window.Window', {
                            //autoHeight: true,
                            height: 600,
                            //autoWidth: true,
                            width: 800,
                            //x: 50,
                            //y: 50,
                            title: 'Создать новый предмет антиквариата',
                            closable: true,
                            plain: true,
                            layout: 'fit',
                            items: Ext.create('Idbm.form.AntiquesEdit', {})
                        });
                        win2.show();
                    }
                }
            ]
        });
        this.bbar = Ext.create('Ext.PagingToolbar', {
            store: config.store,
            displayInfo: true,
            beforePageText: "Страница",
            afterPageText: "из {0}",
            displayMsg: "Загружено {0} - {1} of {2}",
            emptyMsg: "Нет данных"
//                items:[
//                    '-',
//                    {
//                        text: "Show Preview",
//                        pressed: true,
//                        enableToggle: true,
//                        toggleHandler: function(btn, pressed) {
//                            var preview = Ext.getCmp('gv').getPlugin('preview');
//                            preview.toggleExpanded(pressed);
//                        }
//                    }
//                ]
        });
        this.callParent(arguments);
    },

    title: 'Default Tab',
//        disableSelection: true,
//        loadMask: true,
//        autoLoad: false,
//        viewConfig: {
//            id: 'gv',
//                trackOver: false,
//                stripeRows: false,
//                plugins: [{
//                ptype: 'preview',
//                bodyField: 'excerpt',
//                expanded: true,
//                pluginId: 'preview'
//            }]
//        },
    // grid columns
    columns: [
        {
            // id assigned so we can apply custom css (e.g. .x-grid-cell-topic b { color:#333 })
            // TODO: This poses an issue in subclasses of Grid now because Headers are now Components
            // therefore the id will be registered in the ComponentManager and conflict. Need a way to
            // add additional CSS classes to the rendered cells.
//            id: 'topic',
            text: "EmpNo",
            dataIndex: 'EMPNO',
//            flex: 1,
//            renderer: function (value, p, record) {
//                return Ext.String.format(
//                    '<b><a href="http://sencha.com/forum/showthread.php?t={2}" target="_blank">{0}</a></b><a href="http://sencha.com/forum/forumdisplay.php?f={3}" target="_blank">{1} Forum</a>',
//                    value,
//                    record.data.forumtitle,
//                    record.getId(),
//                    record.data.forumid
//                );
//            },
            sortable: true
        },
        {
            text: "EName",
            dataIndex: 'ENAME',
            width: 100,
//            hidden: true,
            sortable: true
        },
        {
            text: "Job",
            dataIndex: 'JOB',
            width: 70,
            align: 'right',
            sortable: true
        },
        {
//            id: 'last',
            text: "Manager",
            dataIndex: 'MGR',
            width: 150,
            sortable: true
        },
        {
            text: "Hire date",
            dataIndex: "HIREDATE",
            renderer: function (value, p, r) {
                return Ext.String.format('{0}', Ext.Date.dateFormat(value, 'd.m.Y'));
            }
        },
        {
            text: "Sal",
            dataIndex: "SAL"
        },
        {
            text: "Comm",
            dataIndex: "COMM"
        },
        {
            text: "DeptNo",
            dataIndex: "DEPTNO",
            hidden: true
        }
    ]

});

