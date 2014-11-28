/*#test*/
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
