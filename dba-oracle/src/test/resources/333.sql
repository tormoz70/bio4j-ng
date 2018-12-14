with rrr as (
    select s.rpt_uid, s.rec_num, s.rpt_state, ds.state_caption, s.cre_date, s.state_desc,
        row_number() over (partition by s.RPT_UID order by s.rec_num desc) rnum
    from
      givc_rpt.rstate s
        inner join rpt$rpt_states ds on ds.state_id = s.rpt_state
), rrr1 as (
select a.rpt_uid, a.rec_num, a.rpt_state, a.state_caption, a.cre_date, a.state_desc, r.usr_uid, u.login_name, u.org_id, u.role_id, r.rpt_code, r.parent_obj_uid, r.manual,
        r.rpt_result, r.rpt_result_fn, dbms_lob.getlength(r.rpt_result) as rpt_result_size
from rrr a
    inner join givc_rpt.rqueue r on r.rpt_uid = a.rpt_uid
    left join usrs u on u.usr_uid = r.usr_uid
 where a.rnum = 1)
select a.rpt_uid, a.rec_num, a.rpt_state, a.state_caption, a.cre_date, a.state_desc, a.usr_uid,
        a.login_name, a.org_id, a.role_id, a.rpt_code, a.parent_obj_uid, a.manual, a.rpt_result_fn, a.rpt_result_size
from rrr1 a
 where a.rpt_uid = :p_rpt_uid