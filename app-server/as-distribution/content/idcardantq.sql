/*#list*/
SELECT
  a.doc_uid/*@doc_uid, title=Уникальный ID, javaType=string*/,
  a.aname, a.prodtime,
  a.region,
  a.subdivision,
  a.autorthng_val,
  a.crereason_val,
  a.storythng_val,
  a.facingthng_val,
  a.theftmthd_val,
  a.aqurdthng_val,
  a.matrlthng_val,
  a.decorthng_val,
  a.colorthng_val,
  a.stylethng_val,
  a.techthng_val,
  a.typantq_val,
  a.sizelen, a.sizewdth, a.sizehght, a.sizeunit, a.weight, a.weightunit,
  a.vprice, a.lettering, a.adesc,
  to_char(a.theftdate, 'DD/MM/YYYY') theftdate/*@theftdate, title=Дата хищения, javaType=string*/,
  a.crimcodartcl, a.story,
  a.owner_fio
  FROM idbm_api.idcardantq_edo a

/*#edit*/
SELECT
  a.doc_uid,
  a.aname, a.prodtime,
  a.region,
  a.subdivision,
  a.autorthng_id,
  a.crereason_id,
  a.storythng_id,
  a.facingthng_id,
  a.theftmthd_id,
  a.aqurdthng_id,
  a.matrlthng_id,
  a.decorthng_id,
  a.colorthng_id,
  a.stylethng_id,
  a.techthng_id,
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