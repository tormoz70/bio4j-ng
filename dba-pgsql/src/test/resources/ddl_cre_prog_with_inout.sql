create or replace function test_stored_inout(p_param1 inout numeric, p_param2 in varchar, p_param3 in numeric, p_param4 in numeric)
returns numeric as
$BODY$
begin
  p_param1 := length(p_param2);
end;
$BODY$ language plpgsql;