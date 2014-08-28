create or replace procedure test_stored_prop(p_param1 in varchar2, p_param2 out number)
  is
  begin
    insert into test_tbl values('test', 1); p_param2 := length(p_param1);
  end;
