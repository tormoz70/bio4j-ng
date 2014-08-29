select a.tkldr_uid as region_uid, a.tkldr_name || ' ' || t.sname as region
  from reg$tkladr0 a
    inner join REG$TKLADRTP0 t on t.tkldrtp_id = a.tkldrtp_id
where a.prnt_tkldr_uid is null and a.tkldr_uid like '__00000000000'
