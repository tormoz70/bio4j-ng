create or replace procedure test_stored_inout(p_param1 in out number, p_param2 in varchar2, p_param3 in number, p_param4 in number)
  is
  begin
    p_param1 := length(p_param2);
  end;
