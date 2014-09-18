Ext.namespace("Bio.request.store");

Ext.define('Bio.request.store.Request', {
    /**
     * Код запрашиваемого инф. объекта. Фактически - это путь к файлу описания метаданных запроса
     */
    bioCode: '',

    /**
     * параметры информационного объекта
     */
    bioParams: undefined,

    /**
     * id объекта Store, который послал данный запрос
     */
    storeId: undefined,

    constructor: function(config) {
        var me = this;
        Ext.apply(me, config);
    }
//    jsonData: function(cfg) {
//        var me = this;
//        return Ext.apply(me, cfg);
//    }

});

Ext.define('Bio.request.store.GetDataSet', {
    extend: 'Bio.request.store.Request',

    rqt: 'crud.ds.get',

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
    location: undefined

});

Ext.define('Bio.request.store.GetRecord', {
    extend: 'Bio.request.store.Request',

    rqt: 'crud.rec.get',

    /**
     * Значение первичного ключя записи, которую надо загрузить на клиент
     * Примечание: используется для загрузки данных в форму редактирования, например.
     */
    id: undefined

});

Ext.define('Bio.request.store.PostData', {
    extend: 'Bio.request.store.Request',

    rqt: 'crud.ds.post',

    modified: [],
    slavePostData: []
});
