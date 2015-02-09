Ext.override(Ext.data.Store, {
    constructor: function(config) {
        var me = this, data;
        if(config && config.proxy)
            config.proxy['store'] = me;
        //me.initConfig(config);
        var storeAutoLoad = config.autoLoad;
        config.autoLoad = false;
        me.callParent([config]);
        me.setModel = function(m){me.model = m};
        me.autoLoad = storeAutoLoad;
        data = me.inlineData;
        if(!data && me.autoLoad)
            Ext.defer(me.load, 1, me, [ typeof me.autoLoad === 'object' ? me.autoLoad : undefined ]);
    }
});

Ext.override(Ext.data.Connection, {

    request0 : function(options) {
        options = options || {};
        var me = this,
            scope = options.scope || window,
            username = options.username || me.username,
            password = options.password || me.password || '',
            async,
            requestOptions,
            request,
            headers,
            xhr;
        if (me.fireEvent('beforerequest', me, options) !== false) {

            requestOptions = me.setOptions(options, scope);

            if (me.isFormUpload(options)) {
                me.upload(options.form, requestOptions.url, requestOptions.data, options);
                return null;
            }


            if (options.autoAbort || me.autoAbort) {
                me.abort();
            }


            async = options.async !== false ? (options.async || me.async) : false;
            xhr = me.openRequest(options, requestOptions, async, username, password);


            if (!me.isXdr) {
                headers = me.setupHeaders(xhr, options, requestOptions.data, requestOptions.params);
            }


            request = {
                id: ++Ext.data.Connection.requestId,
                xhr: xhr,
                headers: headers,
                options: options,
                async: async,
                binary: options.binary || me.binary,
                timeout: setTimeout(function() {
                    request.timedout = true;
                    me.abort(request);
                }, options.timeout || me.timeout)
            };

            me.requests[request.id] = request;
            me.latestId = request.id;

            if (async) {
                if (!me.isXdr) {
                    xhr.onreadystatechange = Ext.Function.bind(me.onStateChange, me, [request]);
                }
            }

            if (me.isXdr) {
                me.processXdrRequest(request, xhr);
            }


            xhr.send(requestOptions.data);
            if (!async) {
                return me.onComplete(request);
            }
            return request;
        } else {
            Ext.callback(options.callback, options.scope, [options, undefined, undefined]);
            return null;
        }
    },

    request : function(options) {
        var me = this;//, bioPubLogin = "$bio-anonymous$";
        var gp = {
            bm: Bio.app.APP_MODULE_KEY
        };
        Ext.apply(options.params, gp);
        var bioCode = (options && options.jsonData ? options.jsonData.bioCode : null);
            //isBiosysRequest = false;
//        if (Bio.tools.isDefined(bioCode)) {
//            isBiosysRequest = Ext.String.startsWith(bioCode, bioPubLogin+".");
//            bioCode = bioCode.substr((bioPubLogin+".").length);
//            options.jsonData.bioCode = bioCode;
//        }


//        if (isBiosysRequest === true) {
//            var o = Ext.apply({}, options);
//            var p = {
//                uid : bioPubLogin
//            };
//            Ext.apply(o.params, p);
//            return me.request0(o);
//        } else
//            return Bio.login.getUser({
//                fn: function(usr) {
//                    var o = Ext.apply({}, options);
//                    var p = {
//                        biocd : bioCode,
//                        uid : usr.login||usr.uid
//                    };
//                    o.params = Ext.apply(o.params, p);
//                    return me.request0(o);
//                },
//                scope: me
//            });

        var o = Ext.apply({}, options);
        var p = { biocd : bioCode };
        o.params = Ext.apply(o.params, p);
        if(Bio.app && Bio.app.curUsr) {
            p = { uid : Bio.app.curUsr.login || Bio.app.curUsr.uid };
            o.params = Ext.apply(o.params, p);
        }
        return me.request0(o);

    },

    onComplete0 : function(request, xdrResult) {
        var me = this,
            options = request.options,
            result,
            success,
            response;

        try {
            result = me.parseStatus(request.xhr.status);
        } catch (e) {
            // in some browsers we can't access the status if the readyState is not 4, so the request has failed
            result = {
                success : false,
                isException : false
            };

        }
        success = me.isXdr ? xdrResult : result.success;

        if (success) {
            response = me.createResponse(request);
            me.fireEvent('requestcomplete', me, response, options);
            Ext.callback(options.success, options.scope, [response, options]);
        } else {
            if (result.isException || request.aborted || request.timedout) {
                response = me.createException(request);
            } else {
                response = me.createResponse(request);
            }
            me.fireEvent('requestexception', me, response, options);
            Ext.callback(options.failure, options.scope, [response, options]);
        }
        Ext.callback(options.callback, options.scope, [options, success, response]);
        delete me.requests[request.id];
        return response;
    },

    onComplete : function(request, xdrResult) {
        Bio.app.waitMaskHide();
        var me = this,
            options = request.options,
            result,
            success,
            response;

        try {
            result = me.parseStatus(request.xhr.status);
        } catch (e) {
            // in some browsers we can't access the status if the readyState is not 4, so the request has failed
            result = {
                success : false,
                isException : false
            };

        }
        success = me.isXdr ? xdrResult : result.success;
        if (success)
            response = me.createResponse(request);
        else {
            if (result.isException || request.aborted || request.timedout)
                response = me.createException(request);
            else
                response = me.createResponse(request);
        }

        if(response.responseText) {
            var bioResponse = Ext.decode(response.responseText);
            if (bioResponse) {
                success = success && Bio.login.processUser(bioResponse, function(dr) { me.request(options); });
                // show unknown error
                if(bioResponse && bioResponse.success === false && bioResponse.exception && bioResponse.exception.class == "ru.bio4j.ng.model.transport.BioError") {
                    success = false;
                    Bio.dlg.showErr("Ошибка", bioResponse.exception.message, 400, 300, null);
                }
            } else {
                success = false;
                Bio.dlg.showErr("Ошибка", "Unknown response recived. responseText: " + (response.responseText || "<null>"), 400, 300, null);
            }
        } else if (response.timedout === true) {
            success = false;
            Bio.dlg.showErr("Ошибка", "Истекло время ожидания ответа!", 400, 300, null);
        } else {
            success = false;
            Bio.dlg.showErr("Ошибка", "ResponseText is empty!", 400, 300, null);
        }



        if (success === true) {
            me.fireEvent('requestcomplete', me, response, options);
            Ext.callback(options.success, options.scope, [response, options]);
        } else {
            me.fireEvent('requestexception', me, response, options);
            Ext.callback(options.failure, options.scope, [response, options]);
        }
        Ext.callback(options.callback, options.scope, [options, success, response]);
        delete me.requests[request.id];
        return response;
    }
});

