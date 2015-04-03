Ext.namespace('Ims');
Ims.catalog = {
    groups: [
        {
            "title": "Личный кабинет",
            "items": [
                {
                    "text": "Панель управления",
                    "url": "bservices/cabinet/panel/index.jsp",
                    "icon": "kitchensink.gif",
                    "desc": "Панель управления демонтрациями"
                }
            ]
        },
        {
            "title": "НСИ",
            "items": [
                {
                    "text": "Справочник сценариев",
                    "url": "bservices/nsi/scripts/index.jsp",
                    "icon": "soap-grid.png",
                    "desc": "Каталог всех сценариев ..."
                },
                {
                    "text": "Справочник медиа-файлов",
                    "url": "bservices/nsi/media/index.jsp",
                    "icon": "soap-grid.png",
                    "desc": "Каталог всех медиа-файлов загруженных на сервер ..."
                }
            ]

        },
        {
            "title": "Администрирование",
            "items": [
                {
                    "text": "Пользователи",
                    "url": "bservices/admin/usr/index.jsp",
                    "icon": "kitchensink.gif",
                    "desc": "Управление пользователями"
                }
            ]

        }
    ]
};
