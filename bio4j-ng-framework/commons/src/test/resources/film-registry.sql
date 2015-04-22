select * from (
  SELECT a.pureg_id, a.film_id, a.prnt_film_id, a.film_name, a.film_name_orig,
       a.hide_sess, a.cre_date,
       a.pu_verified,
       (case when a.pu_verified > 0 then 1 else 0 end) is_verified,
       decode(a.pu_verified, 0, '--новый', 1, '--автомат', 2, '--ручн', '--ошиб') as pu_verified_state,
       a.pu_num, a.pu_num_found,
       a.pu_vrfd_date, a.pu_vrfd_usr, a.org_id, a.prnt_org_id,
       a.org_name, a.prnt_org_name, a.genre
 FROM nsi$filmpus a WHERE rownum < 101) b
WHERE --(:SYS_CURUSERROLES in ('*', '1','2','5','6','7','8')) and
  (
    ((:seld_from is null) or (b.cre_date >= trunc(to_date(:seld_from)))) and
    ((:seld_to is null) or (b.cre_date < trunc(to_date(:seld_to)+1)))
  )
  and(
    (:filmname is null) or
    (
      (upper(b.film_name) like '%'||upper(:filmname)||'%') or
      (upper(b.film_name_orig) like '%'||upper(:filmname)||'%')
    )

  )
  and((:holding is null)   or ((b.prnt_org_id = nvl(to_number(regexp_substr(:holding, '^\d+$')), 0)) or
                              (upper(b.prnt_org_name) like '%'||upper(:holding)||'%')))
  and((:org is null)       or ((b.org_id = nvl(to_number(regexp_substr(:org, '^\d+$')), 0)) or
                              (upper(b.org_name) like '%'||upper(:org)||'%')))
