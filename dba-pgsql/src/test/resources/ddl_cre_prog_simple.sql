create or replace function test_stored_prop(p_param1 in text, p_param2 out numeric)
returns numeric as
$BODY$
begin
  insert into test_tbl values('test', 1);
  p_param2 := length(p_param1);
end;
$BODY$ language plpgsql;