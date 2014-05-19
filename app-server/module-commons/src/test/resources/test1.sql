SELECT
  a.doc_uid, a.aname, a.prodtime, a.region,
  a.subdivision,
  a.autor,
  a.crereason_id,
  a.techthng1_id,
  a.typantq_id,
  a.sizelen, a.sizewdth, a.sizehght, a.sizeunit, a.weight, a.weightunit,
  a.vprice, a.lettering, a.adesc, a.theftdate, a.crimcodartcl, a.story,
  a.owner_fio, a.owner_surname, a.owner_name, a.owner_patronymic,
  a.owner_dob, a.owner_pob
  FROM idbm_api.idcardantq_edo a
WHERE a.doc_uid = /*${param.docuid; type:string; debug:*/'test-docuid-2'/*}*/
  AND a.owner_name like /*${param.owner; debug:*/'some%'/*}*/