Ext.override(Ext.data.proxy.Server, {

    processResponse: function(success, operation, request, response, callback, scope) {
        var me = this,
            reader,
            result;

        if (success === true) {
            reader = me.getReader();




            reader.applyDefaults = operation.action === 'read';

            result = reader.read(me.extractResponseData(response));

            Ext.apply(operation, {
                response: response,
                resultSet: result
            });

            operation.setCompleted();

            if (result.success !== false) {

// moved up
//                Ext.apply(operation, {
//                    response: response,
//                    resultSet: result
//                });

                operation.commitRecords(result.records);
//                moved up
//                operation.setCompleted();
                operation.setSuccessful();
            } else {
                operation.setException(result.message);
                me.fireEvent('exception', this, response, operation);
            }
        } else {
            me.setException(operation, response);
            me.fireEvent('exception', this, response, operation);
        }


        if (typeof callback == 'function') {
            callback.call(scope || me, operation);
        }

        me.afterRequest(request, success);
    }

});

Ext.override(Ext.toolbar.Paging, {
    displayInfo: true,
    beforePageText: "Страница",
    afterPageText: "из {0}",
    displayMsg: "Загружено {0} - {1} of {2}",
    emptyMsg: "Нет данных",
    firstText: "Первая страница",
    nextText: "Следующая страница",
    prevText: "Предыдущая страница",
    lastText: "Последняя страница",
    refreshText: "Обновить",
    inputItemWidth: 60,

    doRefresh : function(){
        var me = this,
            current = me.store.currentPage;

        if (me.fireEvent('beforechange', me, current) !== false) {
            //me.store.loadPage(current);
            me.store.reload();
        }
    },

    updateInfo : function(){
        var me = this,
            displayItem = me.child('#displayItem'),
            store = me.store,
            pageData = me.getPageData(),
            count, msg;

        if (displayItem) {
            count = store.getCount();
            if (count === 0) {
                msg = me.emptyMsg;
            } else {
                msg = Ext.String.format(
                    me.displayMsg,
                    pageData.fromRecord,
                    pageData.toRecord,
                    pageData.total
                );
            }
            displayItem.setText(msg);
        }
    }
});

