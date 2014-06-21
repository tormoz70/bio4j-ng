//Ext.namespace("Bio");
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
    }
});

