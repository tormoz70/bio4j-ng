SELECT 
  a.packet_id as id,
  a.org_id,
  o.holding_id,
  a.sess_prnt_org_id,
  a.sess_org_id,
  a.ip_addr as ip,
  a.registred as date_incoming,
  decode(a.packet_name, '...', a.zip_name, a.packet_name) as packet_name,
  a.zip_name,
  a.processed as date_processing,
  a.is_loaded,
  a.cur_pstate as cur_pstate0,
  case a.tcktsdeleted when '1' then 'удален' else 
        (case a.is_loaded when '1' then 'загружен' else (
            case a.cur_pstate
            when -1  then 'получен'
            when 0  then 'зарегистрирован'
            when 1  then 'к загрузке'
            when 2  then 'проверка'
            when 3  then 'проверен'
            when 4  then 'загрузка'
            when 5  then 'загружен'
            when 6  then 'ошибка'
            when 7  then 'обработан'
            end
        ) end) 
    end as cur_pstate,
  a.cur_pstate_msg as last_pstate_msg,
  a.is_log_downloaded,
  a.log_downloaded,
  o.time_zone as time_zone,
  decode(o.test, 1, 'тестовый', 'реальный') as test,
  decode(a.load_method, 
          0, 'Авт. система', 
          1, 'Из кабинета', 
          2, 'CreateXMLStatic', 
          3, 'CreateXMLMobile', 
          4, 'EkbUploadRobot', 
          5, 'Grader', 'не определен') as load_method,
  decode(a.show_date, null, '00000000', to_char(a.show_date, 'YYYYMMDD')) part_key,
  a.show_date,
  a.tcktsdeleted
  FROM FPACKETND a
        inner join org$r o on o.id_org = a.sess_org_id
  WHERE a.packet_name = nvl(:packet_name_full, a.packet_name) 
    and (a.registred >= :reg_from AND a.registred < :reg_to+1)
    and (a.sess_org_id = case when :force_org_id is null then a.sess_org_id else :force_org_id end)
    /*@{cutiif}*//*[SYS_CURUSERROLES != '3']*/and (a.org_id = to_number(:SYS_CURODEPUID))/*{cutiif}@*/
    and (:SYS_CURUSERROLES != '4' or (:SYS_CURUSERROLES = '4' and o.holding_id = to_number(:SYS_CURODEPUID)))
    and (        
        /*@{cutiif}*//*[org_id != null]*/(a.org_id = to_number(regexp_substr(:org_id, '^\d+$'))) and/*{cutiif}@*/
        /*@{cutempty-sess_prnt_org_id}*/(a.sess_prnt_org_id = to_number(regexp_substr(:sess_prnt_org_id, '^\d+$'))) and/*{cutempty-sess_prnt_org_id}@*/
        /*@{cutempty-sess_org_id}*/(a.sess_org_id = to_number(regexp_substr(:sess_org_id, '^\d+$'))) and/*{cutempty-sess_org_id}@*/
        /*@{cutempty-packet_name}*/(lower(a.packet_name) like '%'||lower(:packet_name)||'%') and/*{cutempty-packet_name}@*/
        /*@{cutempty-ip}*/(lower(a.ip_addr) like '%'||lower(:ip)||'%') and/*{cutempty-ip}@*/
        /*@{cutempty-cur_pstate}*/(a.cur_pstate = detectCurPStateId(:cur_pstate)) and/*{cutempty-cur_pstate}@*/
        /*@{cutempty-message}*/(lower(a.cur_pstate_msg) like '%'||lower(:message)||'%') and/*{cutempty-message}@*/
        /*@{cutempty-test}*/(o.test = :test) and/*{cutempty-test}@*/
        1=1
    )
