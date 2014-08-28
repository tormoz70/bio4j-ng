create or replace procedure test_stored_error(p_param1 in varchar2, p_param2 out number)
  is
  begin
    raise_application_error(-20000, 'FTW');
  end;