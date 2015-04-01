create or replace function test_stored_cursor(p_param1 in varchar, p_param2 out refcursor)
returns refcursor as
$BODY$
begin
  open p_param2 for select * from pg_roles;
end;
$BODY$ language plpgsql;