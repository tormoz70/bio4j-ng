--alter session set current_schema = BIOSYS;
create or replace view usrs as
select
    u.usr_uid,                    -- uid пользовател€
    u.usr_login,                  -- Ћогин пользовател€
    u.fio_fam,                    -- ‘амили€ пользовател€
    u.fio_fname,                  -- »м€ пользовател€
    u.fio_sname,                  -- ќтчество пользовател€
    u.reg_date,                   -- ƒата регистрации
    login$utl.get_usr_roles(u.usr_uid) as usr_roles, -- список ролей пользовател€ (разделитель ';')
    login$utl.get_usr_grants(u.usr_uid) as usr_grants, -- список разрешений пользовател€ (разделитель ';')
    u.email_addr,                 -- список эл. адресов пользовател€
    u.usr_phone,                  -- список телефонов пользовател€
    u.org_id,                     -- id подразделени€ пользовател€
    o.aname as org_name,          -- им€ подразделени€ пользовател€
    o.adesc as org_adesc,         -- описание подразделени€ пользовател€
    login$utl.get_org_path(u.org_id) as org_path, -- (вычисл€емое) - путь подразделени€ пользовател€ (описание)
    login$utl.get_org_idpath(u.org_id) as org_idpath, -- (вычисл€емое) - путь подразделени€ пользовател€ (ids)
    o.usr_uid as ws_owner,        -- (вычисл€емое) - UID пользовател€ - владельца рабочего пространства
    u.confirmed,                  -- —огласован
    u.garbaged,                   -- ¬ корзине
    '0' as locked,                -- (вычисл€емое) - «аблокирован
    login$utl.usr_has_role(u.usr_uid, 'DEBUGGER') as is_debug,   -- (вычисл€емое) - '1' - имеет роль debug
    login$utl.usr_has_role(u.usr_uid, 'ADMIN') as is_admin,      -- (вычисл€емое) - '1' - имеет роль admin
    login$utl.usr_has_role(u.usr_uid, 'WSADMIN') as is_wsadmin,  -- (вычисл€емое) - '1' - имеет роль wsowner - владелец рабочего пространства
    login$utl.usr_has_role(u.usr_uid, 'BIOROOT') as is_bioroot,  -- (вычисл€емое) - '1' - имеет роль bioroot
    u.extinfo                     -- ƒоп инф.
from usr u
  left join (
    select o1.*, w.usr_uid from org o1
      inner join uworkspace w on w.workspace_id = o1.workspace_id
  ) o on o.org_id = u.org_id

