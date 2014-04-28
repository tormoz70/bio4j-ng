--{hints type:select(default)|exec; wrapMode:all(default)|none|filter+sort+paging}
/*${hints type:exec; wrap:sort+paging}*/
SELECT
  a.doc_uid, a.aname, a.prodtime, a.region,
  a.subdivision,
  /*${col.subdivision;
    pk:true;
    mandatory:true;
    align:right;
    title:"\"Дивизион\"; (miter)";
    width:150;
    format:"0,00";
    hidden:true;
    readonly:true;
  }*/
  a.autor, /*${col.autor; type:string; title:"Автор"}*/
  a.crereason_id, /*${col.crereason_id; title:"Причина"}*/
  a.techthng1_id,
  a.typantq_id,
  a.sizelen, a.sizewdth, a.sizehght, a.sizeunit, a.weight, a.weightunit,
  a.vprice, a.lettering, a.adesc, a.theftdate, a.crimcodartcl, a.story,
  a.owner_fio, a.owner_surname, a.owner_name, a.owner_patronymic,
  a.owner_dob, a.owner_pob
  FROM idbm_api.idcardantq_edo a
WHERE a.doc_uid = /*${param.docuid; type:string; debug:*/'test-docuid-2'/*}*/
  AND a.owner_name like /*${param.owner; debug:*/'some%'/*}*/
