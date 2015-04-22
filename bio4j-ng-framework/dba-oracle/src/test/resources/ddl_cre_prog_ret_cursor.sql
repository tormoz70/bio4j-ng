create or replace procedure test_stored_cursor(p_param1 in varchar2, p_param2 out sys_refcursor)
  is
  begin
    open p_param2 for select * from sys.user_users;
  end;
