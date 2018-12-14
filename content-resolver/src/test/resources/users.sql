/*${sql.list}*/
SELECT
    a.usr_uid,    /*${col.usr_uid; type:string; title:"UID"}*/
    a.role_id,    /*${col.role_id; type:integer; title:"Роль"}*/
    a.login_name, /*${col.login_name; type:string; title:"Логин"}*/
    a.email,      /*${col.email; type:string; title:"email"}*/
    a.org_id,     /*${col.org_id; type:integer; title:"Ссылка на орг"}*/
    a.fio,        /*${col.email; type:string; title:"ФИО"}*/
    a.contacts,   /*${col.email; type:string; title:"Контакты"}*/
    a.deleted,    /*${col.email; type:boolean; title:"Удален"}*/
    a.comments    /*${col.email; type:string; title:"Комменты"}*/
FROM givcapi.usrs a
/
