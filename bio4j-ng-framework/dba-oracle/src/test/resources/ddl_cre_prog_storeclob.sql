create or replace procedure test_store_clob(p_param1 in varchar2, p_param2 in number, p_param3 in clob)
  is
  begin
    insert into test_tbl(fld1, fld2, fld3)
    values(p_param1, p_param2, p_param3);
  end;
