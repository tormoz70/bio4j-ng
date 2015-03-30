create or replace function test_stored_error(p_param1 in varchar, p_param2 out integer)
returns void as
$BODY$
begin
  raise exception 'FTW';
end;
$BODY$ language plpgsql;