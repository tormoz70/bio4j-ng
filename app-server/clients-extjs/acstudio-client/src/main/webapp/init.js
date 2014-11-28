Ext.require('*');
Ext.onReady(function() {

    Ext.ns('Ekb');
    Ext.define('Ekb.RootPanel', {
        extend: 'Ext.view.View',
        alias: 'widget.rootpanel',
        autoHeight   : true,
        frame        : false,
        cls          : 'bservices',
        itemSelector : 'dl',
        overItemCls  : 'over',
        trackOver    : true,
        tpl          : Ext.create('Ext.XTemplate',
            '<div id="bsrv-ct">',
                '<tpl for=".">',
                    '<div style="clear:left">',
                        '<a name="{id}"></a>',
                        '<h2><div>{title}</div></h2>',
                        '<dl>',
                            '<tpl for="items">',
                                '<dd><a href="{url}" target="_blank"><img src="shared/screens/{icon}"/>',
                                    '<div><h4>{text}',
                                        '<tpl if="this.isNew(values.status)">',
                                            '<span class="new-sample"> (New)</span>',
                                        '<tpl elseif="this.isUpdated(values.status)">',
                                            '<span class="updated-sample"> (Updated)</span>',
                                        '<tpl elseif="this.isExperimental(values.status)">',
                                            '<span class="new-sample"> (Experimental)</span>',
                                        '<tpl elseif="status">',
                                            '<span class="status"> ({status})</span>',
                                        '</tpl>',
                                    '</h4><p>{desc}</p></div>',
                                '</a></dd>',
                            '</tpl>',
                        '</dl>',
                    '</div>',
                '</tpl>',
            '</div>', {
             isExperimental: function(status){
                 return status == 'experimental';
             },
             isNew: function(status){
                 return status == 'new';
             },
             isUpdated: function(status){
                 return status == 'updated';
             }
        }),

        onContainerClick: function(e) {
            var group = e.getTarget('h2', 3, true);

            if (group) {
                group.up('div').toggleCls('collapsed');
            }
        }
    });

    var catalog = Ekb.catalog.groups;

    for (var i = 0, c; c = catalog[i]; i++) {
        c.id = 'bsrv-' + i;
    }

    var doOnLogin = function(options, success, response){

        if(success === true) {
            var store = Ext.create('Ext.data.Store', {
                fields     : ['id', 'title', 'items'],
                data       : catalog
            });

            Ext.create('Ekb.RootPanel', {
                renderTo   : 'root-container',
                store : store
            });

            var tpl = Ext.create('Ext.XTemplate',
                '<tpl for="."><li><a href="#{id}">{title:stripTags}</a></li></tpl>'
            );
            tpl.overwrite('bservices-menu', catalog);
        }
    }

//    Bio.login.showDialog({
//        scope: this,
//        fn: doOnLogin
//    });
    Ext.Ajax.request({
        url: Bio.tools.bldBioUrl("/biosrv"),
        params: {rqt: 'ping'},
        callback:doOnLogin
    });


    var bodyStyle = document.body.style,
        headerEl  = Ext.get('hd'),
        footerEl  = Ext.get('ft'),
        bodyEl    = Ext.get('bd'),
        sideBoxEl = bodyEl.down('div.side-box'),
        leftColumnEl = bodyEl.down('div.left-column'),
        rightColumnEl = bodyEl.down('div.right-column');

    var doResize = function() {
        bodyStyle.overflow = 'hidden';
        var windowHeight = Ext.Element.getViewportHeight(),
            windowWidth = Ext.Element.getViewportWidth(),
            viewportWidth = Math.max(960, windowWidth - 600),
            marginWidth = Math.floor((windowWidth - viewportWidth) / 2),
            footerHeight  = footerEl.getHeight(),
            //titleElHeight = titleEl.getHeight() + titleEl.getMargin('tb'),
            headerHeight  = headerEl.getHeight(),// + titleElHeight,
            warnEl = Ext.get('fb'),
            warnHeight = warnEl ? warnEl.getHeight() : 0,
            availHeight;

        Ext.fly('viewport').setStyle('marginLeft', marginWidth + 'px');
        leftColumnEl.setStyle('width', (viewportWidth - leftColumnEl.getMargin('r')) + 'px');
        rightColumnEl.setStyle('left', (viewportWidth - rightColumnEl.getWidth()) + 'px');

        availHeight = windowHeight - ( footerHeight + headerHeight + bodyEl.getMargin('tb')) - warnHeight;
        Ext.fly('root-container').setHeight(Math.floor(Math.max(availHeight, sideBoxEl.getHeight())));
        bodyStyle.overflow = '';
    };

    // Resize on demand
    Ext.EventManager.onWindowResize(doResize);
    doResize();

    var firebugWarning = function () {
        var cp = Ext.create('Ext.state.CookieProvider');

        if (window.console && window.console.firebug && ! cp.get('hideFBWarning')){
            var tpl = Ext.create('Ext.Template',
                '<div id="fb" style="border: 1px solid #FF0000; background-color:#FFAAAA; display:none; padding:15px; color:#000000;"><b>Warning: </b> Firebug is known to cause performance issues with Ext JS. <a href="#" id="hideWarning">[ Hide ]</a></div>'
            );
            var newEl = tpl.insertFirst('root-container');

            Ext.fly('hideWarning').on('click', function() {
                Ext.fly(newEl).slideOut('t',{remove:true});
                cp.set('hideFBWarning', true);
            });
            Ext.fly(newEl).slideIn();
        }
    };

    var hideMask = function () {
        Ext.get('loading').remove();
        Ext.fly('loading-mask').animate({
            opacity:0,
            remove:true,
            callback: firebugWarning
        });
    };

    Ext.defer(hideMask, 250);



});