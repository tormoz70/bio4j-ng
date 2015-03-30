create or replace function test_stored_inout(p_param1 inout integer, p_param2 in varchar, p_param in integer, p_param4 in integer)
returns void as
$BODY$
begin
  p_param1 := length(p_param2);
end;
$BODY$ language plpgsql;