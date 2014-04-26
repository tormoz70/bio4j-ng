/*${sql.list}*/
SELECT
  a.doc_uid/*${col.doc_uid; title:"Уникальный ID"; type:string}*/,
  a.aname, a.prodtime,
  a.region,
  a.subdivision,
  a.autor,
  a.crereason_val,
  a.storythng1_val,
  a.facingthng1_val,
  a.theftmthd_val,
  a.aqurdthng_val,
  a.matrlthng1_val,
  a.decorthng_val,
  a.coloring1,
  a.makestyle1,
  a.techthng1_val,
  a.typantq_val,
  a.sizelen, a.sizewdth, a.sizehght, a.sizeunit, a.weight, a.weightunit,
  a.vprice, a.lettering, a.adesc,
  to_char(a.theftdate, 'DD/MM/YYYY') theftdate/*${col.theftdate; title:Дата хищения; type:string}*/,
  a.crimcodartcl, a.story,
  a.owner_fio
  FROM idbm_api.idcardantq_edo a
/

/*${sql.edit}*/
SELECT
  a.doc_uid,
  a.aname, a.prodtime,
  a.region,
  a.subdivision,
  a.autor,
  a.crereason_id,
  a.storythng1_id,
  a.storythng2_id,
  a.facingthng1_id,
  a.facingthng2_id,
  a.theftmthd_id,
  a.aqurdthng_id,
  a.matrlthng1_id,
  a.matrlthng2_id,
  a.decorthng_id,
  a.coloring1,
  a.coloring2,
  a.makestyle1,
  a.makestyle2,
  a.techthng1_id,
  a.techthng2_id,
  a.typantq_id,
  a.sizelen, a.sizewdth, a.sizehght, a.sizeunit, a.weight, a.weightunit,
  a.vprice, a.lettering, a.adesc,
  a.theftdate,
  a.crimcodartcl, a.story,
  a.owner_fio,
  a.owner_surname,
  a.owner_name,
  a.owner_patronymic,
  a.owner_dob, a.owner_pob
  FROM idbm_api.idcardantq_edo a
WHERE a.doc_uid = /*$docuid, type=string {*/'test-docuid-2'/*}*/
/
