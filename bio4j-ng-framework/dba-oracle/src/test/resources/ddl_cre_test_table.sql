begin
  execute immediate 'create table test_tbl(fld1 varchar2(10), fld2 number)';
exception
  when others then
    null;
end;
