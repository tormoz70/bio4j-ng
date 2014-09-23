declare 
  procedure schema_ddl(p_data_location in varchar2, p_user_name in varchar2, p_drop_only in boolean default false)
  is
    cs_init_pwd varchar2(30) := 'j125';
    v_ts_data varchar2(30) := upper(p_user_name)||'_DATA';
    v_ts_indx varchar2(30) := upper(p_user_name)||'_INDX';
    function ts_exists(pp_ts_name in varchar2) return number
    is
      v_result pls_integer;
    begin
      select count(1) into v_result from sys.dba_tablespaces a
        where a.tablespace_name = upper(pp_ts_name);
      return v_result;
    end;
    function user_exists(pp_user_name in varchar2) return number
    is
      v_result pls_integer;
    begin
      select count(1) into v_result from sys.dba_users a
        where a.username = upper(pp_user_name);
      return v_result;
    end;
    procedure drop_user
    is
    begin
      if user_exists(p_user_name) > 0 then
        execute immediate 'DROP USER '||upper(p_user_name)||' CASCADE';
      end if;
      if ts_exists(v_ts_data) > 0 then
        execute immediate 'DROP TABLESPACE '||v_ts_data||' INCLUDING CONTENTS AND DATAFILES CASCADE CONSTRAINTS';
      end if;
      if ts_exists(v_ts_indx) > 0 then
        execute immediate 'DROP TABLESPACE '||v_ts_indx||' INCLUDING CONTENTS AND DATAFILES CASCADE CONSTRAINTS';
      end if;
    end;
  begin
    if p_drop_only then
      drop_user;
      return;
    end if;
    if ts_exists(v_ts_data) = 0 then
      execute immediate 'CREATE TABLESPACE '||v_ts_data||' DATAFILE '''||p_data_location||'/'||v_ts_data||'.ORA'' SIZE 100M';
    end if;
    if ts_exists(v_ts_indx) = 0 then
      execute immediate 'CREATE TABLESPACE '||v_ts_indx||' DATAFILE '''||p_data_location||'/'||v_ts_indx||'.ORA'' SIZE 100M';
    end if;
    if user_exists(p_user_name) = 0 then
      execute immediate 'CREATE USER '||upper(p_user_name)||'
  IDENTIFIED BY '||cs_init_pwd||'
  DEFAULT TABLESPACE '||upper(p_user_name)||'_DATA
  TEMPORARY TABLESPACE TEMP
  PROFILE DEFAULT';
    end if;
    execute immediate 'GRANT RESOURCE TO '||upper(p_user_name);
    execute immediate 'GRANT UNLIMITED TABLESPACE TO '||upper(p_user_name);
  end;
begin
  schema_ddl('&datapath', 'BIOSYS', true);
  schema_ddl('&datapath', 'BIOSYS');
end;
/
GRANT ALTER ANY PROCEDURE TO biosys
/
GRANT CREATE ANY DIRECTORY TO biosys
/
GRANT CREATE ANY SEQUENCE TO biosys
/
GRANT CREATE ANY TABLE TO biosys
/
GRANT CREATE ANY VIEW TO biosys
/
GRANT CREATE PROCEDURE TO biosys
/
GRANT CREATE SESSION TO biosys
/
GRANT DROP ANY DIRECTORY TO biosys
/
GRANT DROP ANY SEQUENCE TO biosys
/
GRANT SELECT ANY TABLE TO biosys
/
GRANT UNLIMITED TABLESPACE TO biosys
/
GRANT AQ_ADMINISTRATOR_ROLE TO biosys
/
GRANT DBA TO biosys
/
GRANT RESOURCE TO biosys
/
ALTER USER biosys DEFAULT ROLE ALL
/
GRANT SELECT ON dba_datapump_jobs TO biosys
/
GRANT SELECT ON dba_tab_columns TO biosys
/
GRANT EXECUTE ON dbms_lock TO biosys
/
GRANT EXECUTE ON dbms_lock TO biosys
/
GRANT EXECUTE ON dbms_session TO biosys
/
GRANT EXECUTE ON utl_smtp TO biosys
/
GRANT SELECT ANY TABLE TO biosys
/

exec DBMS_NETWORK_ACL_ADMIN.DROP_ACL(acl => 'send_mail.xml');
exec DBMS_NETWORK_ACL_ADMIN.CREATE_ACL(acl => 'send_mail.xml',description => 'send_mail ACL',principal => 'BIOSYS',is_grant => true,privilege => 'connect');
exec DBMS_NETWORK_ACL_ADMIN.ADD_PRIVILEGE(acl => 'send_mail.xml',principal => 'BIOSYS',is_grant  => true,privilege => 'resolve');
exec DBMS_NETWORK_ACL_ADMIN.ASSIGN_ACL(acl => 'send_mail.xml',host => 'mail.givc.ru'); 
commit;

