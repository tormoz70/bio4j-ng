Ext.namespace("Bio.request.store");
Ext.define('Bio.request.store.GetDataSet', {
    singleton:true,
    config: {

        rqt: 'crud.ds.get',
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
        pageSize: 0,

        /**
         * Параметры сортировки для запросов GET
         */
        sort: undefined,

        /**
         * Параметры фильтрации для запросов GET
         */
        filter: undefined,

        /**
         * Параметр для запросов GET если надо установить курсор в нужную позицию
         * Фактически это значение первичного ключя записи которую нужно найти в таблице
         *   и установить на нее курсор после загрузки на клиент
         * Примечание: необходимо использовать в сочетании с offset
         */
        location: undefined,

        /**
         * Значение первичного ключя записи, которую надо загрузить на клиент
         * Примечание: используется для загрузки данных в форму редактирования, например.
         */
        id: undefined

    },
    jsonData: function(cfg) {
        var me = this;
        return Ext.apply(me.config, cfg);
    }
});

Ext.define('Bio.request.store.PostData', {

});
