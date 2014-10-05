Ext.namespace('Ekb');
Ekb.catalog = {
    groups: [
        {
            "title": "Личный кабинет",
            "items": [
                {
                    "text": "Регистрация",
                    "url" : "bservices/cabinet/monitor/index.jsp",
                    "icon": "kitchensink.gif",
                    "desc": "Монитор"
                },
                {
                    "text": "Протоколы",
                    "url" : "bservices/cabinet/protocols/index.jsp",
                    "icon": "feeds.png",
                    "desc": "Протоколы загрузки данных"
                },
                {
                    "text": "Отчеты",
                    "url" : "bservices/cabinet/reports/index.jsp",
                    "icon": "calendar.png",
                    "desc": "Аналитические отчеты по загруженным данным"
                }
            ]
        },
        {
            "title": "НСИ",
            "items": [
                {
                    "text": "Реестр счетчиков",
                    "url" : "bservices/nsi/cntrs/index.jsp",
                    "icon": "soap-grid.png",
                    "desc": "Реестр спецификаций счетчиков"
                },
                {
                    "text": "Классификаторы",
                    "url" : "bservices/nsi/dicts/index.jsp",
                    "icon": "soap-grid.png",
                    "desc": "Редактор всех классификаторов"
                },
                {
                    "text": "Справочник адресов",
                    "url" : "bservices/nsi/kladr/index.jsp",
                    "icon": "amf-grid.png",
                    "desc": "Справочник адресов"
                }
            ]

        },
        {
            "title": "Администрирование",
            "items": [
                {
                    "text": "Пользователи",
                    "url" : "bservices/admin/usrs/index.jsp",
                    "icon": "soap-grid.png",
                    "desc": "Редактор всех классификаторов"
                }

            ]

        }

    ]
};
