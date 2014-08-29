    select * --MIN(a.tkldr_uid) KEEP (DENSE_RANK FIRST ORDER BY level) into v_rslt
      from REG$TKLADR a
     where (((a.tkldr_uid like substr(a.tkldr_uid, 1, 5)||'000___00') and (substr(a.tkldr_uid, 9, 3) <> '000')) or
             ((a.tkldr_uid like substr(a.tkldr_uid, 1, 5)||'___00000') and (substr(a.tkldr_uid, 6, 3) <> '000')) or
              ((a.tkldr_uid like substr(a.tkldr_uid, 1, 5)||'______00') and (substr(a.tkldr_uid, 6, 6) <> '000000')) or
               ((a.tkldr_uid like substr(a.tkldr_uid, 1, 5)||'00000000') and (a.tkldrtp_id = 103))
          )
          and a.tkldrtp_id  not in (418)
     start with a.tkldr_uid = '7700000000000'
      connect by prior a.tkldr_uid = a.prnt_tkldr_uid;

select * from givc_org.org o where o.id_org = 4314
