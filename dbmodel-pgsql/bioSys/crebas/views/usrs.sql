create or replace view BIOSYS.usrs as
select
    u.usr_uid,                    -- uid пользователя
    u.usr_login,                  -- Логин пользователя
    u.fio_fam,                    -- Фамилия пользователя
    u.fio_fname,                  -- Имя пользователя
    u.fio_sname,                  -- Отчество пользователя
    u.reg_date,                   -- Дата регистрации
    login$utl.get_usr_roles(u.usr_uid) as usr_roles, -- список ролей пользователя (разделитель ';')
    login$utl.get_usr_grants(u.usr_uid) as usr_grants, -- список разрешений пользователя (разделитель ';')
    u.email_addr,                 -- список эл. адресов пользователя
    u.usr_phone,                  -- список телефонов пользователя
    u.org_id,                     -- id подразделения пользователя
    o.aname as org_name,          -- имя подразделения пользователя
    o.adesc as org_adesc,         -- описание подразделения пользователя
    login$utl.get_org_path(u.org_id) as org_path, -- (вычисляемое) - путь подразделения пользователя (описание)
    login$utl.get_org_idpath(u.org_id) as org_idpath, -- (вычисляемое) - путь подразделения пользователя (ids)
    o.usr_uid as ws_owner,        -- (вычисляемое) - UID пользователя - владельца рабочего пространства
    u.confirmed,                  -- Согласован
    u.garbaged,                   -- В корзине
    '0' as locked,                -- (вычисляемое) - Заблокирован
    login$utl.usr_has_role(u.usr_uid, 'DEBUGGER') as is_debug,   -- (вычисляемое) - '1' - имеет роль debug
    login$utl.usr_has_role(u.usr_uid, 'ADMIN') as is_admin,      -- (вычисляемое) - '1' - имеет роль admin
    login$utl.usr_has_role(u.usr_uid, 'WSADMIN') as is_wsadmin,  -- (вычисляемое) - '1' - имеет роль wsowner - владелец рабочего пространства
    login$utl.usr_has_role(u.usr_uid, 'BIOROOT') as is_bioroot,  -- (вычисляемое) - '1' - имеет роль bioroot
    u.extinfo                     -- Доп инф.
from BIOSYS.usr u
  left join (
    select o1.*, w.usr_uid from BIOSYS.org o1
      inner join BIOSYS.uworkspace w on w.workspace_id = o1.workspace_id
  ) o on o.org_id = u.org_id