// Localization

Ext.override(Ext.view.AbstractView, {
    loadingText: 'Загрузка...'
});

Ext.override(Ext.form.field.Base, {
    invalidText: 'Значение в этом поле не корректно!' //'The value in this field is invalid'
});


Ext.override(Ext.form.field.Text, {
    minLengthText : 'Длинна поля должна быть не менее {0}!', //'The minimum length for this field is {0}',
    maxLengthText : 'Длинна поля должна быть не более {0}!', //'The maximum length for this field is {0}',
    blankText : 'Поле не может быть пустым' //'This field is required'

});

Ext.override(Ext.form.field.Number, {
    minText: 'Значение поля не может быть меньше чем {0}!', //'The minimum value for this field is {0}',
    maxText: 'Значение поля не может быть больше чем {0}!', //'The maximum value for this field is {0}',
    nanText: 'Значение "{0}"  не является числом!', //'{0} is not a valid number',
    negativeText: 'Значение поля не может быть меньше нуля!' //'The value cannot be negative'
});

Ext.override(Ext.form.field.Date, {
    disabledDaysText: 'Отключено', //"Disabled",
    disabledDatesText: 'Отключено', //"Disabled",
    minText: 'Дата должно быть равно либо позднее {0}!', //"The date in this field must be equal to or after {0}",
    maxText: 'Дата должно быть равно либо ранее {0}!', //"The date in this field must be equal to or before {0}",
    invalidText: 'Значение "{0}" не является датой. Должно удовлетворять формату "{1}"!', //"{0} is not a valid date - it must be in the format {1}"
    format : "d.m.Y"
});

Ext.override(Ext.picker.Date, {
    todayText: 'Сегодня', //'Today',
    minText: 'Дата ранее минимальной!', //'This date is before the minimum date',
    maxText: 'Дата позднее максимальной!', //'This date is after the maximum date',
    disabledDaysText: 'Отключено', //'Disabled',
    disabledDatesText: 'Отключено', //'Disabled',
    nextText: 'Следующий месяц (CTRL+Right)', //'Next Month (Control+Right)',
    prevText: 'Предыдущий месяц (CTRL+Left)', //'Previous Month (Control+Left)',
    monthYearText: 'Выберите месяц (Control+Up/Down чтобы изменить год)', //'Choose a month (Control+Up/Down to move years)'
    cancelText: 'Отмена', //'Cancel',
    startDay : 1
});

//Ext.Date = Ext.apply(Ext.Date, {dayNames: ["Sunday","Monday","Tuesday","Wednesday","Thursday","Friday","Saturday"]});
//Ext.Date = Ext.apply(Ext.Date, {dayNames: ["Воскресенье","Понедельник","Вторник","Среда","Четверг","Пятница","Суббота"]});
Ext.Date.dayNames = ["Воскресенье","Понедельник","Вторник","Среда","Четверг","Пятница","Суббота"];
//Ext.Date = Ext.apply(Ext.Date, {monthNames: ["January","February","March","April","May","June",
//    "July","August","September","October","November","December"]});
//Ext.Date = Ext.apply(Ext.Date, {monthNames: ["Январь","Февраль","Март","Апрель","Май","Июнь",
//    "Июль","Август","Сентябрь","Октябрь","Ноябрь","Декабрь"]});
Ext.Date.monthNames = ["Январь","Февраль","Март","Апрель","Май","Июнь",
    "Июль","Август","Сентябрь","Октябрь","Ноябрь","Декабрь"];

Ext.Date.defaultFormat = "d.m.Y";

Ext.override(Ext.grid.RowEditor, {
    saveBtnText  :  'Применить', //'Update',
    cancelBtnText:  'Отменить', //'Cancel',
    errorsText:     'Ошибки', //'Errors',
    dirtyText:      'Необходимо применить или отменить изменения!' //'You need to commit or cancel your changes'
});

