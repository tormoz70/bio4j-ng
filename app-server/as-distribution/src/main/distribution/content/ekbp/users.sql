/*${sql.list}*/
SELECT
    a.usr_uid,    /*${col.usr_uid; type:string; title:"UID"}*/
    a.role_id,    /*${col.role_id; type:integer; title:"����"}*/
    a.login_name, /*${col.login_name; type:string; title:"�����"}*/
    a.email,      /*${col.email; type:string; title:"email"}*/
    a.org_id,     /*${col.org_id; type:integer; title:"������ �� ���"}*/
    a.fio,        /*${col.email; type:string; title:"���"}*/
    a.contacts,   /*${col.email; type:string; title:"��������"}*/
    a.deleted,    /*${col.email; type:boolean; title:"������"}*/
    a.comments    /*${col.email; type:string; title:"��������"}*/
FROM givcapi.usrs a
/
