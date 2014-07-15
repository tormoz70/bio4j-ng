Ext.namespace("Bio.request.store");
Ext.define('Bio.request.store.GetData', {
    singleton:true,
    config: {

        /**
         * Код модуля
         */
        bioModuleKey: '',

        /**
         * Код запрашиваемого инф. объекта. Фактически - это путь к файлу описания метаданных запроса
         */
        bioCode: '',

        /**
         * параметры информационного объекта
         */
        bioParams: undefined,

        /**
         * Начальная позиция
         */
        offset: 0,

        /**
         * Размер страницы
         */
        pagesize: 0,

        /**
         * Параметры сортировки для запросов GET
         */
        sort: undefined,

        /**
         * Параметры фильтрации для запросов GET
         */
        filter: undefined,

        /**
         * Параметры для запросов GET если надо установить курсор в нужную позицию
         * Примечание: необходимо использовать в сочетании с offset
         */
        location: undefined

    },
    jsonData: function(cfg) {
        var me = this;
        return Ext.apply(me.config, cfg);
    }
});

Ext.define('Bio.request.store.PostData', {

});
