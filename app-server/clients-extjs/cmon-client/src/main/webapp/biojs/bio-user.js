Ext.namespace("Bio");
//Ext.define('Bio.Org', {
//    config: {
//
//        /**
//         * UID организации
//         */
//        uid: '',
//
//        /**
//         * Название организации
//         */
//        name: ''
//    }
//});

Ext.define('Bio.User', {
    config: {

        /**
         * LOGIN пользователя в виде <user_name>/<password>
         */
        login: '',

        /**
         * UID пользователя
         */
        uid: '',

        /**
         * Имя пользователя
         */
        name: '',

        /**
         * Список ролей через разделитель ","
         */
        roles: '',

        /**
         * Список разрешений через разделитель ","
         */
        grants: '',

        /**
         * Организация
         */
        org: null
    },

    constructor: function(config) {
        var me = this;
        Ext.apply(me, config);
    }
});

