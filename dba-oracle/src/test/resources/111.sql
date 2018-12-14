    SELECT * FROM (
        SELECT pgng$wrpr0.*, ROWNUM rnum$pgng
          FROM ( with fss as (
    SELECT
      a.film_id, a.org_id, a.pureg_id, a.pu_num,
      a.prnt_film_id,  a.pu_num_found,
      b.rent_name as film_name_orig
    FROM givc_nsi.filmpus a
      left join givc_nsi.pfilmnums b on b.pu_id = a.punum_id_found
)
  , sess0 as (
    SELECT
      a.part_key, a.sess_id,
      a.sess_title, a.pu_num,
      max(f.film_name_orig) as film_name_orig,
      max(f.pu_num_found) as pu_num_found,
      max(sr.sroom_name) as sroom_name,

      sum(a.peop_cnt_ttl) as people_total,
      sum(a.tckt_cnt_ttl) as tickets_total,
      sum(a.free_cnt_ttl) as tickets_free,
      sum(a.storn_cnt_ttl) as tickets_total_annul,
      sum(a.sum_ttl + a.disc_ttl) as tickets_sum_plan,
      sum(a.disc_ttl) as tickets_discount_sum,
      sum(a.sum_ttl) as tickets_sum_fact,

      min(a.show_date) as show_date_min,
      max(a.show_date) as show_date,
      max(a.load_date) as load_date,
      max(a.nonstop) as nonstop,
      max(a.festival) as festival
    FROM DWH$SESS a
      INNER JOIN FSS f ON f.film_id = a.film_id AND f.org_id = a.org_id AND f.pureg_id = a.pureg_id
      INNER JOIN ORGM$SHOWROOMS0 sr ON sr.sroom_id = a.sroom_id
    WHERE (
            (:p_sys_curusr_roles in ('6'))
            or ((:p_sys_curusr_roles in ('4')) and (
              (a.org_id = to_number(:p_sys_curusr_org_uid)) or
              (a.org_id in (select o.id_org from givc_org.org o where o.holding_id = to_number(:p_sys_curusr_org_uid))))
            )
            or ((:p_sys_curusr_roles in ('3')) and (
              (a.org_id = to_number(:p_sys_curusr_org_uid)) or
              (a.org_id in (select nvl(o.holding_id, o.id_org) from givc_org.org o where o.id_org = to_number(:p_sys_curusr_org_uid))))
            )
          )
          AND a.org_id = :org_id
          AND (a.part_key between
    decode(:reg_from, null, '00000000', to_char(:reg_from, 'YYYYMMDD')) AND
    decode(:reg_to, null, '99999999', to_char(:reg_to, 'YYYYMMDD')))
          --AND ((:reg_from is null or a.show_date >= :reg_from) and
          --      (:reg_to is null or a.show_date <= :reg_to))
          AND (:film is null or lower(a.sess_title) like '%'||lower(:film)||'%')
          AND (:sroom_id is null or a.sroom_id = :sroom_id)
    GROUP BY a.part_key, a.sess_id, a.sess_title, a.pu_num
)
select
  sess_id,
  sess_title,
  film_name_orig,
  pu_num,
  pu_num_found,
  sroom_name,
  show_date,
  part_key,
  tickets_total,
  tickets_free,
  tickets_total_annul,
  tickets_sum_plan,
  tickets_discount_sum,
  tickets_sum_fact,
  decode(tickets_total, 0, 0, round(tickets_sum_plan/tickets_total, 5)) as ticket_price_plan,
  decode(tickets_total, 0, 0, round(tickets_sum_fact/tickets_total, 5)) as ticket_price_fact,
  festival,
  :force_org_id as force_org_id
from sess0
ORDER BY sroom_name, show_date desc, sess_title
 ) pgng$wrpr0
    ) pgng$wrpr WHERE (pgng$wrpr.rnum$pgng > :paging$offset) AND (pgng$wrpr.rnum$pgng <= :paging$last)