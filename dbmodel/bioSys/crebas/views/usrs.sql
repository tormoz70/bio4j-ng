--alter session set current_schema = BIOSYS;
create or replace view usrs as
select
    u.usr_uid,                    -- uid ������������
    u.usr_login,                  -- ����� ������������
    u.fio_fam,                    -- ������� ������������
    u.fio_fname,                  -- ��� ������������
    u.fio_sname,                  -- �������� ������������
    u.reg_date,                   -- ���� �����������
    login$utl.get_usr_roles(u.usr_uid) as usr_roles, -- ������ ����� ������������ (����������� ';')
    login$utl.get_usr_grants(u.usr_uid) as usr_grants, -- ������ ���������� ������������ (����������� ';')
    u.email_addr,                 -- ������ ��. ������� ������������
    u.usr_phone,                  -- ������ ��������� ������������
    u.org_id,                     -- id ������������� ������������
    o.aname as org_name,          -- ��� ������������� ������������
    o.adesc as org_adesc,         -- �������� ������������� ������������
    login$utl.get_org_path(u.org_id) as org_path, -- (�����������) - ���� ������������� ������������ (��������)
    login$utl.get_org_idpath(u.org_id) as org_idpath, -- (�����������) - ���� ������������� ������������ (ids)
    o.usr_uid as ws_owner,        -- (�����������) - UID ������������ - ��������� �������� ������������
    u.confirmed,                  -- ����������
    u.garbaged,                   -- � �������
    '0' as locked,                -- (�����������) - ������������
    login$utl.usr_has_role(u.usr_uid, 'DEBUGGER') as is_debug,   -- (�����������) - '1' - ����� ���� debug
    login$utl.usr_has_role(u.usr_uid, 'ADMIN') as is_admin,      -- (�����������) - '1' - ����� ���� admin
    login$utl.usr_has_role(u.usr_uid, 'WSADMIN') as is_wsadmin,  -- (�����������) - '1' - ����� ���� wsowner - �������� �������� ������������
    login$utl.usr_has_role(u.usr_uid, 'BIOROOT') as is_bioroot,  -- (�����������) - '1' - ����� ���� bioroot
    u.extinfo                     -- ��� ���.
from usr u
  left join (
    select o1.*, w.usr_uid from org o1
      inner join uworkspace w on w.workspace_id = o1.workspace_id
  ) o on o.org_id = u.org_id

